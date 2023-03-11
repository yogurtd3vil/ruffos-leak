package ruffos._blackjack;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos._config.ConfigManager;

public class BlackJackListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message m = event.getMessage();
		User u = event.getAuthor();
		Guild g = event.getGuild();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (event.isFromType(ChannelType.TEXT)) {
			if (m.getContentRaw().equalsIgnoreCase("comprar") && BlackJackCommand.blackjack.containsKey(u)
					&& ConfigManager.hasChat(g, tc)) {
				Blackjack bj = BlackJackCommand.blackjack.get(u);
				if (bj.jogar(g)) {
					BlackJackCommand.blackjack.remove(u, bj);
				} else {
					BlackJackCommand.blackjack.put(u, bj);
				}
			} else if (m.getContentRaw().equalsIgnoreCase("parar") && BlackJackCommand.blackjack.containsKey(u)
					&& ConfigManager.hasChat(g, tc)) {
				Blackjack bj = BlackJackCommand.blackjack.get(u);
				bj.parar(g);
				BlackJackCommand.blackjack.remove(u, bj);
			}
		}
	}

}
