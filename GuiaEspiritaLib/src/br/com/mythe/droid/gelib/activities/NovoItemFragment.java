package br.com.mythe.droid.gelib.activities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jredfoot.sophielib.transacao.Transacao;
import org.jredfoot.sophielib.util.NetUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseMapFragment;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class NovoItemFragment extends BaseMapFragment implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
	OnMapClickListener, OnClickListener, Transacao {

	
	// Global constants
	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	// Define an object that holds accuracy and frequency parameters
	LocationRequest mLocationRequest;
	LocationClient mLocationClient;
	boolean mUpdatesRequested;
	SharedPreferences mPrefs;
	Editor mEditor;

	Marker marker;
	Location location;
	boolean markerClicked;
	
	Map<String, String> parametros;

	private EditText mEdtNome;
	private EditText mEdtEndereco;
	private EditText mEdtFone;
	private EditText mEdtInfo;
	private Button mBtnSalvar;
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
						getActivity(),
						 "Cadastro atualizado.",
						Toast.LENGTH_LONG).show();
				break;
				case ADD_ITEM_SUCCESS:
					Toast.makeText(
							getActivity(),
							 "Cadastro enviado.",
							Toast.LENGTH_LONG).show();
					break;
				case ADD_ITEM_FAILED:
					Toast.makeText(
							getActivity(),
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
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void startDemo() {

		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		// Open the shared preferences
		mPrefs = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(getActivity(), this, this);
		// Start with updates turned off
		mUpdatesRequested = false;

		getMap().getUiSettings().setMyLocationButtonEnabled(true);
		getMap().getUiSettings().setCompassEnabled(true);
		getMap().setMyLocationEnabled(true);
		getMap().setOnMapClickListener(this);

	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		String pais = null;
		String estado = null;
		String cidade = null;
		

		mStaticHandler = mHandler;

		if (savedInstanceState != null) {
			pais = savedInstanceState.getString("pais");
			estado = savedInstanceState.getString("estado");
			cidade = savedInstanceState.getString("cidade");
		} else {
			Bundle intent = getArguments();
			pais = intent.getString("pais");
			estado = intent.getString("estado");
			cidade = intent.getString("cidade");
		}

		spTipo = (Spinner) getView().findViewById(R.id.spTipo);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				getActivity(), R.array.tipo_item, android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spTipo.setAdapter(adapter1);
		spTipo.setSelection(-1);

		mEdtNome = (EditText) getView().findViewById(R.id.txt_entidade);
		mEdtEndereco = (EditText) getView().findViewById(R.id.txt_endereco);
		mEdtFone = (EditText) getView().findViewById(R.id.txt_telefone);
		mEdtInfo = (EditText) getView().findViewById(R.id.txt_info);
		mBtnSalvar = (Button) getView().findViewById(R.id.btn_save);
		
		mBtnSalvar.setOnClickListener(this);

		mEdtFone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//showContactDialog();
				int activityID = 0x101;
				Intent intent;
				intent = new Intent().setClass(getActivity(), NovoItemContatoDialog.class);
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
				intent = new Intent().setClass(getActivity(), NovoItemEnderecoDialog.class);
				
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
				intent = new Intent().setClass(getActivity(), NovoItemInfoDialog.class);
				Bundle extras = new Bundle();
				extras.putString("info", mInfo);
				
				intent.putExtras(extras);
				startActivityForResult(intent, activityID);
			}
		});
		
		geocoder = new Geocoder(getActivity());
	}
	
	@Override
	protected void popularTela() {
		// TODO Auto-generated method stub
		
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

	public void onClick(View v) {
		
		if(mEdtNome.getText() == null || mEdtNome.getText().toString().trim().length() < 5){
			Toast.makeText(getActivity(), "Favor preencher corretamente o campo 'Nome'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mEndereco == null || mEndereco.trim().length() < 5 ) {
			Toast.makeText(getActivity(), "Favor preencher corretamente o campo 'Endereço'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mCidade == null || mCidade.trim().length() < 4 ){
			Toast.makeText(getActivity(), "Favor preencher corretamente o campo 'Cidade'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mEstado == null || mEstado.trim().length() < 2){
			Toast.makeText(getActivity(), "Favor preencher corretamente o campo 'Estado'.", Toast.LENGTH_LONG).show();
			return;
		}
		if(mLng == null || mLat == null || mLng.longValue() == 0 || mLat.longValue() == 0 ){
			Toast.makeText(getActivity(), "Favor marcar um ponto no mapa.", Toast.LENGTH_LONG).show();
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
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    String usuario = settings.getString("usuario","");

		parametros.put("TIPO", String.valueOf(selectedVal));
		parametros.put("USER", usuario);

		parametros.put("acao", String.valueOf("salvar"));

		startTransacao(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 0x100: {
				if (resultCode == Activity.RESULT_OK) {
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
				if (resultCode == Activity.RESULT_OK) {
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
				if (resultCode == Activity.RESULT_OK) {
					Bundle extras = data.getExtras();
					mInfo = extras.getString("info");
					if (mInfo != null && mInfo.trim().length() > 0) {
						mEdtInfo.setHint("Alterar Informações...");
					}
				}
			}
			case 0x103: {
				if (resultCode == Activity.RESULT_OK) {
					Bundle extras = data.getExtras();
					double latitude = extras.getDouble("latitude");
					double longitude = extras.getDouble("longitude");
					mLat = (float)latitude;
					mLng = (float)longitude;
					Location l = new Location("");
					l.setLatitude(latitude);
					l.setLongitude(longitude);
					atualizarMapa(l);
					System.out.println(l);
				}
			}
			case CONNECTION_FAILURE_RESOLUTION_REQUEST:
				switch (resultCode) {
					case Activity.RESULT_OK:
						/*
						 * Try the request again
						 */
						break;
				}
				break;
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
			data.put(Casas.LAT, Double.valueOf(casa.getAttribute("lat")));
			data.put(Casas.LNG, Double.valueOf(casa.getAttribute("lng")));
			data.put(Casas.INFORMACOES, casa.getAttribute("info"));
			data.put(Casas.ATUALIZADO, casa.getAttribute("atualizado"));
			data.put(Casas.TYPE, casa.getAttribute("tipo"));
	

			if (casa.getAttribute("status").equals("update")) {
				getActivity().getContentResolver().update( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data,
						"rem_id = ?",
						new String[] { casa.getAttribute("codigo") });
				Message m = Message.obtain(mHandler, ADD_ITEM_UPDATED);
				mHandler.sendMessage(m);
			}

			if (casa.getAttribute("status").equals("new")) {
				getActivity().getContentResolver().insert( CasasEspiritasProvider.getContentUri(isLite() , Casas.CONTENT ), data);
				Message m = Message.obtain(mHandler, ADD_ITEM_SUCCESS);
				mHandler.sendMessage(m);
			}

		}
		
	}

	@Override
	public void atualizarView() {
		getActivity().finish();
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_novo_item;
	}
	
	@Override
	public void onPause() {
		// Save the current setting for updates
		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
		mEditor.commit();
		getGlobals().setCurrentMapPosition(getMap().getCameraPosition().target);
		getGlobals().setCurrentZoom(getMap().getCameraPosition().zoom);
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * Get any previous setting for location updates Gets "false" if an
		 * error occurs
		 */
		if (mPrefs.contains("KEY_UPDATES_ON")) {
			mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);

			// Otherwise, turn off location updates
		} else {
			mEditor.putBoolean("KEY_UPDATES_ON", false);
			mEditor.commit();
		}
		getMap().moveCamera(
				CameraUpdateFactory
						.newLatLngZoom(getGlobals().getCurrentMapPosition(),
								getGlobals().getCurrentZoom()));
	}

	@Override
	public void onStop() {
		// If the client is connected
		if (mLocationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			mLocationClient.removeLocationUpdates(this);
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		String msg = "Updated Location: "
				+ Double.toString(location.getLatitude()) + ","
				+ Double.toString(location.getLongitude());
		// Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		atualizarMapa(location);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(getActivity(),
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			// showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// Display the connection status
		// Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		// If already requested, start periodic updates
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
		Location myLocation = mLocationClient.getLastLocation();
		atualizarMapa(myLocation);
	}

	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(getActivity(), "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			int errorCode = 0;// connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode, getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getActivity().getSupportFragmentManager(),
						"Location Updates");
			}
		}
		return false;
	}

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		int activityID = 0x103;
		Intent i = new Intent(getActivity(), NovoItemMapaDialog.class);
		i.putExtra("pais", mPais);
		i.putExtra("estado", mEstado);
		i.putExtra("cidade", mCidade);
		i.putExtra("bairro", mBairro);
		i.putExtra("endereco", mEndereco);
		startActivityForResult(i, activityID);
	}
	
	public void atualizarMapa(Location myLocation) {
		LatLng position = null;
		
		if (myLocation != null) {
			position = new LatLng(myLocation.getLatitude(), myLocation
					.getLongitude());
			getGlobals().setLocationFilter(position);
		}
		
		getMap().clear();
		
		markerClicked = false;

		marker = getMap().addMarker(
				new MarkerOptions()
						.position(position)
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.fromResource(android.R.drawable.ic_menu_myplaces))
						.title("Você está aqui."));
		
		getMap().animateCamera(
				CameraUpdateFactory.newLatLng(position));
	}

	@Override
	protected int getMapaId() {
		return R.id.mapnewitem;
	}
}
