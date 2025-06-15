package br.com.mythe.droid.gelib.provider;

import java.util.HashMap;

import br.com.mythe.droid.gelib.constants.DBConst;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.database.objects.Dicas;
import br.com.mythe.droid.gelib.database.objects.Eventos;
import br.com.mythe.droid.gelib.database.objects.Favoritos;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public abstract class CasasEspiritasProvider extends ContentProvider implements DBConst  {

	public static final String AUTHORITY = 
	        "br.com.mythe.droid.gelib.provider.CasasEspiritasProvider";

	protected static  final String DATABASE_NAME = "guiaespirita.db";

	protected static  final int  DATABASE_VERSION = 2;  
	
	// Tag usada para imprimir os logs.
	public static final String TAG = "CasasEspiritasProvider";

	// Instância da classe utilitária
	protected DBHelper mHelper;

	// Uri matcher - usado para extrair informações das Uris
    protected static UriMatcher mMatcher;

	/////////////////////////////////////////////////////////////////
	//           Métodos overrided de ContentProvider              //
	/////////////////////////////////////////////////////////////////
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count;
        switch (mMatcher.match(uri)) {
        	case CASAS:
                count = db.delete(TB_CASAS, selection, selectionArgs);
                break;
        	case FAVORITOS:
        		count = db.delete(TB_FAVORITOS, selection, selectionArgs);
        		break;	
        	case EVENTOS:
        		count = db.delete(TB_EVENTOS, selection, selectionArgs);
        		break;
        	case DICAS:
        		count = db.delete(TB_DICAS, selection, selectionArgs);
        		break;        	
            default:
                throw new IllegalArgumentException(
                  "URI desconhecida " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
	        switch (mMatcher.match(uri)) {
	            case CASAS:
	                return getContentType( Casas.CONTENT );
	            case FAVORITOS:
	            	return getContentType( Favoritos.CONTENT );
	            case EVENTOS:
	            	return getContentType( Eventos.CONTENT );
	            case DICAS:
	            	return getContentType( Dicas.CONTENT );
	            default:
	                throw new IllegalArgumentException(
                        "URI desconhecida " + uri);
	        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db;
		long rowId;
		switch (mMatcher.match(uri)) {
	    	case CASAS:
		        db = mHelper.getWritableDatabase();
		        rowId = db.insert(TB_CASAS, Casas.COD_CASA, values);
		        if (rowId > 0) {
		            Uri noteUri = ContentUris.withAppendedId(
		            			getContentUri( Casas.CONTENT ), rowId);
		            getContext().getContentResolver().notifyChange(
                                 noteUri, null);
		            return noteUri;
		        }
	    	case FAVORITOS:
		        db = mHelper.getWritableDatabase();
		        rowId = db.insert(TB_FAVORITOS, Casas.COD_CASA, values);
		        if (rowId > 0) {
		            Uri noteUri = ContentUris.withAppendedId(
		            			getContentUri( Favoritos.CONTENT ), rowId);
		            getContext().getContentResolver().notifyChange(
                                 noteUri, null);
		            return noteUri;
		        }
	    	case EVENTOS:
		        db = mHelper.getWritableDatabase();
		        rowId = db.insert(TB_EVENTOS, Eventos.COD_EVT, values);
		        if (rowId > 0) {
		            Uri noteUri = ContentUris.withAppendedId(
		            			getContentUri( Eventos.CONTENT ), rowId);
		            getContext().getContentResolver().notifyChange(
                                 noteUri, null);
		            return noteUri;
		        }
	    	case DICAS:
		        db = mHelper.getWritableDatabase();
		        rowId = db.insert(TB_DICAS, Dicas.COD_CASA, values);
		        if (rowId > 0) {
		            Uri noteUri = ContentUris.withAppendedId(
                                 getContentUri( Dicas.CONTENT ), rowId);
		            getContext().getContentResolver().notifyChange(
                                 noteUri, null);
		            return noteUri;
		        }
	    	default:
	            throw new IllegalArgumentException(
                        "URI desconhecida " + uri);
        }
	}
	
	@Override
	public boolean onCreate() {
		mHelper = new DBHelper(getContext());;
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
	        SQLiteQueryBuilder builder = new  SQLiteQueryBuilder();
	        SQLiteDatabase database = mHelper.getReadableDatabase();
	        String groupBy = null;
	        
	        Cursor cursor;
	        switch (mMatcher.match(uri)) {
	            case CASAS:
	                builder.setTables(TB_CASAS);
	                HashMap<String, String> mProjection = new HashMap<String, String>();
	        		mProjection.put(Casas.CASA_ID, Casas.CASA_ID);
	        		mProjection.put(Casas.COD_CASA, Casas.COD_CASA); 
	        		mProjection.put(Casas.NOME, Casas.NOME); 
	        		mProjection.put(Casas.INFORMACOES, Casas.INFORMACOES ); 
	        		mProjection.put(Casas.ENDERECO, Casas.ENDERECO ); 
	        		mProjection.put(Casas.BAIRRO, Casas.BAIRRO ); 
	        		mProjection.put(Casas.CIDADE, Casas.CIDADE ); 
	        		mProjection.put(Casas.ESTADO, Casas.ESTADO ); 
	        		mProjection.put(Casas.CEP, Casas.CEP ); 
	        		mProjection.put(Casas.PAIS, Casas.PAIS ); 		        		
	        		mProjection.put(Casas.FONE, Casas.FONE ); 
	        		mProjection.put(Casas.EMAIL, Casas.EMAIL ); 
	        		mProjection.put(Casas.SITE, Casas.SITE );
	        		mProjection.put(Casas.LAT, Casas.LAT ); 
	        		mProjection.put(Casas.LNG, Casas.LNG); 
	        		mProjection.put(Casas.TYPE, Casas.TYPE); 
	        		mProjection.put(Casas.USUARIO, Casas.USUARIO); 
	        		mProjection.put(Casas.ATUALIZADO, Casas.ATUALIZADO); 
	                builder.setProjectionMap(mProjection);
	                break;
	                
	            case CIDADES:
	                builder.setTables(TB_CASAS);
	                HashMap<String, String> mCidProjection = new HashMap<String, String>();
	                mCidProjection.put(Casas._ID, Casas._ID);
	                mCidProjection.put(Casas.CIDADE, Casas.CIDADE);
	                mCidProjection.put(Casas.ESTADO, Casas.ESTADO);
	                mCidProjection.put(Casas.QTDE, "COUNT(*) AS " + Casas.QTDE  );
	                groupBy = Casas.ESTADO + "," + Casas.CIDADE  ;
	                builder.setProjectionMap(mCidProjection);
	                break;

	            case ESTADOS:
	                builder.setTables(TB_CASAS);
	                HashMap<String, String> mUfProjection = new HashMap<String, String>();
	                mUfProjection.put(Casas._ID, Casas._ID);
	                mUfProjection.put(Casas.ESTADO, Casas.ESTADO);
	                mUfProjection.put(Casas.QTDE, "COUNT(*) AS " + Casas.QTDE  );
	                groupBy = Casas.ESTADO  ;
	                builder.setProjectionMap(mUfProjection);
	                break;
	                
	            case PAIS:
	                builder.setTables(TB_CASAS);
	                HashMap<String, String> mCoProjection = new HashMap<String, String>();
	                mCoProjection.put(Casas._ID, Casas._ID);
	                mCoProjection.put(Casas.PAIS, Casas.PAIS);
	                mCoProjection.put(Casas.QTDE, "COUNT(*) AS " + Casas.QTDE  );
	                groupBy = Casas.PAIS  ;
	                builder.setProjectionMap(mCoProjection);
	                break;	            
	                
	            case CASASFAVORITAS:
	                builder.setTables(TR_CASASFAVORITOS);
	                HashMap<String, String> mCFProjection = new HashMap<String, String>();
	                mCFProjection.put(Casas.CASA_ID, "c." + Casas.CASA_ID);
	                mCFProjection.put(Casas.COD_CASA, Casas.COD_CASA); 
	                mCFProjection.put(Casas.NOME, Casas.NOME); 
	                mCFProjection.put(Casas.INFORMACOES, Casas.INFORMACOES ); 
	                mCFProjection.put(Casas.ENDERECO, Casas.ENDERECO ); 
	                mCFProjection.put(Casas.BAIRRO, Casas.BAIRRO ); 
	        		mCFProjection.put(Casas.CIDADE, Casas.CIDADE ); 
	        		mCFProjection.put(Casas.ESTADO, Casas.ESTADO ); 
	        		mCFProjection.put(Casas.CEP, Casas.CEP ); 
	        		mCFProjection.put(Casas.PAIS, Casas.PAIS ); 	        		
	        		mCFProjection.put(Casas.FONE, Casas.FONE ); 
	        		mCFProjection.put(Casas.EMAIL, Casas.EMAIL ); 
	        		mCFProjection.put(Casas.SITE, Casas.SITE );
	        		mCFProjection.put(Casas.LAT, Casas.LAT ); 
	        		mCFProjection.put(Casas.LNG, Casas.LNG); 
	        		mCFProjection.put(Casas.TYPE, Casas.TYPE); 
	        		mCFProjection.put(Casas.USUARIO, Casas.USUARIO); 
	        		mCFProjection.put(Casas.ATUALIZADO, "c." + Casas.ATUALIZADO); 
	                builder.setProjectionMap(mCFProjection);
	                break;
	                
	            case FAVORITOS:
	                builder.setTables(TB_FAVORITOS);
	                HashMap<String, String> mFavProjection = new HashMap<String, String>();
	                mFavProjection.put(Favoritos._ID, Favoritos._ID);
	                mFavProjection.put(Favoritos.CASA_ID, Favoritos.CASA_ID);
	                mFavProjection.put(Favoritos.STAR , Favoritos.STAR );
	                mFavProjection.put(Favoritos.ATUALIZADO, Favoritos.ATUALIZADO);
	                builder.setProjectionMap(mFavProjection);
	                break;
	            case EVENTOS:
	                builder.setTables(TB_EVENTOS);
	                HashMap<String, String> mEvtProjection = new HashMap<String, String>();
	        		mEvtProjection.put(Eventos._ID, Eventos._ID );
	                mEvtProjection.put(Eventos.COD_EVT, Eventos.COD_EVT );
	        		mEvtProjection.put(Eventos.CASA_ID, Eventos.CASA_ID );
	                mEvtProjection.put(Eventos.COD_CASA, Eventos.COD_CASA );
	                mEvtProjection.put(Eventos.EVENTO, Eventos.EVENTO );
	        		mEvtProjection.put(Eventos.LOCAL, Eventos.LOCAL );
	        		mEvtProjection.put(Eventos.DTINI, Eventos.DTINI );
	        		mEvtProjection.put(Eventos.DTFIM, Eventos.DTFIM );
	        		mEvtProjection.put(Eventos.TMINI, Eventos.TMINI );
	        		mEvtProjection.put(Eventos.TMFIM, Eventos.TMFIM );
	        		mEvtProjection.put(Eventos.DINT, Eventos.DINT );
	        		mEvtProjection.put(Eventos.INFO, Eventos.INFO );
	        		mEvtProjection.put(Eventos.USUARIO, Eventos.USUARIO );
	        		mEvtProjection.put(Eventos.ATUALIZADO, Eventos.ATUALIZADO );
	                builder.setProjectionMap(mEvtProjection);
	                break;
	            case DICAS:
	                builder.setTables(TB_DICAS);
	                HashMap<String, String> mDicProjection = new HashMap<String, String>();
	                mDicProjection.put(Dicas._ID, Dicas._ID);
	                mDicProjection.put(Dicas.DICA_ID, Dicas.DICA_ID);
	                mDicProjection.put(Dicas.CASA_ID, Dicas.CASA_ID);
	                mDicProjection.put(Dicas.COD_CASA, Dicas.COD_CASA);
	                mDicProjection.put(Dicas.INFO , Dicas.INFO );
	                mDicProjection.put(Dicas.USUARIO , Dicas.USUARIO );
	                mDicProjection.put(Dicas.ATUALIZADO, Dicas.ATUALIZADO);
	                builder.setProjectionMap(mDicProjection);
	                break;

	            default:
	                throw new IllegalArgumentException(
                          "URI desconhecida " + uri);
	        }

	        cursor = builder.query(database, projection, selection,
	         selectionArgs, groupBy, null, sortOrder);

	        cursor.setNotificationUri(getContext().getContentResolver(), uri);
	        return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
	        SQLiteDatabase db = mHelper.getWritableDatabase();
	        int count;
	        switch (mMatcher.match(uri)) {
	            case CASAS:
	                count = db.update(TB_CASAS, values,
                                                     selection, selectionArgs);
	                break;
	            case FAVORITOS:
	                count = db.update(TB_FAVORITOS, values,
                                                     selection, selectionArgs);
	                break;
	            case EVENTOS:
	                count = db.update(TB_EVENTOS, values,
                                                     selection, selectionArgs);
	                break;
	            case DICAS:
	                count = db.update(TB_DICAS, values,
                                                     selection, selectionArgs);
	                break;
	            default:
	                throw new IllegalArgumentException(
                            "URI desconhecida " + uri);
	        }

	        getContext().getContentResolver().notifyChange(uri, null);
	        return count;
	}
	
    public Uri getContentUri(String raiz){
    	return Uri.parse("content://" 
                + AUTHORITY + raiz);            	
    }
    
    public static Uri getContentUri(boolean isLite, String raiz){
    	if(isLite){
        	return Uri.parse("content://" 
                    + CasasEspiritasProviderLite.AUTHORITY + raiz);
    	} else {
        	return Uri.parse("content://" 
                    + CasasEspiritasProviderFull.AUTHORITY + raiz);
    	}
    	

    }
    
    public String getContentType(String raiz){
    	return
            "vnd.android.cursor.dir/" + AUTHORITY + raiz;
    }


    private static class DBHelper extends SQLiteOpenHelper {

    	
        private static final String CREATE_FAVORITOS = "CREATE TABLE " + TB_FAVORITOS + "(" +
                Favoritos.FAV_ID       + " INTEGER PRIMARY KEY," +
                Favoritos.CASA_ID      + " INTEGER," +
                Favoritos.STAR         + " INTEGER," +
                Favoritos.ATUALIZADO   + " TIMESTAMP" +
            ")";

        private static final String CREATE_CASAS = "CREATE TABLE " + TB_CASAS + " (" +
				Casas.CASA_ID 		+ " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				Casas.COD_CASA		+ " INTEGER," +
				Casas.NOME 			+ " TEXT," + 
				Casas.INFORMACOES 	+ " LONGTEXT," + 
				Casas.ENDERECO		+ " TEXT," + 
				Casas.BAIRRO 		+ " TEXT," + 
				Casas.CIDADE 		+ " TEXT," + 
				Casas.ESTADO 		+ " TEXT," + 
				Casas.CEP 			+ " TEXT," + 
				Casas.PAIS 			+ " TEXT," + 				
				Casas.FONE 			+ " TEXT," + 
				Casas.EMAIL 		+ " TEXT," + 
				Casas.SITE 			+ " TEXT," + 
				Casas.LAT 			+ " REAL," + 
				Casas.LNG 			+ " REAL," + 
				Casas.USUARIO		+ " TEXT, " +  
				Casas.TYPE			+ " TEXT," + 
				Casas.ATUALIZADO 	+ " TIMESTAMP" +
			")";
        
        private static final String CREATE_EVENTOS = "CREATE TABLE " + TB_EVENTOS + " (" +
        		Eventos.EVT_ID 		+ " integer primary key autoincrement, " +
        		Eventos.COD_EVT 	+ " INTEGER not null, " +
        		Eventos.CASA_ID 	+ " INTEGER not null, " +
        		Eventos.COD_CASA	+ " INTEGER not null, " +
        		Eventos.EVENTO 		+ " TEXT not null, " +
        		Eventos.LOCAL 		+ " TEXT not null, " +
        		Eventos.DTINI 		+ " DATE, " +
        		Eventos.DTFIM 		+ " DATE, " + 
        		Eventos.TMINI 		+ " TIME, " +
        		Eventos.TMFIM 		+ " TIME, " + 
        		Eventos.DINT 		+ " BOOLEAN, " + 
        		Eventos.INFO 		+ " LONGTEXT, " +  
        		Eventos.USUARIO		+ " TEXT, " +  
        		Eventos.ATUALIZADO  + " TIMESTAMP" +
            ")";
        
        private static final String CREATE_DICAS = "CREATE TABLE " + TB_DICAS + " (" +
        		Dicas.DICA_ID 		+ " integer primary key autoincrement, " +
        		Dicas.COD_DICA	 	+ " INTEGER not null, " +
        		Dicas.CASA_ID 		+ " INTEGER not null, " +
        		Dicas.COD_CASA		+ " INTEGER not null, " +
        		Dicas.INFO 			+ " LONGTEXT, " + 
        		Dicas.USUARIO		+ " TEXT, " +          		
        		Dicas.ATUALIZADO  	+ " TIMESTAMP" +
            ")";

        private static final String DELETE = "DROP TABLE IF EXISTS ";    	
    	
        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate( SQLiteDatabase db )
        {
            db.execSQL(CREATE_FAVORITOS);
            db.execSQL(CREATE_CASAS);
            db.execSQL(CREATE_EVENTOS);
            db.execSQL(CREATE_DICAS);

        }

        @Override
        public void onUpgrade( SQLiteDatabase db, int oldV, int newV )
        {
            db.execSQL(DELETE + TB_FAVORITOS);
            db.execSQL(DELETE + TB_CASAS);
            db.execSQL(DELETE + TB_EVENTOS);
            db.execSQL(DELETE + TB_DICAS);
            onCreate( db );
        }

        public SQLiteDatabase getWritableDatabase()
        { 
            return super.getWritableDatabase();
        }
    }
}
