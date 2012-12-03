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
package pt.iflow.api.documents;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;


public abstract class DocumentIdentifier {

  public static DocumentIdentifier getInstance(int docid) {
    return new IFlowDocumentIdentifier(docid);
  }
  
  public static DocumentIdentifier getInstance(String docid) {
    if (StringUtils.isEmpty(docid)) {
      return new NullDocumentIdentifier();
    }
    try {
      return new IFlowDocumentIdentifier(Integer.parseInt(docid));
    } catch (NumberFormatException nfe) {
      return new DMSDocumentIdentifier(UUID.fromString(docid));
    }
  }
  
  public abstract String getId();

  @Override
  public String toString() {
    return getId();
  }

  DocumentIdentifier() {}
}
