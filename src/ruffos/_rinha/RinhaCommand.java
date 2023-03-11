package ruffos._rinha;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos._inventario.InventarioManager;
import ruffos.utils.EventWaiter;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class RinhaCommand implements ICommand {

	private EventWaiter waiter;

	public RinhaCommand(EventWaiter waiter) {
		this.waiter = waiter;
	}

	public static List<Rinha> rinhas = new ArrayList<>();

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ctx.getArgs().size() != 1) {
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!rinha @usuário.",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
			return;
		}
		if (Utils.getMentionedMember(g, ctx.getArgs().get(0)) == null) {
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					Utils.getEmote("nao").getFormatted() + " **Erro:** Membro mencionado não encontrado.",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
			return;
		}
		Member dois = Utils.getMentionedMember(g, ctx.getArgs().get(0));
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
		eb.setDescription("🐓 " + u.getAsMention() + " está desafiando " + dois.getAsMention()
				+ " para uma rinha de Galo valendo **" + Utils.getDinheiro(30000) + "**!\n\n" + dois.getAsMention()
				+ " tem **30 segundos** para aceitar ou recusar o desafio.");
		eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
		eb.setFooter(g.getName());
		eb.setTimestamp(Instant.now());
		ctx.getChannel().sendMessageEmbeds(eb.build()).queue(m -> {
			m.addReaction(Utils.getEmote("sim")).queue();
			m.addReaction(Utils.getEmote("nao")).queue();
			desafiar(ctx.getMember(), dois, m);
		});
	}

	public void desafiar(Member desafiou, Member desafiado, Message msg) {
		waiter.waitForEvent(MessageReactionAddEvent.class, (event) -> {
			if (!event.isFromType(ChannelType.TEXT))
				return true;
			TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
			String emote = event.getReaction().getEmoji().getName();
			Guild g = event.getGuild();
			if (!event.getUser().isBot() && msg.getId().equals(event.getMessageId())
					&& event.getUser().getId().equals(desafiado.getId())) {
				if (emote.equalsIgnoreCase("sim_ruffos")) {
					if (!InventarioManager.hasItem(g, desafiado.getUser(), "Galo", 1)) {
						msg.delete().queue();
						Utils.enviarEmbed(tc, desafiado.getUser(), null, Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você precisa de um galo para iniciar uma rinha. Adquira em: c!loja.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return true;
					} else if (!InventarioManager.hasItem(g, desafiou.getUser(), "Galo", 1)) {
						msg.delete().queue();
						Utils.enviarEmbed(tc, desafiado.getUser(), null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** " + desafiou.getAsMention()
										+ " precisa de um galo para iniciar uma rinha. Adquira em: c!loja.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return true;
					} else if (!EconomiaManager.hasMaos(g, desafiou.getUser(), 15000)) {
						msg.delete().queue();
						Utils.enviarEmbed(tc, desafiado.getUser(), null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** " + desafiou.getAsMention()
										+ " precisa de **" + Utils.getDinheiro(15000)
										+ "** em mãos para iniciar uma rinha.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return true;
					} else if (!EconomiaManager.hasMaos(g, desafiado.getUser(), 15000)) {
						msg.delete().queue();
						Utils.enviarEmbed(tc, desafiado.getUser(), null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
										+ Utils.getDinheiro(15000) + "** em mãos para iniciar uma rinha.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return true;
					}
					msg.delete().queue();
					Utils.enviarEmbed(tc, null, "🐓 Rinha de Galo iniciada!",
							"Para seu galo vencer, envie a palavra " + "**\"brigar\""
									+ "** o máximo de vezes que puder e torça para o seu Galo vencer.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					EconomiaManager.removeDinheiroMaos(g, desafiado.getUser(), 15000);
					EconomiaManager.removeDinheiroMaos(g, desafiou.getUser(), 15000);
					rinhas.add(new Rinha(desafiou, desafiado));
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							Rinha rinha = RinhaListener.getRinha(desafiou);
							if (rinha.getContagemUm() > rinha.getContagemDois()) {
								int contagemUm = rinha.getContagemUm();
								int contagemDois = rinha.getContagemDois();
								Member um = rinha.getUm();
								Member dois = rinha.getDois();
								rinhas.remove(RinhaListener.getRinha(desafiou));
								Utils.enviarEmbed(tc, null, "🐓 Rinha de Galo finalizada!",
										"O galo de " + um.getAsMention() + " venceu o galo de " + dois.getAsMention()
												+ "!\n\nPontos de " + um.getAsMention() + ": **" + contagemUm
												+ "**.\nPontos de: " + dois.getAsMention() + ": **" + contagemDois
												+ "**.\n\nPremiação final: **" + Utils.getDinheiro(30000) + "**.",
										((ConfigManager.temCfg(g, "cor")
												? Color.decode(ConfigManager.getConfig(g, "cor"))
												: null)),
										g.getName(), null, null, null, 0, true);
								EconomiaManager.addDinheiroMaos(g, um.getUser(), 30000);
								InventarioManager.removeItem(g, dois.getUser(), "Galo", 1);
							} else if (rinha.getContagemDois() > rinha.getContagemUm()) {
								int contagemUm = rinha.getContagemUm();
								int contagemDois = rinha.getContagemDois();
								Member um = rinha.getUm();
								Member dois = rinha.getDois();
								rinhas.remove(RinhaListener.getRinha(desafiou));
								Utils.enviarEmbed(tc, null, "🐓 Rinha de Galo finalizada!",
										"O galo de " + dois.getAsMention() + " venceu o galo de " + um.getAsMention()
												+ "!\n\nPontos de " + um.getAsMention() + ": **" + contagemUm
												+ "**.\nPontos de: " + dois.getAsMention() + ": **" + contagemDois
												+ "**.\n\nPremiação final: **" + Utils.getDinheiro(30000) + "**.",
										((ConfigManager.temCfg(g, "cor")
												? Color.decode(ConfigManager.getConfig(g, "cor"))
												: null)),
										g.getName(), null, null, null, 0, true);
								EconomiaManager.addDinheiroMaos(g, dois.getUser(), 30000);
								InventarioManager.removeItem(g, um.getUser(), "Galo", 1);
							} else if (rinha.getContagemUm() == rinha.getContagemDois()) {
								int contagemUm = rinha.getContagemUm();
								int contagemDois = rinha.getContagemDois();
								Member um = rinha.getUm();
								Member dois = rinha.getDois();
								rinhas.remove(RinhaListener.getRinha(desafiou));
								Utils.enviarEmbed(tc, null, "🐓 Rinha de Galo finalizada!",
										"O galo de " + dois.getAsMention() + " empatou com o galo de "
												+ um.getAsMention() + "!\n\nPontos de " + um.getAsMention() + ": **"
												+ contagemUm + "**.\nPontos de: " + dois.getAsMention() + ": **"
												+ contagemDois + "**.\n\nAmbos receberam **" + Utils.getDinheiro(15000)
												+ "** de volta.",
										((ConfigManager.temCfg(g, "cor")
												? Color.decode(ConfigManager.getConfig(g, "cor"))
												: null)),
										g.getName(), null, null, null, 0, true);
								EconomiaManager.addDinheiroMaos(g, um.getUser(), 15000);
								EconomiaManager.addDinheiroMaos(g, dois.getUser(), 15000);
							}
						}
					}, 15000);
				} else if (emote.equalsIgnoreCase("nao_ruffos")) {
					msg.delete().queue(s -> {

					}, f -> {

					});
				}
			}
			return false;
		}, (event) -> {

		}, 30, TimeUnit.SECONDS, () -> {
			msg.delete().queue(s -> {

			}, f -> {

			});
		});
	}

	@Override
	public String getName() {
		return "c!rinha";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
