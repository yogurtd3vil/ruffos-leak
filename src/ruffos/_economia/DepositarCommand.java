package ruffos._economia;

import java.awt.Color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class DepositarCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!depositar [valor] ou c!depositar td.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (Utils.isDouble(ctx.getArgs().get(0))) {
				double quantidade = Double.parseDouble(ctx.getArgs().get(0));
				if (quantidade < 100) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não pode depositar um valor menor que **"
									+ Utils.getDinheiro(100) + "**.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasMaos(g, u, quantidade)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não possui dinheiro em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, quantidade);
				EconomiaManager.addDinheiroBanco(g, u, quantidade);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você depositou **" + Utils.getDinheiro(quantidade) + "** em sua conta bancária.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equals("td")) {
				double quantidade = EconomiaManager.getDinheiroMaos(g, u);
				if (!EconomiaManager.hasMaos(g, u, quantidade)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não possui dinheiro em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, quantidade);
				EconomiaManager.addDinheiroBanco(g, u, quantidade);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você depositou **" + Utils.getDinheiro(quantidade) + "** em sua conta bancária.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando errado:** Use c!depositar [valor] ou c!depositar td.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!depositar";
	}

	@Override
	public String getHelp() {
		return "Deposite seu dinheiro no banco. (Para depositar tudo = c!depositar td)";
	}

}
