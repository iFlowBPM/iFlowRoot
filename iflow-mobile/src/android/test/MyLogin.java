package android.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyLogin extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		
		
		Button loginButton = (Button) findViewById(R.id.loginButton);
		
		loginButton.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v) {
				
				//----- TODO validação do Login -------------------
				
				startActivity(new Intent("android.test.MYTASKS"));
				finish();
			}
		});
		
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
