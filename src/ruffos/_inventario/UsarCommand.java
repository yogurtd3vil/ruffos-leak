package ruffos._inventario;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._perfil.PersonagemManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class UsarCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!usar [roupa] - Veja suas roupas em: `c!inventario`.",
						Color.GREEN, g.getName(), null, null, null, 0, true);
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i != ctx.getArgs().size(); i++) {
				sb.append(ctx.getArgs().get(i)).append(" ");
			}
			String roupa = StringUtils.chop(sb.toString());
			if (InventarioManager.hasRoupa(g, u, roupa)) {
				PersonagemManager.mudar(g, u, "avatar", InventarioManager.getRoupa(g, u, roupa));
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Roupa alterada! Veja utilizando: `c!perfil`.", Color.GREEN,
						g.getName(), null, null, null, 0, true);
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você não possui este item em seu inventário.",
						Color.GREEN, g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!usar";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
