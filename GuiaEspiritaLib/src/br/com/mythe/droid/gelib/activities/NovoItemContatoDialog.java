package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;

public class NovoItemContatoDialog extends DashboardActivity {

	// Variaveis
	private String mTelefone;
	private String mEmail;
	private String mSite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_form_contato);

		// Obter Valores enviados pela tela anterior
		Bundle extras = getIntent().getExtras();
		mTelefone = extras.getString("telefone");
		mEmail = extras.getString("email");
		mSite = extras.getString("site");
		
		//Definir valores iniciais
		EditText telefone = (EditText) findViewById(R.id.txt_telefone);
		if (mTelefone != null && mTelefone.trim() != "") {
			telefone.setText(mTelefone);
		}
		EditText email = (EditText) findViewById(R.id.txt_email);
		if (mEmail != null && mEmail.trim() != "") {
			email.setText(mEmail);
		}
		EditText site = (EditText) findViewById(R.id.txt_site);
		if (mSite != null && mSite.trim() != "") {
			site.setText(mSite);
		}

		((Button) findViewById(R.id.OkDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						EditText telefone = (EditText) findViewById(R.id.txt_telefone);
						EditText email = (EditText) findViewById(R.id.txt_email);
						EditText site = (EditText) findViewById(R.id.txt_site);

						if(email.getText() != null && !email.getText().toString().trim().equals("") ){
							if(!Utils.validarEmail(email.getText().toString().trim())){
								Toast.makeText(NovoItemContatoDialog.this, "E-mail inválido", Toast.LENGTH_LONG).show();
								return;
							}
						}
						
						mTelefone = (telefone.getText() == null ? "" : telefone
								.getText().toString());
						mEmail = (email.getText() == null ? "" : email
								.getText().toString());
						mSite = (site.getText() == null ? "" : site.getText()
								.toString());

						Intent intent = new Intent();
						Bundle extras = new Bundle();
						extras.putString("telefone", mTelefone);
						extras.putString("email", mEmail);
						extras.putString("site", mSite);

						intent.putExtras(extras);
						setResult(RESULT_OK, intent);
						finish();// Finish current activity
					}

				});

		((Button) findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						setResult(RESULT_CANCELED);
						finish();
					}
				});

	}

}
