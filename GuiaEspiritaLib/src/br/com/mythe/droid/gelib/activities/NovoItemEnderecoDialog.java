package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;

public class NovoItemEnderecoDialog extends DashboardActivity {

	// Variaveis
	private String mEndereco;
	private String mBairro;
	private String mCidade;
	private String mCep;
	private String mEstado;
	private String mPais;
	private boolean brasil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_form_endereco);
		
		//Obter Valores enviados pela tela anterior
		Bundle extras = getIntent().getExtras();
		mEndereco = extras.getString("endereco");
        mBairro = extras.getString("bairro");
        mCidade = extras.getString("cidade");
        mCep = extras.getString("cep");
        mEstado = extras.getString("estado");
        mPais = extras.getString("pais");
        
		//Definir valores iniciais
		EditText endereco = (EditText) findViewById(R.id.txt_endereco);
		if (mEndereco != null && mEndereco.trim() != "") {
			endereco.setText(mEndereco);
		}

		EditText bairro = (EditText) findViewById(R.id.txt_bairro);
		if (mBairro != null && mBairro.trim() != "") {
			bairro.setText(mBairro);
		}

		EditText cidade = (EditText) findViewById(R.id.txt_cidade);
		if (mCidade != null && mCidade.trim() != "") {
			cidade.setText(mCidade);
		}

		EditText cep = (EditText) findViewById(R.id.txt_cep);
		if (mCep != null && mCep.trim() != "") {
			cep.setText(mCep);
		}

		Spinner spPais = (Spinner) findViewById(R.id.spPais);
		ArrayAdapter<CharSequence> adapterPais = ArrayAdapter.createFromResource(
				this, R.array.paises, android.R.layout.simple_spinner_item);
		adapterPais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spPais.setAdapter(adapterPais);

		
		if (mPais != null && mPais.trim() != "") {
			//String selectedValue = GelibUtils.getPais(mPais);
			int selectedIndex = adapterPais.getPosition(mPais);
			spPais.setSelection(selectedIndex);
			if(mPais.equals("Brasil")){
				brasil = true;
			}
		}	
		
		Spinner spEstado = (Spinner) findViewById(R.id.spEstado);
		ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
				this, R.array.estados, android.R.layout.simple_spinner_item);
		adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spEstado.setAdapter(adapterEstado);

		if(brasil){			
			if (mEstado != null && mEstado.trim() != "") {
				String selectedValue = Utils.getEstado(mEstado);
				int selectedIndex = adapterEstado.getPosition(selectedValue);
				spEstado.setSelection(selectedIndex);
			}
		} else {
			EditText province = (EditText) findViewById(R.id.txt_province);
			if (mEstado != null && mEstado.trim() != "") {
				province.setText(mEstado);
			}
		}
				
		
		spPais.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	if(!parentView.getItemAtPosition(position).toString().equals("Brasil") ){
		    		TableRow linhaEstado = (TableRow) findViewById(R.id.linha_estado);
		    		linhaEstado.setVisibility(View.GONE);
		    		TableRow linhaProvincia = (TableRow) findViewById(R.id.linha_provincia);
		    		linhaProvincia.setVisibility(View.VISIBLE);
		    		brasil = false;
		    	} else {
		    		TableRow linhaEstado = (TableRow) findViewById(R.id.linha_estado);
		    		linhaEstado.setVisibility(View.VISIBLE);
		    		TableRow linhaProvincia = (TableRow) findViewById(R.id.linha_provincia);
		    		linhaProvincia.setVisibility(View.GONE);
		    		brasil = true;
		    	}
		    	
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});

		
		((Button) findViewById(R.id.OkDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						EditText endereco = (EditText) findViewById(R.id.txt_endereco);
						EditText bairro = (EditText) findViewById(R.id.txt_bairro);
						EditText cidade = (EditText) findViewById(R.id.txt_cidade);
						EditText cep = (EditText) findViewById(R.id.txt_cep);
						Spinner estado = (Spinner) findViewById(R.id.spEstado);
						EditText province = (EditText) findViewById(R.id.txt_province);
						Spinner pais = (Spinner) findViewById(R.id.spPais);

						String locationName = (endereco.getText() == null ? ""
								: endereco.getText().toString());
						locationName += " - "
								+ (cidade.getText() == null ? "" : cidade
										.getText().toString());
						locationName += " - "
								+ (estado.getSelectedItem().toString());

						mEndereco = (endereco.getText() == null ? "" : endereco
								.getText().toString());
						mBairro = (bairro.getText() == null ? "" : bairro
								.getText().toString());
						mCidade = (cidade.getText() == null ? "" : cidade
								.getText().toString());
						mCep = (cep.getText() == null ? "" : cep.getText()
								.toString());

						if(brasil){
							mEstado = Utils.getEstadoSigla(estado.getSelectedItem()
								.toString());
						} else {
							mEstado = (province.getText() == null ? "" : province.getText()
									.toString());
						}
						
						mPais = pais.getSelectedItem().toString();
						
						if("-".equals(mCep.trim())){
							mCep = "";
						}

						//mEdtEndereco.setText(locationName);
						
			            Intent intent = new Intent();
			            Bundle extras = new Bundle();
			            extras.putString("endereco", mEndereco);
			            extras.putString("bairro", mBairro);
			            extras.putString("cidade", mCidade);
			            extras.putString("cep", mCep);
			            extras.putString("estado", mEstado);
			            extras.putString("pais", mPais);
			            extras.putString("location", locationName);
			            
			            intent.putExtras(extras);
			            setResult(RESULT_OK, intent);        
			            finish();//Finish  current activity

						//posicionaoMapa();
						finish();

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
