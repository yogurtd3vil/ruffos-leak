package ruffos._receberdinheiro;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class CrimeCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "crime") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " Aguarde, você só poderá cometer crimes novamente em **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "crime") - time) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			CooldownsManager.addCooldown(g, u.getId(), "crime", Long.parseLong("1800000"));
			List<String> msgs = new ArrayList<>();
			Random r = new Random();
			int random = r.nextInt(1500);
			while (random < 800) {
				random = r.nextInt(1500);
			}
			String msg = "Não há configurações definidas para as mensagens de crime (o dinheiro foi enviado do mesmo jeito). Use: `c!config crime`.";
			if (ConfigManager.temCfg(g, "crime")) {
				String[] split = ConfigManager.getConfig(g, "crime").split("/");
				msg = split[r.nextInt(split.length)];
			}
			msgs.clear();
			Utils.enviarEmbed(ctx.getChannel(), u, null, msg.replace("@valor", Utils.getDinheiro(random)),
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
			EconomiaManager.addDinheiroMaos(g, u, random);
			if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
				ClansManager.addFarm(u, random);
			}
		}
	}

	@Override
	public String getName() {
		return "c!crime";
	}

	@Override
	public String getHelp() {
		return "Cometa um crime e receba uma quantia em dinheiro.";
	}

}
