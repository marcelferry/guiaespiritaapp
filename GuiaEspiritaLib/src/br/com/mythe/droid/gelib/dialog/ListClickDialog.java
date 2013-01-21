package br.com.mythe.droid.gelib.dialog;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import br.com.mythe.droid.gelib.R;
import br.com.mythe.droid.gelib.activities.ItemDetalheActivity;
import br.com.mythe.droid.gelib.activities.ViewInMapActivity;
import br.com.mythe.droid.gelib.database.GuiaEspiritaDB;
import br.com.mythe.droid.gelib.database.objects.Favoritos;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;
import br.com.mythe.droid.gelib.vo.FavoritoVO;

public class ListClickDialog extends AlertDialog {
	
	protected ListClickDialog(Context context, int theme) {
		super(context, theme);
	}
	
	

	public static class Builder extends AlertDialog.Builder {
		private int mIdItem;
		private Context mContext;
		private boolean fave = false;
		private String mSite = null;

		public Builder(Context context, int idItem) {
			this(context, idItem, null);
		}
		
		public Builder(Context context, int idItem, String site) {
			super(context);
			this.mIdItem = idItem;
			this.mContext = context;
			this.mSite = site;
			setFave();
			setTitle("Opções");
			
			setItems(getListItems(), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					boolean isLite = mContext.getPackageName().contains("lite");
					AlertDialog ad = (AlertDialog) dialog;
					switch (which) {
					case 0:
						Intent i = new Intent(ad.getContext(), ItemDetalheActivity.class);
						i.putExtra("id_centro", mIdItem);
						ad.getContext().startActivity(i);
						break;
					case 1:
						i = new Intent(ad.getContext(), ViewInMapActivity.class);
						i.putExtra("id_centro", mIdItem);
						ad.getContext().startActivity(i);
						break;
					case 2:				
						if (fave) {						
					    	mContext.getContentResolver().delete( CasasEspiritasProvider.getContentUri(isLite , Favoritos.CONTENT ), Favoritos.CASA_ID + " = ?" , new String[]{String.valueOf(mIdItem)} );	
						} else {

					    	ContentValues data = new ContentValues();
					    	data.put(Favoritos.CASA_ID, mIdItem); 
					    	data.put(Favoritos.STAR, 5); 
						
					    	mContext.getContentResolver().insert(CasasEspiritasProvider.getContentUri(isLite , Favoritos.CONTENT ), data );	
						}
						break;
					case 3:
						i = new Intent(Intent.ACTION_VIEW, Uri.parse(mSite));
						ad.getContext().startActivity(i);
					}
				}
			});
			setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}

		private void setFave() {

			FavoritoVO vo = GuiaEspiritaDB.getFavoritoByCasa(mContext, new Long(mIdItem));

			if (vo != null ) {
				fave = true;
			}
		}

		private String[] getListItems() {
			ArrayList<String> items = new ArrayList<String>();
			items.add("Detalhes");
			items.add("Mapa");
		
			if (fave) {
				items.add("Remover dos Favoritos");
			} else {
				items.add("Adic. para Favoritos");
			}
			if(mSite != null){
				items.add("Visitar o Site");
			}

			return items.toArray(new String[0]);
		}
		
	}

}