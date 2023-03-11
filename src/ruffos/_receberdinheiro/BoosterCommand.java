package ruffos._receberdinheiro;

import java.awt.Color;
import java.util.Date;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class BoosterCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(ctx.getGuild(), ctx.getChannel())) {
			Utils.getRuffosGuild().retrieveMemberById(ctx.getAuthor().getId()).queue(m -> {
				if (m.getTimeBoosted() != null) {
					long time = new Date().getTime();
					if (CooldownsManager.getCooldownTime(g, u.getId(), "booster") >= time) {
						Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
								+ " Aguarde, você só poderá pegar sua recompensa por boostar o Ruffos novamente em **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "booster") - time)
								+ "**!",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("boost").getFormatted()
									+ " Você resgatou sua recompensa por boostar o Ruffos no valor de **"
									+ Utils.getDinheiro(6000) + "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					EconomiaManager.addDinheiroMaos(g, u, 6000);
					if (ClansManager.hasClan(u)
							&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
						ClansManager.addFarm(u, 6000);
					}
					CooldownsManager.addCooldown(g, u.getId(), "booster", Long.parseLong("3600000"));
				} else {
					Utils.enviarEmbed(ctx.getChannel(), ctx.getAuthor(), null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Você deve contribuir dando pelo menos um BOOST no servidor do Ruffos para ter acesso a este comando.\nPara isto, entre no servidor do Ruffos clicando [aqui](https://discord.gg/sBcnmss).",
							((ConfigManager.temCfg(ctx.getGuild(), "cor")
									? Color.decode(ConfigManager.getConfig(ctx.getGuild(), "cor"))
									: null)),
							ctx.getGuild().getName(), null, null, null, 0, true);
				}
			}, f -> {
				Utils.enviarEmbed(ctx.getChannel(), ctx.getAuthor(), null, Utils.getEmote("nao").getFormatted()
						+ " **Erro:** Você deve contribuir dando pelo menos um BOOST no servidor do Ruffos para ter acesso a este comando.\nPara isto, entre no servidor do Ruffos clicando [aqui](https://discord.gg/sBcnmss).",
						((ConfigManager.temCfg(ctx.getGuild(), "cor")
								? Color.decode(ConfigManager.getConfig(ctx.getGuild(), "cor"))
								: null)),
						ctx.getGuild().getName(), null, null, null, 0, true);
			});
		}
	}

	@Override
	public String getName() {
		return "c!booster";
	}

	@Override
	public String getHelp() {
		return "";
	}

}
