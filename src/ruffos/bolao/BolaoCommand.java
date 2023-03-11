package ruffos.bolao;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class BolaoCommand implements ICommand {

	public static String bolao = "";
	public static List<String> apostas = new ArrayList<>();
	public static List<Apostador> apostadores = new ArrayList<>();
	public static Timer timer;

	@Override
	public void handle(CommandContext ctx) {
		Member m = ctx.getMember();
		User u = m.getUser();
		Guild g = ctx.getGuild();
		if (ctx.getArgs().size() != 1 && !m.getId().equals("380570412314001410")) {
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					Utils.getEmote("nao").getFormatted()
							+ " **Comando incompleto:** Use c!bolao [APOSTA]\n\nBolão atual: **"
							+ (bolao.isEmpty() ? "Nenhum" : bolao) + "**",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
			return;
		}
		if (ctx.getArgs().get(0).equalsIgnoreCase("iniciar") && m.getId().equals("380570412314001410")) {
			if (ctx.getArgs().size() < 3) {
				ctx.getChannel().sendMessage("Use c!bolao iniciar [valores] [APOSTA]").queue();
				return;
			}
			String valores = ctx.getArgs().get(1);
			for (String v : valores.split(",")) {
				apostas.add(v.toUpperCase());
			}
			String aposta = "";
			for (int i = 2; i != ctx.getArgs().size(); i++) {
				aposta += ctx.getArgs().get(i) + " ";
			}
			bolao = StringUtils.chop(aposta);
			ctx.getChannel()
					.sendMessage("Iniciando bolão: **" + bolao + "**.\nValores para aposta: **" + apostas + "**")
					.queue();
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					for (Guild gg : ctx.getJDA().getGuilds()) {
						if (!Utils.isIgnoredServer(gg)) {
							if (!ConfigManager.temCfg(gg, "canal") || gg
									.getTextChannelById(ConfigManager.getConfig(gg, "canal").split(",")[0]) == null) {
								continue;
							}
							EmbedBuilder eb = new EmbedBuilder();
							eb.setAuthor(gg.getName() + " - Bolão!", gg.getIconUrl(), gg.getIconUrl());
							eb.setDescription(
									"**" + bolao + "**!\n\nPara apostar, digite `c!bolao [APOSTA]`.\nPreço: **"
											+ Utils.getDinheiro(15000) + "**.");
							eb.setFooter(g.getName());
							eb.setTimestamp(Instant.now());
							eb.setColor(
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)));
							gg.getTextChannelById(ConfigManager.getConfig(gg, "canal").split(",")[0])
									.sendMessageEmbeds(eb.build()).queue();
						}
					}
				}
			}, 30000, 1800000);
		} else if (ctx.getArgs().get(0).equalsIgnoreCase("cancelar") && m.getId().equals("380570412314001410")) {
			apostadores.forEach(apostador -> {
				apostador.getGuild().retrieveMemberById(apostador.getId()).queue(s -> {
					EconomiaManager.addDinheiroBanco(apostador.getGuild(), s.getUser(), 15000);
					if (!ConfigManager.temCfg(apostador.getGuild(), "canal") || apostador.getGuild().getTextChannelById(
							ConfigManager.getConfig(apostador.getGuild(), "canal").split(",")[0]) != null) {
						apostador.getGuild()
								.getTextChannelById(
										ConfigManager.getConfig(apostador.getGuild(), "canal").split(",")[0])
								.sendMessage(
										s.getAsMention() + ", seu dinheiro foi devolvido pelo cancelamento do bolão!")
								.queue();
					}
				}, f -> {

				});
			});
			ctx.getChannel().sendMessage("Cancelando...").queue();
			timer.cancel();
			apostadores.clear();
			apostas.clear();
			bolao = "";
		} else if (ctx.getArgs().get(0).equalsIgnoreCase("finalizar") && m.getId().equals("380570412314001410")) {
			if (ctx.getArgs().size() != 2) {
				return;
			}
			ctx.getChannel().sendMessage("Finalizando com **" + apostadores.size() + "** participações.").queue();
			String vencedor = ctx.getArgs().get(1).toUpperCase();
			Map<Guild, Integer> valores = new HashMap<>();
			List<String> vencedores = new ArrayList<>();
			apostadores.forEach(apostador -> {
				if (valores.containsKey(apostador.getGuild())) {
					valores.put(apostador.getGuild(), valores.get(apostador.getGuild()) + 15000);
				} else {
					valores.put(apostador.getGuild(), 15000);
				}
				if (apostador.getAposta().equals(vencedor)) {
					vencedores.add(apostador.getId() + ";" + apostador.getGuild().getId());
				}
			});
			vencedores.forEach(s -> {
				int ganho = valores.get(ctx.getJDA().getGuildById(s.split(";")[1])) / vencedores.size();
				ctx.getGuild().retrieveMemberById(s.split(";")[0]).queue(ss -> {
					EconomiaManager.addDinheiroBanco(ctx.getJDA().getGuildById(s.split(";")[1]), ss.getUser(), ganho);
					ctx.getJDA().getGuildById(s.split(";")[1]).getTextChannelById(
							ConfigManager.getConfig(ctx.getJDA().getGuildById(s.split(";")[1]), "canal").split(",")[0])
							.sendMessage(ss.getAsMention() + ", você apostou no vencedor do bolão e recebeu **"
									+ Utils.getDinheiro(ganho) + "** em seu banco!")
							.queue();
				}, f -> {

				});
			});
			timer.cancel();
			apostadores.clear();
			apostas.clear();
			bolao = "";
		} else {
			String aposta = ctx.getArgs().get(0).toUpperCase();
			if (!apostas.contains(aposta)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Aposta inválida! Use c!bolao [APOSTA]\n\nBolão atual: **"
								+ (bolao.isEmpty() ? "Nenhum" : bolao) + "**",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			List<Member> apostadors = new ArrayList<>();
			apostadores.forEach(apostador -> {
				if (apostador.getGuild().retrieveMemberById(apostador.getId()).complete() != null) {
					apostadors.add(apostador.getGuild().retrieveMemberById(apostador.getId()).complete());
				}
			});
			if (apostadors.contains(ctx.getMember())) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você já apostou!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (EconomiaManager.getDinheiroMaos(g, u) >= 15000) {
				EconomiaManager.removeDinheiroMaos(g, u, 15000);
				apostadores.add(new Apostador(u.getId(), aposta, ctx.getGuild()));
				Map<Guild, Integer> valores = new HashMap<>();
				apostadores.forEach(apostador -> {
					if (valores.containsKey(apostador.getGuild())) {
						valores.put(apostador.getGuild(), valores.get(apostador.getGuild()) + 15000);
					} else {
						valores.put(apostador.getGuild(), 15000);
					}
				});
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("sim").getFormatted() + " Você apostou no bolão!\n\nTotal das apostas: **"
								+ Utils.getDinheiro(valores.get(g)) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
								+ Utils.getDinheiro(15000) + "** em mãos para isto.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!bolao";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
