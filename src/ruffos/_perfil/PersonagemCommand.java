package ruffos._perfil;

import java.awt.Color;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class PersonagemCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Comando incompleto:** Use c!personagem [masculino/feminino] [branco/nego] ou c!personagem remover.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getArgs().get(0).equalsIgnoreCase("set")) {
				if (u.getId().equalsIgnoreCase("380570412314001410")
						|| u.getId().equalsIgnoreCase("261778260025671681")) {
					if (ctx.getArgs().size() != 3) {
						return;
					}
					User user = ctx.getJDA().retrieveUserById(ctx.getArgs().get(1)).complete();
					if (user == null) {
						return;
					}
					PersonagemManager.mudar(g, user, "avatar", ctx.getArgs().get(2));
					Utils.enviarEmbed(ctx.getChannel(), u, null, "Personagem de " + user.getAsMention() + " alterado.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("remover")) {
				if (!PersonagemManager.existe(g, u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não possui um personagem.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasMaos(g, u, 10000)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(10000) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 10000);
				PersonagemManager.deletar(g, u);
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Você removeu seu personagem e pagou **"
						+ Utils.getDinheiro(10000)
						+ "**! Para criar outro, utilize o comando **c!personagem [masculino/feminino] [branco/nego]**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!PersonagemManager.existe(g, u)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Antes de utilizar este comando, use `c!perfil`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!PersonagemManager.get(g, u, "sexo").equals("sexo")) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Erro:** Você já possui um personagem. Caso queira fazer uma alteração, contate <@478868013219577876>.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			String sexo = ctx.getArgs().get(0).toLowerCase();
			String corPessoa = ctx.getArgs().get(1).toLowerCase();
			if (!sexo.equals("masculino") && !sexo.equals("feminino")) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você apenas pode utilizar **masculino** ou **feminino**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!corPessoa.equals("branco") && !corPessoa.equals("nego")) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você apenas pode utilizar **branco** ou **nego**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if(corPessoa.equals("nego")) {
				corPessoa = "negro";
			}
			PersonagemManager.mudar(g, u, "avatar", Utils.getLink(sexo, corPessoa));
			PersonagemManager.mudar(g, u, "sexo", sexo);
			PersonagemManager.mudar(g, u, "corpessoa", corPessoa);
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					"Você criou seu personagem! Você é do sexo **" + sexo + "** e é **" + corPessoa
							+ "(a)**!\n\nVocê pode adquirir uma roupa para seu personagem utilizando `c!loja`!",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
		}
	}

	@Override
	public String getName() {
		return "c!personagem";
	}

	@Override
	public String getHelp() {
		return "Crie seu personagem.";
	}

}
