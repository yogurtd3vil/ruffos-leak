package ruffos._lojatags;

import java.awt.Color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class AddLojaTagCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ctx.getMember().isOwner() || u.getId().equals("380570412314001410")) {
			if (ctx.getArgs().size() != 2) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!addlojatag [preço] [tag]",
						(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
						g.getName(), null, null, null, 0, true);
				return;
			}
			int preco = 0;
			Role tag = null;
			if (Utils.isInteger(ctx.getArgs().get(0))) {
				preco = Integer.parseInt(ctx.getArgs().get(0));
				if (ctx.getMessage().getMentions().getRoles().size() > 0) {
					tag = ctx.getMessage().getMentions().getRoles().get(0);
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"Você adicionou " + tag.getAsMention() + " à loja de tags por **" + Utils.getDinheiro(preco)
									+ "**!",
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
							g.getName(), null, null, null, 0, true);
					LojaTagsManager.addTag(g, tag.getId(), preco);
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Comando errado (mencione a tag):** Use c!addlojatag [preço] [tag]",
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
							g.getName(), null, null, null, 0, true);
				}
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando errado (insira o preço):** Use c!addlojatag [preço] [tag]",
						(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!addlojatag";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
