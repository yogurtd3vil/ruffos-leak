package ruffos._economia;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ruffos._config.ConfigManager;
import ruffos._leveis.LeveisManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class EnviarCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (!Utils.isFakeGuild(g)) {
				if (LeveisManager.getLevel(u.getId(), g) < 30) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você deve alcançar o level **30** para enviar dinheiro.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
			}
			if (ctx.getArgs().size() != 2) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Comando incompleto:** Use c!enviar @usuário [valor].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getMessage().getMentions().getMembers().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!enviar @usuário [valor].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			User enviar = ctx.getMessage().getMentions().getMembers().get(0).getUser();
			if (!Utils.isDouble(ctx.getArgs().get(1))) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!enviar @usuário [valor].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			/**
			 * if (DueloManager.hasDuelo(u.getId()) ||
			 * DesafioDueloManager.hasDesafio(u.getId())) {
			 * Utils.enviarEmbed(ctx.getChannel(), u, null,
			 * Utils.getEmote("nao").getFormatted() + " **Erro:** Você não pode enviar
			 * dinheiro enquanto está em um duelo ou um desafio de duelo.",
			 * ((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g,
			 * "cor")) : null)), g.getName(), null, null, null, 0, true); return; }
			 **/
			double quantidade = Double.parseDouble(ctx.getArgs().get(1));
			if (quantidade >= 100.0) {
				if (u != enviar) {
					if (!EconomiaManager.hasMaos(g, u, quantidade)) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted()
										+ " **Erro:** Você não possui dinheiro em mãos suficiente para isso.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					EconomiaManager.removeDinheiroMaos(g, u, quantidade);
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTimestamp(Instant.now());
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setFooter(g.getName());
					eb.setColor(
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
					eb.setDescription("Você realmente deseja enviar **" + Utils.getDinheiro(quantidade) + "** para "
							+ enviar.getAsMention() + "?");
					Button sim = Button.success("simenviar;" + u.getId() + ";" + enviar.getId() + ";" + quantidade,
							Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
							nao = Button.danger("naoenviar;" + u.getId() + ";" + enviar.getId() + ";" + quantidade,
									Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
						msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
						msg.delete().queueAfter(30, TimeUnit.SECONDS);
					});
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Use outro usuário além de você.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				}
			} else {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** O valor deve ser maior ou igual à **"
								+ Utils.getDinheiro(100) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Button b = event.getButton();
		Guild g = event.getGuild();
		String[] split = b.getId().split(";");
		User u = event.getUser();
		if (split[0].equals("simenviar") && split[1].equals(event.getUser().getId())) {
			event.getMessage().delete().queue();
			User enviar = g.retrieveMemberById(split[2]).complete().getUser();
			double qnt = Double.parseDouble(split[3]);
			if (!EconomiaManager.existeUser(g, enviar)) {
				EconomiaManager.criarUser(g, enviar, qnt);
			} else {
				EconomiaManager.addDinheiroMaos(g, enviar, qnt);
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTimestamp(Instant.now());
			eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			eb.setFooter(g.getName());
			eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
			eb.setDescription("Você enviou **" + Utils.getDinheiro(qnt) + "** para " + enviar.getAsMention() + ".");
			event.getChannel().sendMessageEmbeds(eb.build()).queue();
			/**
			 * TextChannel logs = jda.getTextChannelById("750846709604810923");
			 * logs.sendMessageEmbeds(Utils.getEmote("freemoney").getAsMention() + " **ENVIO
			 * DE DINHEIRO**\n\n**Usuário:** `" + u.getAsTag() + "` - `" + u.getId() +
			 * "`.\n**Enviado para:** `" + e.getAsTag() + "` - `" + e.getId() +
			 * "`.\n**Dinheiro enviado:** `" +
			 * Utils.getDinheiro(envio.get(message).getQuantidade()) + "`.\n**Servidor:** `"
			 * + g.getName() + "` - `" + g.getId() + "`.").queue();
			 **/
		} else if (split[0].equals("naoenviar")) {
			String us = split[1];
			if (event.getUser().getId().equals(us)) {
				event.getMessage().delete().queue();
			}
		}
	}

	@Override
	public String getName() {
		return "c!enviar";
	}

	@Override
	public String getHelp() {
		return "Envia dinheiro para um membro.";
	}

}
