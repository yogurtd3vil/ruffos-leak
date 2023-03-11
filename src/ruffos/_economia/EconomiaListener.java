package ruffos._economia;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos.quitandjoingame.QuitAndJoinManager;
import ruffos.utils.Utils;

public class EconomiaListener extends ListenerAdapter {

	List<User> cooldown = new ArrayList<>();

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Guild g = event.getGuild();
		User u = event.getAuthor();
		if (!Utils.isIgnoredServer(g) && !QuitAndJoinManager.isQuited(g, event.getMember())
				&& event.isFromType(ChannelType.TEXT)) {
			if ((!u.isBot() && !cooldown.contains(u))) {
				TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
				if (ConfigManager.hasChat(g, tc)
						|| event.getChannel().getId().equals(ConfigManager.getConfig(g, "chatgeral"))) {
					if (EconomiaManager.existeUser(g, u)) {
						EconomiaManager.addDinheiroMaos(g, u, 30.0);
					} else {
						EconomiaManager.criarUser(g, u, 30.0);
					}
					if (ClansManager.hasClan(event.getMember().getUser())) {
						ClansManager.addFarm(event.getMember().getUser(), 30);
					}
					cooldown.add(u);
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							cooldown.remove(u);
						}
					}, 60000);
					return;
				}
				if (EconomiaManager.existeUser(g, u)) {
					EconomiaManager.addDinheiroMaos(g, u, 30.0);
				} else {
					EconomiaManager.criarUser(g, u, 30.0);
				}
				if (ClansManager.hasClan(event.getMember().getUser())) {
					ClansManager.addFarm(event.getMember().getUser(), 30);
				}
				cooldown.add(u);
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						cooldown.remove(u);
					}
				}, 60000);
			}
		}
	}

}
