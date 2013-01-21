package br.com.mythe.droid.gelib.database.objects;

import android.provider.BaseColumns;

public class Dicas implements  BaseColumns {
	
        public static final String CONTENT = "/dicas";        

		public static final String DICA_ID 		= "_id";
		public static final String COD_DICA 	= "rem_id";
        public static final String CASA_ID 		= "casa_id";
        public static final String COD_CASA 	= "rem_cod_id";
		public static final String INFO 		= "informacoes";
		public static final String USUARIO 		= "usuario";
		public static final String ATUALIZADO 	= "atualizado";
	}
