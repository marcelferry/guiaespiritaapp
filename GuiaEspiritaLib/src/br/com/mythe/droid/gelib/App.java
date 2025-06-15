package br.com.mythe.droid.gelib;

import java.util.ArrayList;
import java.util.List;

import br.com.mythe.droid.gelib.vo.CasaVO;

import com.google.android.gms.maps.model.LatLng;

import android.app.Application;

public class App extends Application{
	
	public static int TP_CITY = 0;
	public static int TP_LOCAL = 1;
	
	
	private List<CasaVO> profissionais;
	private List<CasaVO> selectedProfissionais;

	private int filterType = TP_CITY;
	
	private String stateSelected = "";
	private String citySelected = "";
	
	
    
	private int distanceFilter = 2000;
	private int recordsRetrieveFilter = 10;
	private String genderFilter = null; 
	private int ageFilter = 1;
	private LatLng locationFilter = new LatLng(-23.319617,-51.16657);
	private double longitudeFilter = -23.319617;
	private double latitudeFilter = -51.16657;
	private float currentZoom = 15;
	private LatLng currentMapPosition = new LatLng(-23.319617,-51.16657);
	
	private CasaVO currentProfissional;

	public void setCurrentMapPosition(LatLng target) {
		currentMapPosition = target;
	}

	public void setCurrentZoom(float zoom) {
		currentZoom = zoom;
	}

	public void setSelectedProfissionais(List<CasaVO> arrayList) {
		this.selectedProfissionais = arrayList;
	}

	public List<CasaVO> getSelectedProfissionais() {
		return selectedProfissionais;
	}

	public List<CasaVO> getProfissionais() {
		return profissionais;
	}

	public LatLng getCurrentMapPosition() {
		return currentMapPosition;
	}

	public float getCurrentZoom() {
		return currentZoom;
	}

	public LatLng getLocationFilter() {
		return locationFilter;
	}

	public Double getLongitudeFilter() {
		return longitudeFilter;
	}

	public Double getLatitudeFilter() {
		return latitudeFilter;
	}

	public void setLocationFilter(LatLng latLng) {
		this.locationFilter = latLng;
	}

	public double getDistanceFilter() {
		return distanceFilter;
	}

	public void setProfissionais(List<CasaVO> results) {
		this.profissionais = results;
	}
	
	public int getRecordsRetrieveFilter() {
		return recordsRetrieveFilter;
	}
	
	public void setRecordsRetrieveFilter(int recordsRetrieveFilter) {
		this.recordsRetrieveFilter = recordsRetrieveFilter;
	}

}
