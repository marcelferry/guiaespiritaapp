package br.com.mythe.droid.gelib.activities;

import org.jredfoot.sophielib.util.AdMobsUtil;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.DashboardActivity;
import br.com.mythe.droid.gelib.database.GuiaEspiritaDB;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.database.objects.Favoritos;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;
import br.com.mythe.droid.gelib.vo.FavoritoVO;


public class ItemDetalheActivity extends DashboardActivity {
    private TextView mNome;
	private TextView mEndereco;
	private TextView mBairro;
	private TextView mCep;
	private TextView mCidade;
	private TextView mEstado;
	private LinearLayout mBlocoFone;
	private TextView mFone;
	private LinearLayout mBlocoInfo;
	private TextView mInformacoes;
	private LinearLayout mBlocoEmail;
	private TextView mEmail;
	private LinearLayout mBlocoSite;
	private TextView mSite;
	private ImageView mImagem;
	private Button mCheckin;
	private ToggleButton mDicas;
	private TextView mDicasTitulo;
	private TableLayout mDicasTabela;
	
	private ToggleButton mEventos;
	private TextView mEventosTitulo;
	private TableLayout mEventosTabela;
	
	private ToggleButton mContatos;
	private TextView mContatosTitulo;
	private LinearLayout mContatosTabela;
	
	private SharedPreferences mSettings;
	
	private boolean favorito;
	private Integer id;


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
        mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
		setContentView(R.layout.activity_item_detalhe);
        
        mNome = (TextView)findViewById(R.id.txt_entidade);
        mEndereco = (TextView)findViewById(R.id.txt_endereco);
        mBairro  = (TextView)findViewById(R.id.txt_bairro);
        mCep  = (TextView)findViewById(R.id.txt_cep);
        mCidade  = (TextView)findViewById(R.id.txt_cidade);
        mEstado  = (TextView)findViewById(R.id.txt_estado);

        mBlocoFone = (LinearLayout)findViewById(R.id.blocoFone);
        mFone  = (TextView)findViewById(R.id.txt_telefone);

        mBlocoInfo = (LinearLayout)findViewById(R.id.blocoInfo);
        mInformacoes  = (TextView)findViewById(R.id.txt_info);
        
        mBlocoEmail = (LinearLayout)findViewById(R.id.blocoEmail);
        mEmail  = (TextView)findViewById(R.id.txt_email);
        
        mBlocoSite = (LinearLayout)findViewById(R.id.blocoSite);
        mSite  = (TextView)findViewById(R.id.txt_site);
        
        //mImagem = (ImageView)findViewById(R.id.img_logo);
        
        mCheckin = (Button) findViewById(R.id.checkin);
        
