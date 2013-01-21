package br.com.mythe.droid.gelib.vo;

public class FavoritoVO {

	private long id;
	private long casa;
	private long star;
	private String atualizado;
	
	public FavoritoVO() {
	}
	
	
	public FavoritoVO(int id, int casa, int star, String atualizado) {
		this.id = id;
		this.casa = casa;
		this.star = star;
		this.atualizado = atualizado;
	}


	public long getStar() {
		return star;
	}

	public long getId() {
		return id;
	}
	
	public long getCasa() {
		return casa;
	}
	
	public String getAtualizado() {
		return atualizado;
	}

}
