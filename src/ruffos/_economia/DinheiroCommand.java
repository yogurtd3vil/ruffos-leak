package ruffos._economia;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._prisao.PresosManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class DinheiroCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				double dinheiroMaos = 0.0;
				double dinheiroBanco = 0.0;
				if (EconomiaManager.existeUser(g, u)) {
					dinheiroMaos = EconomiaManager.getDinheiroMaos(g, u);
					dinheiroBanco = EconomiaManager.getDinheiroBanco(g, u);
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.addField("Dinheiro em mãos:", "" + Utils.getDinheiro(dinheiroMaos), true);
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.addField("Dinheiro no banco:", "" + Utils.getDinheiro(dinheiroBanco), true);
				eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
			} else if (ctx.getArgs().size() == 1) {
				if (ctx.getMessage().getMentions().getMembers().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!dinheiro @usuário.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				u = ctx.getMessage().getMentions().getMembers().get(0).getUser();
				double dinheiroMaos = 0.0;
				double dinheiroBanco = 0.0;
				if (EconomiaManager.existeUser(g, u)) {
					dinheiroMaos = EconomiaManager.getDinheiroMaos(g, u);
					dinheiroBanco = EconomiaManager.getDinheiroBanco(g, u);
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.addField("Dinheiro em mãos:", "" + Utils.getDinheiro(dinheiroMaos), true);
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.addField("Dinheiro no banco:", "" + Utils.getDinheiro(dinheiroBanco), true);
				eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
				String desc = null;
				if (PresosManager.estaPreso(g, u)) {
					SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
					desc = "Este(a) usuário(a) está preso desde o dia **" + fmt.format(PresosManager.getDiaPreso(g, u))
							+ "**!\n\nMotivo: **" + PresosManager.getMotivo(g, u) + "**\nA pena acaba em **"
							+ Utils.getTime(PresosManager.getPena(g, u) - System.currentTimeMillis())
							+ "**.\n\nO(A) usuário(a) pode ser solto a qualquer momento digitando o comando **c!fianca** ou pedir para algum amigo pagar para ele utilizando **c!fianca "
							+ u.getAsMention() + "**!";
				}
				eb.setDescription(desc);
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
			}
		}

	}

	@Override
	public String getName() {
		return "c!dinheiro";
	}

	@Override
	public String getHelp() {
		return "Veja seu dinheiro.";
	}

}
