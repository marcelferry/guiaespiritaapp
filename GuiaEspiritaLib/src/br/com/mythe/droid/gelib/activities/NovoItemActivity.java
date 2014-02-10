package br.com.mythe.droid.gelib.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jredfoot.sophielib.transacao.Transacao;
import org.jredfoot.sophielib.util.NetUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.MapaActivity;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class NovoItemActivity extends MapaActivity implements Transacao {

	InterestingLocations mPoint;
	private static MyLocationOverlay mMeuLocal;
	Map<String, String> parametros;

	private EditText mEdtNome;
	private EditText mEdtEndereco;
	private EditText mEdtFone;
	private EditText mEdtInfo;
	Geocoder geocoder;
	Spinner spTipo;
	MapView mapView;

	// Variaveis
	String mEndereco;
	String mBairro;
	String mCidade;
	String mCep;
	String mEstado;
	String mPais;
	String mTelefone;
	String mEmail;
	String mSite;
	String mInfo;
	Float mLat;
	Float mLng;
	
	private static final int ADD_ITEM_UPDATED = 1;
	private static final int ADD_ITEM_SUCCESS = 0;
	private static final int ADD_ITEM_FAILED = -1;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ADD_ITEM_UPDATED:
				Toast.makeText(
						NovoItemActivity.this,
						 "Cadastro atualizado.",
						Toast.LENGTH_LONG).show();
				break;
				case ADD_ITEM_SUCCESS:
					Toast.makeText(
							NovoItemActivity.this,
							 "Cadastro enviado.",
							Toast.LENGTH_LONG).show();
					break;
				case ADD_ITEM_FAILED:
					Toast.makeText(
							NovoItemActivity.this,
							"Erro ao sincronizar conteúdo Web, verifique sua conexão ou tente novamente mais tarde.",
							Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	public static Handler mStaticHandler = null;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		String pais = null;
		String estado = null;
		String cidade = null;

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_novo_item);
		
		mStaticHandler = mHandler;

		if (savedInstanceState != null) {
			pais = savedInstanceState.getString("pais");
			estado = savedInstanceState.getString("estado");
			cidade = savedInstanceState.getString("cidade");
		} else {
			Intent intent = getIntent();
			pais = intent.getStringExtra("pais");
			estado = intent.getStringExtra("estado");
			cidade = intent.getStringExtra("cidade");
		}

		mapView = (MapView) findViewById(R.id.mapa);

		spTipo = (Spinner) findViewById(R.id.spTipo);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				this, R.array.tipo_item, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spTipo.setAdapter(adapter1);
		spTipo.setSelection(-1);

		mEdtNome = (EditText) findViewById(R.id.txt_entidade);
		mEdtEndereco = (EditText) findViewById(R.id.txt_endereco);
		mEdtFone = (EditText) findViewById(R.id.txt_telefone);
		mEdtInfo = (EditText) findViewById(R.id.txt_info);

		mEdtFone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//showContactDialog();
				int activityID = 0x101;
				Intent intent;
				intent = new Intent().setClass(NovoItemActivity.this, NovoItemContatoDialog.class);
				Bundle extras = new Bundle();
				extras.putString("telefone", mTelefone);
				extras.putString("email", mEmail);
				extras.putString("site", mSite);
				
				intent.putExtras(extras);
				startActivityForResult(intent, activityID);
			}
		});
		
		mCidade = cidade;
		mEstado = estado;
		mPais = pais;

		mEdtEndereco.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//showAddressDialog();
				int activityID = 0x100;
				Intent intent;
				intent = new Intent().setClass(NovoItemActivity.this, NovoItemEnderecoDialog.class);
				
				Bundle extras = new Bundle();
	            extras.putString("endereco", mEndereco);
	            extras.putString("bairro", mBairro);
	            extras.putString("cidade", mCidade);
	            extras.putString("cep", mCep);
	            extras.putString("estado", mEstado);
	            extras.putString("pais", mPais);
	            
	            intent.putExtras(extras);
	            
				startActivityForResult(intent, activityID);

			}
		});

		mEdtInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//showInfoDialog();
				int activityID = 0x102;
				Intent intent;
				intent = new Intent().setClass(NovoItemActivity.this, NovoItemInfoDialog.class);
				Bundle extras = new Bundle();
				extras.putString("info", mInfo);
				
				intent.putExtras(extras);
				startActivityForResult(intent, activityID);
			}
		});

		mapView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				GeoPoint p = null;

				if (event.getAction() == MotionEvent.ACTION_UP) {
					p = mapView.getProjection().fromPixels((int) event.getX(),
							(int) event.getY());

					mLat = p.getLatitudeE6() / 1000000F;
					mLng = p.getLongitudeE6() / 1000000F;

					mapView.getController().animateTo(p);

					Drawable marker = getResources().getDrawable(
							android.R.drawable.ic_menu_myplaces);
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
							marker.getIntrinsicHeight());

					if (mPoint == null) {
						mPoint = new InterestingLocations(marker, p);
						mapView.getOverlays().add(mPoint);
					} else {
						mapView.getOverlays().remove(mPoint);
						mPoint = new InterestingLocations(marker, p);
						mapView.getOverlays().add(mPoint);
					}

					return true;
				}
				return false;
			}
		});

		mMeuLocal = new MyLocationOverlay(this, mapView);
		mMeuLocal.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(mMeuLocal.getMyLocation());
			}
		});

		mapView.getController().setZoom(15);

		mapView.getOverlays().add(mMeuLocal);

		geocoder = new Geocoder(this);
		
	}

	private void posicionaoMapa() {
		try {
			String locationName = (mEdtEndereco.getText() == null ? ""
					: mEdtEndereco.getText().toString());
			locationName += " Brasil";
			List<Address> addressList = geocoder.getFromLocationName(
					locationName, 5);
			if (addressList != null && addressList.size() > 0) {
				int lat = (int) (addressList.get(0).getLatitude() * 1000000);
				int lng = (int) (addressList.get(0).getLongitude() * 1000000);
				GeoPoint pt = new GeoPoint(lat, lng);
				mapView.getController().setZoom(15);
				mapView.getController().setCenter(pt);
			} else {

			}
		} catch (IOException e) {
			Log.e(GEConst.APP_NAME,
					"Erro ao buscar posição no mapa pelo endereço", e);
		}
	}

	/**
	 * Shows an error dialog to the user.
	 * 
	 * @param ctx
	 * @param title
	 * @param description
	 */
	public void presentError(Context ctx, String title, String description) {
		AlertDialog.Builder d = new AlertDialog.Builder(ctx);
		d.setTitle(title);
		d.setMessage(description);
		d.setIcon(android.R.drawable.ic_dialog_alert);
		d.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		d.show();
	}

	public void onClickSave(View v) {
		
		if(mEdtNome.getText() == null || mEdtNome.getText().toString().trim().length() < 5){
			Toast.makeText(this, "Favor preencher corretamente o campo 'Nome'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mEndereco == null || mEndereco.trim().length() < 5 ) {
			Toast.makeText(this, "Favor preencher corretamente o campo 'Endereço'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mCidade == null || mCidade.trim().length() < 4 ){
			Toast.makeText(this, "Favor preencher corretamente o campo 'Cidade'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mEstado == null || mEstado.trim().length() < 2){
			Toast.makeText(this, "Favor preencher corretamente o campo 'Estado'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mLng == null || mLat == null || mLng.longValue() == 0 || mLat.longValue() == 0 ){
			Toast.makeText(this, "Favor marcar um ponto no mapa.", Toast.LENGTH_LONG).show();
			return;
		}
		

		parametros = new HashMap<String, String>();
		parametros.put("NOME", String.valueOf(mEdtNome.getText()));
		parametros.put("ENDERECO", String.valueOf(mEndereco == null? "": mEndereco));
		parametros.put("BAIRRO", String.valueOf(mBairro == null? "": mBairro));
		parametros.put("CIDADE", String.valueOf(mCidade == null? "": mCidade));
		parametros.put("ESTADO", String.valueOf(mEstado == null? "": mEstado));
		parametros.put("PAIS", String.valueOf(mPais == null? "": mPais));
		parametros.put("CEP", String.valueOf(mCep == null? "": mCep));
		parametros.put("FONE", String.valueOf(mTelefone == null? "": mTelefone ));
		parametros.put("EMAIL", String.valueOf(mEmail == null? "": mEmail));
		parametros.put("SITE", String.valueOf(mSite == null? "": mSite));
		parametros.put("INFO", String.valueOf(mInfo == null? "": mInfo));
		parametros.put("LAT", String.valueOf(mLat == null? "": mLat));
		parametros.put("LNG", String.valueOf(mLng == null? "": mLng));

		String selectedVal = getResources().getStringArray(
				R.array.tipo_item_value)[spTipo.getSelectedItemPosition()];
		
		// Restore preferences
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	    String usuario = settings.getString("usuario","");

		parametros.put("TIPO", String.valueOf(selectedVal));
		parametros.put("USER", usuario);

		parametros.put("acao", String.valueOf("salvar"));

		startTransacao(this);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 0x100: {
				if (resultCode == RESULT_OK) {
					Bundle extras = data.getExtras();
					mEndereco = extras.getString("endereco");
			        mBairro = extras.getString("bairro");
			        mCidade = extras.getString("cidade");
			        mCep = extras.getString("cep");
			        mEstado = extras.getString("estado");
			        mPais = extras.getString("pais");
			        mEdtEndereco.setText( extras.getString("location") );
				}
			}
			case 0x101: {
				if (resultCode == RESULT_OK) {
					Bundle extras = data.getExtras();
					mTelefone = extras.getString("telefone");
					mEmail = extras.getString("email");
					mSite = extras.getString("site");
					if ((mTelefone != null && mTelefone.trim().length() > 0)
							|| (mEmail != null && mEmail.trim().length() > 0)
							|| (mSite != null && mSite.trim().length() > 0)) {
						mEdtFone.setHint("Alterar Contato...");
					}
				}
			}
			case 0x102: {
				if (resultCode == RESULT_OK) {
					Bundle extras = data.getExtras();
					mInfo = extras.getString("info");
					if (mInfo != null && mInfo.trim().length() > 0) {
						mEdtInfo.setHint("Alterar Informações...");
					}
				}

			}
		}
	}	
	

	class InterestingLocations extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;

		public InterestingLocations(Drawable defaultMarker, int LatitudeE6,
				int LongitudeE6) {
			super(defaultMarker);
			// TODO Auto-generated constructor stub
			this.marker = defaultMarker;
			// create locations of interest
			GeoPoint myPlace = new GeoPoint(LatitudeE6, LongitudeE6);
			locations.add(new OverlayItem(myPlace, "Entidade", "Entidade"));
			populate();
		}

		public InterestingLocations(Drawable defaultMarker, GeoPoint myPlace) {
			super(defaultMarker);
			// TODO Auto-generated constructor stub
			this.marker = defaultMarker;
			// create locations of interest
			locations.add(new OverlayItem(myPlace, "Entidade", "Entidade"));
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return locations.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return locations.size();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);

			boundCenterBottom(marker);
		}
	}

	@Override
	public void executar() throws Exception {
		
		String caminho = NetUtils.generateGetUrl(GEConst.ADD_ENTIDADES_URI,
				this.parametros);

		Log.w("Mythe", caminho);

		Document doc;

		doc = NetUtils.getXmlFromService(caminho);

		int casasSize = doc.getElementsByTagName("entidade").getLength();

		for (int i = 0; i < casasSize; i++) {
			Element casa = (Element) doc.getElementsByTagName("entidade")
					.item(i);

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
	

			if (casa.getAttribute("status").equals("update")) {
				getContentResolver().update( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data,
						"rem_id = ?",
						new String[] { casa.getAttribute("codigo") });
				Message m = Message.obtain(mHandler, ADD_ITEM_UPDATED);
				mHandler.sendMessage(m);
			}

			if (casa.getAttribute("status").equals("new")) {
				getContentResolver().insert( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data);
				Message m = Message.obtain(mHandler, ADD_ITEM_SUCCESS);
				mHandler.sendMessage(m);
			}

		}
		
	}

	@Override
	public void atualizarView() {
		finish();
	}

}
