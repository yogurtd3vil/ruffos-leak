package ruffos.utils.commands;

import java.util.Arrays;
import java.util.List;

public interface ICommand {
	void handle(CommandContext ctx);

	String getName();

	default List<String> getAliases() {
		return Arrays.asList();
	}
	
	String getHelp();

}
