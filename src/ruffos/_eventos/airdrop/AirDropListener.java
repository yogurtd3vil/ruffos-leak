package ruffos._eventos.airdrop;

import java.awt.Color;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;

public class AirDropListener extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		Guild g = event.getGuild();
		Member m = event.getMember();
		if (Main.airDrop.containsKey(g) && Main.airDrop.get(g).idMSG == event.getMessageIdLong()
				&& event.getReaction().getEmoji().getName().equals("🎁") && !m.getUser().isBot()
				&& event.isFromType(ChannelType.TEXT)) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🚁 Air Drop!");
			eb.setDescription("Parabéns, " + m.getAsMention() + " foi o primeiro a pegar o Air Drop!");
			eb.setFooter(g.getName());
			eb.addField("Valor do Air Drop:", "**" + Utils.getDinheiro(Main.airDrop.get(g).valor) + "**", true);
			eb.setTimestamp(Instant.now());
			eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
			Message msg = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
			msg.editMessageEmbeds(eb.build()).queue(mm -> mm.removeReaction(Emoji.fromUnicode("U+1F381")).queue());
			Main.airDrop.get(g).idMSG = 0;
		}
	}

}
