package ruffos._eventos.porquinho;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;

public class PorquinhoListener extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		Guild g = event.getGuild();
		User u = event.getUser();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (!Utils.isIgnoredServer(g) && !u.isBot() && Main.porquinhos.containsKey(g)
				&& event.isFromType(ChannelType.TEXT)) {
			if (Main.porquinhos.get(g).iniciado && event.getMessageIdLong() == Main.porquinhos.get(g).messageId
					&& ConfigManager.hasChat(g, tc)) {
				if (event.getReaction().getEmoji().getName().equals("porcolouco_ruffos")) {
					Main.porquinhos.get(g).addParticipante(u, "porco1");
				} else if (event.getReaction().getEmoji().getName().equalsIgnoreCase("porcotarado_ruffos")) {
					Main.porquinhos.get(g).addParticipante(u, "porco2");
				} else if (event.getReaction().getEmoji().getName().equalsIgnoreCase("porcosafado_ruffos")) {
					Main.porquinhos.get(g).addParticipante(u, "porco3");
				} else if (event.getReaction().getEmoji().getName().equalsIgnoreCase("porcoinstagrammer_ruffos")) {
					Main.porquinhos.get(g).addParticipante(u, "porco4");
				} else if (event.getReaction().getEmoji().getName().equalsIgnoreCase("porcofofo_ruffos")) {
					Main.porquinhos.get(g).addParticipante(u, "porco5");
				} else if (event.getReaction().getEmoji().getName().equalsIgnoreCase("porcofaminto_ruffos")) {
					Main.porquinhos.get(g).addParticipante(u, "porco6");
				} else if (event.getReaction().getEmoji().getName().equalsIgnoreCase("porcodancarino_ruffos")) {
					Main.porquinhos.get(g).addParticipante(u, "porco7");
				}
			}
		}
	}

}