        mCheckin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {				
				boolean logado = mSettings.getBoolean("logado", false);
				if (logado) {
					checkin();
				} else {
					abrirDialogLogin(ItemDetalheActivity.this);
				}
			}
		});

        mDicas = (ToggleButton) findViewById(R.id.dicas);   
		mDicasTitulo  = (TextView)findViewById(R.id.title_dicas);
		mDicasTabela  = (TableLayout)findViewById(R.id.table_dicas);
        
        boolean dicasAtivo = mSettings.getBoolean("dicasAtivas", true);
        
        if(dicasAtivo){
        	mDicas.toggle();
        	mDicasTitulo.setVisibility(View.VISIBLE);
        	mDicasTabela.setVisibility(View.VISIBLE);
        }
        
        mEventos = (ToggleButton) findViewById(R.id.eventos);   
		mEventosTitulo  = (TextView)findViewById(R.id.title_eventos);
		mEventosTabela  = (TableLayout)findViewById(R.id.table_eventos);
        
        boolean eventosAtivo = mSettings.getBoolean("eventosAtivos", true);
        
        if(eventosAtivo){
        	mEventos.toggle();
        	mEventosTitulo.setVisibility(View.VISIBLE);
        	mEventosTabela.setVisibility(View.VISIBLE);
        }
        
        
        mContatos = (ToggleButton) findViewById(R.id.contatos);   
		mContatosTitulo  = (TextView)findViewById(R.id.title_contatos);
		mContatosTabela  = (LinearLayout)findViewById(R.id.table_contatos);
        
        boolean contatosAtivo = mSettings.getBoolean("contatosAtivos", true);
        
        if(contatosAtivo){
        	mContatos.toggle();
        	mContatosTitulo.setVisibility(View.VISIBLE);
        	mContatosTabela.setVisibility(View.VISIBLE);
        }
        
        
        id = null;
        
        if(savedInstanceState != null){
        	id = savedInstanceState.getInt("id_centro");
        } else {
        	Intent intent = getIntent();
        	id = intent.getIntExtra("id_centro",0);
        }
        
        obterCasaPorID(id);
        
        if(isLite()){
    		AdMobsUtil.addAdView(this);
    	}
        
    }
    
    public void onDicasToogleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			//show dicas			
			mDicasTitulo.setVisibility(View.VISIBLE);
			mDicasTabela.setVisibility(View.VISIBLE);
			SharedPreferences.Editor ed = mSettings.edit();
			ed.putBoolean("dicasAtivas", true );
			ed.commit();
		} else {
			//hide dicas
			mDicasTitulo.setVisibility(View.GONE);
			mDicasTabela.setVisibility(View.GONE);
			SharedPreferences.Editor ed = mSettings.edit();
			ed.putBoolean("dicasAtivas", false );
			ed.commit();
		}
	}
    
    public void onEventosToogleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			//show eventos			
			mEventosTitulo.setVisibility(View.VISIBLE);
			mEventosTabela.setVisibility(View.VISIBLE);
			SharedPreferences.Editor ed = mSettings.edit();
			ed.putBoolean("eventosAtivos", true );
			ed.commit();
		} else {
			//hide eventos
			mEventosTitulo.setVisibility(View.GONE);
			mEventosTabela.setVisibility(View.GONE);
			SharedPreferences.Editor ed = mSettings.edit();
			ed.putBoolean("eventosAtivos", false );
			ed.commit();
		}
	}
    
    public void onContatosToogleClicked(View view) {
		boolean on = ((ToggleButton) view).isChecked();
		if (on) {
			//show contatos			
			mContatosTitulo.setVisibility(View.VISIBLE);
			mContatosTabela.setVisibility(View.VISIBLE);
			SharedPreferences.Editor ed = mSettings.edit();
			ed.putBoolean("contatosAtivos", true );
			ed.commit();
		} else {
			//hide contatos
			mContatosTitulo.setVisibility(View.GONE);
			mContatosTabela.setVisibility(View.GONE);
			SharedPreferences.Editor ed = mSettings.edit();
			ed.putBoolean("contatosAtivos", false );
			ed.commit();
		}
	}
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detalhe, menu);
        if(!favorito)
        	menu.removeItem(R.id.del_bookmark);
        else
        	menu.removeItem(R.id.add_bookmark);        	
    	return super.onPrepareOptionsMenu(menu);
    }
    
    /** Handles menu clicks */
    public boolean onOptionsItemSelected( MenuItem item )
    {
        if (item.getItemId() == R.id.ver_no_mapa) {
			Intent intent = new Intent(this, ViewInMapActivity.class);
			intent.putExtra("id_centro", id );
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.add_bookmark) {
			adicionarFavoritos(id);
			return true;
		} else if (item.getItemId() == R.id.del_bookmark) {
			removerFavoritos(id);
			return true;
		}
	    
	    
        return false;
    }

        
    private void removerFavoritos(Integer id) {
    	getContentResolver().delete( CasasEspiritasProvider.getContentUri(isLite() , Favoritos.CONTENT ), Favoritos.CASA_ID + " = ?" , new String[]{String.valueOf(id)} );	
	}

	private void adicionarFavoritos(Integer id) {
		ContentValues data = new ContentValues();
    	data.put(Favoritos.CASA_ID, id); 
    	data.put(Favoritos.STAR, 5); 
	
    	getContentResolver().insert(CasasEspiritasProvider.getContentUri(isLite() , Favoritos.CONTENT ), data );	
		
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
    	
    	FavoritoVO vo = GuiaEspiritaDB.getFavoritoByCasa(this, Long.valueOf(id));
    	
    	if(vo != null){
    		favorito = true;
    	}
    	
    }
    
    private void setCasa(Cursor c) 
    {
        if (c == null) return;
        try {
            if (c.moveToFirst()) {
            	mNome.setText(      c.getString(1) );
                mEndereco.setText(  c.getString(2) );
                
                String bairro = c.getString(3);          
                String cep = c.getString(7);
                // Adicionar espaco após o nome da bairro
                if(!"".equals( cep ) && !"".equals( bairro )){
                	mBairro.setText(  bairro + ", " );
                } else {
                	mBairro.setText(  bairro );
                }
                mCep.setText(   cep    );
                
                String cidade = c.getString(4) ;
                String estado = c.getString(5) ;
                // Adicionar / após o nome da cidade
                if(!"".equals( estado ) && !"".equals( cidade )){
                	mCidade.setText( cidade + "/" );
                } else {
                	mCidade.setText( cidade );
                }                                              
                mEstado.setText( estado );    
                
                boolean hasContatosInfo = false;
                
                String info = c.getString(9) ;
                if(info == null || info.trim().length() == 0){
                	mBlocoInfo.setVisibility(View.GONE);
                } else {
                	mInformacoes.setText( info );
                	hasContatosInfo = true;
                }  
                
                String telefone = c.getString(6);
                if(telefone == null || telefone.trim().length() == 0){
                	mBlocoFone.setVisibility(View.GONE);
                } else {
                	mFone.setText( telefone );
                	hasContatosInfo = true;
                }
                
                String email = c.getString(8);
                if(email == null || email.trim().length() == 0){
                	mBlocoEmail.setVisibility(View.GONE);
                } else {
                	mEmail.setText( email );
                	hasContatosInfo = true;
                }
                
                String site = c.getString(12);
                if(site == null || site.trim().length() == 0){
                	mBlocoSite.setVisibility(View.GONE);
                } else {
                	mSite.setText( site );
                	hasContatosInfo = true;
                }
                
                if(!hasContatosInfo){
                	TextView semContatos = (TextView) findViewById(R.id.msgSemContatos);
                	semContatos.setVisibility(View.VISIBLE);
                }
                
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

	private void checkin() {
		Intent intent = new Intent(this, ItemCheckinActivity.class);
		intent.putExtra("id_centro", id);
		startActivity(intent);
	}

	private void dicas() {
		Intent intent = new Intent(this, ItemDicasActivity.class);
		intent.putExtra("id_centro", id);
		startActivity(intent);
	}
    
}
