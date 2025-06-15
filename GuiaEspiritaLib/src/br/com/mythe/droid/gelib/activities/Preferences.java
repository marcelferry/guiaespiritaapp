package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.NetUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.login.LoginActivity;
import br.com.mythe.droid.gelib.util.GelibUtils;

public class Preferences extends PreferenceActivity {

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GelibUtils.NETWOTK_ERROR_WAIT:
				Toast.makeText(
						Preferences.this,
						"Erro ao sincronizar conteúdo Web, verifique sua conexão ou tente novamente mais tarde.",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	public static Handler mStaticHandler = null;

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		super.onCreate(savedInstanceState);

		// to create a custom title bar for activity window
		addPreferencesFromResource(R.xml.preferences);

		// use custom layout for title bar. make sure to use it after
		// setContentView()
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title_bar);

		mStaticHandler = mHandler;

		Preference syncReset = (Preference) findPreference("syncInit");
		syncReset.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				if (NetUtils.isNetworkConnected(Preferences.this)) {
					new SyncProgressTask(Preferences.this, mStaticHandler).execute(true);
				} else {
					toast("Não há conexão no momento. Tente mais tarde.");
				}
				return true;
			}
		});

		Preference syncUpdate = (Preference) findPreference("syncUpdate");
		syncUpdate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						if (NetUtils.isNetworkConnected(Preferences.this)) {
							new SyncProgressTask(Preferences.this, mStaticHandler).execute(false);
						} else {
							toast("Não há conexão no momento. Tente mais tarde.");
						}
						return true;
					}
				});

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		getPrefs();
	}
	
	private void getPrefs() {
		final CheckBoxPreference dont_show = (CheckBoxPreference) getPreferenceManager().findPreference("silentMode");
		dont_show.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {            
			public boolean onPreferenceChange(Preference preference, Object newValue) {
			    if (newValue.toString().equals("true"))
			    {  
			        //Toast.makeText(getApplicationContext(), "CB: " + "true", Toast.LENGTH_SHORT).show();
			    } 
			    else 
			    {  			    
			    	//Toast.makeText(getApplicationContext(), "CB: " + "false", Toast.LENGTH_SHORT).show();
			    	startActivity(new Intent(getApplicationContext(),
							LoginActivity.class));	
			    	finish();
			    }
			    return true;
			}
		});

 }


	@Override
	protected void onStop() {
		super.onStop();
	}


	/**
	 */
	// Click Methods

	public void onClickHome(View v) {
		goHome(this);
	}

	public void onClickSearch(View v) {
		//startActivity(new Intent(getApplicationContext(), SearchGuiaActivity.class));
		onSearchRequested();
	}

	/**
	 */
	// More Methods

	/**
	 * Go back to the home activity.
	 * 
	 * @param context
	 *            Context
	 * @return void
	 */

	public void goHome(Context context) {
		final Intent intent = new Intent(context, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public void setTitleFromActivityLabel(int textViewId) {
		setTitle(textViewId);
	}
	
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

	
}
