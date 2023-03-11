package ruffos.commands;

import java.awt.Color;
import java.time.Instant;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ruffos._config.ConfigManager;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class AjudaCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Ruffos - O melhor bot de economia do Discord!", ctx.getSelfUser().getAvatarUrl(),
				ctx.getSelfUser().getAvatarUrl());
		eb.setDescription(
				"Fui criado em **20/01/2020** por `anony'ᴊs 💡#9398` e descontinuado em **12/06/2021**. Estou retornando no dia **28/01/2022** para ficar e trazer diversão novamente para todos vocês!\n\nSe você tem alguma dúvida, sugestão ou precisa de suporte, clique em algum dos botões abaixo:");
		eb.setFooter(g.getName());
		eb.setTimestamp(Instant.now());
		eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
		eb.setThumbnail(ctx.getJDA().getSelfUser().getAvatarUrl());
		Button tutorial = Button.link("https://discord.com/invite/fkrUxtP", "Tutorial"),
				adquirir = Button.link("https://discord.com/invite/5KuckJD", "Adquirir o BOT"),
				regras = Button.link("https://discord.com/invite/9xNCCEE", "Regras"),
				sugestoes = Button.link("https://discord.gg/cHqtBTmqUE", "Sugestões");
		ctx.getChannel().sendMessageEmbeds(eb.build())
				.queue(msg -> msg.editMessageComponents(ActionRow.of(adquirir, tutorial, sugestoes, regras)).queue());
	}

	@Override
	public String getName() {
		return "c!ajuda";
	}

	@Override
	public String getHelp() {
		return "Apenas o comando de ajuda.";
	}

}
