package ruffos._receberdinheiro;

import java.awt.Color;
import java.text.SimpleDateFormat;
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

public class MadrugadaCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
			String tim = fmt.format(new Date());
			StringBuilder sb = new StringBuilder(tim);
			sb.deleteCharAt(2);
			int t = Integer.parseInt(sb.toString());
			if (t >= 2 && t <= 210) {
				long time = new Date().getTime();
				if (CooldownsManager.getCooldownTime(g, u.getId(), "madrugada") >= time) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " Aguarde, você só poderá pegar a sua recompensa da madrugada novamente em **"
							+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "madrugada") - time) + "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				CooldownsManager.addCooldown(g, u.getId(), "madrugada", Long.parseLong("1800000"));
				Random r = new Random();
				int random = r.nextInt(15000);
				while (random < 5000) {
					random = r.nextInt(15000);
				}
				if (Utils.chance(20)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("roubo").getFormatted()
							+ " Ops! Enquanto você andava na madrugada pelos becos escuros do(a) `" + g.getName()
							+ "` uma família de web bandidos lhe assaltaram e levaram **" + Utils.getDinheiro(random)
							+ "** da sua mão!\n\n**OBS:** Se você possui um clan este dinheiro também foi retirado do seu lucro individual.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					EconomiaManager.removeDinheiroMaos(g, u, random);
					if (ClansManager.hasClan(u)
							&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
						ClansManager.addFarm(u, -random);
					}
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"Você pegou sua recompensa da madrugada e recebeu **" + Utils.getDinheiro(random) + "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					EconomiaManager.addDinheiroMaos(g, u, random);
					if (ClansManager.hasClan(u)
							&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
						ClansManager.addFarm(u, random);
					}
				}
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Este comando só é disponibilizado das **02:00** até às **02:10**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!madrugada";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
