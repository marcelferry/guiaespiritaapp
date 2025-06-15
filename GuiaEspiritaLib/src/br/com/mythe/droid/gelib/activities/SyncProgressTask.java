package br.com.mythe.droid.gelib.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jredfoot.sophielib.exception.NegocioException;
import org.jredfoot.sophielib.util.NetUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import br.com.mythe.droid.gelib.constants.GEConst;
import br.com.mythe.droid.gelib.database.GuiaEspiritaDB;
import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.provider.CasasEspiritasProvider;
import br.com.mythe.droid.gelib.util.GelibUtils;

public class SyncProgressTask extends AsyncTask<Boolean, Integer, Boolean> {
	
		private Context _context;
		private Handler _handler;
		private ProgressDialog pdia;
		private List<String> casasInseridas = new ArrayList<String>();
	
		public SyncProgressTask(Context context, Handler handler) {
			_context = context;
			_handler = handler;
		}
	
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia = new ProgressDialog(_context);
		        pdia.setMessage("Carregando dados...");
		        pdia.show();    
		}
		
		@Override
		protected Boolean doInBackground(Boolean... params) {
			try{
				boolean reset = params[0];
				
				Document doc;
				
				if(reset){
					doc = NetUtils.getXmlFromService(GEConst.LIST_ENTIDADES_URI);
				} else {
					String update[] = GuiaEspiritaDB.getLastTimestamp(_context);
					Map<String, String> parametros = new HashMap<String, String>();
					parametros.put("last_id", update[0]);
					parametros.put("update", update[1]);
					String caminho = NetUtils.generateGetUrl(GEConst.LIST_ENTIDADES_URI, parametros);
					doc = NetUtils.getXmlFromService(caminho);			
				}
	
				int casasSize = doc.getElementsByTagName("entidade").getLength();
	
				if (reset) {
					if (casasSize > 0) {
						_context.getContentResolver().delete(CasasEspiritasProvider.getContentUri(GelibUtils.isLite(_context) , Casas.CONTENT ), null, null);
					}
				} 
	
				for (int i = 0; i < casasSize; i++) {
					Element casa = (Element) doc.getElementsByTagName("entidade").item(
							i);
	
					ContentValues data = new ContentValues();
					data.put(Casas.COD_CASA, casa.getAttribute("codigo"));
					data.put(Casas.NOME, casa.getAttribute("nome"));
					data.put(Casas.ENDERECO, casa.getAttribute("endereco"));
					data.put(Casas.BAIRRO, casa.getAttribute("bairro"));
					data.put(Casas.CIDADE, casa.getAttribute("cidade"));
					data.put(Casas.ESTADO, casa.getAttribute("estado"));
					data.put(Casas.PAIS, casa.getAttribute("pais"));
					data.put(Casas.CEP, casa.getAttribute("cep"));
					data.put(Casas.FONE, casa.getAttribute("fone"));
					data.put(Casas.EMAIL, casa.getAttribute("email"));
					data.put(Casas.SITE, casa.getAttribute("site"));
					data.put(Casas.LAT, Double.valueOf(casa.getAttribute("lat")));
					data.put(Casas.LNG, Double.valueOf(casa.getAttribute("lng")));
					data.put(Casas.INFORMACOES, casa.getAttribute("info"));
					data.put(Casas.ATUALIZADO, casa.getAttribute("atualizado"));
					data.put(Casas.TYPE, casa.getAttribute("tipo"));
	
					if(reset){
						_context.getContentResolver().insert( CasasEspiritasProvider.getContentUri(GelibUtils.isLite(_context) , Casas.CONTENT ), data);
					} else {
					
						if(casa.getAttribute("status").equals("update")){
							_context.getContentResolver().update( CasasEspiritasProvider.getContentUri(GelibUtils.isLite(_context) , Casas.CONTENT ), data,"rem_id = ?", new String[]{casa.getAttribute("codigo")});
						}
						
						if(casa.getAttribute("status").equals("new")){
							_context.getContentResolver().insert( CasasEspiritasProvider.getContentUri(GelibUtils.isLite(_context) , Casas.CONTENT ), data);					
						}
						
						String nome = casa.getAttribute("entidade") + " - " + casa.getAttribute("cidade") + "/" + casa.getAttribute("estado"); 
						casasInseridas.add(nome);
					}
	
				}
				
				return Boolean.TRUE;
			} catch (NegocioException e) {
				e.printStackTrace();
				Log.e("Guia Espirita", e.getMessage());
				if(_handler != null){
					Message m = Message.obtain(_handler, GelibUtils.NETWOTK_ERROR_WAIT);
					_handler.sendMessage(m);
				}
			}
			return Boolean.FALSE;
		}


		@Override
		protected void onPostExecute(Boolean result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   
		   if(casasInseridas.size() > 0){
			   AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(_context);
			   
				// Setting Dialog Title
				mAlertDialog.setTitle("Casas Novas");
				
				String mensagem = "";
				
				for (String casa : casasInseridas) {
					mensagem += casa + "\r\n";
				}
				
				// Setting Dialog Message
				mAlertDialog.setMessage(mensagem);
				
				// Setting Icon to Dialog
				mAlertDialog.setIcon(android.R.drawable.checkbox_on_background);
				
				// Setting OK Button
				mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				    	
				    }
				});
				
				mAlertDialog.show();
				
		   }
		   
		}
	}