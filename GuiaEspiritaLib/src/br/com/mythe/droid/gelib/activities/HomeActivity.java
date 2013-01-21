package br.com.mythe.droid.gelib.activities;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.mythe.droid.common.util.AdMobsUtil;
import br.com.mythe.droid.common.util.NetUtils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.database.GuiaEspiritaDB;

/**
 * This is a simple activity that demonstrates the dashboard user interface pattern.
 *
 */

public class HomeActivity extends DashboardActivity 
{

	
	private static final int ABOUT_DIALOG = 0;
	private static final int ALERT_DIALOG = 2;

	

	private Dialog mAboutDialog;
	private AlertDialog mAlertDialog;

/**
 * onCreate - called when the activity is first created.
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 */

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
   
	setContentView(R.layout.activity_home);

	
	int qtde = GuiaEspiritaDB.getQtdeCasas(this);
	if(qtde == 0){
		showDialog(ALERT_DIALOG);
	} else {
		if(NetUtils.isNetworkConnected(this)){
			try{
				Document doc;
				
				String update[] = GuiaEspiritaDB.getLastTimestamp(this);
				Map<String, String> parametros = new HashMap<String, String>();
				parametros.put("last_id", update[0]);
				parametros.put("update", update[1]);
				//String caminho = NetUtils.generateGetUrl(GEConst.ENTIDADES, parametros);
				String caminho = NetUtils.generateGetUrl(GEConst.LIST_ENTIDADES_URI, parametros);
				doc = NetUtils.getXmlFromService(caminho);			
		
				int casasSize = doc.getElementsByTagName("entidade").getLength();
				if(casasSize > 0){
					abrirDialogSincronizar(casasSize);
				}
			} catch (Exception e) {
				Log.e("GuiaEspirita", e.getLocalizedMessage());
			}
		}
	}

	if(isLite()){
		AdMobsUtil.addAdView(this);
	}
	
}
    
/**
 * onDestroy
 * The final call you receive before your activity is destroyed. 
 * This can happen either because the activity is finishing (someone called finish() on it, 
 * or because the system is temporarily destroying this instance of the activity to save space. 
 * You can distinguish between these two scenarios with the isFinishing() method.
 *
 */

protected void onDestroy ()
{
   super.onDestroy ();
}

/**
 * onPause
 * Called when the system is about to start resuming a previous activity. 
 * This is typically used to commit unsaved changes to persistent data, stop animations 
 * and other things that may be consuming CPU, etc. 
 * Implementations of this method must be very quick because the next activity will not be resumed 
 * until this method returns.
 * Followed by either onResume() if the activity returns back to the front, 
 * or onStop() if it becomes invisible to the user.
 *
 */

protected void onPause ()
{
   super.onPause ();
}

/**
 * onRestart
 * Called after your activity has been stopped, prior to it being started again.
 * Always followed by onStart().
 *
 */

protected void onRestart ()
{
   super.onRestart ();
}

/**
 * onResume
 * Called when the activity will start interacting with the user. 
 * At this point your activity is at the top of the activity stack, with user input going to it.
 * Always followed by onPause().
 *
 */

protected void onResume ()
{
   super.onResume ();
}

/**
 * onStart
 * Called when the activity is becoming visible to the user.
 * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
 *
 */

protected void onStart ()
{
   super.onStart ();
}

/**
 * onStop
 * Called when the activity is no longer visible to the user
 * because another activity has been resumed and is covering this one. 
 * This may happen either because a new activity is being started, an existing one 
 * is being brought in front of this one, or this one is being destroyed.
 *
 * Followed by either onRestart() if this activity is coming back to interact with the user, 
 * or onDestroy() if this activity is going away.
 */

@Override
protected void onStop() {
	if (mAboutDialog != null && mAboutDialog.isShowing())
		dismissDialog(ABOUT_DIALOG);
	if (mAlertDialog != null && mAlertDialog.isShowing())
		dismissDialog(ALERT_DIALOG);

	super.onStop();
}


/**
 */
// Click Methods


/**
 */
// More Methods


@Override
protected Dialog onCreateDialog(int id) {
	Dialog d = null;
	if (id == ABOUT_DIALOG) {
		createAboutDialog();
		d = mAboutDialog;
	}
	if (id == ALERT_DIALOG) {
		createAlertDialog();
		d = mAlertDialog;
	}
	return d;
}

private void createAboutDialog() {
	AlertDialog.Builder builder;
	LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.about_dialog,
			(ViewGroup) findViewById(R.id.layout_about_root));

	((TextView) layout.findViewById(R.id.about_player_name_view))
			.setText(R.string.about_player_name);
	((TextView) layout.findViewById(R.id.about_bugs_link_view))
			.setText(R.string.about_bugs_link);
	((TextView) layout.findViewById(R.id.about_code_link_view))
			.setText(R.string.about_code_link);
	TextView text = (TextView) layout.findViewById(R.id.about_text_view);

	String version = "unknown";
	try {
		version = getPackageManager().getPackageInfo(
				"br.com.mythe.droid.ge", 0).versionName;
	} catch (NameNotFoundException e) {
		e.printStackTrace();
	}
	String t = getString(R.string.about_text);
	t = String.format(t, version);
	text.setText(t);

	builder = new AlertDialog.Builder(this);
	builder.setView(layout);
	mAboutDialog = builder.create();
	mAboutDialog.setCancelable(true);
	mAboutDialog.setCanceledOnTouchOutside(true);
}

private void createAlertDialog() {

	mAlertDialog = new AlertDialog.Builder(
        this).create();

	// Setting Dialog Title
	mAlertDialog.setTitle("Bem Vindo ao Guia Espírita");
	
	// Setting Dialog Message
	mAlertDialog.setMessage("Antes de começarmos, é preciso estar conectado a internet e fazer uma atualização da base de dados.");
	
	// Setting Icon to Dialog
	mAlertDialog.setIcon(android.R.drawable.checkbox_on_background);
	
	// Setting OK Button
	mAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int which) {
	    	Intent intent = new Intent(HomeActivity.this, Preferences.class);
	    	intent.putExtra("sync", true);
	        startActivity(intent);
	    }
	});
}


public void abrirDialogSincronizar(int casasSize){
	AlertDialog.Builder alert = new AlertDialog.Builder(this);

	alert.setTitle("Sincronizar Base");
	alert.setMessage("Existe(m) " + casasSize + " entidade(s) nova(s) ou atualizada(s). Atualizar a base de dados?");

	alert.setPositiveButton("Sincronizar", new DialogInterface.OnClickListener() {
	public void onClick(DialogInterface dialog, int whichButton) {
		Intent intent = new Intent(HomeActivity.this, Preferences.class);
    	intent.putExtra("sync", true);
        startActivity(intent);
	  }
	});

	alert.setNegativeButton("Agora não", new DialogInterface.OnClickListener() {
	  public void onClick(DialogInterface dialog, int whichButton) {
	    // Canceled.
	  }
	});

	alert.show();
	// see http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
}


} // end class
