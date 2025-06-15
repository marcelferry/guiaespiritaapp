package br.com.mythe.droid.gelib.activities.dashboard;

import org.jredfoot.sophielib.transacao.Transacao;
import org.jredfoot.sophielib.transacao.TransacaoTask;
import org.jredfoot.sophielib.util.MapUtils;
import org.jredfoot.sophielib.util.NetUtils;
import org.jredfoot.sophielib.util.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.AboutFragment;
import br.com.mythe.droid.gelib.activities.HomeActivity;
import br.com.mythe.droid.gelib.activities.ListaEstadosFragment;
import br.com.mythe.droid.gelib.activities.ListaFavoritosFragment;
import br.com.mythe.droid.gelib.activities.ListaPaisesFragment;
import br.com.mythe.droid.gelib.activities.ListaProximosFragment;
import br.com.mythe.droid.gelib.activities.Preferences;
import br.com.mythe.droid.gelib.activities.RadarProximidadeFragment;
import br.com.mythe.droid.gelib.login.LoginActivity;

public abstract class DashboardActivity extends  ActionBarActivity {
	
	TransacaoTask task;

	/**
	 * onCreate - called when the activity is first created.
	 * 
	 * Called when the activity is first created. This is where you should do
	 * all of your normal static set up: create views, bind data to lists, etc.
	 * This method also provides you with a Bundle containing the activity's
	 * previously frozen state, if there was one.
	 * 
	 * Always followed by onStart().
	 * 
	 */

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	 * onPause Called when the system is about to start resuming a previous
	 * activity. This is typically used to commit unsaved changes to persistent
	 * data, stop animations and other things that may be consuming CPU, etc.
	 * Implementations of this method must be very quick because the next
	 * activity will not be resumed until this method returns. Followed by
	 * either onResume() if the activity returns back to the front, or onStop()
	 * if it becomes invisible to the user.
	 * 
	 */

	protected void onPause() {
		super.onPause();
	}

	/**
	 * onRestart Called after your activity has been stopped, prior to it being
	 * started again. Always followed by onStart().
	 * 
	 */

	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * onResume Called when the activity will start interacting with the user.
	 * At this point your activity is at the top of the activity stack, with
	 * user input going to it. Always followed by onPause().
	 * 
	 */

	protected void onResume() {
		super.onResume();
	}

	/**
	 * onStart Called when the activity is becoming visible to the user.
	 * Followed by onResume() if the activity comes to the foreground, or
	 * onStop() if it becomes hidden.
	 * 
	 */

	protected void onStart() {
		super.onStart();
	}

	/**
	 * onStop Called when the activity is no longer visible to the user because
	 * another activity has been resumed and is covering this one. This may
	 * happen either because a new activity is being started, an existing one is
	 * being brought in front of this one, or this one is being destroyed.
	 * 
	 * Followed by either onRestart() if this activity is coming back to
	 * interact with the user, or onDestroy() if this activity is going away.
	 */

	protected void onStop() {
		super.onStop();
	}

	/**
 */
	// Click Methods

	/**
	 * Handle the click on the home button.
	 * 
	 * @param v
	 *            View
	 * @return void
	 */

	public void onClickHome(View v) {
		goHome(this);
	}

	/**
	 * Handle the click on the search button.
	 * 
	 * @param v
	 *            View
	 * @return void
	 */

	public void onClickSearch(View v) {
		// startActivity (new Intent(getApplicationContext(),
		// SearchBestActivity.class));
		onSearchRequested();
	}

	/**
	 * Handle the click on the About button.
	 * 
	 * @param v
	 *            View
	 * @return void
	 */

	public void onClickAbout(View v) {
		startActivity(new Intent(getApplicationContext(), AboutFragment.class));
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

	/**
	 * Use the activity label to set the text in the activity's title text view.
	 * The argument gives the name of the view.
	 * 
	 * <p>
	 * This method is needed because we have a custom title bar rather than the
	 * default Android title bar. See the theme definitons in styles.xml.
	 * 
	 * @param textViewId
	 *            int
	 * @return void
	 */

	public void setTitleFromActivityLabel(int textViewId) {
		// TextView tv = (TextView) findViewById (textViewId);
		// if (tv != null) tv.setText (getTitle ());
		setTitle(textViewId);
	} // end setTitleText

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

	/**
	 * Send a message to the debug log and display it using Toast.
	 */
	public void trace(String msg) {
		Log.d("Demo", msg);
		toast(msg);
	}

	public static void showAlertDialog(Context context, int messageResId,
			int titleResId) {

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setTitle(context.getResources().getString(titleResId))
				.setMessage(context.getResources().getString(messageResId))
				.setIcon(android.R.drawable.checkbox_on_background)
				.setOnKeyListener(new DialogInterface.OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						dialog.dismiss();
						return false;
					}
				}).create().show();
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
	
	public void abrirDialogLogin(final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				context);

		alert.setTitle(R.string.title_naologado);
		alert.setMessage(R.string.msgNaoLogado);

		alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(context,
						LoginActivity.class);
				startActivity(intent);
			}
		});

		alert.setNegativeButton("Agora não",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
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
	
} // end class

