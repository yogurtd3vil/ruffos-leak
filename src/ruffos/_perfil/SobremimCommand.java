package ruffos._perfil;

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

public class SobremimCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (!PersonagemManager.existe(g, u)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Antes de utilizar este comando, você deverá digitar `c!perfil`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use: c!sobremim [mensagem].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!EconomiaManager.hasMaos(g, u, 1000.0)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **" + Utils.getDinheiro(1000)
								+ "** em mãos para isso.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}

			String bio = "";
			for (int i = 0; i != ctx.getArgs().size(); i++) {
				if (!bio.isEmpty()) {
					bio = bio + " " + ctx.getArgs().get(i);
				} else {
					bio = ctx.getArgs().get(i);
				}
			}
			final String b = bio;
			if (bio.length() > 140) {
				int bioApagar = bio.length() - 140;
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Erro:** A frase não pode conter mais de **140** caractéres. Você deverá apagar: **"
						+ bioApagar + "** caractéres.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			} else if (bio.toString().contains(";")) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** A frase não pode conter ';'.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			eb.setDescription("Deseja realmente gastar **" + Utils.getDinheiro(1000)
					+ "** e alterar a mensagem de **SOBRE MIM** para \"**" + bio.toString() + "**\"?");
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setTimestamp(Instant.now());
			eb.setFooter(g.getName());
			Button sim = Button.success("simbio;" + u.getId() + ";" + bio.toString(),
					Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
					nao = Button.danger("naobio;" + u.getId() + ";" + bio.toString(),
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
		if (split[0].equals("simbio")) {
			String us = split[1];
			String bio = split[2];
			if (event.getUser().getId().equals(us)) {
				event.getMessage().delete().queue();
				User u = event.getUser();
				if (EconomiaManager.hasMaos(g, u, 1000.0)) {
					EconomiaManager.removeDinheiroMaos(g, u, 1000.0);
					PersonagemManager.mudar(g, u, "sobremim", bio);
					Utils.enviarEmbed(tc, u, null, "Sua bio foi alterada! Frase: \"**" + bio + "**\"!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				} else {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(1000) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				}
			}
		} else if (split[0].equals("naobio")) {
			String us = split[1];
			if (event.getUser().getId().equals(us)) {
				event.getMessage().delete().queue();
			}
		}
	}

	@Override
	public String getName() {
		return "c!sobremim";
	}

	@Override
	public String getHelp() {
		return "Altere sua bio no perfil.";
	}

}
