package ruffos._lojatags;

import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class LojaTagsCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		LojaTagsManager.listarTags(ctx.getChannel(), ctx.getAuthor());
	}

	@Override
	public String getName() {
		return "c!lojatags";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
