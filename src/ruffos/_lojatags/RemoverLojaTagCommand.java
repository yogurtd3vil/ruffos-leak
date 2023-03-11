package ruffos._lojatags;

import java.awt.Color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class RemoverLojaTagCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ctx.getMember().isOwner() || u.getId().equals("380570412314001410")) {
			if (ctx.getArgs().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!removerlojatag [tag]",
						(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
						g.getName(), null, null, null, 0, true);
				return;
			}
			Role tag = null;
			if (ctx.getMessage().getMentions().getRoles().size() > 0) {
				tag = ctx.getMessage().getMentions().getRoles().get(0);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você removeu a tag " + tag.getAsMention() + " da loja de tags.",
						(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
						g.getName(), null, null, null, 0, true);
				LojaTagsManager.removerTag(g, tag.getId());
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando errado (mencione a tag):** Use c!removerlojatag [tag]",
						(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!removerlojatag";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
