import com.java4less.ocr.docparser.DocumentDef;
import com.java4less.ocr.docparser.Parser;
import com.java4less.ocr.docparser.TextMarkDef;
import com.java4less.ocr.docparser.data.Document;
import com.java4less.ocr.docparser.data.DocumentSet;
import com.java4less.ocr.docparser.data.Section;
import com.java4less.ocr.docparser.fields.FieldDef;
import com.java4less.ocr.docparser.fields.FieldPositionDef;
import com.java4less.ocr.docparser.references.BeginOfLineRef;
import com.java4less.ocr.docparser.references.BeginOfSectionRef;
import com.java4less.ocr.docparser.references.EndOfLineRef;
import com.java4less.ocr.docparser.sections.SectionDef;
import com.java4less.ocr.docparser.sections.SectionFixedLength;
import com.java4less.ocr.docparser.sections.TextMarkCondition;
import com.java4less.ocr.tess3.OCRFacade;


public class ParserTest {

	/**
	 * this program will conver the file order.PNG to text and then will parse the text to extract the purchase order information
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {
			OCRFacade facade=new OCRFacade();
			String text=facade.recognizeFile("order.png", "eng");
			
			DocumentDef docDef=new DocumentDef();
			docDef.loadFromXml("ordedef.xml");
			
			Parser parser=new Parser(docDef);
			DocumentSet docSet=parser.parse(text);
			
			printAllSectionsAndFields(docSet);
			
			if (docSet.getCount()<1) {
				System.err.println("no form found");
				System.exit(-1);
			}
			
			Document doc=docSet.getDocument(0); // we assume each image has one order only
			
			// print order information to standard output
//			System.out.println("Nome: "+doc.getSectionByName("order header")[0].getField("NameLabel"));
//			System.out.println("Morada: "+doc.getSectionByName("order header")[0].getField("AddressLabel"));
//			System.out.println("Observacoes: "+doc.getSectionByName("order header")[0].getField("CommentsLabel"));
			System.out.println("Purchase order number: "+doc.getSectionByName("order header")[0].getField("number"));
			System.out.println("Delivery date: "+doc.getSectionByName("order header")[0].getField("DeliveryDate"));
			Section[] items=doc.getSectionByName("items detail");
			for (int i=0;i<items.length;i++) {
				System.out.println("article= "+items[i].getField("Article")+" quantity= "+items[i].getField("Quantity"));
			}

			
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
		docDef.setName("PurchaseOrder");
		SectionDef header=new SectionDef("order header");
		header.setStartCondition(new TextMarkCondition(new TextMarkDef("Purchase order")));
		docDef.addSection(header);
		
		// fields of the header
		// define label
		TextMarkDef numberlabel=new TextMarkDef("Number:","NumberLabel");
		header.addTextMark(numberlabel);
		// define field which will be right to the label
		header.addField(new FieldDef("Number",new FieldPositionDef(numberlabel,1,FieldPositionDef.DIR_RIGHT),new FieldPositionDef(numberlabel,0,FieldPositionDef.DIR_DOWN)));

		// define delivery date label
		TextMarkDef ddatelabel=new TextMarkDef("Delivery date:","DelDateLabel");
		header.addTextMark(ddatelabel);
		// define delivery date field which will be to the right of the label
		header.addField(new FieldDef("DeliveryDate",new FieldPositionDef(ddatelabel,1,FieldPositionDef.DIR_RIGHT),new FieldPositionDef(ddatelabel,0,FieldPositionDef.DIR_DOWN)));
					
		// items header section
		SectionDef columnsHeader=new SectionDef("col header");
		columnsHeader.setStartCondition(new TextMarkCondition(new TextMarkDef("Number Article Description")));
		columnsHeader.setLengthType(new SectionFixedLength(1));
		docDef.addSection( columnsHeader);
	
		// items
		SectionDef detail=new SectionDef("items detail");
		detail.setLengthType(new SectionFixedLength(1));
		detail.setRepeatable(true);
		docDef.addSection(detail);	
		
		detail.addField(new FieldDef("Article",new FieldPositionDef(new BeginOfLineRef(),2,FieldPositionDef.DIR_RIGHT),new FieldPositionDef(new BeginOfSectionRef(),0,FieldPositionDef.DIR_DOWN)));
		detail.addField(new FieldDef("Quantity",new FieldPositionDef(new EndOfLineRef(),3,FieldPositionDef.DIR_LEFT),new FieldPositionDef(new BeginOfSectionRef(),0,FieldPositionDef.DIR_DOWN)));
					
		
		// footer
		SectionDef footer=new SectionDef("order footer");
		footer.setStartCondition(new TextMarkCondition(new TextMarkDef("Tax:")));
		docDef.addSection( footer);
		
		// you can save the document definition to an XML file
	    // docDef.saveToXml(new FileOutputStream("orderDefinition.xml"));
		
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
