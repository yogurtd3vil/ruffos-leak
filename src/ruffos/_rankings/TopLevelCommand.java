package ruffos._rankings;

import net.dv8tion.jda.api.entities.Guild;
import ruffos._config.ConfigManager;
import ruffos._leveis.LeveisManager;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class TopLevelCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			LeveisManager.enviarTOPLevel(ctx.getChannel());
		}
	}

	@Override
	public String getName() {
		return "c!toplevel";
	}

	@Override
	public String getHelp() {
		return "Veja o top 10 usuários com os maiores leveis do servidor.";
	}

}
