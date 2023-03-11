package ruffos.commands;

import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class EvalCommand implements ICommand {

	private final GroovyShell engine;
	private final String imports;

	public EvalCommand() {
		this.engine = new GroovyShell();
		this.imports = "import java.io.*\n" + "import java.lang.*\n" + "import java.util.*\n"
				+ "import java.util.concurrent.*\n" + "import net.dv8tion.jda.api.*\n"
				+ "import net.dv8tion.jda.api.entities.*\n" + "import net.dv8tion.jda.api.entities.impl.*\n"
				+ "import net.dv8tion.jda.api.managers.*\n" + "import net.dv8tion.jda.api.managers.impl.*\n"
				+ "import net.dv8tion.jda.api.utils.*\n";
	}

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		if (u.getId().equals("380570412314001410")) {
			if (ctx.getArgs().size() == 0) {
				return;
			}
			try {
				engine.setProperty("args", ctx.getArgs());
				engine.setProperty("event", MessageReceivedEvent.class);
				engine.setProperty("message", ctx.getMessage());
				engine.setProperty("channel", ctx.getChannel());
				engine.setProperty("jda", ctx.getJDA());
				engine.setProperty("guild", ctx.getGuild());
				engine.setProperty("member", ctx.getMember());
				String script = imports + ctx.getMessage().getContentRaw().split("\\s+", 2)[1];
				Object out = engine.evaluate(script);
				System.out.println(script);

				ctx.getChannel().sendMessage("Eval realizado. Verifique o console!").queue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public String getName() {
		return "c!eval";
	}

	@Override
	public String getHelp() {
		return "Eval";
	}

}
