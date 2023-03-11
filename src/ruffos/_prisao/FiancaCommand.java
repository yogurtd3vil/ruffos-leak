package ruffos._prisao;

import java.awt.Color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class FiancaCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				if (!PresosManager.estaPreso(g, u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está preso(a).",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				int fianca = 10000;
				if (PresosManager.getMotivo(g, u).equals("Tentativa de roubo à banco e troca de tiro com policiais.")) {
					fianca = 15000;
				}
				if (EconomiaManager.hasTrabalho(g, u) && EconomiaManager.getTrabalho(g, u).equals("Policial")) {
					fianca = (fianca / 2);
				}
				if ((EconomiaManager.getDinheiroMaos(g, u) + EconomiaManager.getDinheiroBanco(g, u)) < fianca) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(fianca) + "** para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroBanco(g, u, fianca);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"LIBERDADE CANTOU! Você pagou **" + Utils.getDinheiro(fianca)
								+ "** e foi liberado da prisão virtual.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				PresosManager.soltar(g, u);
			} else if (ctx.getArgs().size() == 1) {
				if (ctx.getMessage().getMentions().getMembers().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!fianca @usuário.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				User u2 = ctx.getMessage().getMentions().getMembers().get(0).getUser();
				if (!PresosManager.estaPreso(g, u2)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** " + u2.getAsMention()
									+ " não está preso(a).",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				int fianca = 10000;
				if (PresosManager.getMotivo(g, u2)
						.equals("Tentativa de roubo à banco e troca de tiro com policiais.")) {
					fianca = 15000;
				}
				if (EconomiaManager.hasTrabalho(g, u2) && EconomiaManager.getTrabalho(g, u2).equals("Policial")) {
					fianca = (fianca / 2);
				}
				if (EconomiaManager.getDinheiroMaos(g, u) < fianca) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(fianca) + "** para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, fianca);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"LIBERDADE CANTOU! Você pagou **" + Utils.getDinheiro(fianca) + "** e " + u2.getAsMention()
								+ " foi liberado da prisão virtual.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				PresosManager.soltar(g, u2);
			}
		}
	}

	@Override
	public String getName() {
		return "c!fianca";
	}

	@Override
	public String getHelp() {
		return "Pague a sua ou a fiança de um amigo.";
	}

}
