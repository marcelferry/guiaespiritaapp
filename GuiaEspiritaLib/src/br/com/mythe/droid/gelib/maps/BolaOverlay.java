package br.com.mythe.droid.gelib.maps;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BolaOverlay extends Overlay{

	// Constante 
	private int cor;
	private Paint paint = new Paint();
	private GeoPoint geoPoint;
	
	public BolaOverlay(GeoPoint geoPoint, int cor){
		this.cor = cor;
		this.geoPoint = geoPoint;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);
		if(geoPoint != null){
			paint.setColor(cor);
			Point ponto = mapView.getProjection().toPixels(geoPoint, null);
			canvas.drawCircle(ponto.x, ponto.y, 5, paint);
		}
	}
	
	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
	
	
}
