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
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class SemanalCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "semanal") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " Aguarde, você só poderá resgatar sua recompensa semanal novamente em **"
						+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "semanal") - time) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			int valor = new Random().nextInt(17000);
			while (valor < 9000) {
				valor = new Random().nextInt(17000);
			}
			EconomiaManager.addDinheiroMaos(g, u, valor);
			if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
				ClansManager.addFarm(u, valor);
			}
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					"Você resgatou sua recompensa semanal no valor de **" + Utils.getDinheiro(valor) + "**!",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
			CooldownsManager.addCooldown(g, u.getId(), "semanal", Long.parseLong("604800000"));
		}
	}

	@Override
	public String getName() {
		return "c!semanal";
	}

	@Override
	public String getHelp() {
		return "Comando semanal";
	}

}
