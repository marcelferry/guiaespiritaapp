package br.com.mythe.droid.gelib.activities;

import java.util.HashMap;
import java.util.Map;

import org.jredfoot.sophielib.exception.NegocioException;
import org.jredfoot.sophielib.util.NetUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.database.GuiaEspiritaDB;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.login.LoginActivity;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

public class Preferences extends PreferenceActivity {

	private static final int PROGRESS_DIALOG = 1;

	private static final int NETWOTK_ERROR_WAIT = -1;

	private ProgressDialog mProgressDialog;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NETWOTK_ERROR_WAIT:
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
					new SyncProgressTask().execute(true);
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
							new SyncProgressTask().execute(false);
						} else {
							toast("Não há conexão no momento. Tente mais tarde.");
						}
						return true;
					}
				});

		createProgressDialog();

		boolean sync = false;
		boolean reset = true;
		if (savedInstanceState != null) {
			sync = savedInstanceState.getBoolean("sync");
			reset = savedInstanceState.getBoolean("reset");
		} else {
			Intent intent = getIntent();
			sync = intent.getBooleanExtra("sync", false);
			reset = intent.getBooleanExtra("reset", true);
		}

		if (sync) {
			new SyncProgressTask().execute(reset);
		}

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
		if (mProgressDialog.isShowing())
			dismissDialog(PROGRESS_DIALOG);
		super.onStop();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog d = null;
		if (id == PROGRESS_DIALOG) {
			createProgressDialog();
			d = mProgressDialog;
		}
		return d;
	}

	private void carregarDadosInternet(boolean reset) {
		try {
			carregaCasasEspiritasNovo(reset);
		} catch (NegocioException e) {
			e.printStackTrace();
			Log.e("Guia Espirita", e.getMessage());
			Message m = Message.obtain(mHandler, NETWOTK_ERROR_WAIT);
			mHandler.sendMessage(m);
		}

	}

	
	private void carregaCasasEspiritas(boolean reset) throws NegocioException {
		
		Document doc;
		
		if(reset){
			doc = NetUtils.getXmlFromService(GEConst.ENTIDADES);
		} else {
			String update[] = GuiaEspiritaDB.getLastTimestamp(this);
			Map<String, String> parametros = new HashMap<String, String>();
			parametros.put("last_id", update[0]);
			parametros.put("update", update[1]);
			String caminho = NetUtils.generateGetUrl(GEConst.ENTIDADES, parametros);
			doc = NetUtils.getXmlFromService(caminho);			
		}

		int casasSize = doc.getElementsByTagName("entidade").getLength();

		if (reset) {
			if (casasSize > 0) {
				getContentResolver().delete(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), null, null);
			}
		} 

		for (int i = 0; i < casasSize; i++) {
			Element casa = (Element) doc.getElementsByTagName("entidade").item(
					i);

			ContentValues data = new ContentValues();
			data.put(Casas.COD_CASA, casa.getAttribute("codigo"));
			data.put(Casas.NOME, casa.getAttribute("name"));
			data.put(Casas.ENDERECO, casa.getAttribute("address"));
			data.put(Casas.BAIRRO, casa.getAttribute("bairro"));
			data.put(Casas.CIDADE, casa.getAttribute("cidade"));
			data.put(Casas.ESTADO, casa.getAttribute("estado"));
			data.put(Casas.CEP, casa.getAttribute("cep"));
			data.put(Casas.PAIS, casa.getAttribute("pais"));
			data.put(Casas.FONE, casa.getAttribute("fone"));
			data.put(Casas.EMAIL, casa.getAttribute("email"));
			data.put(Casas.SITE, casa.getAttribute("site"));
			data.put(Casas.LAT, new Double(casa.getAttribute("lat")));
			data.put(Casas.LNG, new Double(casa.getAttribute("lng")));
			data.put(Casas.INFORMACOES, casa.getAttribute("info"));
			data.put(Casas.ATUALIZADO, casa.getAttribute("atualizado"));
			data.put(Casas.TYPE, casa.getAttribute("type"));

			if(reset){
				getContentResolver().insert( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data);
			} else {
			
				if(casa.getAttribute("status").equals("update")){
					getContentResolver().update( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data,"rem_id = ?", new String[]{casa.getAttribute("codigo")});
				}
				
				if(casa.getAttribute("status").equals("new")){
					getContentResolver().insert(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data);					
				}
				
			}


		}

	}

	/** Em desenvolvimento 
	 */
	private void carregaCasasEspiritasNovo(boolean reset) throws NegocioException {
		
		Document doc;
		
		if(reset){
			doc = NetUtils.getXmlFromService(GEConst.LIST_ENTIDADES_URI);
		} else {
			String update[] = GuiaEspiritaDB.getLastTimestamp(this);
			Map<String, String> parametros = new HashMap<String, String>();
			parametros.put("last_id", update[0]);
			parametros.put("update", update[1]);
			String caminho = NetUtils.generateGetUrl(GEConst.LIST_ENTIDADES_URI, parametros);
			doc = NetUtils.getXmlFromService(caminho);			
		}

		int casasSize = doc.getElementsByTagName("entidade").getLength();

		if (reset) {
			if (casasSize > 0) {
				getContentResolver().delete(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), null, null);
			}
		} 

		for (int i = 0; i < casasSize; i++) {
			Element casa = (Element) doc.getElementsByTagName("entidade").item(
					i);

			ContentValues data = new ContentValues();
			data.put(Casas.COD_CASA, casa.getAttribute("codigo"));
			data.put(Casas.NOME, casa.getAttribute("nome"));
			data.put(Casas.ENDERECO, casa.getAttribute("endereco"));
			data.put(Casas.BAIRRO, casa.getAttribute("bairro"));
			data.put(Casas.CIDADE, casa.getAttribute("cidade"));
			data.put(Casas.ESTADO, casa.getAttribute("estado"));
			data.put(Casas.PAIS, casa.getAttribute("pais"));
			data.put(Casas.CEP, casa.getAttribute("cep"));
			data.put(Casas.FONE, casa.getAttribute("fone"));
			data.put(Casas.EMAIL, casa.getAttribute("email"));
			data.put(Casas.SITE, casa.getAttribute("site"));
			data.put(Casas.LAT, new Double(casa.getAttribute("lat")));
			data.put(Casas.LNG, new Double(casa.getAttribute("lng")));
			data.put(Casas.INFORMACOES, casa.getAttribute("info"));
			data.put(Casas.ATUALIZADO, casa.getAttribute("atualizado"));
			data.put(Casas.TYPE, casa.getAttribute("tipo"));

			if(reset){
				getContentResolver().insert( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data);
			} else {
			
				if(casa.getAttribute("status").equals("update")){
					getContentResolver().update( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data,"rem_id = ?", new String[]{casa.getAttribute("codigo")});
				}
				
				if(casa.getAttribute("status").equals("new")){
					getContentResolver().insert( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data);					
				}
				
			}


		}

	}
	/**/

	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Carregando dados...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
	}

	class SyncProgressTask extends AsyncTask<Boolean, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Boolean... params) {
			carregarDadosInternet(params[0]);
			return Boolean.TRUE;
		}

		@Override
		protected void onPreExecute() {
			showDialog(PROGRESS_DIALOG);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			removeDialog(PROGRESS_DIALOG);
			finish();
		}
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
		//startActivity(new Intent(getApplicationContext(), SearchGuiaActivity.class));
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
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}

	/**
	 * Handle the click of a Feature button.
	 * 
	 * @param v
	 *            View
	 * @return void
	 */

	public void onClickFeature(View v) {
		int id = v.getId();
		if (id == R.id.home_btn_lista_item) {
			startActivity(new Intent(getApplicationContext(),
					ListaEstadosActivity.class));
		} else if (id == R.id.home_btn_favoritos) {
			startActivity(new Intent(getApplicationContext(),
					ListaFavoritosActivity.class));
		} else if (id == R.id.home_btn_items_proximos) {
			startActivity(new Intent(getApplicationContext(),
					ListaProximosActivity.class));
		} else if (id == R.id.home_btn_mapa_proximos) {
			startActivity(new Intent(getApplicationContext(),
					MapaProximosActivity.class));
		} else if (id == R.id.home_btn_preferences) {
			startActivity(new Intent(getApplicationContext(), Preferences.class));
		} else if (id == R.id.home_btn_about) {
			startActivity(new Intent(getApplicationContext(),
					AboutActivity.class));
		} else {
		}
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
	
	protected boolean isLite() {
		return getPackageName().toLowerCase().contains("lite");
	}

}
