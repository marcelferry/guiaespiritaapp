package br.com.mythe.droid.gelib.database.objects;

import android.provider.BaseColumns;

public class Eventos implements  BaseColumns {
	
        public static final String CONTENT = "/eventos"; 
		
		public static final String EVT_ID 		= "_id";
        public static final String COD_EVT 		= "rem_id";
		public static final String CASA_ID 		= "casa_id";
        public static final String COD_CASA 	= "rem_cod_id";
        public static final String EVENTO 		= "evento";
		public static final String LOCAL 		= "local";
		public static final String DTINI 		= "dt_inicio";
		public static final String DTFIM 		= "dt_fim";
		public static final String TMINI 		= "tm_inicio";
		public static final String TMFIM 		= "tm_fim";
		public static final String DINT 		= "dia_inteiro";
		public static final String INFO 		= "informacoes";
		public static final String USUARIO 		= "usuario";
		public static final String ATUALIZADO 	= "atualizado";
	}