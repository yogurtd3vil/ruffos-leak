package ruffos._rankings;

import net.dv8tion.jda.api.entities.Guild;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class TopDinheiroCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			EconomiaManager.enviarTopMoney(ctx.getChannel(), ctx.getAuthor());
		}
	}

	@Override
	public String getName() {
		return "c!topdinheiro";
	}

	@Override
	public String getHelp() {
		return "Veja o top 10 usuários mais ricos do servidor.";
	}

}
