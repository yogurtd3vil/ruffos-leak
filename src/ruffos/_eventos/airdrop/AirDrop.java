package ruffos._eventos.airdrop;

import java.awt.Color;
import java.time.Instant;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;

public class AirDrop {

	public int valor, msgs;
	public long idMSG;

	public void dropar(Guild g) {
		Random r = new Random();
		valor = r.nextInt(10000);
		while (valor < 5000) {
			valor = r.nextInt(10000);
		}
		String[] canais = ConfigManager.getConfig(g, "canal").split(",");
		TextChannel canal = g.getTextChannelById(canais[r.nextInt(canais.length)]);
		while (canal == null) {
			canal = g.getTextChannelById(canais[r.nextInt(canais.length)]);
		}
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("🚁 Air Drop!");
		eb.setDescription(
				"Um Air Drop acaba de cair neste chat!\nPara receber o valor que foi dropado, você deverá ser o primeiro a reagir em 🎁!");
		eb.setFooter(g.getName());
		eb.addField("Valor do Air Drop:", "**" + Utils.getDinheiro(valor) + "**", true);
		eb.setTimestamp(Instant.now());
		eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
		canal.sendMessageEmbeds(eb.build()).queue(m -> {
			idMSG = m.getIdLong();
			m.addReaction(Emoji.fromUnicode("U+1F381")).queue();
		});
	}

}
