package ruffos._administrativos;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class AddMoneyCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (u.getId().equals("380570412314001410") || u.getId().equals("592538329413582857")
				|| (u.getId().equals("905355892751474748") && g.getId().equals("922788916438913055"))
				|| (u.getId().equals("905355892751474748") && g.getId().equals("741453415082885281"))
				|| ((u.getId().equals("890062478367678555") || u.getId().equals("751092108806324255")
						|| u.getId().equals("938637565270061146")) && g.getId().equals("968939697046970429"))
				|| (u.getId().equals("416640382248681483") && g.getId().equals("1020184969022279750"))) {
			if (ctx.getArgs().size() != 2) {
				Utils.enviarEmbed(ctx.getChannel(), null, "<a:nao_ruffos:717039420389458021> Comando incompleto",
						"Use c!addmoney [usuário] [valor]", null, null, null, null, u.getAvatarUrl(), 0, true);
				return;
			}
			if (ctx.getMessage().getMentions().getUsers().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), null, "<a:nao_ruffos:717039420389458021> Erro",
						"Use c!addmoney [usuário] [valor]", null, null, null, null, u.getAvatarUrl(), 0, true);
				return;
			}
			User m = ctx.getMessage().getMentions().getUsers().get(0);
			double valor = 0;
			try {
				valor = Integer.parseInt(ctx.getArgs().get(1));
			} catch (NumberFormatException e) {
				Utils.enviarEmbed(ctx.getChannel(), null, "<a:nao_ruffos:717039420389458021> Erro",
						"Use c!addmoney [usuário] [valor]", null, null, null, null, u.getAvatarUrl(), 0, true);
				return;
			}
			EconomiaManager.addDinheiroBanco(g, m, valor);
			Utils.enviarEmbed(ctx.getChannel(), null, "<a:sim_ruffos:717039486646616155> Dinheiro adicionado",
					"**" + Utils.getDinheiro(valor) + "** adicionado para " + m.getAsMention(), null, null, null, null,
					u.getAvatarUrl(), 0, true);
		}
	}

	@Override
	public String getName() {
		return "c!addmoney";
	}

	@Override
	public String getHelp() {
		return "Adicionar dinheiro, apenas donos.";
	}

}
