package br.com.mythe.droid.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class Utils {
	
	public static final Pattern EMAIL_ADDRESS = Pattern.compile(
			"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
			"\\@" +
			"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
			"(" +
				"\\." +
				"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
			")+" 
			);
	
	private static Map<String,String> estados = new HashMap<String,String>();
	static{
		estados.put("AC","Acre");
		estados.put("AL", "Alagoas");
		estados.put("AP", "Amap�");
		estados.put("AM", "Amazonas");
		estados.put("BA", "Bahia");
		estados.put("CE", "Cear�");
		estados.put("DF", "Distrito Federal");
		estados.put("ES", "Esp�rito Santo");
		estados.put("GO", "Goi�s");
		estados.put("MA", "Maranh�o");
		estados.put("MT", "Mato Grosso");
		estados.put("MS", "Mato Grosso do Sul");
		estados.put("MG", "Minas Gerais");
		estados.put("PR", "Paran�");
		estados.put("PB", "Para�ba");
		estados.put("PA", "Par�");
		estados.put("PE", "Pernambuco");
		estados.put("PI", "Piau�");
		estados.put("RJ", "Rio de Janeiro");
		estados.put("RN", "Rio Grande do Norte");
		estados.put("RS", "Rio Grande do Sul");
		estados.put("RO", "Rond�nia");
		estados.put("RR", "Roraima");
		estados.put("SC", "Santa Catarina");
		estados.put("SE", "Sergipe");
		estados.put("SP", "S�o Paulo");
		estados.put("TO", "Tocantins");

	}
	

	public static String getEstado(String sigla){
		return estados.get(sigla);
	}
	
	public static String getEstadoSigla(String estado){
		Iterator<String> chaves = estados.keySet().iterator();
		while(chaves.hasNext()){
			String chave = chaves.next();
			if(estado.equals( estados.get(chave) ) ){
				return chave;
			}
		}
		return "";
	}
	
	public static String criptografaSenha (String senha) throws NoSuchAlgorithmException {  
        MessageDigest md = MessageDigest.getInstance("MD5");  
        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));  
        String s = hash.toString(16);  
        if (s.length() %2 != 0)  
            s = "0" + s;  
        return s;  
    }  
	
	public static boolean validarEmail(String email){
		return EMAIL_ADDRESS.matcher(email).matches();
	}
	
	public static void alertDialog(final Context context, final int mensagem, int appName) {
        try {
                AlertDialog dialog = new AlertDialog.Builder(context).setTitle(
                                context.getString(appName)).setMessage(mensagem).create();
                dialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                return;
                        }
                });
                dialog.show();
        } catch (Exception e) {
                Log.e("GELib", e.getMessage(), e);
        }
	}
	public static void alertDialog(final Context context, final String mensagem, int appName) {
        try {
                AlertDialog dialog = new AlertDialog.Builder(context).setTitle(
                                context.getString(appName)).setMessage(mensagem)
                                .create();
                dialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                                return;
                        }
                });
                dialog.show();
        } catch (Exception e) {
                Log.e("GELib", e.getMessage(), e);
        }
	}
	
	
}
