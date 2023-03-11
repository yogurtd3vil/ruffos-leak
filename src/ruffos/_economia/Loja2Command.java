package ruffos._economia;

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
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos._inventario.InventarioManager;
import ruffos._leveis.LeveisManager;
import ruffos._perfil.PersonagemManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class Loja2Command extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Member m = ctx.getMember();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setTitle("🛒 LOJA!");
				eb.setFooter(g.getName());
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				if (Utils.isTDP(g)) {
					eb.setDescription(Utils.getEmote("pistola").getFormatted() + " Loja de armas & munição\n"
							+ Utils.getEmote("bebidas").getFormatted()
							+ " Loja de bebidas\n🛍️ Loja de roupas\n💼 Loja de utilidades\n💰 Loja de VIPs");
				} else {
					eb.setDescription(Utils.getEmote("pistola").getFormatted() + " Loja de armas & munição\n"
							+ Utils.getEmote("bebidas").getFormatted()
							+ " Loja de bebidas\n🛍️ Loja de roupas\n💼 Loja de utilidades");
				}
				Button pistola = Button.success("armas;" + m.getId(),
						Emoji.fromFormatted(Utils.getEmote("pistola").getFormatted()));
				Button bebidas = Button.success("bebidas;" + m.getId(),
						Emoji.fromFormatted(Utils.getEmote("bebidas").getFormatted()));
				Button roupas = Button.success("roupas;" + m.getId(), "🛍️");
				Button utilidades = Button.success("utilidades;" + m.getId(), "💼");
				Button vip = Button.success("vip;" + m.getId(), "💰");
				eb.setTimestamp(Instant.now());
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue((message) -> {
					if (Utils.isTDP(g)) {
						message.editMessageComponents(ActionRow.of(pistola, bebidas, roupas, utilidades, vip)).queue();
					} else {
						message.editMessageComponents(ActionRow.of(pistola, bebidas, roupas, utilidades)).queue();
					}
					message.delete().queueAfter(30, TimeUnit.SECONDS, s -> {
					}, f -> {
					});
				}, (erro) -> {
					return;
				});
			}
		}
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Button b = event.getButton();
		User u = event.getUser();
		Member m = event.getMember();
		Guild g = m.getGuild();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (b.getId().equals("armas;" + u.getId())) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setFooter(m.getGuild().getName());
			eb.setAuthor(u.getName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			eb.setDescription("**__LOJA DE ARMAS & MUNIÇÃO__**\n\n" + Utils.getEmote("faca").getFormatted()
					+ " **Faca** » **" + Utils.getDinheiro(5000) + "**\n" + Utils.getEmote("glock").getFormatted()
					+ " **Glock** » **" + Utils.getDinheiro(15000) + "**\n" + Utils.getEmote("deagle").getFormatted()
					+ " **Desert Eagle** » **" + Utils.getDinheiro(20000) + "**\n"
					+ Utils.getEmote("ump45").getFormatted() + " **UMP45** » **" + Utils.getDinheiro(25000) + "**\n"
					+ Utils.getEmote("shotgun").getFormatted() + " **Shotgun** » **" + Utils.getDinheiro(30000) + "**\n"
					+ Utils.getEmote("ak47").getFormatted() + " **AK-47** » **" + Utils.getDinheiro(40000) + "**\n"
					+ Utils.getEmote("municao").getFormatted() + " **Munição (30 balas)** » **"
					+ Utils.getDinheiro(5000) + "**\n" + Utils.getEmote("colete").getFormatted()
					+ " **Colete a prova de balas** » **" + Utils.getDinheiro(40000)
					+ "**\n\nSelecione uma das opções abaixo:");
			eb.setTimestamp(Instant.now());
			SelectMenu.Builder sm = SelectMenu.create("armas;" + m.getId())
					.addOption("Faca", "faca;" + m.getId(), Emoji.fromFormatted(Utils.getEmote("faca").getFormatted()))
					.addOption("Glock", "glock;" + m.getId(),
							Emoji.fromFormatted(Utils.getEmote("glock").getFormatted()))
					.addOption("Desert Eagle", "deagle;" + m.getId(),
							Emoji.fromFormatted(Utils.getEmote("deagle").getFormatted()))
					.addOption("UMP45", "ump45;" + m.getId(),
							Emoji.fromFormatted(Utils.getEmote("ump45").getFormatted()))
					.addOption("Shotgun", "shotgun;" + m.getId(),
							Emoji.fromFormatted(Utils.getEmote("shotgun").getFormatted()))
					.addOption("AK-47", "ak47;" + m.getId(), Emoji.fromFormatted(Utils.getEmote("ak47").getFormatted()))
					.addOption("Munição", "municao;" + m.getId(),
							Emoji.fromFormatted(Utils.getEmote("municao").getFormatted()))
					.addOption("Colete a prova de balas", "colete;" + m.getId(),
							Emoji.fromFormatted(Utils.getEmote("colete").getFormatted()));
			event.getMessage().delete().queue();
			event.getChannel().sendMessageEmbeds(eb.build()).queue(msgg -> {
				msgg.editMessageComponents(ActionRow.of(sm.build())).queue();
				msgg.delete().queueAfter(30, TimeUnit.SECONDS, s -> {
				}, f -> {
				});
			});
		} else if (b.getId().equals("bebidas;" + m.getId())) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setFooter(m.getGuild().getName());
			eb.setAuthor(u.getName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			eb.setDescription("**__LOJA DE BEBIDAS__**\n\n" + Utils.getEmote("cerveja").getFormatted()
					+ " 2 Litros de Cerveja (6 copos) » **" + Utils.getDinheiro(1200) + "** \n"
					+ Utils.getEmote("vodka").getFormatted() + " 2 Garrafas de Vodka (8 copos) » **"
					+ Utils.getDinheiro(1600) + "**\n" + Utils.getEmote("gin").getFormatted()
					+ " 2 Garrafa de Gin (10 copos) » **" + Utils.getDinheiro(2000) + "**");
			eb.setTimestamp(Instant.now());
			SelectMenu.Builder sm = SelectMenu.create("bebidas;" + m.getId())
					.addOption("Cerveja", "cerveja", Emoji.fromFormatted(Utils.getEmote("cerveja").getFormatted()))
					.addOption("Vodka", "vodka", Emoji.fromFormatted(Utils.getEmote("vodka").getFormatted()))
					.addOption("Gin", "gin", Emoji.fromFormatted(Utils.getEmote("gin").getFormatted()));
			event.getMessage().delete().queue();
			event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
				msg.editMessageComponents(ActionRow.of(sm.build())).queue();
				msg.delete().queueAfter(30, TimeUnit.SECONDS, s -> {

				}, f -> {

				});
			});
		} else if (b.getId().equals("roupas;" + m.getId())) {
			event.getMessage().delete().queue();
			if (!PersonagemManager.existe(event.getGuild(), event.getUser())) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(m.getGuild().getName());
				eb.setAuthor(u.getName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
				eb.setDescription(Utils.getEmote("nao").getFormatted()
						+ " **Erro:** Você precisa criar um personagem primeiro! Use: **c!personagem**");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build())
						.queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
				return;
			}
			if (PersonagemManager.get(event.getGuild(), event.getUser(), "sexo").equals("masculino")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(m.getGuild().getName());
				eb.setTitle("🛍️ Loja de roupas - Sessão: Masculina");
				eb.setDescription("**__LOJA DE ROUPAS__**\n\n" + Utils.getEmote("nike").getFormatted() + " Nike » **"
						+ Utils.getDinheiro(20000) + "**\n" + Utils.getEmote("adidas").getFormatted() + " Adidas » **"
						+ Utils.getDinheiro(30000) + "**\n" + Utils.getEmote("lacoste").getFormatted() + " Lacoste » **"
						+ Utils.getDinheiro(60000) + "**\n" + Utils.getEmote("gucci").getFormatted() + " Gucci » **"
						+ Utils.getDinheiro(100000) + "**\n" + Utils.getEmote("boost").getFormatted()
						+ " Ruffos Boost » **GRÁTIS para BOOSTERS**\n\nCaso você queira uma roupa **EXCLUSIVA**, de qualquer marca e no estilo que você quiser (cor e etc), você pode fazer uma doação no valor de **R$ 10,00** entrando em https://discord.gg/sBcnmss e assim ajudando o Ruffos a se manter online!");
				eb.setTimestamp(Instant.now());
				SelectMenu.Builder sm = SelectMenu.create("roupas;" + m.getId())
						.addOption("Nike", "nike", Emoji.fromFormatted(Utils.getEmote("nike").getFormatted()))
						.addOption("Adidas", "adidas", Emoji.fromFormatted(Utils.getEmote("adidas").getFormatted()))
						.addOption("Lacoste", "lacoste", Emoji.fromFormatted(Utils.getEmote("lacoste").getFormatted()))
						.addOption("Gucci", "gucci", Emoji.fromFormatted(Utils.getEmote("gucci").getFormatted()))
						.addOption("Boost", "boost", Emoji.fromFormatted(Utils.getEmote("boost").getFormatted()));
				event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
					msg.editMessageComponents(ActionRow.of(sm.build())).queue();
					msg.delete().queueAfter(30, TimeUnit.SECONDS, s -> {
					}, f -> {
					});
				});
			} else if (PersonagemManager.get(event.getGuild(), event.getUser(), "sexo").equals("feminino")) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setTitle("🛍️ Loja de roupas - Sessão: Feminina");
				eb.setDescription("**__LOJA DE ROUPAS__**\n\n" + Utils.getEmote("versace").getFormatted()
						+ " Versace » **" + Utils.getDinheiro(20000) + "**\n" + Utils.getEmote("prada").getFormatted()
						+ " Prada » **" + Utils.getDinheiro(30000) + "**\n"
						+ Utils.getEmote("louisvuitton").getFormatted() + " Louis Vuitton » **"
						+ Utils.getDinheiro(60000) + "**\n" + Utils.getEmote("gucci").getFormatted() + " Gucci » **"
						+ Utils.getDinheiro(100000) + "**\n" + Utils.getEmote("boost").getFormatted()
						+ " Ruffos Boost » **GRÁTIS para BOOSTERS**\n\nCaso você queira uma roupa **EXCLUSIVA**, de qualquer marca e no estilo que você quiser (cor e etc), você pode fazer uma doação no valor de **R$ 10,00** entrando em https://discord.gg/sBcnmss e assim ajudando o Ruffos a se manter online!");
				eb.setTimestamp(Instant.now());
				SelectMenu.Builder sm = SelectMenu.create("roupas;" + m.getId())
						.addOption("Versace", "versace", Emoji.fromFormatted(Utils.getEmote("versace").getFormatted()))
						.addOption("Prada", "prada", Emoji.fromFormatted(Utils.getEmote("prada").getFormatted()))
						.addOption("Louis Vuitton", "louisvuitton",
								Emoji.fromFormatted(Utils.getEmote("louisvuitton").getFormatted()))
						.addOption("Gucci", "gucci", Emoji.fromFormatted(Utils.getEmote("gucci").getFormatted()))
						.addOption("Boost", "boost", Emoji.fromFormatted(Utils.getEmote("boost").getFormatted()));
				event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
					msg.editMessageComponents(ActionRow.of(sm.build())).queue();
					msg.delete().queueAfter(30, TimeUnit.SECONDS, s -> {
					}, f -> {
					});
				});
			} else {
				Utils.enviarEmbed(tc, event.getUser(), null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você não possui um sexo definido, crie um personagem com `c!personagem`.",
						((ConfigManager.temCfg(m.getGuild(), "cor")
								? Color.decode(ConfigManager.getConfig(m.getGuild(), "cor"))
								: null)),
						m.getGuild().getName(), null, null, null, 0, false);
			}
		} else if (b.getId().equals("utilidades;" + m.getId())) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setFooter(m.getGuild().getName());
			eb.setAuthor(u.getName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			eb.setDescription("**__LOJA DE UTILIDADES__**\n\n🐔 Galo de briga » **" + Utils.getDinheiro(30000) + "**");
			eb.setTimestamp(Instant.now());
			SelectMenu.Builder sm = SelectMenu.create("utilidades;" + m.getId()).addOption("Galo de briga",
					"galodebriga", Emoji.fromUnicode("U+1F414"));
			event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
				msg.editMessageComponents(ActionRow.of(sm.build())).queue();
				msg.delete().queueAfter(30, TimeUnit.SECONDS, s -> {
				}, f -> {
				});
			});
		} else if (b.getId().equals("vip;" + m.getId())) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setFooter(m.getGuild().getName());
			eb.setAuthor(u.getName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			eb.setDescription(
					"**__LOJA DE VIPs__**\n\nVIP Crazy » **" + Utils.getDinheiro(15000000) + "**\nVIP Magnata » **"
							+ Utils.getDinheiro(10000000) + "**\nVIP Rico » **" + Utils.getDinheiro(5000000) + "**");
			eb.setTimestamp(Instant.now());
			SelectMenu.Builder sm = SelectMenu.create("vip;" + m.getId()).addOption("VIP Crazy", "vipcrazy")
					.addOption("VIP Magnata", "vipmagnata").addOption("VIP Rico", "viprico");
			event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
				msg.editMessageComponents(ActionRow.of(sm.build())).queue();
				msg.delete().queueAfter(30, TimeUnit.SECONDS, s -> {
				}, f -> {
				});
			});
		}
	}

	@Override
	public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
		User u = event.getUser();
		Guild g = event.getGuild();
		Member m = event.getMember();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		SelectOption b = event.getSelectedOptions().get(0);
		if (event.getSelectMenu().getId().equals("armas;" + u.getId())) {
			if (b.getValue().equals("faca;" + m.getId())) {
				if (!EconomiaManager.hasMaos(g, u, 5000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 5000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("faca").getFormatted() + " " + m.getAsMention()
						+ " comprou uma **Faca** por **" + Utils.getDinheiro(5000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getMessage().delete().complete();
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasArma(g, u)) {
					EconomiaManager.setArma(g, u, "faca");
				} else {
					EconomiaManager.updateArma(g, u, "faca");
				}
			} else if (b.getValue().equals("glock;" + m.getId())) {
				if (!EconomiaManager.hasMaos(g, u, 15000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 15000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("glock").getFormatted() + " " + m.getAsMention()
						+ " comprou uma **Glock** por **" + Utils.getDinheiro(15000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasArma(g, u)) {
					EconomiaManager.setArma(g, u, "glock");
				} else {
					EconomiaManager.updateArma(g, u, "glock");
				}
			} else if (b.getValue().equals("deagle;" + m.getId())) {
				if (LeveisManager.getLevel(m.getId(), g) < 10) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você precisa chegar ao level **10** para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasMaos(g, u, 20000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 20000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("deagle").getFormatted() + " " + m.getAsMention()
						+ " comprou uma **Desert Eagle** por **" + Utils.getDinheiro(20000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasArma(g, u)) {
					EconomiaManager.setArma(g, u, "deagle");
				} else {
					EconomiaManager.updateArma(g, u, "deagle");
				}
			} else if (b.getValue().equals("ump45;" + m.getId())) {
				if (LeveisManager.getLevel(m.getId(), g) < 11) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você precisa chegar ao level **11** para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasMaos(g, u, 25000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 25000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("ump45").getFormatted() + " " + m.getAsMention()
						+ " comprou uma **UMP45** por **" + Utils.getDinheiro(25000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasArma(g, u)) {
					EconomiaManager.setArma(g, u, "ump45");
				} else {
					EconomiaManager.updateArma(g, u, "ump45");
				}
			} else if (b.getValue().equals("shotgun;" + m.getId())) {
				if (LeveisManager.getLevel(m.getId(), g) < 12) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você precisa chegar ao level **12** para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasMaos(g, u, 30000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 30000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("shotgun").getFormatted() + " " + m.getAsMention()
						+ " comprou uma **Shotgun** por **" + Utils.getDinheiro(30000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasArma(g, u)) {
					EconomiaManager.setArma(g, u, "shotgun");
				} else {
					EconomiaManager.updateArma(g, u, "shotgun");
				}
			} else if (b.getValue().equals("ak47;" + m.getId())) {
				if (LeveisManager.getLevel(m.getId(), g) < 15) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você precisa chegar ao level **15** para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasMaos(g, u, 40000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 40000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("ak47").getFormatted() + " " + m.getAsMention()
						+ " comprou uma **AK-47** por **" + Utils.getDinheiro(40000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasArma(g, u)) {
					EconomiaManager.setArma(g, u, "ak47");
				} else {
					EconomiaManager.updateArma(g, u, "ak47");
				}
			} else if (b.getValue().equals("municao;" + m.getId())) {
				if (!EconomiaManager.hasMaos(g, u, 5000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 5000.0);
				if (!EconomiaManager.hasArma(g, u)) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					eb.setFooter(g.getName());
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setDescription(Utils.getEmote("nao").getFormatted() + " **Erro:** Você não possui uma arma.");
					eb.setTimestamp(Instant.now());

					event.getMessage().delete().complete();
					event.getChannel().sendMessageEmbeds(eb.build()).complete();
					return;
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("municao").getFormatted() + " " + m.getAsMention()
						+ " comprou **Munição (30 balas)** por **" + Utils.getDinheiro(5000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getMessage().delete().complete();
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				EconomiaManager.updateArma(g, u, EconomiaManager.getArma(g, u));
			} else if (b.getValue().equals("colete;" + m.getId())) {
				if (!EconomiaManager.hasMaos(g, u, 40000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 40000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("municao").getFormatted() + " " + m.getAsMention()
						+ " comprou **Colete a prova de balas** por **" + Utils.getDinheiro(40000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getMessage().delete().complete();
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				InventarioManager.addItem(g, u, "Colete a prova de balas", 1);
			}
		} else if (event.getSelectMenu().getId().equals("bebidas;" + m.getId())) {
			if (b.getValue().equals("cerveja")) {
				if (!EconomiaManager.hasMaos(g, u, 1200.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				} else if (EconomiaManager.getCopos(g, u) != 0) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Beba todos os copos que você possui de **"
							+ Utils.getBebida(EconomiaManager.getBebida(g, u)) + "** antes de efetuar uma compra.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 1200.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("cerveja").getFormatted() + " " + u.getAsMention()
						+ " comprou **2 Litros de Cerveja (6 copos)** por **" + Utils.getDinheiro(1200) + "**!");
				eb.setTimestamp(Instant.now());

				event.getMessage().delete().complete();
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasBebida(g, u)) {
					EconomiaManager.setBebida(g, u, "cerveja", 6);
				} else {
					EconomiaManager.updateBebida(g, u, "cerveja", 6);
				}
			} else if (b.getValue().equals("vodka")) {
				if (!EconomiaManager.hasMaos(g, u, 1600.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				} else if (EconomiaManager.getCopos(g, u) != 0) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Beba todos os copos que você possui de **"
							+ Utils.getBebida(EconomiaManager.getBebida(g, u)) + "** antes de efetuar uma compra.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 1600.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("vodka").getFormatted() + " " + u.getAsMention()
						+ " comprou **2 Garrafas de Vodka (8 copos)** por **" + Utils.getDinheiro(1600) + "**!");
				eb.setTimestamp(Instant.now());

				event.getMessage().delete().complete();
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasBebida(g, u)) {
					EconomiaManager.setBebida(g, u, "vodka", 8);
				} else {
					EconomiaManager.updateBebida(g, u, "vodka", 8);
				}
			} else if (b.getValue().equals("gin")) {
				if (!EconomiaManager.hasMaos(g, u, 2000.0)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				} else if (EconomiaManager.getCopos(g, u) != 0) {
					Utils.enviarEmbed(tc, u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Beba todos os copos que você possui de **"
							+ Utils.getBebida(EconomiaManager.getBebida(g, u)) + "** antes de efetuar uma compra.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 2000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("gin").getFormatted() + " " + u.getAsMention()
						+ " comprou **2 Garrafas de Gin (10 copos)** por **" + Utils.getDinheiro(2000) + "**!");
				eb.setTimestamp(Instant.now());

				event.getMessage().delete().complete();
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				if (!EconomiaManager.hasBebida(g, u)) {
					EconomiaManager.setBebida(g, u, "gin", 10);
				} else {
					EconomiaManager.updateBebida(g, u, "gin", 10);
				}
			}
		} else if (event.getSelectMenu().getId().equals("roupas;" + m.getId())) {
			event.getMessage().delete().queue();
			if (b.getValue().equals("boost")) {
				Main.getJDA().getGuildById("717003376037986314").retrieveMemberById(m.getId()).queue(mm -> {
					if (mm.getTimeBoosted().toInstant().toEpochMilli() > 0) {
						if (PersonagemManager.get(g, u, "sexo").equals("masculino")) {
							EmbedBuilder eb = new EmbedBuilder();
							eb.setColor(
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)));
							eb.setFooter(g.getName());
							eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
							eb.setDescription(Utils.getEmote("boost").getFormatted() + " " + u.getAsMention()
									+ " está usando **Ruffos Boost** por **" + Utils.getDinheiro(0) + "**!");
							eb.setTimestamp(Instant.now());
							event.getChannel().sendMessageEmbeds(eb.build()).complete();
							String corPessoa = PersonagemManager.get(g, u, "corpessoa");
							PersonagemManager.mudar(g, u, "avatar", Utils.getLink("masculino", corPessoa, "boost"));
						} else {
							EmbedBuilder eb = new EmbedBuilder();
							eb.setColor(
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)));
							eb.setFooter(g.getName());
							eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
							eb.setDescription(Utils.getEmote("boost").getFormatted() + " " + u.getAsMention()
									+ " está usando **Ruffos Boost** por **" + Utils.getDinheiro(0) + "**!");
							eb.setTimestamp(Instant.now());
							event.getChannel().sendMessageEmbeds(eb.build()).complete();
							String corPessoa = PersonagemManager.get(g, u, "corpessoa");
							PersonagemManager.mudar(g, u, "avatar", Utils.getLink("feminino", corPessoa, "boost"));
						}
					} else {
						Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro", Utils
								.getEmote("nao").getFormatted()
								+ " **Erro:** Você precisa dar boost no servidor do Ruffos. Para acessar o mesmo: `c!ajuda`.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								event.getGuild().getName(), null, null, null, 0, true);
					}
				});
			} else if (b.getValue().equals("nike")) {
				if (!EconomiaManager.hasMaos(g, u, 20000.0)) {
					Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 20000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("nike").getFormatted() + " " + u.getAsMention()
						+ " comprou **roupas da Nike** por **" + Utils.getDinheiro(20000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				String corPessoa = PersonagemManager.get(g, u, "corpessoa");
				PersonagemManager.mudar(g, u, "avatar", Utils.getLink("masculino", corPessoa, "nike"));
				InventarioManager.addItem(g, u, "Nike Masculino;" + Utils.getLink("masculino", corPessoa, "nike"), 1);
			} else if (b.getValue().equals("adidas")) {
				if (!EconomiaManager.hasMaos(g, u, 30000.0)) {
					Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 30000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("adidas").getFormatted() + " " + u.getAsMention()
						+ " comprou **roupas da Adidas** por **" + Utils.getDinheiro(30000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				String corPessoa = PersonagemManager.get(g, u, "corpessoa");
				PersonagemManager.mudar(g, u, "avatar", Utils.getLink("masculino", corPessoa, "adidas"));
				InventarioManager.addItem(g, u, "Adidas Masculino;" + Utils.getLink("masculino", corPessoa, "adidas"),
						1);
			} else if (b.getValue().equals("lacoste")) {
				if (!EconomiaManager.hasMaos(g, u, 60000.0)) {
					Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 60000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("lacoste").getFormatted() + " " + u.getAsMention()
						+ " comprou **roupas da Lacoste** por **" + Utils.getDinheiro(60000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				String corPessoa = PersonagemManager.get(g, u, "corpessoa");
				PersonagemManager.mudar(g, u, "avatar", Utils.getLink("masculino", corPessoa, "lacoste"));
				InventarioManager.addItem(g, u, "Adidas Masculino;" + Utils.getLink("masculino", corPessoa, "adidas"),
						1);
			} else if (b.getValue().equals("gucci")) {
				if (PersonagemManager.get(g, u, "sexo").equals("masculino")) {
					if (!EconomiaManager.hasMaos(g, u, 100000.0)) {
						Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
								Utils.getEmote("nao").getFormatted()
										+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								event.getGuild().getName(), null, null, null, 0, true);
						return;
					}
					EconomiaManager.removeDinheiroMaos(g, u, 100000.0);
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					eb.setFooter(g.getName());
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setDescription(Utils.getEmote("gucci").getFormatted() + " " + u.getAsMention()
							+ " comprou **roupas da Gucci** por **" + Utils.getDinheiro(100000) + "**!");
					eb.setTimestamp(Instant.now());
					event.getChannel().sendMessageEmbeds(eb.build()).complete();
					String corPessoa = PersonagemManager.get(g, u, "corpessoa");
					PersonagemManager.mudar(g, u, "avatar", Utils.getLink("masculino", corPessoa, "gucci"));
					InventarioManager.addItem(g, u, "Gucci Masculino;" + Utils.getLink("masculino", corPessoa, "gucci"),
							1);
				} else {
					if (!EconomiaManager.hasMaos(g, u, 100000.0)) {
						Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
								Utils.getEmote("nao").getFormatted()
										+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								event.getGuild().getName(), null, null, null, 0, true);
						return;
					}
					EconomiaManager.removeDinheiroMaos(g, u, 100000.0);
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					eb.setFooter(g.getName());
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setDescription(Utils.getEmote("gucci").getFormatted() + " " + u.getAsMention()
							+ " comprou **roupas da Gucci** por **" + Utils.getDinheiro(100000) + "**!");
					eb.setTimestamp(Instant.now());
					event.getChannel().sendMessageEmbeds(eb.build()).complete();
					String corPessoa = PersonagemManager.get(g, u, "corpessoa");
					PersonagemManager.mudar(g, u, "avatar", Utils.getLink("feminino", corPessoa, "gucci"));
					InventarioManager.addItem(g, u, "Gucci Feminino;" + Utils.getLink("feminino", corPessoa, "gucci"),
							1);
				}
			} else if (b.getValue().equals("versace")) {
				if (!EconomiaManager.hasMaos(g, u, 20000.0)) {
					Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 20000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("versace").getFormatted() + " " + u.getAsMention()
						+ " comprou **roupas da Versace** por **" + Utils.getDinheiro(20000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				String corPessoa = PersonagemManager.get(g, u, "corpessoa");
				PersonagemManager.mudar(g, u, "avatar", Utils.getLink("feminino", corPessoa, "versace"));
				InventarioManager.addItem(g, u, "Versace Feminino;" + Utils.getLink("feminino", corPessoa, "versace"),
						1);
			} else if (b.getValue().equals("prada")) {
				if (!EconomiaManager.hasMaos(g, u, 30000.0)) {
					Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 30000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("prada").getFormatted() + " " + u.getAsMention()
						+ " comprou **roupas da Prada** por **" + Utils.getDinheiro(30000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				String corPessoa = PersonagemManager.get(g, u, "corpessoa");
				PersonagemManager.mudar(g, u, "avatar", Utils.getLink("feminino", corPessoa, "prada"));
				InventarioManager.addItem(g, u, "Prada Feminino;" + Utils.getLink("feminino", corPessoa, "prada"), 1);
			} else if (b.getValue().equals("louisvuitton")) {
				if (!EconomiaManager.hasMaos(g, u, 60000.0)) {
					Utils.enviarEmbed(tc, null, Utils.getEmote("nao").getFormatted() + " Erro",
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							event.getGuild().getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 60000.0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription(Utils.getEmote("louisvuitton").getFormatted() + " " + u.getAsMention()
						+ " comprou **roupas da Louis Vuitton** por **" + Utils.getDinheiro(60000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				String corPessoa = PersonagemManager.get(g, u, "corpessoa");
				PersonagemManager.mudar(g, u, "avatar", Utils.getLink("feminino", corPessoa, "louisvuitton"));
				InventarioManager.addItem(g, u,
						"Lous Vuitton Feminino;" + Utils.getLink("feminino", corPessoa, "louisvuitton"), 1);
			}
		} else if (event.getSelectMenu().getId().equals("utilidades;" + m.getId())) {
			event.getMessage().delete().queue();
			if (b.getValue().equals("galodebriga")) {
				if (!EconomiaManager.hasMaos(g, u, 30000)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 30000);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("🐔 " + u.getAsMention() + " comprou **1 Galo** por **" + Utils.getDinheiro(30000)
						+ "**!\n\nPara por seu galo para brigar contra o de algum usuário, use `c!rinha`.");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				InventarioManager.addItem(g, u, "Galo", 1);
			}
		} else if (event.getSelectMenu().getId().equals("vip;" + m.getId())) {
			event.getMessage().delete().queue();
			if (b.getValue().equals("vipcrazy")) {
				if (!EconomiaManager.hasBanco(g, u, 15000000)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em seu banco para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroBanco(g, u, 15000000);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("🐔 " + u.getAsMention() + " comprou **1 VIP Crazy** por **"
						+ Utils.getDinheiro(15000000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				event.getJDA().retrieveUserById("589098447332835328").complete().openPrivateChannel().complete()
						.sendMessage(event.getMember().getAsMention() + " comprou VIP Crazy.").queue();
			} else if (b.getValue().equals("vipmagnata")) {
				if (!EconomiaManager.hasBanco(g, u, 10000000)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em seu banco para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroBanco(g, u, 10000000);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("🐔 " + u.getAsMention() + " comprou **1 VIP Magnata** por **"
						+ Utils.getDinheiro(10000000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				event.getJDA().retrieveUserById("589098447332835328").complete().openPrivateChannel().complete()
						.sendMessage(event.getMember().getAsMention() + " comprou VIP Magnata.").queue();
			} else if (b.getValue().equals("viprico")) {
				if (!EconomiaManager.hasBanco(g, u, 5000000)) {
					Utils.enviarEmbed(tc, u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você não tem dinheiro o suficiente em seu banco para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroBanco(g, u, 5000000);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("🐔 " + u.getAsMention() + " comprou **1 VIP Rico** por **"
						+ Utils.getDinheiro(5000000) + "**!");
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).complete();
				event.getJDA().retrieveUserById("589098447332835328").complete().openPrivateChannel().complete()
						.sendMessage(event.getMember().getAsMention() + " comprou VIP Rico.").queue();
			}
		}
	}

	@Override
	public String getName() {
		return "c!loja";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
