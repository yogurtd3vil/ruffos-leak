package ruffos._pd;

import java.awt.Color;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class PrimeiraDamaCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Member m = ctx.getMember();
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		ctx.getMessage().delete().queue();
		if (m.hasPermission(Permission.ADMINISTRATOR)) {
			if (ctx.getArgs().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!pd [listar/@usuário ou ID]",
						Color.PINK, g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getArgs().get(0).equalsIgnoreCase("listar")) {
				PDManager.sendPD(ctx.getChannel());
			} else {
				if (Utils.getMentionedMember(g, ctx.getArgs().get(0)) == null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Use c!pd [listar/@usuário ou ID]",
							Color.PINK, g.getName(), null, null, null, 0, true);
					return;
				}
				Member pd = Utils.getMentionedMember(g, ctx.getArgs().get(0));
				if (PDManager.getPD(m, g) == 2 && !m.isOwner()) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Máximo de damas excedido (2).",
							Color.PINK, g.getName(), null, null, null, 0, true);
					return;
				}
				if (PDManager.hasPD(m.getId(), pd.getId(), g)) {
					PDManager.remover(m.getId(), pd.getId(), g);
					Utils.enviarEmbed(ctx.getChannel(), u, null, pd.getAsMention() + " não é mais sua Primeira Dama!",
							Color.PINK, g.getName(), null,
							"https://cdn.discordapp.com/attachments/915983664318394389/937083456029085776/BANNER_PRIOMEIRA_DAMA.png",
							null, 0, true);
					g.removeRoleFromMember(pd, g.getRoleById("937094108038709258")).queue();
				} else {
					PDManager.add(m.getId(), pd.getId(), g);
					Utils.enviarEmbed(ctx.getChannel(), u, null, pd.getAsMention() + " agora é sua Primeira Dama!",
							Color.PINK, g.getName(), null,
							"https://cdn.discordapp.com/attachments/915983664318394389/937083456029085776/BANNER_PRIOMEIRA_DAMA.png",
							null, 0, true);
					g.addRoleToMember(pd, g.getRoleById("937094108038709258")).queue();
				}
			}
		}
	}

	@Override
	public String getName() {
		return "c!pd";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
