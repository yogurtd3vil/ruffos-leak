package ruffos._administrativos;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._inventario.InventarioManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class AddInventarioCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (u.getId().equals("380570412314001410") || u.getId().equals("261778260025671681")) {
			if (ctx.getArgs().size() < 3) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"<a:nao_ruffos:717039420389458021> Use: c!addinventario [usuário] [quantidade] [item]",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (Utils.getMentionedMember(g, ctx.getArgs().get(0)) != null) {
				Member m = Utils.getMentionedMember(g, ctx.getArgs().get(0));
				if (Utils.isInteger(ctx.getArgs().get(1))) {
					int qnt = Integer.parseInt(ctx.getArgs().get(1));
					StringBuilder sb = new StringBuilder();
					for (int i = 2; i != ctx.getArgs().size(); i++) {
						sb.append(ctx.getArgs().get(i)).append(" ");
					}
					String item = StringUtils.chop(sb.toString());
					InventarioManager.addItem(g, m.getUser(), item, qnt);
					Utils.enviarEmbed(ctx.getChannel(), u, null, "Item adicionado: `" + qnt + "x " + item + "`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,

							"<a:nao_ruffos:717039420389458021> Use: c!addinventario [usuário] [quantidade] [item]",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				}
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"<a:nao_ruffos:717039420389458021> Use: c!addinventario [usuário] [quantidade] [item]",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!addinventario";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
