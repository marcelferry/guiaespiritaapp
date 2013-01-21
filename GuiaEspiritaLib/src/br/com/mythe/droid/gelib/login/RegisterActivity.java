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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.common.util.NetUtils;
import br.com.mythe.droid.common.util.Utils;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.HomeActivity;
import br.com.mythe.droid.gelib.constants.GEConst;

public class RegisterActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.activity_register);
        
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        
        Button login = (Button) findViewById(R.id.btnRegister);
        
        login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText campoNome = (EditText) findViewById(R.id.reg_fullname);
				EditText campoUsuario = (EditText) findViewById(R.id.reg_email);
				EditText campoSenha = (EditText) findViewById(R.id.reg_password);
				
				String nome = campoNome.getText().toString();
				String usuario = campoUsuario.getText().toString();
				String senha = campoSenha.getText().toString();
				if(!"".equals(nome) && !"".equals(usuario) && !"".equals(senha) ){
					if(Utils.validarEmail(usuario)){
						new RegisterTask(RegisterActivity.this, nome, usuario, senha).execute();
					} else {
						Utils.alertDialog(RegisterActivity.this, "Utilize um e-mail válido", R.string.app_name);
					}
				} else {
					Utils.alertDialog(RegisterActivity.this, "Preencher todos os dados.", R.string.app_name);
				}
				
			}
		});
        
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// Switching to Login Screen/closing register screen
				finish();
			}
		});
    }
}

class RegisterTask extends AsyncTask<String, Void, Boolean> {

	public RegisterTask(Activity activity, String nome, String usuario, String senha) {
		this.activity = activity;
		dialog = new ProgressDialog(activity);
		this.nome = nome;
		this.usuario = usuario;
		this.senha = senha;

	}

	/** progress dialog to show user that the backup is processing. */
	private ProgressDialog dialog;
	/** application context. */
	private Activity activity;
	/** nome */
	private String nome;
	/** usuario */
	private String usuario;
	/** senha */
	private String senha;

	protected void onPreExecute() {
		this.dialog.setMessage("Registrando usuário");
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
			Toast.makeText(this.activity, "Usuário Registrado!", Toast.LENGTH_LONG).show();
			Intent i = new Intent(this.activity.getApplicationContext(),
					HomeActivity.class);
			this.activity.startActivity(i);
			this.activity.finish();
		} else {
			SharedPreferences.Editor ed = settings.edit();
			ed.remove("usuario");
			ed.remove("senha");
			ed.putBoolean("silentMode", false);
			ed.putBoolean("logado", false);
			ed.commit();
			Toast.makeText(this.activity, "Falha ao registrar o usuário!", Toast.LENGTH_LONG)
					.show();
		}
	}

	protected Boolean doInBackground(final String... args) {
		try {
			Document doc;

			senha = Utils.criptografaSenha(senha);

			Map<String, String> parametros = new HashMap<String, String>();
			parametros.put("NOME", nome);
			parametros.put("USUARIO", usuario);
			parametros.put("SENHA", senha);

			String caminho = NetUtils.generateGetUrl(GEConst.REGISTER_URI, parametros);
			doc = NetUtils.getXmlFromService(caminho);			
			
			int loginResult = doc.getElementsByTagName("usuario").getLength();

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