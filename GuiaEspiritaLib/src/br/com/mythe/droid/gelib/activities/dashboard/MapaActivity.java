package br.com.mythe.droid.gelib.activities.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import br.com.mythe.droid.common.transacao.Transacao;
import br.com.mythe.droid.common.transacao.TransacaoTask;
import br.com.mythe.droid.common.util.NetUtils;
import br.com.mythe.droid.common.util.Utils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.AboutActivity;
import br.com.mythe.droid.gelib.activities.HomeActivity;
import br.com.mythe.droid.gelib.activities.ListaEstadosActivity;
import br.com.mythe.droid.gelib.activities.ListaFavoritosActivity;
import br.com.mythe.droid.gelib.activities.ListaProximosActivity;
import br.com.mythe.droid.gelib.activities.MapaProximosActivity;
import br.com.mythe.droid.gelib.activities.Preferences;

import com.google.android.maps.MapActivity;

public abstract class MapaActivity extends MapActivity {
	
	TransacaoTask task;

	/**
	 * onCreate - called when the activity is first created.
	 *
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
	}
	
	/**
	 * onDestroy The final call you receive before your activity is destroyed.
	 * This can happen either because the activity is finishing (someone called
	 * finish() on it, or because the system is temporarily destroying this
	 * instance of the activity to save space. You can distinguish between these
	 * two scenarios with the isFinishing() method.
	 * 
	 */

	protected void onDestroy() {
		super.onDestroy();
		if(task != null){
			boolean executando = task.getStatus().equals(AsyncTask.Status.RUNNING);
			if(executando){
				task.cancel(true);
				task.fecharProgress();
			}
		}
	}
	
	/**
	 */
	// Click Methods

	/**
	 * Handle the click on the home button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickHome (View v)
	{
	    goHome (this);
	}

	/**
	 * Handle the click on the search button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickSearch (View v)
	{
	    //startActivity (new Intent(getApplicationContext(), SearchBestActivity.class));
		onSearchRequested();
	}

	/**
	 * Handle the click on the About button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickAbout (View v)
	{
	    startActivity (new Intent(getApplicationContext(), AboutActivity.class));
	}

	/**
	 * Handle the click of a Feature button.
	 * 
	 * @param v View
	 * @return void
	 */

	public void onClickFeature (View v)
	{
	    int id = v.getId ();
	    if (id == R.id.home_btn_lista_item) {
			startActivity (new Intent(getApplicationContext(), ListaEstadosActivity.class));
		} else if (id == R.id.home_btn_favoritos) {
			startActivity (new Intent(getApplicationContext(), ListaFavoritosActivity.class));
		} else if (id == R.id.home_btn_items_proximos) {
			startActivity (new Intent(getApplicationContext(), ListaProximosActivity.class));
		} else if (id == R.id.home_btn_mapa_proximos) {
			startActivity (new Intent(getApplicationContext(), MapaProximosActivity.class));
		} else if (id == R.id.home_btn_preferences) {
			startActivity (new Intent(getApplicationContext(), Preferences.class));
		} else if (id == R.id.home_btn_about) {
			startActivity (new Intent(getApplicationContext(), AboutActivity.class));
		} else {
		}
	}

	/**
	 */
	// More Methods

	/**
	 * Go back to the home activity.
	 * 
	 * @param context Context
	 * @return void
	 */

	public void goHome(Context context) 
	{
	    final Intent intent = new Intent(context, HomeActivity.class);
	    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    context.startActivity (intent);
	}

	/**
	 * Use the activity label to set the text in the activity's title text view.
	 * The argument gives the name of the view.
	 *
	 * <p> This method is needed because we have a custom title bar rather than the default Android title bar.
	 * See the theme definitons in styles.xml.
	 * 
	 * @param textViewId int
	 * @return void
	 */

	public void setTitleFromActivityLabel (int textViewId)
	{
	    //TextView tv = (TextView) findViewById (textViewId);
	    //if (tv != null) tv.setText (getTitle ());
		setTitle(textViewId);
	} // end setTitleText

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg String
	 * @return void
	 */

	public void toast (String msg)
	{
	    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	} // end toast

	/**
	 * Send a message to the debug log and display it using Toast.
	 */
	public void trace (String msg) 
	{
	    Log.d("Demo", msg);
	    toast (msg);
	}
	
	
	/**
	 * Shows an error dialog to the user.
	 * @param ctx
	 * @param title
	 * @param description
	 */
	public void presentError(Context ctx, String title, String description) 
	{
	    AlertDialog.Builder d = new AlertDialog.Builder(ctx);
	    d.setTitle(title);
	    d.setMessage(description);
	    d.setIcon(android.R.drawable.ic_dialog_alert);
	    d.setNeutralButton("OK",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton)
	                {
	                }
	            });
	    d.show();
	}  
	
	
	public void startTransacao(Transacao transacao){
		boolean redeOk = NetUtils.isNetworkConnected(this);
		if(redeOk){
			task = new TransacaoTask(this, transacao, R.string.aguarde , R.string.app_name);
			task.execute();
		} else {
			Utils.alertDialog(this, R.string.erro_conexao_indisponivel, R.string.app_name);
		}
	}
	
	
	protected boolean isLite() {
		return getPackageName().toLowerCase().contains("lite");
	}
	
	
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
