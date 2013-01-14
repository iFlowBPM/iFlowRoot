package android.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyTasks extends Activity{
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.my_tasks);   	
    	
    	TextView tv = (TextView) findViewById(R.id.tasks_message);
    	
    	tv.setTextColor(Color.WHITE);
    	tv.setText("Estas são as tarefas que lhe estão atribuídas:");
    
    	
    	ImageView i = (ImageView) findViewById(R.id.table_img2);
    	
    	i.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Fluxo: xpto\nPID: whatever\nData: 07/05/2012", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
		
		switch (item.getItemId()) {
	
		case R.id.menu_messages:
			intent = new Intent(this, MyMessages.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_new:
			intent = new Intent(this, NewTask.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_task:
			
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
	
}