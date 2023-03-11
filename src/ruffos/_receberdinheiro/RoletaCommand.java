package ruffos._receberdinheiro;

import java.awt.Color;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class RoletaCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "roleta") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " Aguarde, você só poderá roletar novamente em **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "roleta") - time)
								+ "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getArgs().size() != 2) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!roleta [valor] [verde/vermelho/azul].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!Utils.isInteger(ctx.getArgs().get(0))) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando errado:** Use c!roleta [valor] [verde/vermelho/azul].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			String cor = ctx.getArgs().get(1).toUpperCase();
			if (!cor.equals("AZUL") && !cor.equals("VERMELHO") && !cor.equals("VERDE")) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando errado:** Use c!roleta [valor] [verde/vermelho/azul].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			int aposta = Integer.parseInt(ctx.getArgs().get(0));
			if (!(aposta > 100 && aposta < 10000)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Erro:** O valor da aposta deve ser maior que **R$ 100,00** e menor que **R$ 10,000**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!EconomiaManager.hasMaos(g, u, aposta)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
								+ Utils.getDinheiro(aposta) + "** em mãos para apostar na roleta.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			EconomiaManager.removeDinheiroMaos(g, u, aposta);
			CooldownsManager.addCooldown(g, u.getId(), "roleta", Long.parseLong("1800000"));
			int ganho = aposta * 2;
			Random r = new Random();
			int i = r.nextInt(3);
			if (cor.equals("AZUL")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("ROLETA: A roleta deverá cair no " + cor + "!");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				if (i == 0) {
					eb.setImage("https://i.imgur.com/yErzd2t.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/A4ARmuA.png");
							eb.setTitle("ROLETA: Você ganhou! Seu dinheiro foi duplicado para "
									+ Utils.getDinheiro(ganho) + "!");
							s.editMessageEmbeds(eb.build()).queue();
							EconomiaManager.addDinheiroMaos(g, u, ganho);
							if (ClansManager.hasClan(u)
									&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
								ClansManager.addFarm(u, aposta);
							}
						}
					}, 6100);

				} else if (i == 1) {
					eb.setImage("https://i.imgur.com/6hlnjti.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();

					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/U87cTek.png");
							eb.setTitle("ROLETA: Você perdeu " + Utils.getDinheiro(aposta) + "!");
							s.editMessageEmbeds(eb.build()).queue();
						}
					}, 6100);

				} else {
					eb.setImage("https://i.imgur.com/grRcYQj.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();

					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/vjGGF9P.png");
							eb.setTitle("ROLETA: Você perdeu " + Utils.getDinheiro(aposta) + "!");
							s.editMessageEmbeds(eb.build()).queue();
						}
					}, 6100);
				}
			} else if (cor.equals("VERMELHO")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("ROLETA: A roleta deverá cair no " + cor + "!");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				if (i == 0) {
					eb.setImage("https://i.imgur.com/yErzd2t.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/A4ARmuA.png");
							eb.setTitle("ROLETA: Você perdeu " + Utils.getDinheiro(aposta) + "!");
							s.editMessageEmbeds(eb.build()).queue();
						}
					}, 6100);

				} else if (i == 1) {
					eb.setImage("https://i.imgur.com/6hlnjti.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();

					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/U87cTek.png");
							eb.setTitle("ROLETA: Você ganhou! Seu dinheiro foi duplicado para "
									+ Utils.getDinheiro(ganho) + "!");
							s.editMessageEmbeds(eb.build()).queue();
							EconomiaManager.addDinheiroMaos(g, u, ganho);
							if (ClansManager.hasClan(u)
									&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
								ClansManager.addFarm(u, aposta);
							}
						}
					}, 6100);

				} else {
					eb.setImage("https://i.imgur.com/grRcYQj.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();

					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/vjGGF9P.png");
							eb.setTitle("ROLETA: Você perdeu " + Utils.getDinheiro(aposta) + "!");
							s.editMessageEmbeds(eb.build()).queue();
						}
					}, 6100);
				}
			} else if (cor.equals("VERDE")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("ROLETA: A roleta deverá cair no " + cor + "!");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				if (i == 0) {
					eb.setImage("https://i.imgur.com/yErzd2t.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/A4ARmuA.png");
							eb.setTitle("ROLETA: Você perdeu " + Utils.getDinheiro(aposta) + "!");
							s.editMessageEmbeds(eb.build()).queue();
						}
					}, 6100);

				} else if (i == 1) {
					eb.setImage("https://i.imgur.com/6hlnjti.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();

					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/U87cTek.png");
							eb.setTitle("ROLETA: Você perdeu " + Utils.getDinheiro(aposta) + "!");
							s.editMessageEmbeds(eb.build()).queue();
						}
					}, 6100);

				} else {
					eb.setImage("https://i.imgur.com/grRcYQj.gif");
					Message s = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();

					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							eb.setImage("https://i.imgur.com/vjGGF9P.png");
							eb.setTitle("ROLETA: Você ganhou! Seu dinheiro foi duplicado para "
									+ Utils.getDinheiro(ganho) + "!");
							s.editMessageEmbeds(eb.build()).queue();
							EconomiaManager.addDinheiroMaos(g, u, ganho);
							if (ClansManager.hasClan(u)
									&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
								ClansManager.addFarm(u, aposta);
							}
						}
					}, 6100);
				}
			}
		}
	}

	@Override
	public String getName() {
		return "c!roleta";
	}

	@Override
	public String getHelp() {
		return "Roleta";
	}

}
