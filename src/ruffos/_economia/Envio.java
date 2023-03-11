package ruffos._economia;

import net.dv8tion.jda.api.entities.User;

public class Envio {

	private String messageId, userId;
	User userEnviar = null;
	private double quantidade;

	public Envio(String messageId, String userId, User userEnviar, double quantidade) {
		this.messageId = messageId;
		this.userId = userId;
		this.userEnviar = userEnviar;
		this.quantidade = quantidade;
	}

	public String getUserId() {
		return userId;
	}

	public String getMessageId() {
		return messageId;
	}

	public User getUserEnviar() {
		return userEnviar;
	}

	public double getQuantidade() {
		return quantidade;
	}

}
