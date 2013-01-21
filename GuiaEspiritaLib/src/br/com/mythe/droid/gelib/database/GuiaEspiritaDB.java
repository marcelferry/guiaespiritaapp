package br.com.mythe.droid.gelib.database;

import android.content.Context;
import android.database.Cursor;
import br.com.mythe.droid.gelib.constants.DBConst;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.database.objects.Favoritos;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;
import br.com.mythe.droid.gelib.vo.CasaVO;
import br.com.mythe.droid.gelib.vo.FavoritoVO;

public class GuiaEspiritaDB implements DBConst {

	public static CasaVO getCasa(Context mContext, String name) {
		String[] args = new String[] { name };
		String where = Casas.COD_CASA + "=?";
		boolean isLite = mContext.getPackageName().contains("lite");
		return cursorToCasa(mContext.getContentResolver().query( CasasEspiritasProvider.getContentUri(isLite ,  Casas.CONTENT ), PROJ_CASAS,
				where, args, null));
	}

	public static CasaVO getCasa(Context mContext, Integer id) {
		boolean isLite = mContext.getPackageName().contains("lite");
		return cursorToCasa(mContext.getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite ,  Casas.CONTENT ), PROJ_CASAS,
				"_id=" + id, null, null));
	}

	public static FavoritoVO getFavoritoById(Context mContext, Integer id) {
		boolean isLite = mContext.getPackageName().contains("lite");
		return cursorToFavorito(mContext.getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite ,  Favoritos.CONTENT ),
				PROJ_FAVORITOS, Favoritos._ID + "=" + id, null, null));
	}

	public static FavoritoVO getFavoritoByCasa(Context mContext, Long id) {
		boolean isLite = mContext.getPackageName().contains("lite");
		return cursorToFavorito(mContext.getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite ,  Favoritos.CONTENT ),
				PROJ_FAVORITOS, Favoritos.CASA_ID + "=\"" + id + "\"",
				null, null));
	}

	public static int getQtdeCasas(Context mContext){

		Cursor cursor = null;
		try{
			String registro[] = new String[] { "COUNT(*) AS QTDE" };
			
			boolean isLite = mContext.getPackageName().contains("lite");
			
			cursor = mContext.getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite ,  Casas.CONTENT ),
					 registro, null,null, null);			
			if(cursor != null && cursor.moveToFirst()){
				return cursor.getInt(0);
			} else {
				return 0;
			}
		} finally{
			if(cursor != null)
				cursor.close();
		}
	
	}

	
	public static String[] getLastTimestamp(Context mContext){

		Cursor cursor = null;
		try{
			String registro[] = new String[] { "MAX(" + Casas.COD_CASA + ") AS ID", "MAX(" + Casas.ATUALIZADO + ") AS ID" };
			
			boolean isLite = mContext.getPackageName().contains("lite");
			
			cursor = mContext.getContentResolver().query(CasasEspiritasProvider.getContentUri(isLite ,  Casas.CONTENT ), registro,
					null,null, null);
			if(cursor != null && cursor.moveToFirst()){
				
				return new String[]{cursor.getString(0),cursor.getString(1)};
			} else {
				return null;
			}
		} finally{
			if(cursor != null)
				cursor.close();
		}
	
	}
	
	// helpers to turn cursors into api data structures

	public static CasaVO cursorToCasa(Cursor c) {
		if (c == null)
			return null;
		try {
			if (c.moveToFirst()) {
				CasaVO casa = new CasaVO();
				casa._id = new Long(c.getString(c
						.getColumnIndex(Casas._ID)));
				casa.id_casa = new Long(c.getString(c
						.getColumnIndex(Casas.COD_CASA)));
				casa.entidade = c.getString(c.getColumnIndex(Casas.NOME));
				casa.info = c.getString(c.getColumnIndex(Casas.INFORMACOES));
				casa.endereco = c.getString(c.getColumnIndex(Casas.ENDERECO));
				casa.bairro = c.getString(c.getColumnIndex(Casas.BAIRRO));
				casa.cidade = c.getString(c.getColumnIndex(Casas.CIDADE));
				casa.estado = c.getString(c.getColumnIndex(Casas.ESTADO));
				casa.cep = c.getString(c.getColumnIndex(Casas.CEP));
				casa.endereco = c.getString(c.getColumnIndex(Casas.FONE));
				casa.email = c.getString(c.getColumnIndex(Casas.EMAIL));
				casa.site = c.getString(c.getColumnIndex(Casas.SITE));
				casa.lat = new Long(c.getString(c.getColumnIndex(Casas.LAT)));
				casa.lng = new Long(c.getString(c.getColumnIndex(Casas.LNG)));
				casa.type = c.getString(c.getColumnIndex(Casas.TYPE));
				casa.atualizado = c.getString(c
						.getColumnIndex(Casas.ATUALIZADO));

				return casa;
			}
		} finally {
			c.close();
		}
		return null;
	}

	public static FavoritoVO cursorToFavorito(Cursor c) {
		if (c == null)
			return null;
		try {
			if (c.moveToFirst()) {
				FavoritoVO favorito = new FavoritoVO(c.getInt(c
						.getColumnIndex(Favoritos.FAV_ID)), c.getInt(c
						.getColumnIndex(Favoritos.CASA_ID)), c.getInt(c
						.getColumnIndex(Favoritos.STAR)), c.getString(c
						.getColumnIndex(Favoritos.ATUALIZADO)));
				return favorito;
			}
		} finally {
			c.close();
		}
		return null;
	}

}
