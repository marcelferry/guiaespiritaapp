package br.com.mythe.droid.gelib.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jredfoot.droid.componentes.MultiDrawable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import br.com.mythe.droid.gelib.App;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.dashboard.BaseMapFragment;
import br.com.mythe.droid.gelib.database.GuiaEspiritaDB;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;
import br.com.mythe.droid.gelib.vo.CasaVO;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

/**
 * Demonstrates heavy customisation of the look of rendered clusters.
 */
public class RadarProximidadeFragment extends BaseMapFragment implements
		ClusterManager.OnClusterClickListener<CasaVO>,
		ClusterManager.OnClusterInfoWindowClickListener<CasaVO>,
		ClusterManager.OnClusterItemClickListener<CasaVO>,
		ClusterManager.OnClusterItemInfoWindowClickListener<CasaVO>,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		OnClickListener, OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener, OnMyLocationButtonClickListener{
	
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

	private ClusterManager<CasaVO> mClusterManager;
	private Random mRandom = new Random(1984);

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

	private static final double DISTANCE_PRECISION = 0.003;

	protected static final int CREATE_ERROR_DIALOG = 0;

	private ProgressDialog barProgressDialog;
	private int total = 0;

	private String tipoPesquisa;
	
	Marker marker;
	Location location;
	boolean markerClicked;

	private Cluster<CasaVO> clickedCluster;

	private CasaVO clickedClusterItem;

	/**
	 * Draws profile photos inside markers (using IconGenerator). When there are
	 * multiple people in the cluster, draw multiple photos (using
	 * MultiDrawable).
	 */
	private class ItemRenderer extends DefaultClusterRenderer<CasaVO> {
		private final IconGenerator mIconGenerator = new IconGenerator(
				getActivity().getApplicationContext());
		private final IconGenerator mClusterIconGenerator = new IconGenerator(
				getActivity().getApplicationContext());
		private final ImageView mImageView;
		private final ImageView mClusterImageView;
		private final int mDimension;

		public ItemRenderer() {
			super(getActivity().getApplicationContext(), getMap(), mClusterManager);

			View multiProfile = getActivity().getLayoutInflater().inflate(
					R.layout.multi_profile, null);
			mClusterIconGenerator.setContentView(multiProfile);
			mClusterImageView = (ImageView) multiProfile
					.findViewById(R.id.image);

			mImageView = new ImageView(getActivity().getApplicationContext());
			mDimension = (int) getResources().getDimension(
					R.dimen.custom_profile_image);
			mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension,
					mDimension));
			int padding = (int) getResources().getDimension(
					R.dimen.custom_profile_padding);
			mImageView.setPadding(padding, padding, padding, padding);
			mIconGenerator.setContentView(mImageView);
		}

		@Override
		protected void onBeforeClusterItemRendered(CasaVO person,
				MarkerOptions markerOptions) {
			// Draw a single person.
			// Set the info window to show their name.
			mImageView.setImageBitmap(person.image);
			Bitmap icon = mIconGenerator.makeIcon();
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(
					person.entidade);
		}

		@Override
		protected void onBeforeClusterRendered(Cluster<CasaVO> cluster,
				MarkerOptions markerOptions) {
			// Draw multiple people.
			// Note: this method runs on the UI thread. Don't spend too much
			// time in here (like in this example).
			List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4,
					cluster.getSize()));
			int width = mDimension;
			int height = mDimension;

			for (CasaVO p : cluster.getItems()) {
				// Draw 4 at most.
				if (profilePhotos.size() == 4)
					break;
				Drawable drawable = new BitmapDrawable(getActivity().getResources(),
						p.image);
				if (drawable != null){
					drawable.setBounds(0, 0, width, height);
					profilePhotos.add(drawable);
				}
			}

			MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
			multiDrawable.setBounds(0, 0, width, height);

			mClusterImageView.setImageDrawable(multiDrawable);
			Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster
					.getSize()));
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
		}

		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			// Always render clusters.
			return cluster.getSize() > 1;
		}
	}

	@Override
	public boolean onClusterClick(Cluster<CasaVO> cluster) {
		// Show a toast with some info when the cluster is clicked.
		clickedCluster = cluster;
		// String firstName = cluster.getItems().iterator().next().entidade;
		// Toast.makeText(getActivity(), cluster.getSize() + " (including " + firstName +
		// ")", Toast.LENGTH_SHORT).show();
		
		//getGlobals().setCurrentMapPosition(getMap().getCameraPosition().target);
		//getGlobals().setCurrentZoom(getMap().getCameraPosition().zoom);
		//getGlobals().setSelectedProfissionais(new ArrayList<CasaVO>());

		//for (CasaVO profissional : cluster.getItems()) {
		//	getGlobals().getSelectedProfissionais().add(profissional);
		//}
		return false;
	}

	@Override
	public void onClusterInfoWindowClick(Cluster<CasaVO> cluster) {
		// Does nothing, but you could go to a list of the users.
		// Toast.makeText(getApplicationContext(),
		// "Cliquei no onClusterInfoWindowClick", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onClusterItemClick(CasaVO item) {
		// Does nothing, but you could go into the user's profile page, for
		// example.
		
		clickedClusterItem = item;
		
		//getGlobals().setCurrentMapPosition(getMap().getCameraPosition().target);
		//getGlobals().setCurrentZoom(getMap().getCameraPosition().zoom);
		//int index = getGlobals().getProfissionais().indexOf(item);
		
		// Toast.makeText(getActivity().getApplicationContext(),
		//		 item.entidade, Toast.LENGTH_LONG).show();
		
		return false;
	}

	@Override
	public void onClusterItemInfoWindowClick(CasaVO item) {
		// Does nothing, but you could go into the user's profile page, for
		// example.
		getGlobals().setCurrentMapPosition(getMap().getCameraPosition().target);
		getGlobals().setCurrentZoom(getMap().getCameraPosition().zoom);
		int index = getGlobals().getProfissionais().indexOf(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Toast.makeText(getApplication(), longitude + "" + latitude,
		// Toast.LENGTH_LONG).show();

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

		mClusterManager = new ClusterManager<CasaVO>(getActivity(), getMap());
		mClusterManager.setRenderer(new ItemRenderer());
		getMap().setOnCameraChangeListener(mClusterManager);
		getMap().setOnMarkerClickListener(mClusterManager);
		getMap().setOnInfoWindowClickListener(mClusterManager);
		getMap().setInfoWindowAdapter(mClusterManager.getMarkerManager());
	
		mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForClusters());
		mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());

		mClusterManager.setOnClusterClickListener(this);
		mClusterManager.setOnClusterInfoWindowClickListener(this);
		mClusterManager.setOnClusterItemClickListener(this);
		mClusterManager.setOnClusterItemInfoWindowClickListener(this);
		
	
		getMap().getUiSettings().setMyLocationButtonEnabled(true);
		getMap().getUiSettings().setCompassEnabled(true);
		getMap().setMyLocationEnabled(true);
		getMap().setOnMyLocationButtonClickListener(this);

		mClusterManager.cluster();
	}
	
	@Override
	protected void inicializar(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void popularTela() {
		// TODO Auto-generated method stub
	}

	public void addItems() {
		
		List<CasaVO> results = getGlobals().getProfissionais();
		if (results != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			synchronized(this){
				mClusterManager.clearItems();
				for (CasaVO c : results) {
					builder.include(c.getPosition());
					mClusterManager.addItem(c);
				}
				mClusterManager.cluster();
			}
			
			if (results != null && results.size() > 1) {
				LatLngBounds bounds = builder.build();

				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
						bounds, 100);

				getMap().animateCamera(cu);
			}
		}
	}

	private LatLng position(LatLng position) { // -23.319617,-51.16657
		LatLng item = new LatLng(random(position.latitude + DISTANCE_PRECISION,
				position.latitude - DISTANCE_PRECISION), random(
				position.longitude + DISTANCE_PRECISION, position.longitude
						- DISTANCE_PRECISION));
		System.out.println(item.latitude + " " + item.longitude);
		return item;
	}

	private double random(double min, double max) {
		return mRandom.nextDouble() * (max - min) + min;
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
		atualizarMap(location);
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
		atualizarMap(myLocation);
	}

	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(getActivity(), "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */
				break;
			}
		}
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
		//Toast.makeText(getApplicationContext(), "Marker Dragged..!",
		//		Toast.LENGTH_LONG).show();

		getMap().animateCamera(
				CameraUpdateFactory.newLatLng(arg0.getPosition()));
		
		getGlobals().setLocationFilter(dragPosition);
		
		List<CasaVO> results = getCasas();
		
		getGlobals().setProfissionais(results);
		
		addItems();

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
								.fromResource(R.drawable.marker_icon_red))
						.title("Você está aqui"));

		getGlobals().setLocationFilter(arg0);
		
		List<CasaVO> results = getCasas();
		
		getGlobals().setProfissionais(results);
		
		addItems();
		
	}

	@Override
	public void onMapClick(LatLng arg0) {
		getMap().animateCamera(CameraUpdateFactory.newLatLng(arg0));
		markerClicked = false;
	}

	public void atualizarMap(Location myLocation) {
		
		LatLng position = null;
		
		getMap().clear();
		
		if (tipoPesquisa != null && tipoPesquisa.equals("CIDADE")
				&& getGlobals().getLongitudeFilter() != 0
				&& getGlobals().getLatitudeFilter() != 0) {
			position = new LatLng(getGlobals().getLatitudeFilter(), getGlobals()
					.getLongitudeFilter());
			getGlobals().setLocationFilter(position);
		} else {
			if (myLocation != null) {
				position = new LatLng(myLocation.getLatitude(), myLocation
						.getLongitude());
				getGlobals().setLocationFilter(position);
			}
		}

		if (getGlobals().getCurrentMapPosition() != getGlobals()
				.getLocationFilter()) {
			getMap().moveCamera(
					CameraUpdateFactory.newLatLngZoom(getGlobals()
							.getCurrentMapPosition(), getGlobals()
							.getCurrentZoom()));
		} else {
			getMap().moveCamera(
					CameraUpdateFactory
							.newLatLngZoom(getGlobals().getLocationFilter(),
									getGlobals().getCurrentZoom()));
		}
		
		
		

		// getMap().addMarker(new MarkerOptions()
		// .position(locationFilter)
		// .title("Sua Localizacao")
		// .snippet("Voce esta aqui!")
		// .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


		getMap().setOnMapClickListener(this);

		getMap().setOnMapLongClickListener(this);

		getMap().setOnMarkerDragListener(this);

		markerClicked = false;

		marker = getMap().addMarker(
				new MarkerOptions()
						.position(position)
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker_icon_red))
						.title("Você está aqui."));
		
		List<CasaVO> results = getCasas();
		
		getGlobals().setProfissionais(results);
		
		addItems();
	}

	private List<CasaVO> getCasas() {
		String[] projection = new String[]{	
				Casas._ID,
				Casas.COD_CASA,
				Casas.NOME,
				Casas.INFORMACOES,
				Casas.ENDERECO,
				Casas.BAIRRO,
				Casas.CIDADE,
				Casas.ESTADO,
				Casas.CEP,
				Casas.FONE,
				Casas.EMAIL,
				Casas.SITE,
				Casas.LAT,
				Casas.LNG,
				Casas.TYPE,
				Casas.ATUALIZADO
			};
		
		
		LatLng local = getGlobals().getLocationFilter();
		Cursor cur = getActivity().getContentResolver().query(CasasEspiritasProvider.getContentUri(true , Casas.CONTENT ), projection, null, null, " (lat - "+ local.latitude +") * (lat - "+ local.latitude +")  + (lng - " + local.longitude + " ) * (lng - " + local.longitude + " ) LIMIT " + getGlobals().getRecordsRetrieveFilter());		
		//Cursor cur = getActivity().getContentResolver().query(CasasEspiritasProvider.getContentUri(true , Casas.CONTENT ), projection, null, null, " (lat - "+ local.latitude +") * (lat - "+ local.latitude +")  + (lng - " + local.longitude + " ) * (lng - " + local.longitude + " )  ");		
		
		List<CasaVO> results = GuiaEspiritaDB.cursorToListaCasa(cur, getActivity());
		
		return results;
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		int id = v.getId();
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	//@Override
	//public void onRestoreInstanceState(Bundle savedInstanceState) {
	//	super.onRestoreInstanceState(savedInstanceState);
	//}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_radar;
	}

	private List<Marker> selectLowDistanceMarkers(List<Marker> markers,
			int maxDistanceMeters) {

		List<Marker> acceptedMarkers = new ArrayList<Marker>();

		if (markers == null)
			return acceptedMarkers;

		Map<Marker, Float> longestDist = new HashMap<Marker, Float>();

		for (Marker marker1 : markers) {

			// in this for loop we remember the max distance for each marker
			// think of a map with a flight company's routes from an airport
			// these lines is drawn for each airport
			// marker1 being the airport and marker2 destinations

			for (Marker marker2 : markers) {
				if (!marker1.equals(marker2)) {
					float distance = distBetween(marker1.getPosition(),
							marker2.getPosition());
					if (longestDist.containsKey(marker1)) {
						// possible we have a longer distance
						if (distance > longestDist.get(marker1))
							longestDist.put(marker1, distance);
					} else {
						// first distance
						longestDist.put(marker1, distance);
					}
				}
			}
		}

		// examine the distances collected
		for (Marker marker : longestDist.keySet()) {
			if (longestDist.get(marker) <= maxDistanceMeters)
				acceptedMarkers.add(marker);
		}

		return acceptedMarkers;
	}

	private List<Marker> getSurroundingMarkers(List<Marker> markers,
			LatLng origin, int maxDistanceMeters) {
		List<Marker> surroundingMarkers = new ArrayList<Marker>();
		if (markers == null)
			return surroundingMarkers;

		for (Marker marker : markers) {

			double dist = distBetween(origin, marker.getPosition());

			if (dist < getGlobals().getDistanceFilter()) {
				surroundingMarkers.add(marker);
			}
		}

		return surroundingMarkers;
	}

	private float distBetween(LatLng pos1, LatLng pos2) {
		return distBetween(pos1.latitude, pos1.longitude, pos2.latitude,
				pos2.longitude);
	}

	/** distance in meters **/
	private float distBetween(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (float) (dist * meterConversion);
	}

	@Override
	public boolean onMyLocationButtonClick() {
		atualizarMap(getMap().getMyLocation());
		return true;
	}
	
	class MyCustomAdapterForClusters implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker arg0) {
			if (clickedCluster != null) {
				
				// Getting view from the layout file info_window_layout
	            View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout_cluster, null);
	
	            TextView tvNome = (TextView) v.findViewById(R.id.tv_titulo);
	
	            tvNome.setText("Entidades próximas:");
	            int count = 0;
	            boolean fim = false;
	            for (CasaVO item : clickedCluster.getItems()) {
	            	if(count == 6){
	            		fim = true;
	            	}
	            	TextView tv = new TextView(getActivity());
	            	if(fim)
	            		tv.setText("e outras " + (clickedCluster.getItems() .size() - 6) + " entidades.");
	            	else
	            		tv.setText(item.entidade);
	            	LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	            	((LinearLayout)v).addView(tv, lp);
	            	count++;
	            	if(fim) break;
	            }
	            //tvEndereco.setText(clickedClusterItem.endereco);
	            //tvBairro.setText(clickedClusterItem.bairro + " " + clickedClusterItem.cidade);
	
	            return v;
	            
	            
	        }
			return null;
		}

		@Override
		public View getInfoWindow(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	class MyCustomAdapterForItems implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker arg0) {
			if( clickedClusterItem != null) {
				// Getting view from the layout file info_window_layout
	            View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
	
	            TextView tvNome = (TextView) v.findViewById(R.id.tv_nome);
	            TextView tvEndereco = (TextView) v.findViewById(R.id.tv_endereco);
	            TextView tvBairro = (TextView) v.findViewById(R.id.tv_bairro);
	
	            tvNome.setText(clickedClusterItem.entidade);
	            tvEndereco.setText(clickedClusterItem.endereco);
	            tvBairro.setText(clickedClusterItem.bairro + " " + clickedClusterItem.cidade);
	
	            return v;
			} 
			
			return null;
		}

		@Override
		public View getInfoWindow(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	@Override
	protected int getMapaId() {
		return R.id.map;
	}


}