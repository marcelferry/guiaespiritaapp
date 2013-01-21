package br.com.mythe.droid.common.util;

import com.google.android.maps.GeoPoint;

public class Ponto extends GeoPoint{

	public Ponto(int latitudeE6, int longitudeE6) {
		super(latitudeE6, longitudeE6);
		// TODO Auto-generated constructor stub
	}
	
	public Ponto(double latitude, double longitude){
		this((int) (latitude * 1E6), (int) (longitude * 1E6));
	}
	

}
