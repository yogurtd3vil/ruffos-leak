package ruffos._leveis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.quitandjoingame.QuitAndJoinManager;
import ruffos.utils.Utils;

public class LeveisListener extends ListenerAdapter {

	public List<Member> cooldown = new ArrayList<>();
	public Map<Member, Timer> timer = new HashMap<>();

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		User u = event.getAuthor();
		Member m = event.getMember();
		if (!Utils.isIgnoredServer(event.getGuild()) && !u.isBot() && !cooldown.contains(m)
				&& !QuitAndJoinManager.isQuited(m.getGuild(), m) && event.isFromType(ChannelType.TEXT)) {
			if (!ConfigManager.temCfg(event.getGuild(), "canal") || event.getGuild()
					.getTextChannelById(ConfigManager.getConfig(event.getGuild(), "canal").split(",")[0]) == null) {
				System.out.println("Chat não encontrado no servidor: " + event.getGuild().getName());
				return;
			}
			if (ConfigManager.getConfig(event.getGuild(), "canal").split(",")[0] == null) {
				System.out.println("Chat não encontrado no servidor: " + event.getGuild().getName());
				return;
			}
			if (event.getGuild()
					.getTextChannelById(ConfigManager.getConfig(event.getGuild(), "canal").split(",")[0]) == null) {
				System.out.println("Chat não encontrado no servidor: " + event.getGuild().getName());
				return;
			}
			LeveisManager.addXP(
					event.getGuild()
							.getTextChannelById(ConfigManager.getConfig(event.getGuild(), "canal").split(",")[0]),
					m.getId(), u.getName(), 0);
			LeveisEXA.addXP(
					event.getGuild()
							.getTextChannelById(ConfigManager.getConfig(event.getGuild(), "canal").split(",")[0]),
					m.getId(), u.getName(), 0);
			cooldown.add(m);
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					cooldown.remove(m);
				}
			}, 60000);
		}

	}

}
