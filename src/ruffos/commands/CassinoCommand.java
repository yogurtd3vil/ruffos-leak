package ruffos.commands;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class CassinoCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!cassino [valor].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!Utils.isDouble(ctx.getArgs().get(0))) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!cassino [valor].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			int quantidade = Integer.parseInt(ctx.getArgs().get(0));
			if (quantidade < 100.0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** O valor deve ser maior ou igual à **"
								+ Utils.getDinheiro(100) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!EconomiaManager.hasMaos(g, u, quantidade)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
								+ Utils.getDinheiro(quantidade) + "** em mãos para isso.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			// EmbedBuilder eb = new EmbedBuilder();
			// eb.setTitle("Testando a sorte de " + u.getName() + " com R$" + quantidade);
			// eb.setTimestamp(Instant.now());
			// Random r = new Random();
			// int i = r.nextInt(1);
			// if (i == 1) {
			// eb.setImage("https://i.imgur.com/CQ6Kr75.png");
			// } else {
			// eb.setImage("https://i.imgur.com/YMvIIZL.png");
			// }
			// ctx.getChannel().sendMessage(eb.build()).queue((message) -> {
			// testarSorte(u, message, quantidade);
			// });
			EconomiaManager.removeDinheiroMaos(g, u, quantidade);
			if (Utils.chance(20)) {
				randomGanhou(ctx.getChannel(), u, quantidade);
			} else {
				randomPerdeu(ctx.getChannel(), u, quantidade);
			}
		}
	}

	void randomPerdeu(TextChannel tc, User u, double quantidade) {
		List<String> perdeuUm = new ArrayList<>();
		perdeuUm.add("https://media.giphy.com/media/h7vFHDJnRDBp5ZRo5o/giphy.gif");
		perdeuUm.add("https://media.giphy.com/media/h7vFHDJnRDBp5ZRo5o/giphy.gif");
		perdeuUm.add("https://media.giphy.com/media/JpS1ySC1tvtOW9Lykv/giphy.gif");
		perdeuUm.add("https://media.giphy.com/media/jUE7ozamIwXnJbrqed/giphy.gif");
		perdeuUm.add("https://media.giphy.com/media/hSQCoPIzPWgLi8TZOf/giphy.gif");
		perdeuUm.add("https://media.giphy.com/media/cNTDM1psb4GjuqHIpd/giphy.gif");
		perdeuUm.add("https://media.giphy.com/media/hQtjOuxxq0bRV5qZp9/giphy.gif");
		perdeuUm.add("https://media.giphy.com/media/H4DmW6b57N4tWsA61R/giphy.gif");
		int venceu = new Random().nextInt(perdeuUm.size());
		EmbedBuilder eb = new EmbedBuilder();
		eb.setFooter(tc.getGuild().getName());
		eb.setTimestamp(Instant.now());
		eb.setTitle("CASSINO!");
		eb.setColor(((ConfigManager.temCfg(tc.getGuild(), "cor")
				? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
				: null)));
		eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
		eb.setImage(perdeuUm.get(venceu));
		List<String> perdeuDois = new ArrayList<>();
		perdeuDois.add("https://i.imgur.com/xthjxom.png");
		perdeuDois.add("https://i.imgur.com/PoqMkET.png");
		perdeuDois.add("https://i.imgur.com/SujRxA1.png");
		perdeuDois.add("https://i.imgur.com/WatAdfO.png");
		perdeuDois.add("https://i.imgur.com/hJoOzJY.png");
		perdeuDois.add("https://i.imgur.com/KgaFZal.png");
		perdeuDois.add("https://i.imgur.com/x1nmQvd.png");
		perdeuDois.add("https://i.imgur.com/Vp9J797.png");

		tc.sendMessageEmbeds(eb.build()).queue((message) -> {
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					eb.setTitle("CASSINO: VOCÊ PERDEU!");
					eb.addField("Valor perdido:", "**" + Utils.getDinheiro(quantidade) + "**", true);
					eb.setDescription("Você perdeu! Mais sorte da próxima vez...");
					eb.setImage(perdeuDois.get(venceu));
					message.editMessageEmbeds(eb.build()).queue();
				}
			}, 12700);
		});
	}

	void randomGanhou(TextChannel tc, User u, int quantidade) {
		List<String> ganhouUm = new ArrayList<>();
		ganhouUm.add("https://media.giphy.com/media/SruE9orYZZ2kCgxfat/giphy.gif");
		ganhouUm.add("https://media.giphy.com/media/jtF442dvjKlvlOcQJg/giphy.gif");
		ganhouUm.add("https://media.giphy.com/media/fZ9KLSnzStjsALzVoc/giphy.gif");
		ganhouUm.add("https://media.giphy.com/media/JTQ5SVK3JXdeLNjld7/giphy.gif");
		int venceu = new Random().nextInt(ganhouUm.size());
		EmbedBuilder eb = new EmbedBuilder();
		eb.setFooter(tc.getGuild().getName());
		eb.setTimestamp(Instant.now());
		eb.setTitle("CASSINO!");
		eb.setColor(((ConfigManager.temCfg(tc.getGuild(), "cor")
				? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
				: null)));
		eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
		eb.setImage(ganhouUm.get(venceu));
		List<String> ganhouDois = new ArrayList<>();
		ganhouDois.add("https://i.imgur.com/bWISIsn.png");
		ganhouDois.add("https://i.imgur.com/wsqpV5q.png");
		ganhouDois.add("https://i.imgur.com/9jUJyCi.png");
		ganhouDois.add("https://i.imgur.com/3iQbSXk.png");
		tc.sendMessageEmbeds(eb.build()).queue((message) -> {
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					eb.setTitle("CASSINO: VOCÊ GANHOU!");
					eb.setDescription("Parabéns, você ganhou! Que tal tentar de novo?");
					eb.addField("Valor ganho:", "**" + Utils.getDinheiro(quantidade * 2) + "**", true);
					eb.setImage(ganhouDois.get(venceu));
					message.editMessageEmbeds(eb.build()).queue();
					EconomiaManager.addDinheiroMaos(tc.getGuild(), u, quantidade * 2);
				}
			}, 12700);
		});
	}

	@Override
	public String getName() {
		return "c!cassino";
	}

	@Override
	public String getHelp() {
		return "Teste sua sorte apostando dinheiro.";
	}

	private void testarSorte(User u, Message m, double quantidade) {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			int duracao = 20;

			@Override
			public void run() {
				duracao--;
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Testando a sorte de " + u.getName() + " com R$" + quantidade);
				eb.setTimestamp(Instant.now());
				Random r = new Random();
				int i = r.nextInt(10);
				boolean ganhou = false;
				if (i > 5) {
					ganhou = true;
					eb.setImage("https://i.imgur.com/CQ6Kr75.png");
				} else {
					eb.setImage("https://i.imgur.com/YMvIIZL.png");
				}
				m.editMessageEmbeds(eb.build()).queue();
				if (duracao == 0) {
					i = r.nextInt(10);
					if (i > 5) {
						ganhou = true;
						eb.setImage("https://i.imgur.com/CQ6Kr75.png");
					} else {
						eb.setImage("https://i.imgur.com/YMvIIZL.png");
					}
					m.editMessageEmbeds(eb.build()).queue();
					if (ganhou) {
						m.editMessage(u.getAsMention() + " dobrou seu dinheiro para **"
								+ Utils.getDinheiro(quantidade * 2) + "**! Parabéns!").queue();
					} else {
						m.editMessage(u.getAsMention() + " perdeu a quantia de **" + Utils.getDinheiro(quantidade)
								+ "**! Não foi dessa vez!").queue();
					}
					cancel();
				}
			}
		}, 5, 100);
	}

}
