package ruffos._casamento;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class CasarCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!casar @usuário.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getMessage().getMentions().getMembers().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!casar @usuário.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			Member m = ctx.getMessage().getMentions().getMembers().get(0);
			if (u.getId().equals(m.getId())) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você não pode casar-se com si mesmo(a).",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (CasamentoManager.casado(g, u.getId()) || CasamentoManager.casado(g, m.getId())) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você ou " + m.getAsMention()
								+ " já possui um parceiro.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTimestamp(Instant.now());
			eb.setTitle("💍 PEDIDO DE CASAMENTO!");
			eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			eb.setFooter(g.getName());
			eb.setTimestamp(Instant.now());
			if (Utils.isFakeWorld(g)) {
				eb.setDescription(u.getAsMention() + " diz: **" + m.getAsMention()
						+ ", desde que lhe escutei pela primeira vez em call eu senti muito tesão e me veio uma imensa vontade de fazer um SF contigo, eu era meio tímido e tals... mas tudo deu certo! Depois do nosso primeiro SF, nunca mais paramos e fizemos X Cam, você se lembra daquilo? Minha bunda cabeluda na cam! Você se tocando pra mim enquanto eu estava muito ofegante, depois do nosso primeiro X Cam, tudo mudou! Eu passei a te olhar com outros Web Olhos, envolvia sentimento... E hoje tenho mais do que certeza de tudo que estou dizendo e quero lhe perguntar: Quer se Web Casar comigo e mudar nossa Web Amizade Colorida para um relacionamento quase sério?**");
			} else {
				eb.setDescription(u.getAsMention() + " diz: **" + m.getAsMention()
						+ ", desde que lhe escutei pela primeira vez em call eu senti muito tesão e me veio uma imensa vontade de fazer um GF contigo, eu era meio tímido e tals... mas tudo deu certo! Depois do nosso primeiro GF, nunca mais paramos e fizemos X Cam, você se lembra daquilo? Minha bunda cabeluda na cam! Você se tocando pra mim enquanto eu estava muito ofegante, depois do nosso primeiro X Cam, tudo mudou! Eu passei a te olhar com outros Web Olhos, envolvia sentimento... E hoje tenho mais do que certeza de tudo que estou dizendo e quero lhe perguntar: Quer se Web Casar comigo e mudar nossa Web Amizade Colorida para um relacionamento quase sério?**");
			}
			if (ConfigManager.temCfg(g, "cor")) {
				eb.setColor(Color.decode(ConfigManager.getConfig(g, "cor")));
			}
			Button sim = Button.success("simcasar;" + u.getId() + ";" + m.getId(),
					Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
					nao = Button.danger("naocasar;" + u.getId() + ";" + m.getId(),
							Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
			ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
				msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
				msg.delete().queueAfter(30, TimeUnit.SECONDS);
			});
		}
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Button b = event.getButton();
		Guild g = event.getGuild();
		String[] split = b.getId().split(";");
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (split[0].equals("simcasar")) {
			String casar1 = split[1];
			String casar2 = split[2];
			User ucasar1 = event.getGuild().retrieveMemberById(casar1).complete().getUser();
			if (event.getUser().getId().equals(casar2)) {
				event.getMessage().delete().queue();
				if (!EconomiaManager.hasMaos(g, ucasar1, 5000)) {
					Utils.enviarEmbed(tc, ucasar1, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(5000) + "** em mãos para casar.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, ucasar1, 5000);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setTitle("💍 CASAMENTO!");
				eb.setDescription("<@" + casar2 + "> aceitou o pedido de casamento de " + ucasar1.getAsMention() + "!");
				event.getChannel().sendMessageEmbeds(eb.build()).queue();
				CasamentoManager.casar(g, ucasar1.getId(), casar2);
			}
		} else if (split[0].equals("naocasar")) {
			String casar1 = split[1];
			String casar2 = split[2];
			User ucasar1 = event.getGuild().retrieveMemberById(casar1).complete().getUser();
			if (event.getUser().getId().equals(casar2)) {
				event.getMessage().delete().queue();
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setDescription("<@" + casar2 + "> recusou o pedido de casamento de " + ucasar1.getAsMention() + "!");
				event.getChannel().sendMessageEmbeds(eb.build()).queue();

			}
		}
	}

	@Override
	public String getName() {
		return "c!casar";
	}

	@Override
	public String getHelp() {
		return "Web case!";
	}

}
