package ruffos._lojatags;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
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

public class ComprarTagCommand extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ctx.getArgs().size() != 1) {
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!comprartag [ID da compra]",
					(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
					g.getName(), null, null, null, 0, true);
			return;
		}
		int id = 0;
		if (!Utils.isInteger(ctx.getArgs().get(0))) {
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					Utils.getEmote("nao").getFormatted()
							+ " **Comando errado (insira o id):** Use c!comprartag [ID da compra]",
					(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
					g.getName(), null, null, null, 0, true);
			return;
		}
		id = Integer.parseInt(ctx.getArgs().get(0));
		int i = id;
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
		eb.setTimestamp(Instant.now());
		eb.setDescription("Tem certeza que você deseja comprar a tag <@&" + LojaTagsManager.getTagById(g, id)
				+ "> por **" + LojaTagsManager.getPrecoById(g, id) + "**?");
		eb.setFooter(g.getName());
		eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
		Button sim = Button.success(
				"simcomprat" + u.getId() + ";" + LojaTagsManager.getTagById(g, i) + ";"
						+ LojaTagsManager.getPrecoById(g, i),
				Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
				nao = Button.danger("naocomprat;" + u.getId(),
						Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
		ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
			msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
			msg.delete().queueAfter(30, TimeUnit.SECONDS);
		});
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Button b = event.getButton();
		Guild g = event.getGuild();
		String[] split = b.getId().split(";");
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (split[0].equals("simcomprart")) {
			String preco = split[3];
			User u = event.getUser();
			if (split[1].equals(u.getId())) {
				Role role = g.getRoleById(split[2]);
				event.getMessage().delete().queue();
				int p = Integer.parseInt(preco.replace("R$ ", "").replace(".", "").replace(",00", ""));
				if (!EconomiaManager.hasMaos(event.getGuild(), u, p)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não possui dinheiro em mãos o suficiente para isto.",
							(ConfigManager.temCfg(event.getGuild(), "cor")
									? Color.decode(ConfigManager.getConfig(event.getGuild(), "cor"))
									: null),
							event.getGuild().getName(), null, null, null, 0, true);
				} else {
					EconomiaManager.removeDinheiroMaos(event.getGuild(), u, p);
					event.getGuild().addRoleToMember(event.getMember(), role).queue();
					Utils.enviarEmbed(tc, u, null,
							"Você adquiriu a tag " + role.getAsMention() + " por **" + preco + "**!",
							(ConfigManager.temCfg(event.getGuild(), "cor")
									? Color.decode(ConfigManager.getConfig(event.getGuild(), "cor"))
									: null),
							event.getGuild().getName(), null, null, null, 0, true);
				}
			}
		} else if (split[0].equals("naocomprat")) {
			if (split[1].equals(event.getUser().getId())) {
				event.getMessage().delete().queue();
			}
		}
	}

	@Override
	public String getName() {
		return "c!comprartag";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
