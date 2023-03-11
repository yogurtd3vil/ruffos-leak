package ruffos._perfil;

import java.awt.Color;
import java.lang.reflect.Field;
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

public class CorCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Comando incompleto:** Use: c!cor [cor]\n\nUse um código de cor ou o nome da cor em inglês.\nExemplo: c!cor yellow ou c!cor #f6ff00.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			String color = ctx.getArgs().get(0);
			if (!EconomiaManager.hasMaos(g, u, 1000.0)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **" + Utils.getDinheiro(1000)
								+ "** em mãos para isso.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			Color cor;
			try {
				Field field = Class.forName("java.awt.Color").getField(color);
				cor = (Color) field.get(null);
				final Color c = cor;
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(
						"Deseja realmente gastar **" + Utils.getDinheiro(1000) + "** e alterar a cor do seu perfil?");
				eb.setColor(cor);
				eb.setTimestamp(Instant.now());
				eb.setFooter(g.getName());
				Button sim = Button.success("simcor;" + u.getId() + ";" + color,
						Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
						nao = Button.danger("naocor;" + u.getId() + ";" + color,
								Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
					msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
					msg.delete().queueAfter(30, TimeUnit.SECONDS);
				});
				return;
			} catch (Exception e) {

			}
			try {
				cor = Color.decode(color);
				final Color c = cor;
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(
						"Deseja realmente gastar **" + Utils.getDinheiro(1000) + "** e alterar a cor do seu perfil?");
				eb.setColor(cor);
				eb.setTimestamp(Instant.now());
				eb.setFooter(g.getName());
				Button sim = Button.success("simcor;" + u.getId() + ";" + color,
						Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
						nao = Button.danger("naocor;" + u.getId() + ";" + color,
								Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
					msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
					msg.delete().queueAfter(30, TimeUnit.SECONDS);
				});
				return;
			} catch (NumberFormatException e) {

			}
			Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
					+ " **Erro:** Use: c!cor [cor]\n\nUse um código de cor ou o nome da cor em inglês.\nExemplo: c!cor yellow ou c!cor #f6ff00.",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
		}
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Button b = event.getButton();
		Guild g = event.getGuild();
		String[] split = b.getId().split(";");
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (split[0].equals("simcor")) {
			String us = split[1];
			String color = split[2];
			Color cor;
			try {
				Field field = Class.forName("java.awt.Color").getField(color);
				cor = (Color) field.get(null);
				final Color c = cor;
				if (event.getUser().getId().equals(us)) {
					event.getMessage().delete().queue();
					User u = event.getUser();
					if (EconomiaManager.hasMaos(g, u, 1000.0)) {
						EconomiaManager.removeDinheiroMaos(g, u, 1000.0);
						PersonagemManager.mudar(g, u, "cor", color);
						Utils.enviarEmbed(tc, u, null, "A cor do seu perfil foi alterada.", cor, g.getName(), null,
								null, null, 0, true);
					} else {
						Utils.enviarEmbed(tc, u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
										+ Utils.getDinheiro(1000) + "** em mãos para isso.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
					}
				}
				return;
			} catch (Exception e) {

			}
			try {
				cor = Color.decode(color);
				final Color c = cor;
				if (event.getUser().getId().equals(us)) {
					event.getMessage().delete().queue();
					User u = event.getUser();
					if (EconomiaManager.hasMaos(g, u, 1000.0)) {
						EconomiaManager.removeDinheiroMaos(g, u, 1000.0);
						PersonagemManager.mudar(g, u, "cor", color);
						Utils.enviarEmbed(tc, u, null, "A cor do seu perfil foi alterada.", cor, g.getName(), null,
								null, null, 0, true);
					} else {
						Utils.enviarEmbed(tc, u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
										+ Utils.getDinheiro(1000) + "** em mãos para isso.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
					}
				}
			} catch (NumberFormatException e) {

			}
		} else if (split[0].equals("naocor")) {
			String us = split[1];
			if (event.getUser().getId().equals(us)) {
				event.getMessage().delete().queue();
			}
		}
	}

	@Override
	public String getName() {
		return "c!cor";
	}

	@Override
	public String getHelp() {
		return "Para alterar a cor do seu perfil.";
	}

}
