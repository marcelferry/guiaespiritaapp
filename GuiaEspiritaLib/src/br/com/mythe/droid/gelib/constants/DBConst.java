package br.com.mythe.droid.gelib.constants;

import br.com.mythe.droid.gelib.database.objects.Casas;
import br.com.mythe.droid.gelib.database.objects.Dicas;
import br.com.mythe.droid.gelib.database.objects.Eventos;
import br.com.mythe.droid.gelib.database.objects.Favoritos;

public interface DBConst {

	public static final String TB_FAVORITOS = "favoritos";
	public static final String TB_CASAS = "casas";
	public static final String TB_EVENTOS = "eventos";
	public static final String TB_DICAS = "dicas";

	public static final String TR_CASASFAVORITOS = TB_CASAS + " c join "
			+ TB_FAVORITOS + " f on c." + Casas.COD_CASA + " = f."
			+ Favoritos.CASA_ID;

	public static final int FAVORITOS = 1;
	public static final int CASAS = 2;
	public static final int ESTADOS = 4;
	public static final int CIDADES = 5;
	public static final int CASASFAVORITAS = 6;
	public static final int PAIS = 7;
	public static final int EVENTOS = 8;
	public static final int DICAS = 9;

	public static String[] PROJ_FAVORITOS = { Favoritos._ID, Favoritos.CASA_ID,
			Favoritos.STAR, Favoritos.ATUALIZADO };

	public static String[] PROJ_CASAS = { Casas._ID, Casas.COD_CASA,
			Casas.NOME, Casas.INFORMACOES, Casas.ENDERECO, Casas.BAIRRO,
			Casas.CIDADE, Casas.ESTADO, Casas.CEP, Casas.PAIS, Casas.FONE,
			Casas.EMAIL, Casas.SITE, Casas.LAT, Casas.LNG, Casas.TYPE,
			Casas.ATUALIZADO };

	public static String[] PROJ_EVENTOS = { Eventos._ID, Eventos.COD_EVT,
			Eventos.CASA_ID, Eventos.COD_CASA, Eventos.EVENTO, Eventos.LOCAL,
			Eventos.DTINI, Eventos.DTFIM, Eventos.TMINI, Eventos.TMFIM,
			Eventos.DINT, Eventos.INFO, Eventos.ATUALIZADO };

	public static String[] PROJ_DICAS = { Dicas._ID, Dicas.COD_DICA,
			Dicas.CASA_ID, Dicas.COD_CASA, Dicas.INFO, Dicas.ATUALIZADO };

}
