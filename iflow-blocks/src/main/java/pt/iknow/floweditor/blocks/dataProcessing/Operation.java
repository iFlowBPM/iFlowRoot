/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package pt.iknow.floweditor.blocks.dataProcessing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.utils.StringUtilities;

public abstract class Operation {
  
  private static final String OPERATIONS_DTD_STRINGID = "-//IFLOW//DTD Operations XML";

  protected final transient FlowEditorAdapter adapter;
  
  public Operation(FlowEditorAdapter adapter) {
    this.adapter = adapter;
  }
  
  public abstract List<OperationField> getFields();

  public String[] getProperties() {
    return new String [0];
  }

  public boolean isIgnorable() {
    return false;
  }
  
  public abstract String getCode();
  
  public abstract String toString();
  
  public static Collection<Operation> getOperations(final FlowEditorAdapter adapter) {
    final Collection<Operation> operations = new ArrayList<Operation>(30);
    operations.add(new OpNone(adapter));
    try {
      InputStream in = Operation.class.getResourceAsStream("operations.xml");
      InputSource src = new InputSource(in);
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(true);
      SAXParser saxParser = factory.newSAXParser();
      
      DefaultHandler handler = new DefaultHandler() {
        private OperationImpl op = null; 
        private HashSet<String> codes = new HashSet<String>(30);
        
        public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
          if(StringUtilities.isEqual(OPERATIONS_DTD_STRINGID, publicId)) {
            return new InputSource(Operation.class.getResourceAsStream("operations.dtd"));
          }
          return super.resolveEntity(publicId, systemId);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
          if(StringUtilities.isEqualIgnoreCase(localName, "operation")) {
            op = null;
          }
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
          if(StringUtilities.isEqualIgnoreCase(localName, "operation")) {
            String className = attributes.getValue("class");
            if(StringUtilities.isNotEmpty(className)) {
              try {
                Operation op = (Operation) Class.forName(className).newInstance();
                if(codes.contains(op.getCode())) return;
                codes.add(op.getCode());
                operations.add(op);
              } catch (Exception e) {
                e.printStackTrace();
              }
            } else {
              String code = attributes.getValue("code");
              String labelkey = attributes.getValue("labelkey");
              String label = attributes.getValue("label");
              if(StringUtilities.isNotEmpty(labelkey)) label = adapter.getString(labelkey);
              op = new OperationImpl(adapter, code, label);
              if(codes.contains(op.getCode())) return;
              codes.add(op.getCode());
              operations.add(op);
            }
          } else if(StringUtilities.isEqualIgnoreCase(localName, "field")) {
            if(null == op) return;
            int type = OperationField.TYPE_EXPRESSION;
            String sType = attributes.getValue("type");
            if(StringUtilities.isEqualIgnoreCase("expression", sType)) {
              type = OperationField.TYPE_EXPRESSION;
            } else if(StringUtilities.isEqualIgnoreCase("catalog", sType)) {
              type = OperationField.TYPE_ANY;
            } else if(StringUtilities.isEqualIgnoreCase("any", sType)) {
              type = OperationField.TYPE_ANY;
            } else if(StringUtilities.isEqualIgnoreCase("array", sType)) {
              type = OperationField.TYPE_ARRAY;
            } else if(StringUtilities.isEqualIgnoreCase("scalar", sType)) {
              type = OperationField.TYPE_SCALAR;
            }
            
            String fieldName = attributes.getValue("name");
            
            String labelkey = attributes.getValue("labelkey");
            String label = attributes.getValue("label");
            if(StringUtilities.isNotEmpty(labelkey)) label = adapter.getString(labelkey);
            
            OperationField field = new OperationField(type, fieldName, label);
            op.fields.add(field);
          }
        }

        // ignore errors and warnings
        public void error(SAXParseException e) throws SAXException {
          adapter.log("Parse error: "+e.getMessage()+" at line: "+e.getLineNumber()+" column: "+e.getColumnNumber());
        }
        public void fatalError(SAXParseException e) throws SAXException {
          adapter.log("FATAL ERROR: "+e.getMessage()+" at line: "+e.getLineNumber()+" column: "+e.getColumnNumber(), e);
        }
        public void warning(SAXParseException e) throws SAXException {
          adapter.log("Parse warning: "+e.getMessage()+" at line: "+e.getLineNumber()+" column: "+e.getColumnNumber());
        }
      };

      saxParser.parse(src, handler);
      handler = null;
    } catch (Throwable t) {
      t.printStackTrace();
    }
    
    return operations;
  }
  
  
  private static class OperationImpl extends Operation {
    private String code;
    private String desc;
    private List<OperationField> fields;
    
    private OperationImpl(FlowEditorAdapter adapter, String code, String desc) {
      super(adapter);
      this.code = code;
      this.desc = desc;
      this.fields = new ArrayList<OperationField>();
    }

    public String getCode() {
      return code;
    }

    public List<OperationField> getFields() {
      return fields;
    }

    public String toString() {
      return desc;
    }
  }

}
