package br.com.mythe.droid.gelib.login;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.common.util.NetUtils;
import br.com.mythe.droid.common.util.Utils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.HomeActivity;
import br.com.mythe.droid.gelib.constants.GEConst;

public class LoginActivity extends Activity {

	boolean back;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		back = false;
		
		if (savedInstanceState != null) {
			back = savedInstanceState.getBoolean("back");
		} else {
			Intent intent = getIntent();
			back  = intent.getBooleanExtra("back", false);
		}

		
		TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

		CheckBox dontShowLogin = (CheckBox) findViewById(R.id.dont_show);

		Button login = (Button) findViewById(R.id.btnLogin);

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText campoUsuario = (EditText) findViewById(R.id.usuarioLogin);
				EditText campoSenha = (EditText) findViewById(R.id.senhaLogin);

				String usuario = campoUsuario.getText().toString();
				String senha = campoSenha.getText().toString();

				new LoginTask(LoginActivity.this, usuario, senha, back).execute();

			}
		});

		// Listening to register new account link
		registerScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
			}
		});

		dontShowLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				CheckBox dontShow = (CheckBox) v;
				boolean dontShowBool = dontShow.isChecked();
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(getBaseContext());
				SharedPreferences.Editor ed = settings.edit();
				ed.putBoolean("silentMode", dontShowBool);
				ed.putBoolean("logado", false);
				ed.commit();
				if (dontShowBool) {
					Intent i = new Intent(getApplicationContext(),
							HomeActivity.class);
					startActivity(i);
					finish();
				}
			}
		});
	}
}

class LoginTask extends AsyncTask<String, Void, Boolean> {

	
	public LoginTask(Activity activity, String usuario, String senha, boolean back) {
		this.activity = activity;
		dialog = new ProgressDialog(activity);
		this.usuario = usuario;
		this.senha = senha;
		this.back = back;
	}

	/** progress dialog to show user that the backup is processing. */
	private ProgressDialog dialog;
	/** application context. */
	private Activity activity;
	/** usuario */
	private String usuario;
	/** senha */
	private String senha;
	/** back */
	private boolean back;
	
	protected void onPreExecute() {
		this.dialog.setMessage("Validando o login");
		this.dialog.show();
	}

	@Override
	protected void onPostExecute(final Boolean success) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}

		SharedPreferences settings = PreferenceManager
		.getDefaultSharedPreferences(this.activity.getBaseContext());
		
		if (success) {
			SharedPreferences.Editor ed = settings.edit();
			ed.putString("usuario", usuario);
			ed.putString("senha", senha);
			ed.putBoolean("silentMode", true);
			ed.putBoolean("logado", true);
			ed.commit();
			Toast.makeText(this.activity, "Login com sucesso!", Toast.LENGTH_LONG).show();
			if(back){
				this.activity.finish();
			} else {
				Intent i = new Intent(this.activity.getApplicationContext(),
						HomeActivity.class);
				this.activity.startActivity(i);
				this.activity.finish();
			}
		} else {
			SharedPreferences.Editor ed = settings.edit();
			ed.remove("usuario");
			ed.remove("senha");
			ed.putBoolean("silentMode", false);
			ed.putBoolean("logado", false);
			ed.commit();
			Toast.makeText(this.activity, "Login inválido!", Toast.LENGTH_LONG)
					.show();
		}
	}

	protected Boolean doInBackground(final String... args) {
		try {
			Document doc;

			senha = Utils.criptografaSenha(senha);

			Map<String, String> parametros = new HashMap<String, String>();
			parametros.put("USUARIO", usuario);
			parametros.put("SENHA", senha);
			String caminho = NetUtils.generateGetUrl(GEConst.LOGIN_URI,
					parametros);
			doc = NetUtils.getXmlFromService(caminho);

			int loginResult = doc.getElementsByTagName("logon").getLength();

			if (loginResult > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("tag", "error", e);
			return false;
		}
	}
}
