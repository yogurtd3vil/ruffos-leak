package ruffos._roubos;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;

public class Roubo {

	private User roubou, roubado;
	private Timer timer;
	private int valorRoubado;
	private TextChannel textChannel;

	public Roubo(TextChannel textChannel, User roubou, User roubado, int valorRoubado) {
		this.roubou = roubou;
		this.roubado = roubado;
		this.valorRoubado = valorRoubado;
		this.textChannel = textChannel;
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (RoubarCommand.reagir.containsKey(roubou) && (RoubarCommand.reagir.get(roubou).roubado == roubado)) {
					RoubarCommand.reagir.remove(roubou);
					Utils.enviarEmbed(textChannel, roubou, null,
							"O roubo de **" + Utils.getDinheiro(valorRoubado) + "** em " + roubado.getAsMention()
									+ " não houve reação. Todo este dinheiro foi para sua conta sem problemas!",
							((ConfigManager.temCfg(textChannel.getGuild(), "cor")
									? Color.decode(ConfigManager.getConfig(textChannel.getGuild(), "cor"))
									: null)),
							textChannel.getGuild().getName()
									+ " » Para ver quem mais roubou nas últimas horas ou dias: c!roubos",
							null, null, null, 0, true);
				}
			}
		}, 5000);
	}

	public User getRoubou() {
		return roubou;
	}

	public User getRoubado() {
		return roubado;
	}

	public Timer getTimer() {
		return timer;
	}

	public int getValorRoubado() {
		return valorRoubado;
	}

	public TextChannel getTextChannel() {
		return textChannel;
	}

}
