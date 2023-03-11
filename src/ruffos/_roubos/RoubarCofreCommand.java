package ruffos._roubos;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class RoubarCofreCommand implements ICommand {

	public static Map<Member, RouboCofre> roubosCofre = new HashMap<>();

	boolean repetidos(int i) {
		boolean contem = false;
		List<Character> chars = new ArrayList<>();
		for (char a : String.valueOf(i).toCharArray()) {
			if (!chars.contains(a)) {
				chars.add(a);
			} else {
				contem = true;
				break;
			}
		}
		return contem;
	}

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "roubocofre") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " Aguarde, você só poderá roubar um cofre novamente em **"
						+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "roubocofre") - time) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!EconomiaManager.hasMaos(g, u, 5000)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de no mínimo **"
								+ Utils.getDinheiro(5000) + "** em mãos para isto.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (roubosCofre.size() > 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Aguarde, já há um usuário roubando o cofre.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			EconomiaManager.removeDinheiroMaos(g, u, 5000);
			Random r = new Random();
			int contem = r.nextInt(20000);
			while (contem < 8000) {
				contem = r.nextInt(20000);
			}
			int cont = contem;

			int s = r.nextInt(9999);
			while (s < 1000) {
				s = r.nextInt(9999);
			}

			String calculo = "";
			int random = r.nextInt(4);
			switch (random) {
			case 0:
				int n = s / 4;
				int resultado = n * 4;
				s = resultado;
				calculo = n + " x 4";
				break;
			case 1:
				int nn = s / 5;
				int rresultado = nn * 5;
				s = rresultado;
				calculo = nn + " + " + nn + " + " + nn + " + " + nn + " + " + nn;
				break;
			case 2:
				int randomnumber = r.nextInt(999);
				int nnn = s + randomnumber;
				int rrresultado = nnn - randomnumber;
				s = rrresultado;
				calculo = nnn + " - " + randomnumber;
				break;
			case 3:
				int nnnn = s * 9;
				int rrrresultado = nnnn / 9;
				s = rrrresultado;
				calculo = nnnn + " / 9";
				break;
			case 4:
				int nnnnnn = s / 4;
				int rrrrrresultado = nnnnnn * 4;
				s = rrrrrresultado;
				calculo = nnnnnn + " x 4";
				break;
			default:
				break;
			}

			String senha = String.valueOf(s);
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			eb.setDescription(
					"Você está roubando um cofre, seja rápido, você tem **30** segundos!\n\nPara ganhar o valor de dinheiro contido neste cofre, você deverá adivinhar a senha de **4** dígitos.\n\nA senha do cofre é o resultado deste cálculo: **"
							+ calculo + "**\nDigite a senha no chat.");
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setFooter(g.getName());
			eb.setTimestamp(Instant.now());
			eb.addField(new Field("Este cofre contém:", "**" + Utils.getDinheiro(contem) + "**", true));
			eb.setImage("https://i.imgur.com/wH06DZu.png");
			ctx.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
				roubosCofre.put(ctx.getMember(), new RouboCofre(ctx.getMember(), cont, senha, m.getId()));
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						roubosCofre.remove(ctx.getMember());
					}
				}, 30000);
			});
			CooldownsManager.addCooldown(g, u.getId(), "roubocofre", 2400000L);
		}

	}

	@Override
	public String getName() {
		return "c!roubarcofre";
	}

	@Override
	public String getHelp() {
		return "cofre";
	}

}
