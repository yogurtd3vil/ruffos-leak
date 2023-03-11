package ruffos._roubos;

import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class RoubarBancoCommand implements ICommand {

	public static Map<String, RouboBanco> rouboBancos = new HashMap<>();

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "roubobanco") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " Aguarde, você só poderá roubar um banco novamente em **"
						+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "roubobanco") - time) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (rouboBancos.containsKey(u.getId())) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você já está no meio de um roubo.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			RouboBanco rouboBanco = new RouboBanco(ctx.getChannel(), u, g);
			rouboBancos.put(u.getId(), rouboBanco);
			String arma = EconomiaManager.getArma(g, u);
			if (EconomiaManager.hasArma(g, u) && arma.equals("ak47")) {
				CooldownsManager.addCooldown(g, u.getId(), "roubobanco", Long.parseLong("2100000"));
			} else if (EconomiaManager.hasArma(g, u) && arma.equals("shotgun")) {
				CooldownsManager.addCooldown(g, u.getId(), "roubobanco", Long.parseLong("2400000"));
			} else {
				CooldownsManager.addCooldown(g, u.getId(), "roubobanco", Long.parseLong("2700000"));
			}
		}
	}

	@Override
	public String getName() {
		return "c!roubarbanco";
	}

	@Override
	public String getHelp() {
		return "Roube um banco e fuja antes que a polícia chegue!";
	}

}
