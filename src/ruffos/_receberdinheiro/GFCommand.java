package ruffos._receberdinheiro;

import java.awt.Color;
import java.util.Date;
import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._casamento.CasamentoManager;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos._leveis.LeveisManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class GFCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (!Utils.isFakeGuild(g) && ConfigManager.hasChat(g, ctx.getChannel())) {
			if (CasamentoManager.casado(g, u.getId())) {
				long time = new Date().getTime();
				if (CooldownsManager.getCooldownTime(g, u.getId(), "gf") >= time) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " Aguarde, você só poderá tentar fazer gf novamente em **"
							+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "gf") - time) + "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				CooldownsManager.addCooldown(g, u.getId(), "gf", Long.parseLong("900000"));
				if (Utils.chance(30)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							" Você broxou em sua web transa com <@" + CasamentoManager.getParceiro(g, u.getId())
									+ ">! Que vergonha... Tá fraco, hein?",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				Random r = new Random();
				int random = r.nextInt(750);
				while (random < 400) {
					random = r.nextInt(750);
				}
				int xp = r.nextInt(15);
				while (xp < 4) {
					xp = r.nextInt(15);
				}
				EconomiaManager.addDinheiroMaos(g, u, random);
				if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
					ClansManager.addFarm(u, random);
				}
				LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), xp);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você web transou com <@" + CasamentoManager.getParceiro(g, u.getId())
								+ "> e mandou muito bem! Você ganhou **" + Utils.getDinheiro(random) + "** e **" + xp
								+ "** de XP!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você deve estar casado(a) para transar virtualmente. Use: `c!casar`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!gf";
	}

	@Override
	public String getHelp() {
		return "Fazer GF, recebe XP e dinheiro";
	}

}
