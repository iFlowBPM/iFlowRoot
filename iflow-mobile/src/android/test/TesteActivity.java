package android.test;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;




public class TesteActivity extends Activity {
  /** Called when the activity is first created. */

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_screen);
        
       
        
        Thread logoTimer = new Thread(){
        	
        	public void run(){
        		
        		try{
        			int logotimer = 0;
        			
        			while(logotimer < 1000){
        				sleep(100);
        				logotimer += 100;
        			}
        			
        			startActivity(new Intent("android.test.MYLOGIN"));
        			
        		} catch (InterruptedException e) {
					
					e.printStackTrace();
				}

        		finally{
        			finish();
        		}
        	}
        };
        
        logoTimer.start();
        
        
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

   
}