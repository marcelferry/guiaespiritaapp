package br.com.mythe.droid.gelib.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.login.LoginActivity;

public class SplashScreenActivity extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		/** set time to splash out */
		final int welcomeScreenDisplay = 3000;
		/** create a thread to show splash up to splash time */
		Thread welcomeThread = new Thread() {

			int wait = 0;

			@Override
			public void run() {
				try {
					super.run();
					/**
					 * use while to get the splash time. Use sleep() to increase
					 * the wait variable for every 100L.
					 */
					while (wait < welcomeScreenDisplay) {
						sleep(100);
						wait += 100;
					}
				} catch (Exception e) {
					System.out.println("EXc=" + e);
				} finally {
					/**
					 * Called after splash times up. Do some action after splash
					 * times up. Here we moved to another main activity class
					 */
					// Restore preferences
				    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				    boolean silent = settings.getBoolean("silentMode", false);
				    if(silent){
				    	startActivity(new Intent(SplashScreenActivity.this,
							HomeActivity.class));
				    }else{
				    	startActivity(new Intent(SplashScreenActivity.this,
							LoginActivity.class));				    	
				    }
					finish();
				}
			}
		};
		welcomeThread.start();

	}
}