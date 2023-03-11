package ruffos._receberdinheiro;

import java.awt.Color;
import java.util.Date;
import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos._leveis.LeveisManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class TrabalharCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (!EconomiaManager.hasTrabalho(g, u)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Antes, você precisa de um emprego! Use: `c!empregos`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "trabalhar") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " Aguarde, você só poderá trabalhar novamente em **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "trabalhar") - time)
								+ "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			String profissao = EconomiaManager.getTrabalho(g, u);
			if (profissao.equalsIgnoreCase("Desmanche")) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você não pode usar este comando.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			CooldownsManager.addCooldown(g, u.getId(), "trabalhar", 1800000);
			int random = 0;
			Random r = new Random();
			int xp = r.nextInt(10);
			if (profissao.equalsIgnoreCase("lixeiro")) {
				random = 500;
			} else if (profissao.equalsIgnoreCase("jardineiro")) {
				random = 700;
			} else if (profissao.equalsIgnoreCase("prostituta")) {
				random = 1000;
			} else if (profissao.equalsIgnoreCase("manobrista")) {
				random = 1300;
			} else if (profissao.equalsIgnoreCase("motorista")) {
				random = 1400;
			} else if (profissao.equalsIgnoreCase("garçom")) {
				random = 1700;
			} else if (profissao.equalsIgnoreCase("recepcionista")) {
				random = 1900;
			} else if (profissao.equalsIgnoreCase("governanta")) {
				random = 2400;
			} else if (profissao.equalsIgnoreCase("chefe de cozinha")) {
				random = 2700;
			} else if (profissao.equalsIgnoreCase("sommelier")) {
				random = 2900;
			} else if (profissao.equalsIgnoreCase("bombeiro")) {
				random = 2950;
			} else if (profissao.equalsIgnoreCase("policial")) {
				random = 3100;
			} else if (profissao.equalsIgnoreCase("médico")) {
				random = 3500;
			} else if (profissao.equalsIgnoreCase("doleiro")) {
				random = 4000;
			}
			EconomiaManager.addDinheiroMaos(g, u, random);
			if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
				ClansManager.addFarm(u, random);
			}
			LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), xp);
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					" Você trabalhou como **" + profissao + "**, recebeu **" + Utils.getDinheiro(random) + "** e **"
							+ xp + "** de XP!",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
		}
	}

	@Override
	public String getName() {
		return "c!trabalhar";
	}

	@Override
	public String getHelp() {
		return "Trabalhe para receber uma quantia em dinheiro.";
	}

}
