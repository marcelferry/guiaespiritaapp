package br.com.mythe.droid.common.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import br.com.mythe.droid.gelib.activities.MapaProximosActivity;

import com.google.android.maps.GeoPoint;

public class MapUtils {
	
	public static LocationManager locationManager;
	public static String provider;
	
	public static boolean isGPSActive(Context context){
		LocationManager service = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return enabled;
	}

	
	public static Location getMyLocation(Context context){
		Location myLocal = null;
		if(locationManager == null){
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if(provider == null){
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setAltitudeRequired(false);
				
				provider = locationManager.getBestProvider(criteria, true);
			}
		}
		
		if(provider != null){
			myLocal = locationManager.getLastKnownLocation(provider);
		}

		if (myLocal == null) {
			if (MapaProximosActivity.getMyLocationOverlay() != null) {
				GeoPoint geoPoint = MapaProximosActivity.getMyLocationOverlay()
						.getMyLocation();
				float latitude = 0;
				float longitude = 0;
				if(geoPoint != null){
					latitude = geoPoint.getLatitudeE6() / 1000000F;
					longitude = geoPoint.getLongitudeE6() / 1000000F;
				}
				myLocal = new Location("MeuLocal");
				myLocal.setLatitude(latitude);
				myLocal.setLongitude(longitude);
			}
		} 
		return myLocal;
	}
	
	public static double getDistance(Context context, Location destino){
		Location myLocal = getMyLocation(context);
		if (myLocal != null) {
			return destino.distanceTo(myLocal);
		} else {
			return 0.0;
		}
	}
	
	public static double getDistance(Location origem, Location destino){
		return origem.distanceTo(destino);
	}
	
	public static String getDistanceFormatada(Location origem, Location destino){
		if(origem == null){
			return null;
		}
		
		double distancia = origem.distanceTo(destino);
		return getDistanceFormatada(distancia);
	}
	
	public static String getDistanceFormatada(Context context, Location destino){
		double distancia = MapUtils.getDistance(context, destino);
		return getDistanceFormatada(distancia);
	}
	
	public static String getDistanceFormatada(double distanciaEmMetros){
		String novoTexto = null;
		if (distanciaEmMetros > 1000) {

			int kil = (int) distanciaEmMetros / 10;
			double km = kil / 100.0;
			novoTexto = km + " km";
		} else {
			int metros = (int) distanciaEmMetros;
			novoTexto = metros + " m";
		}

		if (distanciaEmMetros == 0.0)
			novoTexto = " ";
		
		return novoTexto;
	}

}
