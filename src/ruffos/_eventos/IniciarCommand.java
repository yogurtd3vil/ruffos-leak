package ruffos._eventos;

import java.awt.Color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class IniciarCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getMember().isOwner() || u.getId().equals("380570412314001410")
					|| (Utils.isFakeWorld(g) && u.getId().equals("618897407585026090"))
					|| (Utils.isFakeWorld(g) && u.getId().equals("648903500415107072"))) {
				if (ctx.getArgs().size() < 1) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!iniciar [evento]",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ctx.getArgs().get(0).equalsIgnoreCase("porquinho")) {
					if (Main.porquinhos.get(g).iniciado) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Porquinho da Sorte já iniciado.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					Main.porquinhos.get(g).iniciar(g);
				} else if (ctx.getArgs().get(0).equalsIgnoreCase("dirigivel")) {
					if (Dirigivel.iniciado) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Dirigível já iniciado.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					Dirigivel.start(ctx.getGuild().getTextChannelById(ConfigManager.getConfig(g, "chatgeral")));
				} else if (ctx.getArgs().get(0).equalsIgnoreCase("airdrop")) {
					if (Main.airDrop.get(g).idMSG != 0) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** AirDrop já iniciado.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					Main.airDrop.get(g).dropar(g);
				}
			}
		}
	}

	@Override
	public String getName() {
		return "c!iniciar";
	}

	@Override
	public String getHelp() {
		return "Iniciar algum 'evento', exemplo: loteria, etc...";
	}

}
