package br.com.mythe.droid.gelib.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;

public class NovoItemInfoDialog extends DashboardActivity {

	// Variaveis
	private String mInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_form_info);

		// Obter Valores enviados pela tela anterior
		Bundle extras = getIntent().getExtras();
		mInfo = extras.getString("info");
		
		EditText info = (EditText) findViewById(R.id.txt_info);
		if (mInfo != null && mInfo.trim() != "") {
			info.setText(mInfo);
		}

		((Button) findViewById(R.id.OkDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						EditText info = (EditText) findViewById(R.id.txt_info);

						mInfo = (info.getText() == null ? "" : info.getText()
								.toString());

						Intent intent = new Intent();
						Bundle extras = new Bundle();
						extras.putString("info", mInfo);

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
