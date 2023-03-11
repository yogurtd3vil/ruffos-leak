package ruffos._receberdinheiro;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class RecompensaCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			Calendar saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
			Date dAgora = new Date();
			if (!EconomiaManager.existeUser(g, u)) {
				EconomiaManager.criarUser(g, u, 0);
			}
			if (EconomiaManager.inRecompensa(g, u)) {
				Date d = new Date(EconomiaManager.getRecompensa(g, u));
				d.setDate(d.getDate() + 1);
				if (dAgora.getTime() >= d.getTime()) {
					EconomiaManager.updateRecompensa(g, u);
					int valor = new Random().nextInt(9000);
					while (valor < 6000) {
						valor = new Random().nextInt(9000);
					}
					EconomiaManager.addDinheiroMaos(g, u, valor);
					if (ClansManager.hasClan(u)
							&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
						ClansManager.addFarm(u, valor);
					}
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"Você resgatou sua recompensa diária no valor de **" + Utils.getDinheiro(valor) + "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " Aguarde, você só poderá resgatar sua recompensa diária novamente em **"
									+ Utils.getTime(d.getTime() - dAgora.getTime()) + "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				}
			} else {
				int valor = new Random().nextInt(9000);
				while (valor < 6000) {
					valor = new Random().nextInt(9000);
				}
				EconomiaManager.addRecompensa(g, u);
				EconomiaManager.addDinheiroMaos(g, u, valor);
				if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
					ClansManager.addFarm(u, valor);
				}
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Você resgatou sua recompensa diária no valor de **"
						+ Utils.getDinheiro(valor)
						+ "**!\n\nAparentemente é a primeira vez que você digita o comando de recompensa, vou só lhe lembrar de ler as minhas regras para você evitar de ser punido. É só [clicar aqui](https://discord.gg/9xNCCEE)!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!recompensa";
	}

	@Override
	public String getHelp() {
		return "Pegue sua recompensa diária.";
	}

}
