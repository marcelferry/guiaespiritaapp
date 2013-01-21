package br.com.mythe.droid.gelib.maps;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CasasEspiritasOverlay extends ItemizedOverlay<OverlayItem> {

	private final List<OverlayItem> casas;
	private final Context context;
	
	public CasasEspiritasOverlay(Context context, List<OverlayItem> casas, Drawable drawable) {
		super(drawable);
		this.context = context;
		this.casas = casas;
		boundCenterBottom(drawable);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return casas.get(i);
	}

	@Override
	public int size() {
		return casas.size();
	}
	
	@Override
	protected boolean onTap(int i) {
		OverlayItem overlayItem = casas.get(i);
		String texto = overlayItem.getTitle() + " - " + overlayItem.getSnippet();
		Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
		return true;
	}
}
