package br.com.mythe.droid.gelib.activities.dashboard;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.AboutActivity;
import br.com.mythe.droid.gelib.activities.HomeActivity;
import br.com.mythe.droid.gelib.activities.ListaEstadosActivity;
import br.com.mythe.droid.gelib.activities.ListaFavoritosActivity;
import br.com.mythe.droid.gelib.activities.ListaProximosActivity;
import br.com.mythe.droid.gelib.activities.MapaProximosActivity;
import br.com.mythe.droid.gelib.activities.Preferences;
import br.com.mythe.droid.gelib.login.LoginActivity;

public class ListaActivity extends ListActivity {

	
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
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	    TextView tv = (TextView) findViewById (textViewId);
	    if (tv != null) tv.setText (getTitle ());
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
	
	public void abrirDialogLogin(final Context context, final boolean back) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				context);

		alert.setTitle(R.string.title_naologado);
		alert.setMessage(R.string.msgNaoLogado);
		
		alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(context,
						LoginActivity.class);
				intent.putExtra("back", back);
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
	
	protected boolean isLite() {
		return getPackageName().toLowerCase().contains("lite");
	}

}
