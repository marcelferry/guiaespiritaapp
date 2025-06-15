package br.com.mythe.droid.gelib.vo;

import java.io.Serializable;
import java.util.List;

public class ListaObjetos<T> implements Serializable {
	private static final long serialVersionUID = -2251881666082662021L;
	public static final String KEY = "lista";
	public List<T> lista;
	public ListaObjetos(List<T> lista) {
		this.lista = lista;
	}
}