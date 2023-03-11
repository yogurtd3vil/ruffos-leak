package ruffos._economia;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import ruffos._config.ConfigManager;
import ruffos._leveis.LeveisManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class EmpregosCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(Utils.empregos.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setDescription("Selecione um dos empregos abaixo:");
			eb.setFooter(g.getName());
			eb.setTimestamp(Instant.now());
			SelectMenu.Builder sm = SelectMenu.create("empregos;" + u.getId())
					.addOption("Lixeiro | Level 0", "lixeiro",
							Emoji.fromFormatted(Utils.getEmote("lixeiro").getFormatted()))
					.addOption("Jardineiro | Level 10", "jardineiro",
							Emoji.fromFormatted(Utils.getEmote("jardineiro").getFormatted()))
					.addOption("Prostituta | Level 15", "prostituta",
							Emoji.fromFormatted(Utils.getEmote("puta").getFormatted()))
					.addOption("Manobrista | Level 20", "manobrista",
							Emoji.fromFormatted(Utils.getEmote("manobrista").getFormatted()))
					.addOption("Motorista | Level 25", "motorista",
							Emoji.fromFormatted(Utils.getEmote("motorista").getFormatted()))
					.addOption("Garçom | Level 30", "garcom",
							Emoji.fromFormatted(Utils.getEmote("garcom").getFormatted()))
					.addOption("Recepcionista | Level 35", "recepcionista",
							Emoji.fromFormatted(Utils.getEmote("recepcionista").getFormatted()))
					.addOption("Governanta | Level 40", "governanta",
							Emoji.fromFormatted(Utils.getEmote("governanta").getFormatted()))
					.addOption("Chefe de cozinha | Level 50", "cozinheiro",
							Emoji.fromFormatted(Utils.getEmote("cozinheiro").getFormatted()))
					.addOption("Sommelier | Level 60", "sommelier",
							Emoji.fromFormatted(Utils.getEmote("sommelier").getFormatted()))
					.addOption("Bombeiro | Level 70", "bombeiro",
							Emoji.fromFormatted(Utils.getEmote("bombeiro").getFormatted()))
					.addOption("Policial | Level 80", "policial",
							Emoji.fromFormatted(Utils.getEmote("policial").getFormatted()))
					.addOption("Médico | Level 90", "medico",
							Emoji.fromFormatted(Utils.getEmote("medico").getFormatted()))
					.addOption("Doleiro | Level 100", "doleiro",
							Emoji.fromFormatted(Utils.getEmote("doleiro").getFormatted()));
			ctx.getChannel().sendMessageEmbeds(eb.build()).queue((message) -> {
				message.editMessageComponents(ActionRow.of(sm.build())).queue();
				message.delete().queueAfter(30, TimeUnit.SECONDS);
			}, (failure) -> {
				return;
			});
		}
	}

	@Override
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
		User u = event.getUser();
		Guild g = event.getGuild();
		if (event.getSelectMenu().getId().equals("empregos;" + u.getId())) {
			TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
			event.getMessage().delete().queue();
			if (event.getSelectedOptions().get(0).getValue().equals("lixeiro")) {
				String profissao = "Lixeiro";
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("manobrista").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("jardineiro")) {
				String profissao = "Jardineiro";
				if (LeveisManager.getLevel(u.getId(), g) < 10) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 10. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("manobrista").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("prostituta")) {
				String profissao = "Prostituta";
				if (LeveisManager.getLevel(u.getId(), g) < 15) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 15. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("manobrista").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("manobrista")) {
				String profissao = "Manobrista";
				if (LeveisManager.getLevel(u.getId(), g) < 20) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 20. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("manobrista").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("motorista")) {
				String profissao = "Motorista";
				if (LeveisManager.getLevel(u.getId(), g) < 25) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 25. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("motorista").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("garcom")) {
				String profissao = "Garçom";
				if (LeveisManager.getLevel(u.getId(), g) < 30) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 30. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("garcom").getFormatted() + " Agora, você estará trabalhando na profissão de: **"
								+ profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("recepcionista")) {
				String profissao = "Recepcionista";
				if (LeveisManager.getLevel(u.getId(), g) < 35) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 35. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("recepcionista").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("governanta")) {
				String profissao = "Governanta";
				if (LeveisManager.getLevel(u.getId(), g) < 40) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 40. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("governanta").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("cozinheiro")) {
				String profissao = "Chefe de cozinha";
				if (LeveisManager.getLevel(u.getId(), g) < 50) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 50. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("cozinheiro").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("sommelier")) {
				String profissao = "Sommelier";
				if (LeveisManager.getLevel(u.getId(), g) < 60) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 60. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("sommelier").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("bombeiro")) {
				String profissao = "Bombeiro";
				if (LeveisManager.getLevel(u.getId(), g) < 70) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 70. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("bombeiro").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("policial")) {
				String profissao = "Policial";
				if (LeveisManager.getLevel(u.getId(), g) < 80) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 80. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("policial").getFormatted()
								+ " Agora, você estará trabalhando na profissão de: **" + profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (event.getSelectedOptions().get(0).getValue().equals("medico")) {
				String profissao = "Médico";
				if (LeveisManager.getLevel(u.getId(), g) < 90) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 90. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("medico").getFormatted() + " Agora, você estará trabalhando na profissão de: **"
								+ profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);

			} else if (event.getSelectedOptions().get(0).getValue().equals("doleiro")) {
				String profissao = "Doleiro";
				if (LeveisManager.getLevel(u.getId(), g) < 100) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Não foi possível selecionar esta profissão pois seu level é inferior à 100. Use: `c!perfil`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasTrabalho(g, u)) {
					EconomiaManager.setTrabalho(g, u, profissao);
				} else {
					EconomiaManager.updateTrabalho(g, u, profissao);
				}
				Utils.enviarEmbed(tc, u, null,
						Utils.getEmote("medico").getFormatted() + " Agora, você estará trabalhando na profissão de: **"
								+ profissao + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!empregos";
	}

	@Override
	public String getHelp() {
		return "Selecione um emprego.";
	}

}
