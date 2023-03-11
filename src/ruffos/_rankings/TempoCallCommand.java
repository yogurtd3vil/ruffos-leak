package ruffos._rankings;

import net.dv8tion.jda.api.entities.Guild;
import ruffos._config.ConfigManager;
import ruffos._tempocall.TempoCall;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class TempoCallCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
		}
	}

	@Override
	public String getName() {
		return "c!tempocall";
	}

	@Override
	public String getHelp() {
		return "Veja as suas e o TOP 10 usuários com mais horas em call.";
	}

}
