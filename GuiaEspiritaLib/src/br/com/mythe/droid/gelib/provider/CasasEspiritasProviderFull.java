package br.com.mythe.droid.gelib.provider;

import android.content.UriMatcher;


public class CasasEspiritasProviderFull extends CasasEspiritasProvider {

	public static final String AUTHORITY =
        "br.com.mythe.droid.gelib.provider.CasasEspiritasProviderFull";

	protected static  final String DATABASE_NAME = "guiaespirita.db";

	protected static  final int  DATABASE_VERSION = 2;   
	
	static { 
    	mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mMatcher.addURI(AUTHORITY, TB_CASAS, CASAS);
		mMatcher.addURI(AUTHORITY, TB_CASAS + "/cidades",   CIDADES);
		mMatcher.addURI(AUTHORITY, TB_CASAS + "/estados",   ESTADOS);
		mMatcher.addURI(AUTHORITY, TB_CASAS + "/paises",   PAIS);
		mMatcher.addURI(AUTHORITY, TB_CASAS + "/favoritos", CASASFAVORITAS);
		mMatcher.addURI(AUTHORITY, TB_FAVORITOS, FAVORITOS);
		mMatcher.addURI(AUTHORITY, TB_EVENTOS, EVENTOS);
		mMatcher.addURI(AUTHORITY, TB_DICAS, DICAS);


    }
	
    
}