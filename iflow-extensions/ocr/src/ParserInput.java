import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import com.java4less.ocr.docparser.DocumentDef;
import com.java4less.ocr.docparser.Parser;
import com.java4less.ocr.docparser.TextMarkDef;
import com.java4less.ocr.docparser.data.Document;
import com.java4less.ocr.docparser.data.DocumentSet;
import com.java4less.ocr.docparser.fields.FieldDef;
import com.java4less.ocr.docparser.fields.FieldPositionDef;
import com.java4less.ocr.docparser.references.EndOfLineRef;
import com.java4less.ocr.docparser.sections.SectionDef;
import com.java4less.ocr.docparser.sections.TextMarkCondition;
import com.java4less.ocr.tess3.OCRFacade;


public class ParserInput {

	/**
	 * this program will conver the file order.PNG to text and then will parse the text to extract the purchase order information
	 * @param args
	 */
	public static void main(String[] args) {
		//createDefinitionWithAPI();
		//System.exit(0);
		
		try {
			OCRFacade facade=new OCRFacade();

			String content=facade.recognizeFile("forminput2.png", "eng");
			
            String filename = "input2.txt";            
            FileWriter out = new FileWriter(filename);
            out.write(content);
            out.close();

		
		    BufferedReader reader = new BufferedReader( new FileReader ("input2.txt"));
		    String line  = null;
		    StringBuilder stringBuilder = new StringBuilder();
		    while( ( line = reader.readLine() ) != null ) {
		        stringBuilder.append( line );
		    }
		    
		    String text = stringBuilder.toString();
			
			DocumentDef docDef=new DocumentDef();
			docDef.loadFromXml("formDefinition.xml");
			
			Parser parser=new Parser(docDef);
			DocumentSet docSet=parser.parse(text);
			
			if (docSet.getCount()<1) {
				System.err.println("no form found");
				System.exit(-1);
			}

			printAllSectionsAndFields(docSet);

			//System.exit(0);
			
			Document doc=docSet.getDocument(0); // we assume each image has one order only
			
			// print order information to standard output
			System.out.println("Nome: "+doc.getSectionByName("Header")[0].getField("Name"));
			System.out.println("Morada: "+doc.getSectionByName("Header")[0].getField("Address"));
			System.out.println("Observacoes: "+doc.getSectionByName("Header")[0].getField("Comments"));
			
			// another option, create XML file for the extracted data
			System.out.println("\n\nXML output=\n"+docSet.toXml());
								
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * create the document definition using the Java API instead of a XML file
	 * @return
	 */
	private static DocumentDef createDefinitionWithAPI() {
		
	
		// create document definition
		DocumentDef docDef=new DocumentDef();
		docDef.setName("Formulario");
		SectionDef header=new SectionDef("Header");
		header.setStartCondition(new TextMarkCondition(new TextMarkDef("FORMULARIO")));
		docDef.addSection(header);
		
		// fields of the header
		// define label
		TextMarkDef namelabel=new TextMarkDef("Nome:","NameLabel");
		TextMarkDef addresslabel=new TextMarkDef("Morada:","AdressLabel");
		TextMarkDef commentslabel=new TextMarkDef("Observacoes:","CommentsLabel");
		//TextMarkDef commentslabel=new TextMarkDef("Modelo 23/2011","ModelLabel");

		header.addTextMark(namelabel);
		// define field which will be right to the label
		header.addField(new FieldDef("Name",new FieldPositionDef(namelabel,1,FieldPositionDef.DIR_RIGHT),new FieldPositionDef(namelabel,0,FieldPositionDef.DIR_DOWN)));

		header.addTextMark(addresslabel);
		// define field which will be right to the label
		FieldPositionDef addressFieldPos = new FieldPositionDef(addresslabel,1,FieldPositionDef.DIR_BETWEEN);
		// NAO FUNCIONA
		addressFieldPos.setReference1(addresslabel);
		addressFieldPos.setReference2(new EndOfLineRef());
		
		header.addField(new FieldDef("Address",addressFieldPos,new FieldPositionDef(addresslabel,0,FieldPositionDef.DIR_DOWN)));
		
		header.addTextMark(commentslabel);	
		// define field which will be right to the label
		header.addField(new FieldDef("Comments",new FieldPositionDef(commentslabel,1,FieldPositionDef.DIR_RIGHT),new FieldPositionDef(commentslabel,0,FieldPositionDef.DIR_DOWN)));

		
		// you can save the document definition to an XML file
	    try {
			docDef.saveToXml(new FileOutputStream("formDefinition.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return docDef;
		
	}
	
	/**
	 * print all sections and fields in the standard output
	 * @param doc
	 */
	private static void printAllSectionsAndFields(DocumentSet docSet) {
		
		int docCount=docSet.getCount();
		for (int i=0;i<docCount;i++) {
			Document doc=docSet.getDocument(i);
			System.out.println("New document");
			for (int j=0;j<doc.getAllSections().length;j++)  {
				System.out.println("Section "+doc.getAllSections()[j].getName());
				
				for (int h=0;h<doc.getAllSections()[j].getFieldCount();h++)
					System.out.println("Field  "+doc.getAllSections()[j].getField(h).getName()+"="+doc.getAllSections()[j].getField(h).getValue().getValue());
				
			}
					
		}
		
	}

}
