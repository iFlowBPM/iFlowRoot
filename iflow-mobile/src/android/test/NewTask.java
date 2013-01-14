package android.test;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class NewTask extends ListActivity{
    
    private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<String> myTasks = null;
    private TaskAdapter m_adapter;
    private Runnable viewTasks;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.new_list);
        
        
		ActionBar actionBar = getActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
    	
    	actionBar.setDisplayShowTitleEnabled(false);
        
        myTasks = new ArrayList<String>();
        
        this.m_adapter = new TaskAdapter(this, R.layout.row, myTasks);
        setListAdapter(this.m_adapter);
        
        ListView lv = getListView();
        
        lv.setOnItemClickListener(new OnItemClickListener() {
		
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	        
    	    
        		String selectedTask = ((TextView) view.findViewById(R.id.toptext)).getText().toString();
        		
        		if(selectedTask.compareTo("Hello World") == 0){
        			
        		    Intent i = new Intent("android.test.NEWFORM");
        			
        		    startActivity(i);
        		    
        		}else{
        			
        			Toast.makeText(getApplicationContext(), "Implementar a " + ((TextView) view.findViewById(R.id.toptext)).getText(), Toast.LENGTH_SHORT).show();
        		}
        		
        		new Intent();
    	      
        	}
        
        });
        
        viewTasks = new Runnable(){
            
        	public void run() {
            
        		getTasks();
            
        	}
        };
        
        Thread thread =  new Thread(null, viewTasks, "MagentoBackground");
        thread.start();
        
        m_ProgressDialog = ProgressDialog.show(NewTask.this, "Please wait...", "Retrieving data ...", true);
    }
    
    private Runnable returnRes = new Runnable() {

        public void run() {
        
        	if(myTasks != null && myTasks.size() > 0){
            
        		m_adapter.notifyDataSetChanged();
                
        		for(int i=0;i<myTasks.size();i++)
                
        			m_adapter.add(myTasks.get(i));
            }
            
        	m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
    };
    
    private void getTasks(){
          
    	try{
        	  myTasks = new ArrayList<String>();
              
        	  myTasks.add("Hello World");
        	  myTasks.add("Task 2");
        	  myTasks.add("Task 3");
        	  myTasks.add("Task 4");
        	  
        	  
              Thread.sleep(2000);
              Log.i("ARRAY", ""+ myTasks.size());
            } catch (Exception e) { 
              
            	Log.e("BACKGROUND_PROC", e.getMessage());
          
            }
    
          runOnUiThread(returnRes);
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
			Intent intent = new Intent(this, MyTasks.class);
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
    
    private class TaskAdapter extends ArrayAdapter<String> {

        private ArrayList<String> items;

        public TaskAdapter(Context context, int textViewResourceId, ArrayList<String> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                
        	View v = convertView;
            
        	if (v == null) {
            
        		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
        	
                
        	String s = items.get(position);
            
        	if (s != null) {
            
        		TextView tt = (TextView) v.findViewById(R.id.toptext);
                
        		TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                
        		if (tt != null) {
                
        			tt.setText(s);                            
        		}
                    
        		if(bt != null){
                
        			bt.setText("Status: OK");
                    
        		}   
        	}
            
        	return v;
        }
}
}