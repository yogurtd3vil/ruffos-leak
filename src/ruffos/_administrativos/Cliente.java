package ruffos._administrativos;

public class Cliente {

	private String nome, serverId, adicoes;
	private long expiracao;
	private int preco;

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public void setAdicoes(String adicoes) {
		this.adicoes = adicoes;
	}

	public void setExpiracao(long expiracao) {
		this.expiracao = expiracao;
	}

	public void setPreco(int preco) {
		this.preco = preco;
	}

	public String getNome() {
		return nome;
	}

	public String getServerId() {
		return serverId;
	}

	public String getAdicoes() {
		return adicoes;
	}

	public long getExpiracao() {
		return expiracao;
	}

	public int getPreco() {
		return preco;
	}

}
