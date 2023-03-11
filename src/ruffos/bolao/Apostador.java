package ruffos.bolao;

import net.dv8tion.jda.api.entities.Guild;

public class Apostador {

	private String id, aposta;
	private Guild guild;

	public Apostador(String id, String aposta, Guild guild) {
		this.id = id;
		this.aposta = aposta;
		this.guild = guild;
	}

	public Guild getGuild() {
		return guild;
	}

	public String getId() {
		return id;
	}

	public String getAposta() {
		return aposta;
	}

}
