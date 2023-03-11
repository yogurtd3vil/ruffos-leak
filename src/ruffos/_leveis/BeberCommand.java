package ruffos._leveis;

import java.awt.Color;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class BeberCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			long time = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo")).getTimeInMillis();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "beber") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " Você está em coma alcoólico e não pode beber agora! Aguarde **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "beber") - time) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (EconomiaManager.hasBebida(g, u)) {
				int copos = EconomiaManager.getCopos(g, u);
				String bebida = EconomiaManager.getBebida(g, u);
				if (EconomiaManager.getCopos(g, u) > 0) {
					EconomiaManager.setCopos(g, u, (copos - 1));
					int randomXP = 0;
					Random r = new Random();
					Emoji emote = null;
					if (bebida.equals("cerveja")) {
						randomXP = r.nextInt(15);
						while (randomXP < 10) {
							randomXP = r.nextInt(15);
						}
						emote = Utils.getEmote("cerveja");
					} else if (bebida.equals("vodka")) {
						randomXP = r.nextInt(20);
						while (randomXP < 12) {
							randomXP = r.nextInt(20);
						}
						emote = Utils.getEmote("vodka");
					}
					if (bebida.equals("gin")) {
						randomXP = r.nextInt(27);
						while (randomXP < 14) {
							randomXP = r.nextInt(27);
						}
						emote = Utils.getEmote("gin");
					}
					boolean xp = false;
					if (xp) {
						randomXP = (randomXP * 2);
					}
					if (EconomiaManager.getCopos(g, u) == 0) {
						Utils.enviarEmbed(ctx.getChannel(), u, null, emote.getFormatted()
								+ " Você bebeu **1 copo** de **" + Utils.getBebida(bebida) + "** e recebeu **"
								+ randomXP + "** de XP!"
								+ ((xp) ? " **(x2)**"
										: "" + " Você entrou em coma alcoólico por ter tomado todos seus copos de **"
												+ bebida + "**."),
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), randomXP);
						CooldownsManager.addCooldown(g, u.getId(), "beber", Long.parseLong("900000"));
						return;
					}
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							emote.getFormatted() + " Você bebeu **1 copo** de **" + Utils.getBebida(bebida)
									+ "** e recebeu **" + randomXP + "** de XP!" + ((xp) ? " **(x2)**" : ""),
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), randomXP);
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Seus copos acabaram! Use: `c!loja`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você não possui nenhuma bebida! Use: `c!loja`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
		}
	}

	@Override
	public String getName() {
		return "c!beber";
	}

	@Override
	public String getHelp() {
		return "Beba alguma bebida alcoólica e receba XP.";
	}

}
