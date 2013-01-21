package br.com.mythe.droid.gelib.database.objects;

import android.provider.BaseColumns;
 public class Casas implements  BaseColumns {
    	
        public static final String CONTENT = "/casas";

        public static final String CONTENT_CIDADES = "/casas" + "/cidades";
        
        public static final String CONTENT_ESTADOS =  "/casas" + "/estados";
        
        public static final String CONTENT_PAISES =  "/casas" + "/paises";

        public static final String CONTENT_FAVORITAS = "/casas" + "/favoritos";
        

        public static final String CASA_ID = "_id";
        public static final String COD_CASA = "rem_id";
        public static final String NOME = "entidade";
        public static final String INFORMACOES = "informacoes";
        public static final String ENDERECO = "endereco";
        public static final String BAIRRO = "bairro";
        public static final String CIDADE = "cidade";
        public static final String ESTADO = "estado";
        public static final String CEP = "cep";
        public static final String PAIS = "pais";
        public static final String FONE = "fone";
        public static final String EMAIL = "email";
        public static final String SITE = "site";
        public static final String LAT = "lat";
        public static final String LNG = "lng";
        public static final String TYPE = "type";
        public static final String QTDE = "qtde";
        public static final String USUARIO = "usuario";
        public static final String ATUALIZADO = "atualizado";
        
    }