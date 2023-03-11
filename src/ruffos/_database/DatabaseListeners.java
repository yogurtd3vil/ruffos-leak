package ruffos._database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.commands.CommandManager;

public class DatabaseListeners extends ListenerAdapter {

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		Guild g = event.getGuild();
		Main.getDatabase().criarGuild(g);
		ConfigManager.configurar(g, "cor", "#FF0000");
		ConfigManager.configurar(g, "dirigivelmsgs", "1500");
		ConfigManager.configurar(g, "dirigiveltime", "15");
		ConfigManager.configurar(g, "crime", "Você cometeu um crime e faturou **@valor**!");
		ConfigManager.configurar(g, "clans", "1");
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		// Main.getDatabase().deletarGuild(event.getGuild());
	}

	/**
	 * @Override public void onGuildVoiceMove(GuildVoiceMoveEvent event) { if
	 *           (event.getChannelJoined().getId().equals("687012677654020172") &&
	 *           !event.getMember().getId().equals("380570412314001410") &&
	 *           !event.getMember().getId().equals("419637416349925388")) {
	 *           event.getMember().deafen(true).queue(); event.getGuild()
	 *           .moveVoiceMember(event.getMember(),
	 *           event.getGuild().getVoiceChannelById("680885685888221326"))
	 *           .queue();
	 *           event.getGuild().getMemberById("380570412314001410").getUser().openPrivateChannel().complete()
	 *           .sendMessage(event.getMember().getUser().getName() + " tentou
	 *           entrar na sua sala").complete();
	 *           event.getMember().getUser().openPrivateChannel().complete()
	 *           .sendMessage("Nao entre na sala do anony enquanto ele
	 *           dorme!").complete(); } }
	 * 
	 * @Override public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) { if
	 *           (event.getChannelJoined().getId().equals("687012677654020172") &&
	 *           !event.getEntity().getId().equals("380570412314001410") &&
	 *           !event.getEntity().getId().equals("419637416349925388")) {
	 *           System.out.println("ja ta");
	 *           event.getEntity().deafen(true).queue();
	 *           event.getChannelJoined().getGuild().moveVoiceMember(event.getEntity(),
	 *           event.getChannelJoined().getGuild().getVoiceChannelById("680885685888221326")).queue();
	 *           event.getChannelJoined().getGuild().getMemberById("380570412314001410").getUser().openPrivateChannel()
	 *           .complete().sendMessage(event.getEntity().getUser().getName() + "
	 *           tentou entrar na sua sala") .complete();
	 *           event.getEntity().getUser().openPrivateChannel().complete()
	 *           .sendMessage("Nao entre na sala do anony enquanto ele
	 *           dorme!").complete(); } }
	 * 
	 * @Override public void onGuildVoiceJoin(GuildVoiceJoinEvent event) { if
	 *           (event.getChannelJoined().getId().equals("687012677654020172") &&
	 *           !event.getMember().getId().equals("380570412314001410") &&
	 *           !event.getMember().getId().equals("419637416349925388")) {
	 *           event.getMember().deafen(true).queue(); event.getGuild()
	 *           .moveVoiceMember(event.getMember(),
	 *           event.getGuild().getVoiceChannelById("680885685888221326"))
	 *           .queue();
	 *           event.getGuild().getMemberById("380570412314001410").getUser().openPrivateChannel().complete()
	 *           .sendMessage(event.getMember().getUser().getName() + " tentou
	 *           entrar na sua sala").complete();
	 *           event.getMember().getUser().openPrivateChannel().complete()
	 *           .sendMessage("Nao entre na sala do anony enquanto ele
	 *           dorme!").complete(); } }
	 **/

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.PRIVATE)) {
			if (event.getMessage().getContentRaw().startsWith("c!gc")
					&& event.getAuthor().getId().equals("380570412314001410")) {
				long start = System.currentTimeMillis();

				Runtime.getRuntime().runFinalization();
				Runtime.getRuntime().gc();
				System.gc();

				int cmds = 0;
				// Limpar cooldown de comandos

				List<String> toRemove = new ArrayList<>();
				for (Map.Entry<String, Long> commandManager : CommandManager.cooldown.entrySet()) {
					Date d = new Date(commandManager.getValue());
					Date d2 = new Date();
					d.setSeconds(d.getSeconds() + 1);
					Calendar saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
					d2 = new Date(saoPauloDate.getTimeInMillis());
					int hours = d2.getHours();
					d2.setHours(hours - 1);
					if (d2.getTime() >= d.getTime()) {
						toRemove.add(commandManager.getKey());
					}
				}

				for (String s : toRemove) {
					cmds++;
					CommandManager.cooldown.remove(s);
				}

				toRemove.clear();

				Runtime.getRuntime().runFinalization();
				Runtime.getRuntime().gc();
				System.gc();
				event.getAuthor().openPrivateChannel().complete().sendMessage("Limpeza concluída - Comandos: " + cmds
						+ " - Tempo: " + (System.currentTimeMillis() - start) + " ms").queue();
			} else if (event.getMessage().getContentRaw().startsWith("c!ping")
					&& event.getAuthor().getId().equals("380570412314001410")) {
				event.getAuthor().openPrivateChannel().complete()
						.sendMessage("Gateway Ping: " + event.getJDA().getGatewayPing() + "ms").queue();
				long start = System.currentTimeMillis();
				event.getAuthor().openPrivateChannel().complete()
						.sendMessage("Ping: " + (System.currentTimeMillis() - start) + "ms").queue();
			} else if (event.getMessage().getContentRaw().startsWith("c!memoria")
					&& event.getAuthor().getId().equals("380570412314001410")) {
				Runtime runtime = Runtime.getRuntime();
				final int MB = 1024 * 1024;

				long utilizada = ((runtime.totalMemory() - runtime.freeMemory()) / MB);
				event.getAuthor().openPrivateChannel().complete().sendMessage("Utilizando: " + utilizada + "MB")
						.queue();
			} else if (event.getMessage().getContentRaw().startsWith("c!nickname")
					&& event.getAuthor().getId().equals("380570412314001410")) {
				for (Member m : Main.getJDA().getGuildById("828053766955073606").getMembers()) {
					if (m.getNickname() != null && m.getNickname().contains("឵")) {
						m.modifyNickname(null).queue();
						System.out.println(m.getUser().getName() + " modificado");
					} else if (m.getNickname() != null && m.getNickname().equals("๋")) {
						m.modifyNickname(null).queue();
						System.out.println(m.getUser().getName() + " modificado");
					} else if (m.getNickname() != null && m.getNickname().contains("๋")) {
						m.modifyNickname(null).queue();
						System.out.println(m.getUser().getName() + " modificado");
					} else if (m.getNickname() != null && m.getNickname().equals("Ruffos lindo")) {
						m.modifyNickname(null).queue();
						System.out.println(m.getUser().getName() + " consertado");
					} else if (m.getUser().getName().startsWith("๋")) {
						m.modifyNickname("Ruffos lindo").queue();
						System.out.println(m.getUser().getName() + " modificado para Ruffos lindo");
					} else if (m.getUser().getName().startsWith("឵")) {
						m.modifyNickname("Ruffos lindo").queue();
						System.out.println(m.getUser().getName() + " modificado para Ruffos lindo");
					}
				}
			} else if (event.getMessage().getContentRaw().startsWith("c!bots")
					&& event.getAuthor().getId().equals("380570412314001410")) {
				for (Member m : Main.getJDA().getGuildById("828053766955073606").getMembers()) {
					if (m.getUser().isBot()) {
						event.getAuthor().openPrivateChannel().complete()
								.sendMessage(m.getUser().getName() + " - " + m.getUser().getId()).queue();
					}
				}
			}
		}
	}

}
