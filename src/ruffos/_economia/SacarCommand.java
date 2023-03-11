package ruffos._economia;

import java.awt.Color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class SacarCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!sacar [valor] ou c!sacar td.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (Utils.isDouble(ctx.getArgs().get(0))) {
				double quantidade = Double.parseDouble(ctx.getArgs().get(0));
				if (quantidade < 100) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Você não pode sacar um valor menor que **" + Utils.getDinheiro(100) + "**.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasBanco(g, u, quantidade)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente no banco para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroBanco(g, u, quantidade);
				EconomiaManager.addDinheiroMaos(g, u, quantidade);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você sacou **" + Utils.getDinheiro(quantidade) + "** de sua conta bancária.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				TextChannel logs = ctx.getJDA().getTextChannelById("750845068650217542");
				logs.sendMessage(Utils.getEmote("dinheiro").getFormatted() + " **SAQUE DE DINHEIRO**\n\n**Usuário:** `"
						+ u.getAsTag() + "` - `" + u.getId() + "`.\n**Dinheiro sacado:** `"
						+ Utils.getDinheiro(quantidade) + "`.\n**Servidor:** `" + g.getName() + "` - `" + g.getId()
						+ "`.").queue();
			} else if (ctx.getArgs().get(0).equals("td")) {
				double quantidade = EconomiaManager.getDinheiroBanco(g, u);
				if (!EconomiaManager.hasBanco(g, u, quantidade)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente no banco para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroBanco(g, u, quantidade);
				EconomiaManager.addDinheiroMaos(g, u, quantidade);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você sacou **" + Utils.getDinheiro(quantidade) + "** de sua conta bancária.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				TextChannel logs = ctx.getJDA().getTextChannelById("750845068650217542");
				logs.sendMessage(Utils.getEmote("dinheiro").getFormatted() + " **SAQUE DE DINHEIRO**\n\n**Usuário:** `"
						+ u.getAsTag() + "` - `" + u.getId() + "`.\n**Dinheiro sacado:** `"
						+ Utils.getDinheiro(quantidade) + "`.\n**Servidor:** `" + g.getName() + "` - `" + g.getId()
						+ "`.").queue();
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!sacar [valor] ou c!sacar td.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!sacar";
	}

	@Override
	public String getHelp() {
		return "Saque seu dinheiro do banco. (Para sacar tudo = c!sacar td)";
	}
}