package ruffos._roubos;

import net.dv8tion.jda.api.entities.Member;

public class RouboCofre {

	private int contem, acertos;

	private Member member;

	private String mensagemId, senha;

	public RouboCofre(Member member, int contem, String senha, String mensagemId) {
		this.contem = contem;
		this.senha = senha;
		this.mensagemId = mensagemId;
		this.acertos = 0;
		this.member = member;
	}

	public Member getMember() {
		return member;
	}

	public int getAcertos() {
		return acertos;
	}

	public void addAcerto() {
		acertos++;
	}

	public int getContem() {
		return contem;
	}

	public String getSenha() {
		return senha;
	}

	public String getMensagemId() {
		return mensagemId;
	}

}
