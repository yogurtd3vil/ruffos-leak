package ruffos._eventos;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos.ConsoleColors;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;

public class Dirigivel extends ListenerAdapter {

	public static boolean iniciado = false;
	public static Map<User, Integer> users = new HashMap<>();

	public static void start(TextChannel tc) {
		if (!iniciado) {
			iniciado = true;
			System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Iniciando DIRIGÍVEL em: "
					+ ConsoleColors.BRIGHT_CYAN + tc.getGuild().getName() + ConsoleColors.RESET);
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor((ConfigManager.temCfg(tc.getGuild(), "cor")
					? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
					: null));
			eb.setTitle("🌥️ Dirigível La Casa de Papel!");
			eb.setImage("https://i.imgur.com/eYqcG61.png");
			eb.setDescription("Um dirigível acaba de aparecer no " + tc.getAsMention()
					+ "!\nDigite `pegar` (o máximo de vezes que conseguir) para pegar o dinheiro que está caindo.");
			eb.setTimestamp(Instant.now());
			eb.setFooter(tc.getGuild().getName());
			tc.sendMessageEmbeds(eb.build()).queue();
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					String lucros = "";
					for (User u : users.keySet()) {
						EconomiaManager.addDinheiroMaos(tc.getGuild(), u, users.get(u));
						if (ClansManager.hasClan(u)
								&& tc.getGuild().getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
							ClansManager.addFarm(u, users.get(u));
						}
						lucros = lucros + u.getAsMention() + " lucrou **" + Utils.getDinheiro(users.get(u)) + "**.\n";
					}
					iniciado = false;
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor((ConfigManager.temCfg(tc.getGuild(), "cor")
							? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
							: null));
					eb.setFooter(tc.getGuild().getName() + " » O dirigível passa de "
							+ ConfigManager.getConfig(tc.getGuild(), "dirigivelmsgs") + " em "
							+ ConfigManager.getConfig(tc.getGuild(), "dirigivelmsgs") + " mensagem(ns).");
					eb.setTitle("🌥️ Dirigível La Casa de Papel!");
					eb.setImage("https://i.imgur.com/eYqcG61.png");
					eb.setDescription(
							"O dirigível se foi! O dinheiro lucrado por cada usuário foi enviado diretamente para suas mãos, deposite imediatamente no chat do Ruffos com `c!depositar td`.");
					eb.addField("Lucros:", lucros, true);
					eb.setTimestamp(Instant.now());
					tc.sendMessageEmbeds(eb.build()).queue();
					users.clear();
				}
			}, Integer.parseInt(ConfigManager.getConfig(tc.getGuild(), "dirigiveltime")) * 1000);
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!Utils.isIgnoredServer(event.getGuild()) && iniciado
				&& event.getMessage().getContentRaw().equalsIgnoreCase("pegar") && event.isFromType(ChannelType.TEXT)) {
			int i = users.containsKey(event.getAuthor()) ? users.get(event.getAuthor()) : 0;
			users.put(event.getAuthor(), (i + 150));
		}
	}

}
