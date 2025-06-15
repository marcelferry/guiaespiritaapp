package br.com.mythe.droid.gelib.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class GelibUtils {
	public static final int NETWOTK_ERROR_WAIT = 1;

	public static boolean isLite(Context context) {
		return context.getPackageName().toLowerCase().contains("lite");
	}

	public static void abrirDialogGPS(final Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Habilitar Localização");
		alert.setMessage("Você deve ativar os serviços de localização para utilizar essa funcionalidade.");

		alert.setPositiveButton("Habilitar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						context.startActivity(intent);
					}
				});

		alert.setNegativeButton("Agora não",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}
}
