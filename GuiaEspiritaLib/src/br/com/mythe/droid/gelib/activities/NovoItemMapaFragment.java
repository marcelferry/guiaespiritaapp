package br.com.mythe.droid.gelib.activities;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseMapFragment;
import br.com.mythe.droid.gelib.constants.GEConst;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NovoItemMapaFragment extends BaseMapFragment implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
	OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener {

	
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
	
	Geocoder geocoder;

	// Variaveis
	String mEndereco;
	String mBairro;
	String mCidade;
	String mCep;
	String mEstado;
	String mPais;

	Float mLat;
	Float mLng;
	
	Fragment _self = this;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void startDemo() {
		
		setTitle("Selecione um ponto");

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

	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			mPais = savedInstanceState.getString("pais");
			mEstado = savedInstanceState.getString("estado");
			mCidade = savedInstanceState.getString("cidade");
			mBairro = savedInstanceState.getString("bairro");
			mEndereco = savedInstanceState.getString("endereco");
		} else {
			Bundle intent = getArguments();
			mPais = intent.getString("pais");
			mEstado = intent.getString("estado");
			mCidade = intent.getString("cidade");
			mBairro = intent.getString("bairro");
			mEndereco = intent.getString("endereco");
		}

		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();

		String provider = locationManager.getBestProvider(criteria, true);

		location = locationManager.getLastKnownLocation(provider);
		
		if(location != null){
			atualizarMapa(location);
		}

		geocoder = new Geocoder(getActivity());
	}
	
	@Override
	protected void popularTela() {
		((Button) getView().findViewById(R.id.OkDialog)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
	            Intent intent = new Intent();
	            Bundle extras = new Bundle();
	            extras.putDouble("latitude", marker.getPosition().latitude);
	            extras.putDouble("longitude", marker.getPosition().longitude);
	            
	            intent.putExtras(extras);
	            getActivity().setResult(Activity.RESULT_OK, intent);        
	            getActivity().finish();//Finish  current activity

			}
		});

		((Button) getView().findViewById(R.id.CancelDialog))
		.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
			}
		});
	}

	private void posicionaoMapa() {
		try {
			String locationName = (mEndereco == null ? "": mEndereco);
			locationName += " Brasil";
			List<Address> addressList = geocoder.getFromLocationName(
					locationName, 5);
			if (addressList != null && addressList.size() > 0) {
				double lat = addressList.get(0).getLatitude();
				double lng = addressList.get(0).getLongitude();
				Location location = new Location("Centro");
				location.setLatitude(lat);
				location.setLongitude(lng);
				atualizarMapa(location);
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

	@Override
	protected int getLayoutId() {
		return R.layout.activity_mapa_dialog;
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
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		LatLng dragPosition = arg0.getPosition();
		double dragLat = dragPosition.latitude;
		double dragLong = dragPosition.longitude;
		Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);

		getMap().animateCamera(
				CameraUpdateFactory.newLatLng(arg0.getPosition()));

		marker = arg0;
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		getMap().clear();

		marker = getMap().addMarker(
				new MarkerOptions()
						.position(arg0)
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.fromResource(android.R.drawable.ic_menu_myplaces))
						.title("Você está aqui"));

		getMap().animateCamera(CameraUpdateFactory.newLatLng(arg0));
		
	}

	@Override
	public void onMapClick(LatLng arg0) {
		getMap().clear();

		marker = getMap().addMarker(
				new MarkerOptions()
						.position(arg0)
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.fromResource(android.R.drawable.ic_menu_myplaces))
						.title("Você está aqui"));
		
		getMap().animateCamera(CameraUpdateFactory.newLatLng(arg0));
		markerClicked = false;
	}
	
	public void atualizarMapa(Location myLocation) {
		LatLng position = null;
		
		if (myLocation != null) {
			position = new LatLng(myLocation.getLatitude(), myLocation
					.getLongitude());
			getGlobals().setLocationFilter(position);
		}
		
		getMap().setOnMapClickListener(this);

		getMap().setOnMapLongClickListener(this);

		getMap().setOnMarkerDragListener(this);

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
		return R.id.mapdialog;
	}

}
