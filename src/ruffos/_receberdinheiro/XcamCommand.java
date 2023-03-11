package ruffos._receberdinheiro;

import java.awt.Color;
import java.util.Date;
import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos._leveis.LeveisManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class XcamCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() != 1 || Utils.getMentionedMember(g, ctx.getArgs().get(0)) == null) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!xcam @usuário ou ID.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			Member membro = Utils.getMentionedMember(g, ctx.getArgs().get(0));
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "xcam") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " Aguarde, você só poderá tentar fazer X Cam novamente em **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "xcam") - time) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			CooldownsManager.addCooldown(g, u.getId(), "xcam", Long.parseLong("1200000"));
			Random r = new Random();
			int random = r.nextInt(2000);
			while (random < 1200) {
				random = r.nextInt(2000);
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
					"Você fez X Cam com " + membro.getAsMention() + "! Você ganhou **" + Utils.getDinheiro(random)
							+ "** e **" + xp + "** de XP!",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
		}
	}

	@Override
	public String getName() {
		return "c!xcam";
	}

	@Override
	public String getHelp() {
		return "Fazer GF, recebe XP e dinheiro";
	}

}
