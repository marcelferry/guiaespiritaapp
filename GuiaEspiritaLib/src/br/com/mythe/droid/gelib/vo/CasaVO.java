package br.com.mythe.droid.gelib.vo;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class CasaVO implements ClusterItem {
	
	public long _id;
	public long id_casa;
	public String entidade;
	public String endereco;
	public String bairro;
	public String cidade;
	public String estado;
	public String cep;
	public String fone;
	public String email;
	public String site;
	public Double lat;
	public Double lng;
	public String info;
	public String type;
	public String distance;
	public String atualizado;
	public Bitmap image;
	
	@Override
	public LatLng getPosition() {
		return new LatLng(lat, lng);
	}
}
