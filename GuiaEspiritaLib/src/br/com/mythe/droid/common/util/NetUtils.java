package br.com.mythe.droid.common.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import br.com.mythe.droid.gelib.exception.GuiaException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetUtils {

	public static HttpResponse conecta(String url)
			throws ClientProtocolException, IOException, GuiaException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse resp;
		resp = client.execute(httpget);
		StatusLine status = resp.getStatusLine();
		if (status.getStatusCode() != 200) {
			Log.d("MapView",
					"HTTP error, invalid server status code: "
							+ resp.getStatusLine());
			throw new GuiaException("HTTP error, invalid server status code: "
					+ resp.getStatusLine());
		} else {
			return resp;
		}
	}

	public static Document getXmlFromService(String caminho)
			throws GuiaException {
		HttpResponse resp;
		try {
			resp = NetUtils.conecta(caminho);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();

			Document doc = builder.parse(resp.getEntity().getContent());
			return doc;
		} catch (ClientProtocolException e) {
			throw new GuiaException(
					"getXmlFromService - Falha ao conectar ao webservice.", e);
		} catch (IOException e) {
			throw new GuiaException(
					"getXmlFromService - Falha ao realizar a leitura do wervice.",
					e);
		} catch (ParserConfigurationException e) {
			throw new GuiaException(
					"getXmlFromService - Falha ao decodificar retorno do webservice",
					e);
		} catch (IllegalStateException e) {
			throw new GuiaException(
					"getXmlFromService - Falha ao instanciar documento xml", e);
		} catch (SAXException e) {
			throw new GuiaException(
					"getXmlFromService - Falha ao instanciar documento xml", e);
		}
	}

	public static String generateGetUrl(String caminho,
			Map<String, String> parametros) throws GuiaException  {
		try{
		Set<String> chaves = parametros.keySet();
		String getUrl = "?";
		boolean not_first = false;
		for (String chave : chaves) {
			if (not_first)
				getUrl += "&";
			getUrl += chave + "=" + URLEncoder.encode(parametros.get(chave), "UTF-8");
			not_first = true;
		}

			return caminho + getUrl;			
		} catch (Exception e) {
			throw new GuiaException("Falha ao codificar a url de conexão.", e);
		}
	}

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm == null){
			return false;
		}
	    NetworkInfo netInfo[] = cm.getAllNetworkInfo();
	    if (netInfo != null){
	    	for(int i = 0; i < netInfo.length; i++){
	    		if(netInfo[i].getState() == NetworkInfo.State.CONNECTED) {
	    			return true;
	    		}
	    	}
	    }
	    return false;
	}

}
