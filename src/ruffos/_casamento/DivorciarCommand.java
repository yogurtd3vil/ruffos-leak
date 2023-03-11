package ruffos._casamento;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
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

public class DivorciarCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (!CasamentoManager.casado(g, u.getId())) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está casado(a).",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			long idCasadoCom = Long.parseLong(CasamentoManager.getParceiro(g, u.getId()));
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("💔 PEDIDO DE DIVÓRCIO!");
			eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			eb.setTimestamp(Instant.now());
			eb.setFooter(g.getName());
			eb.setDescription(u.getAsMention() + " diz: **<@" + idCasadoCom
					+ ">, após eu pegar seus nitros, seu dinheiro, seu cartão de crédito, ter web abusado de você e te visto em cam, vamos web terminar!**");
			Button sim = Button.success("simdivorciar;" + u.getId() + ";" + idCasadoCom,
					Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
					nao = Button.danger("naodivorciar;" + u.getId() + ";" + idCasadoCom,
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
		if (split[0].equals("simdivorciar")) {
			String casar1 = split[1];
			User ucasar1 = event.getGuild().retrieveMemberById(casar1).complete().getUser();
			if (event.getUser().getId().equals(casar1)) {
				event.getMessage().delete().queue();
				if (!EconomiaManager.hasMaos(g, ucasar1, 15000)) {
					Utils.enviarEmbed(tc, ucasar1, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(15000) + "** em mãos para divorciar-se.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, ucasar1, 15000);
				Utils.enviarEmbed(tc, null, "💔 DIVÓRCIO!",
						ucasar1.getAsMention() + " divorciou-se de <@"
								+ CasamentoManager.getParceiro(g, ucasar1.getId()) + ">!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				CasamentoManager.divorciar(g, ucasar1.getId());
			}
		} else if (split[0].equals("naodivorciar")) {
			String casar1 = split[1];
			if (event.getUser().getId().equals(casar1)) {
				event.getMessage().delete().queue();

			}
		}
	}

	@Override
	public String getName() {
		return "c!divorciar";
	}

	@Override
	public String getHelp() {
		return "Web divorcie!";
	}

}
