package android.test;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class NewForm extends Activity{

	BaseForm myForm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.hello_world);
		
		ActionBar actionBar = getActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
    	
    	
    	
    
		
		try {
			parseXMLForm(this);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		createFields();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
	
		case android.R.id.home:
		
			// app icon in action bar clicked; go home
			Intent intent = new Intent("android.test.MYTASKS");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_messages:
			intent = new Intent(this, MyMessages.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_new:
			intent = new Intent(this, NewTask.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_task:
			intent = new Intent(this, MyTasks.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_save:
			
		default:
			return super.onOptionsItemSelected(item);
		
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
	private void createFields(){
		
		TextView tv;
		Button b;
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.my_form);
		
		String s;
		
		for(int i = 0 ;  i < myForm.getBlockDivision().size() ; i++)
		{	
			
			 s = myForm.getBlockDivision().get(i).getType();
			
			if(s.compareTo("header") == 0){
		
				tv = new TextView(this);
				
				tv.setText(myForm.getBlockDivision().get(i).getText());
				tv.setTextColor(Color.BLACK);
				tv.setBackgroundColor(Color.WHITE);
				tv.setPadding(2, 2, 2, 2);
				
				ll.addView(tv);
			
			}else if(s.compareTo("textmessage") == 0){
				
				tv = new TextView(this);
				
				tv.setBackgroundColor(Color.YELLOW);
				
				tv.setText(myForm.getBlockDivision().get(i).getText());
				tv.setTextColor(Color.BLACK);
				
				tv.setPadding(2, 2, 2, 2);
					
				
				ll.addView(tv);
				
			}else if(s.compareTo("selection") == 0){
				
				LinearLayout l4 = new LinearLayout(this);
				
				l4.setOrientation(LinearLayout.HORIZONTAL);
				l4.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				
				Spinner sp = new Spinner(this);
				
				tv = new TextView(this);
				
				tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				tv.setGravity(Gravity.LEFT);
				
				tv.setText( myForm.getBlockDivision().get(i).getText());
				
				String[] a = new String[myForm.getBlockDivision().get(i).getOptionsList().size()];
				
				for( int j = 0; j < myForm.getBlockDivision().get(i).getOptionsList().size() ; j++){
					
					a[j] =  myForm.getBlockDivision().get(i).getOptionsList().get(j).getText();
				}
				
				ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, a);
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		        sp.setAdapter(adapter);
		        
		        sp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		        sp.setGravity(Gravity.RIGHT);
		        
		        
		        l4.addView(tv);
		        l4.addView(sp);
				
		        ll.addView(l4);
				
				
			}else if(s.compareTo("textbox") == 0){
			
				LinearLayout l2 = new LinearLayout(this);
				
				l2.setOrientation(LinearLayout.HORIZONTAL);
				l2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				
				tv = new TextView(this);
				
				tv.setText(myForm.getBlockDivision().get(i).getText());
				tv.setTextColor(Color.BLACK);
				tv.setBackgroundColor(Color.WHITE);
				tv.setPadding(2, 2, 2, 2);
				
				tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				
				tv.setGravity(Gravity.LEFT);
				
				l2.addView(tv);
				
				EditText et = new EditText(this);
				
				et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			
				et.setId(Integer.valueOf(6));
		        et.setHint("Text Field");
				
				et.setPadding(2, 2, 2, 2);
				
				et.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				et.setGravity(Gravity.RIGHT);
				
				l2.addView(et);
				
				ll.addView(l2);
				
			}	
		}
		
		ArrayList<FormButton> fb = myForm.getFormButtons();
		
		if(fb.size() > 0){
		
			LinearLayout l3 = new LinearLayout(this);
			
			l3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			l3.setOrientation(LinearLayout.HORIZONTAL);
			l3.setGravity(Gravity.CENTER);
			
			
			for (int i = 0; i < fb.size(); i++){
				
				b = new Button(this);
				
				b.setText(fb.get(i).getTooltip());
				b.setId(500+i);
				
				b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				l3.addView(b);
			}
			
			ll.addView(l3);
			
		}
		
	}
	
	
	private void parseXMLForm(NewForm myNewForm) throws XmlPullParserException, IOException{
    	
		XmlResourceParser myXML = getBaseContext().getResources().getXml(R.xml.form1);
		
		int eventType = myXML.getEventType();
		
		while(eventType != XmlPullParser.TEXT)
		{
			eventType = myXML.next();				
		}
		
		String name = myXML.getText();
		eventType = myXML.next();
		
		while(eventType != XmlPullParser.TEXT)
		{
			eventType = myXML.next();
		}
		
		String action = myXML.getText();
		
		this.myForm = new BaseForm(name, action);
			
		
		ArrayList<FormField> blockdivision = new ArrayList<FormField>();
		ArrayList<FormButton> buttonsList =  new ArrayList<FormButton>();
		ArrayList<Hidden> hiddenList = new ArrayList<Hidden>();
		
		blockdivision = parseFields(myXML, eventType);
		
		buttonsList = parseButtons(myXML, eventType);
		
		hiddenList = parseHidden(myXML);
		
		this.myForm.setBlockDivision(blockdivision);
		this.myForm.setFormButtons(buttonsList);
		this.myForm.setHidden(hiddenList);
		
		//Log.i("Teste", "Option text: " + myForm.getBlockDivision().get(2).getOptionsList().get(0).getText());
		
		
	}


	private ArrayList<FormField> parseFields(XmlResourceParser myXML, int eventType) throws XmlPullParserException, IOException {

		ArrayList<FormField> formFields = new ArrayList<FormField>();
		
		FormField ff = new FormField();
		
		while(!(eventType == XmlPullParser.START_TAG && myXML.getName().compareTo("field") == 0)){
			
			eventType = myXML.next();
		}
		
		//Log.i("First Field", myXML.getName());
		
		while(eventType == XmlPullParser.START_TAG && myXML.getName().compareTo("field") == 0){
			
			ArrayList<ListOption> auxList = new ArrayList<ListOption>();
			
			if(eventType == XmlPullParser.START_TAG &&  myXML.getName().compareTo("field") == 0){
				
				eventType = myXML.next();
				eventType = myXML.next();
				
				//Log.i("Field values", "Type: " + myXML.getText());
				ff.setType(myXML.getText());
				
				eventType = myXML.next();
				eventType = myXML.next();
			}
			
			
			while(!(eventType == XmlPullParser.END_TAG && myXML.getName().compareTo("field") == 0)){
				
				if(eventType == XmlPullParser.START_TAG){
					
					if(myXML.getName().compareTo("text") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Text: " + myXML.getText());
						ff.setText(myXML.getText());
						
						eventType = myXML.next();
						
					}else if(myXML.getName().compareTo("obligatory") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Obligatory: " + myXML.getText());
						
						if(myXML.getText().compareTo("true") == 0)
								ff.setObligatory(true);
						else
							ff.setObligatory(true);
						
						eventType = myXML.next();
						
					}else if(myXML.getName().compareTo("cssclass") == 0){
						
						eventType = myXML.next();
						
						if(eventType == XmlPullParser.TEXT){
							
							//Log.i("Field values", "cssclass: " + myXML.getText());
							ff.setCssClass(myXML.getText());
							eventType = myXML.next();
						
						}		
						
					}else if(myXML.getName().compareTo("align") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Align: " + myXML.getText());
						ff.setAlignment(myXML.getText());
						
						eventType = myXML.next();
						
						
					}else if(myXML.getName().compareTo("even_field") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Evenfield: " + myXML.getText());
						if(myXML.getText().compareTo("true") == 0)
							ff.setEvenField(true);
						else
							ff.setEvenField(false);
						
						eventType = myXML.next();
						
						
					}else if(myXML.getName().compareTo("variable") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Variable: " + myXML.getText());
						ff.setVariable(myXML.getText());
						
						eventType = myXML.next();
						
						
					}else if(myXML.getName().compareTo("onchange_submit") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Onchange_Submit: " + myXML.getText());
						if(eventType == XmlPullParser.TEXT){
							
							ff.setOnChangeSubmit(myXML.getText());
							eventType = myXML.next();
						
						}
						
					}else if(myXML.getName().compareTo("option") == 0){
						
						ListOption lo = new ListOption();
						
						eventType = myXML.next();
						eventType = myXML.next();
						
						//Log.i("ListOption values", "text: " + myXML.getText());
						lo.setText(myXML.getText());
						
						eventType = myXML.next();
						eventType = myXML.next();
						eventType = myXML.next();
						
						//Log.i("ListOption values", "value: " + myXML.getText());
						lo.setValue(myXML.getText());
						
						eventType = myXML.next();
						eventType = myXML.next();
						eventType = myXML.next();
						
						//Log.i("ListOption values", "selected: " + myXML.getText());
						if(myXML.getText().compareTo("true") == 0)
							lo.setSelected(true);
						else
							lo.setSelected(false);
						
						eventType = myXML.next();
						eventType = myXML.next();
						
						auxList.add(lo);
						
					}else if(myXML.getName().compareTo("value") == 0){
						
						eventType = myXML.next();
						
						if(eventType == XmlPullParser.TEXT){
						
							//Log.i("Field values", "Value: " + myXML.getText());
							ff.setValue(myXML.getText());
							eventType = myXML.next();
						
						}
						
					}else if(myXML.getName().compareTo("size") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Size: " + myXML.getText());
						ff.setSize(Integer.parseInt(myXML.getText()));
						
						eventType = myXML.next();
						
					}else if(myXML.getName().compareTo("maxlength") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "MaxLength: " + myXML.getText());
						ff.setMaxLength(Integer.parseInt(myXML.getText()));
						
						eventType = myXML.next();
						
					}else if(myXML.getName().compareTo("suffix") == 0){
						
						eventType = myXML.next();
						
						if(eventType == XmlPullParser.TEXT){
							
							//Log.i("Field values", "Suffix: " + myXML.getText());
							ff.setSuffix(myXML.getText());
							eventType = myXML.next();
						
						}
						
					}else if(myXML.getName().compareTo("disabled") == 0){
						
						eventType = myXML.next();
						
						//Log.i("Field values", "Disabled: " + myXML.getText());
						if(myXML.getText().compareTo("true") == 0)
							ff.setDisabled(true);
						else
							ff.setDisabled(false);
						
						eventType = myXML.next();
						
					}else if(myXML.getName().compareTo("onblur") == 0){
						
						eventType = myXML.next();
						
						if(eventType == XmlPullParser.TEXT){
							
							//Log.i("Field values", "OnBlur: " + myXML.getText());
							ff.setOnBlur(myXML.getText());
							eventType = myXML.next();
						
						}
						
					}else if(myXML.getName().compareTo("onfocus") == 0){
						
						eventType = myXML.next();
						
						if(eventType == XmlPullParser.TEXT){
							
							//Log.i("Field values", "OnFocus: " + myXML.getText());
							ff.setOnFocus(myXML.getText());
							eventType = myXML.next();
						
						}	
					}else
						eventType = myXML.next();
			
				}else
					eventType = myXML.next();
			}
			
			ff.setOptionsList(auxList);
			formFields.add(ff);
			ff = new FormField();
			auxList = new ArrayList<ListOption>();
			
			eventType = myXML.next();
				
		}
		
		return formFields;
	}
	
	private ArrayList<FormButton> parseButtons(XmlResourceParser myXML, int eventType) throws XmlPullParserException, IOException {
		
		FormButton fb = new FormButton();
		
		ArrayList<FormButton> buttonsList = new ArrayList<FormButton>();
		
		while(!(eventType == XmlPullParser.START_TAG && myXML.getName().compareTo("button") == 0)){
			
			eventType = myXML.next();
		}
			
		while(!(eventType == XmlPullParser.START_TAG && myXML.getName().compareTo("hidden") == 0)){
			
			eventType = myXML.next();
			eventType = myXML.next();
			
			//Log.i("Button values", "id: " + myXML.getText());
			fb.setId(myXML.getText());
			
			eventType = myXML.next();
			eventType = myXML.next();
			eventType = myXML.next();
			
			//Log.i("Button values", "name: " + myXML.getText());
			fb.setName(myXML.getText());
			
			eventType = myXML.next();
			eventType = myXML.next();
			eventType = myXML.next();
			
			//Log.i("Button values", "text: " + myXML.getText());
			fb.setText(myXML.getText());
			
			eventType = myXML.next();
			eventType = myXML.next();
			eventType = myXML.next();
			
			//Log.i("Button values", "operation: " + myXML.getText());
			fb.setOperation(myXML.getText());
			
			eventType = myXML.next();
			eventType = myXML.next();
			eventType = myXML.next();
			
			//Log.i("Button values", "tooltip: " + myXML.getText());
			fb.setTooltip(myXML.getText());
			
			eventType = myXML.next();
			eventType = myXML.next();
			
			
			if(eventType == XmlPullParser.END_TAG && myXML.getName().compareTo("button") == 0)
			{
				buttonsList.add(fb);
				
				fb = new FormButton();
			}
			
			
			eventType = myXML.next();
		}
		
		return buttonsList;
	}
	
	private ArrayList<Hidden> parseHidden(XmlResourceParser myXML) throws XmlPullParserException, IOException {
		
		Hidden h = new Hidden();
		
		ArrayList<Hidden> hiddenList = new ArrayList<Hidden>();
		int eventType = myXML.getEventType();
		
		
		while(eventType != XmlPullParser.END_DOCUMENT){
				
			if(eventType == XmlPullParser.START_TAG ){
				
				if( myXML.getName().compareTo("hidden") == 0){
				
					eventType = myXML.next();
					eventType = myXML.next();
					
					//Log.i("Hidden values", "name: " + myXML.getText());
					h.setName(myXML.getText());
						
					eventType = myXML.next();
					eventType = myXML.next();
					eventType = myXML.next();
					
					if(eventType != XmlPullParser.END_TAG)
					{
						//Log.i("Hidden values", "value: " + myXML.getText());
						h.setValue(myXML.getText());
				
					}
				}
			}
				
			
			if( eventType == XmlPullParser.END_TAG)
			{
				if(myXML.getName().compareTo("hidden") == 0)
				{
					hiddenList.add(h);
					h = new Hidden();
				}
			}
			
			eventType = myXML.next();
		}
		
		
		return hiddenList;
	}

	

}
