package ruffos._inventario;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class InventarioCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			InventarioManager.showInventario(g, u, ctx.getChannel());
		}
	}

	@Override
	public String getName() {
		return "c!inventario";
	}

	@Override
	public String getHelp() {
		return "inventario";
	}

}
