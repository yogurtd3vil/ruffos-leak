package ruffos._rinha;

import net.dv8tion.jda.api.entities.Member;

public class Rinha {

	private Member um, dois;
	private int contagemUm, contagemDois;

	public Rinha(Member um, Member dois) {
		this.um = um;
		this.dois = dois;
		this.contagemUm = 0;
		this.contagemDois = 0;
	}

	public void addUm() {
		contagemUm += 1;
	}

	public void addDois() {
		contagemDois += 1;
	}

	public Member getUm() {
		return um;
	}

	public Member getDois() {
		return dois;
	}

	public int getContagemUm() {
		return contagemUm;
	}

	public int getContagemDois() {
		return contagemDois;
	}

}
