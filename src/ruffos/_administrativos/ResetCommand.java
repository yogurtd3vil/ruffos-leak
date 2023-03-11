package ruffos._administrativos;

import net.dv8tion.jda.api.entities.Guild;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class ResetCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		if (ctx.getMember().isOwner()) {
			Guild g = ctx.getGuild();
			String canal = null, cor = null, chatgeral = null, dirigivelmsgs = null, dirigiveltime = null,
					magnata = null, clans = null, fake = null;
			if (ConfigManager.getConfig(g, "canal") != null) {
				canal = ConfigManager.getConfig(g, "canal");
			}
			if (ConfigManager.temCfg(g, "cor")) {
				cor = ConfigManager.getConfig(g, "cor");
			}
			if (ConfigManager.temCfg(g, "chatgeral")) {
				chatgeral = ConfigManager.getConfig(g, "chatgeral");
			}
			if (ConfigManager.temCfg(g, "dirigivelmsgs")) {
				dirigivelmsgs = ConfigManager.getConfig(g, "dirigivelmsgs");
			}
			if (ConfigManager.temCfg(g, "dirigiveltime")) {
				dirigivelmsgs = ConfigManager.getConfig(g, "dirigiveltime");
			}
			if (ConfigManager.temCfg(g, "magnata")) {
				dirigivelmsgs = ConfigManager.getConfig(g, "magnata");
			}
			if (ConfigManager.temCfg(g, "clans")) {
				dirigivelmsgs = ConfigManager.getConfig(g, "clans");
			}
			if (ConfigManager.temCfg(g, "fake")) {
				dirigivelmsgs = ConfigManager.getConfig(g, "fake");
			}
			Main.getDatabase().deletarGuild(g);
			Main.getDatabase().criarGuild(g);
			ConfigManager.configurar(g, "canal", canal);
			ConfigManager.configurar(g, "cor", cor);
			ConfigManager.configurar(g, "chatgeral", chatgeral);
			ConfigManager.configurar(g, "dirigivelmsgs", dirigivelmsgs);
			ConfigManager.configurar(g, "dirigiveltime", dirigiveltime);
			ConfigManager.configurar(g, "magnata", magnata);
			ConfigManager.configurar(g, "clans", clans);
			ConfigManager.configurar(g, "fake", fake);
			ctx.getChannel().sendMessage("foi").queue();
		}
	}

	@Override
	public String getName() {
		return "c!resetall";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
