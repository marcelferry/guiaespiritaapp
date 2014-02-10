package br.com.mythe.droid.gelib.activities;

import java.util.HashMap;
import java.util.Map;

import org.jredfoot.sophielib.util.NetUtils;
import org.w3c.dom.Document;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;


public class ItemCheckinActivity extends DashboardActivity {
    private TextView mNome;
	private TextView mEndereco;
	private TextView mBairro;
	private TextView mCep;
	private TextView mCidade;
	private TextView mEstado;
	private EditText mEdtStatus;
	private Button mCheckin;
	private Integer id;


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
		setContentView(R.layout.activity_checkin);
        
        mNome = (TextView)findViewById(R.id.txt_entidade);
        mEndereco = (TextView)findViewById(R.id.txt_endereco);
        mBairro  = (TextView)findViewById(R.id.txt_bairro);
        mCep  = (TextView)findViewById(R.id.txt_cep);
        mCidade  = (TextView)findViewById(R.id.txt_cidade);
        mEstado  = (TextView)findViewById(R.id.txt_estado);
        mEdtStatus = (EditText)findViewById(R.id.status);
        //mImagem = (ImageView)findViewById(R.id.img_logo);
        
        mCheckin = (Button) findViewById(R.id.checkin);
        
        id = null;
        
        if(savedInstanceState != null){
        	id = savedInstanceState.getInt("id_centro");
        } else {
        	Intent intent = getIntent();
        	id = intent.getIntExtra("id_centro",0);
        }
        
        obterCasaPorID(id);
        
        mCheckin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkin();
			}
		});
        
    }
        
	public void obterCasaPorID(Integer id){
    	String[] projection = new String[]{
    		Casas._ID,
    		Casas.NOME,		//1
    		Casas.ENDERECO,	//2
    		Casas.BAIRRO,	//3
    		Casas.CIDADE,	//4
    		Casas.ESTADO,	//5
    		Casas.FONE,		//6
    		Casas.CEP,		//7
    		Casas.EMAIL,	//8	
    		Casas.INFORMACOES,	//9
    		Casas.TYPE,		//10
    		Casas.COD_CASA,	//12
    		Casas.SITE	//13
    		} ;		
		
    	String selection = Casas.COD_CASA + " = ? ";
		Cursor cur = managedQuery(CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), projection, selection, new String[]{ String.valueOf( id ) }, null);
    	
    	setCasa(cur);
    	 	
    }
    
    private void setCasa(Cursor c) 
    {
        if (c == null) return;
        try {
            if (c.moveToFirst()) {
            	mNome.setText(      c.getString(1) );
                mEndereco.setText(  c.getString(2) );
                mBairro.setText(  c.getString(3) );
                mCidade.setText(  c.getString(4) );
                mEstado.setText(  c.getString(5) );
                mCep.setText(     c.getString(7)  );
                
//                if(c.getString(10).equals("cfas")){
//                	mImagem.setImageDrawable(getResources().getDrawable( R.drawable.cfas_logo ) );
//                } else {
//                	mImagem.setImageDrawable(getResources().getDrawable( R.drawable.no_image ) );
//                }
                              
                return;
            }
        } finally {
            c.close();
        }
    }
    
    private void checkin(){

		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put("LOCAL", String.valueOf( id ));
		parametros.put("USUARIO", String.valueOf(""));
		parametros.put("TEXT", String.valueOf( mEdtStatus.getText() ));
		parametros.put("IMAGE", String.valueOf(""));

		try {
			String caminho = NetUtils.generateGetUrl(GEConst.ADD_CHECKIN_URI, parametros);

			Log.w("Mythe", caminho);

			Document doc;

			doc = NetUtils.getXmlFromService(caminho);

			int casasSize = doc.getElementsByTagName("checkin").getLength();

			if(casasSize > 0 ){
				Toast.makeText(this, "Checkin Realizado", Toast.LENGTH_LONG).show();
			}
			
			finish();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
