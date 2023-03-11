package ruffos._clans;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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

public class ClanCommand2 extends ListenerAdapter implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ClansManager.isClanGuild(g) && ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Comando incompleto:**\n\nPara ver o rank global de clans, use `c!clan rank`.\nPara criar um clan, use `c!clan criar [tag (3 caractéres)] [nome]`.\nPara sair de um clan, use `c!clan sair`.\nPara deletar seu clan, use `c!clan deletar`.\nPara convidar pessoas para seu clan, use `c!clan convidar @usuário`.\nPara expulsar pessoas do seu clan, use `c!clan expulsar [ID]`.\nPara conferir as informações do seu clan ou do clan de outros usuários e a renda nesta temporada, use `c!clan perfil` ou  `c!clan perfil [@usuário ou TAG do clan]`.\nPara definir uma logo para seu clan, use `c!clan logo [URL]`.\nPara definir um banner para seu clan, use `c!clan banner [URL]`\nPara desafiar um clan para uma troca de tiros use `c!clan desafiar [TAG]`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("criar")) {
				if (ctx.getArgs().size() < 3) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Comando incompleto:** c!clan criar [tag (3 caractéres)] [nome].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você já está em um clan. Para sair, use `c!clan sair`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ctx.getArgs().get(1).toUpperCase();

				Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(tag);
				boolean b = m.find();
				if (b) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** A tag do clan não pode conter caractéres especiais.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (tag.length() != 3) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** c!clan criar [tag (3 caractéres)] [nome]",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String nome = "";
				for (int i = 2; i != ctx.getArgs().size(); i++) {
					nome = nome + ctx.getArgs().get(i) + " ";
				}
				nome = StringUtils.chop(nome);
				if (nome.length() > 26) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** O nome do seu clan não pode conter mais de **26** caractéres.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ClansManager.get(tag, "farm") != null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Esta tag de clan já existe.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!EconomiaManager.hasMaos(g, u, 20000)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(20000) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 20000);
				ClansManager.criarClan(g, nome, tag, u);
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Você criou o clan **" + tag + "** - `" + nome
						+ "`.\n\nConvide membros usando `c!clan convidar @usuário`.\nAdicione um banner ao seu clan usando `c!clan banner [URL]`.\nAdicione uma logo ao seu clan usando `c!clan logo [URL]`.\nVeja as informações do seu clan usando `c!clan perfil`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("perfil")) {
				if (ctx.getArgs().size() == 1) {
					if (!ClansManager.hasClan(u)) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					String tag = ClansManager.getClan(u);
					String[] founderSplit = ClansManager.get(tag, "fundador").split(";");
					User fund = ctx.getJDA().retrieveUserById(founderSplit[0]).complete();
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setTitle("CLAN: **" + tag + "** - _" + ClansManager.get(tag, "clan") + "_");
					eb.addField("**Fundação:**",
							"Fundado por " + (fund == null ? founderSplit[0] : fund.getAsMention()) + " em **"
									+ ClansManager.get(tag, "criado") + "** no servidor `"
									+ ctx.getJDA().getGuildById(ClansManager.get(tag, "guild")).getName() + "`.",
							false);
					eb.addField("**Membros:**", ((ClansManager.get(tag, "membros").equals("nenhum") ? 1
							: ClansManager.getMembros(tag).size() + 1)) + "/5", true);
					eb.addField("**Lucro total:**", ClansManager.get(tag, "farm"), false);
					String mem = "<@" + founderSplit[0] + "> - Lucro: "
							+ Utils.getDinheiro(Integer.parseInt(founderSplit[1])) + "\n";
					if (!ClansManager.get(tag, "membros").equals("nenhum")) {
						for (String s : ClansManager.getMembros(tag)) {
							String[] split = s.split(";");
							mem = mem + "<@" + split[0] + "> - Lucro: " + Utils.getDinheiro(Integer.parseInt(split[1]))
									+ "\n";
						}
					}
					eb.addField("**Lucros individuais:**", mem, false);
					eb.addField("Pontuação total:", "" + ClansManager.getPontos(tag), false);
					eb.setFooter(g.getName());
					eb.setTimestamp(Instant.now());
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					try {
						eb.setThumbnail(ClansManager.get(tag, "logo"));
						eb.setImage(ClansManager.get(tag, "banner"));
						ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
					} catch (IllegalArgumentException e) {
						ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
					}
				} else if (ctx.getArgs().size() == 2) {
					String tag = ctx.getArgs().get(1).toUpperCase();
					if (ClansManager.get(tag, "farm") == null) {
						if (ctx.getMessage().getMentions().getMembers().size() == 0) {
							Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
									+ " **Comando errado:** Use `c!clan perfil` ou  `c!clan perfil [@usuário ou TAG do clan]`.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
							return;
						}
						u = ctx.getMessage().getMentions().getMembers().get(0).getUser();
						if (!ClansManager.hasClan(u)) {
							Utils.enviarEmbed(ctx.getChannel(), u, null,
									Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
							return;
						}
						tag = ClansManager.getClan(u);
						String[] founderSplit = ClansManager.get(tag, "fundador").split(";");
						User fund = ctx.getJDA().getUserById(founderSplit[0]);
						EmbedBuilder eb = new EmbedBuilder();
						eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
						eb.setTitle("CLAN: **" + tag + "** - _" + ClansManager.get(tag, "clan") + "_");
						eb.addField("**Fundação:**",
								"Fundado por " + (fund == null ? founderSplit[0] : fund.getAsMention()) + " em **"
										+ ClansManager.get(tag, "criado") + "** no servidor `"
										+ ctx.getJDA().getGuildById(ClansManager.get(tag, "guild")).getName() + "`.",
								false);
						eb.addField("**Membros:**", ((ClansManager.get(tag, "membros").equals("nenhum") ? 1
								: ClansManager.getMembros(tag).size() + 1)) + "/5", true);
						eb.addField("**Lucro total:**", ClansManager.get(tag, "farm"), false);
						String mem = "<@" + founderSplit[0] + "> - Lucro: "
								+ Utils.getDinheiro(Integer.parseInt(founderSplit[1])) + "\n";
						if (!ClansManager.get(tag, "membros").equals("nenhum")) {
							for (String s : ClansManager.getMembros(tag)) {
								String[] split = s.split(";");
								mem = mem + "<@" + split[0] + "> - Lucro: "
										+ Utils.getDinheiro(Integer.parseInt(split[1])) + "\n";
							}
						}
						eb.addField("**Lucros individuais:**", mem, false);
						eb.addField("Pontuação total:", "" + ClansManager.getPontos(tag), false);
						eb.setFooter(g.getName());
						eb.setTimestamp(Instant.now());
						eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
								: null)));
						try {
							eb.setThumbnail(ClansManager.get(tag, "logo"));
							eb.setImage(ClansManager.get(tag, "banner"));
							ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
						} catch (IllegalArgumentException e) {
							ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
						}
						return;
					} else {
						String[] founderSplit = ClansManager.get(tag, "fundador").split(";");
						User fund = ctx.getJDA().getUserById(founderSplit[0]);
						EmbedBuilder eb = new EmbedBuilder();
						eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
						eb.setTitle("CLAN: **" + tag + "** - _" + ClansManager.get(tag, "clan") + "_");
						eb.addField("**Fundação:**",
								"Fundado por " + (fund == null ? founderSplit[0] : fund.getAsMention()) + " em **"
										+ ClansManager.get(tag, "criado") + "** no servidor `"
										+ ctx.getJDA().getGuildById(ClansManager.get(tag, "guild")).getName() + "`.",
								false);
						eb.addField("**Membros:**", ((ClansManager.get(tag, "membros").equals("nenhum") ? 1
								: ClansManager.getMembros(tag).size() + 1)) + "/5", true);
						eb.addField("**Lucro total:**", ClansManager.get(tag, "farm"), false);
						String mem = "<@" + founderSplit[0] + "> - Lucro: "
								+ Utils.getDinheiro(Integer.parseInt(founderSplit[1])) + "\n";
						if (!ClansManager.get(tag, "membros").equals("nenhum")) {
							for (String s : ClansManager.getMembros(tag)) {
								String[] split = s.split(";");
								mem = mem + "<@" + split[0] + "> - Lucro: "
										+ Utils.getDinheiro(Integer.parseInt(split[1])) + "\n";
							}
						}
						eb.addField("**Lucros individuais:**", mem, false);
						eb.addField("Pontuação total:", "" + ClansManager.getPontos(tag), false);
						eb.setFooter(g.getName());
						eb.setTimestamp(Instant.now());
						eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
								: null)));
						try {
							eb.setThumbnail(ClansManager.get(tag, "logo"));
							eb.setImage(ClansManager.get(tag, "banner"));
							ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
						} catch (IllegalArgumentException e) {
							ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
						}
					}
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Comando errado:** Use `c!clan perfil` ou  `c!clan perfil [@usuário ou TAG do clan]`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("convidar")) {
				if (!ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ClansManager.getClan(u);
				if (!ClansManager.getFundador(tag).equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Apenas o(a) fundador(a) do clan pode recrutar novos membros.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Comando incompleto:** Use c!clan convidar @usuário.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ctx.getMessage().getMentions().getMembers().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!clan convidar @usuário.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				Member convite = ctx.getMessage().getMentions().getMembers().get(0);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("**[" + tag + "]** " + u.getAsMention() + " está convidando " + convite.getAsMention()
						+ " para entrar em seu clan.\nPara aceitar o convite, clique em: "
						+ Utils.getEmote("sim").getFormatted() + ".");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				try {
					eb.setThumbnail(ClansManager.get(tag, "logo"));
					eb.setImage(ClansManager.get(tag, "banner"));
					Button sim = Button.success("simclan;" + convite.getId() + ";" + tag,
							Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
							nao = Button.danger("naoclan;" + convite.getId() + ";" + tag,
									Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
						msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
						msg.delete().queueAfter(30, TimeUnit.SECONDS);
					});
				} catch (IllegalArgumentException e) {
					Button sim = Button.success("simclan;" + convite.getId() + ";" + tag,
							Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
							nao = Button.danger("naoclan;" + convite.getId() + ";" + tag,
									Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
						msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
						msg.delete().queueAfter(30, TimeUnit.SECONDS);
					});
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("sair")) {
				if (!ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ClansManager.getClan(u);
				if (ClansManager.getFundador(tag).equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** O(A) fundador(a) do clan não pode simplesmente se retirar. Para se sair, você deverá deletar seu clan. Use `c!clan deletar`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setFooter(g.getName());
				eb.setDescription("Você está prestes a sair do clan **" + tag
						+ "**, caso tenha certeza disso, clique em: " + Utils.getEmote("sim").getFormatted()
						+ ".\n\n**ATENÇÃO:** Caso você saia, o clan perderá todo o seu lucro individual.");
				eb.setTimestamp(Instant.now());
				Button sim = Button.success("simclans;" + u.getId() + ";" + tag,
						Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
						nao = Button.danger("naoclans;" + u.getId() + ";" + tag,
								Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
					msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
					msg.delete().queueAfter(30, TimeUnit.SECONDS);
				});
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("deletar")) {
				if (!ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ClansManager.getClan(u);
				if (!ClansManager.getFundador(tag).equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Apenas o(a) fundador(a) do clan pode deletar o mesmo.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				ClansManager.deletarClan(tag);
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Você deletou o clan **" + tag + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("expulsar")) {
				if (!ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ClansManager.getClan(u);
				if (!ClansManager.getFundador(tag).equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Apenas o(a) fundador(a) do clan pode expulsar membros.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Comando incompleto:** Use c!clan expulsar @usuário.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String id = ctx.getArgs().get(1);
				if (!ClansManager.hasClan(id) || !ClansManager.getClan(id).equals(ClansManager.getClan(u))) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Este(a) usuário(a) não possui um clan ou não participa do mesmo clan que você.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				ClansManager.removerMembro(tag, id, 1);
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você expulsou <@" + id + "> de seu clan **(" + tag + ")**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("logo")) {
				if (!ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ClansManager.getClan(u);
				if (!ClansManager.getFundador(tag).equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Apenas o(a) fundador(a) do clan pode definir a logo.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				ClansManager.update(tag, "logo", ctx.getArgs().get(1));
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você definiu a logo do clan. Caso nenhuma logo apareça, o link informado não é válido.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("banner")) {
				if (!ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ClansManager.getClan(u);
				if (!ClansManager.getFundador(tag).equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Apenas o(a) fundador(a) do clan pode definir o banner.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				ClansManager.update(tag, "banner", ctx.getArgs().get(1));
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"Você definiu o banner do clan. Caso nenhum banner apareça, o link informado não é válido.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("rank")) {
				ClansManager.sendRank(ctx.getChannel());
			}/** else if (ctx.getArgs().get(0).equalsIgnoreCase("desafiar")) {
				if (Tiroteio.pedido != null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Um desafio está em andamento.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!clan desafiar [TAG].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!ClansManager.hasClan(u)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String tag = ClansManager.getClan(u);
				if (!ClansManager.getFundador(tag).equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Apenas o(a) fundador(a) do clan pode desafiar outros clans para uma trocação de tiros.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String clan2 = ctx.getArgs().get(1);
				if (ClansManager.get(clan2, "farm") != null) {
					Button sim = Button.success("aceitardsf;" + tag + ";" + clan2,
							Emoji.fromFormatted(Utils.getEmote("sim").getFormatted())),
							nao = Button.danger("negardsf;" + tag + ";" + clan2,
									Emoji.fromFormatted(Utils.getEmote("nao").getFormatted()));
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					eb.setFooter(g.getName());
					eb.setDescription("O clan **" + tag + "** está desafiando o clan **" + clan2
							+ "** para uma trocação de tiros.\n\nPara aceitar, o fundador do clan **" + clan2
							+ "** deverá clicar no botão abaixo.");
					eb.setTimestamp(Instant.now());
					User uu = u;
					u.openPrivateChannel().queue(a -> {
						a.sendMessage("Aguarde o fundador do clan **" + clan2 + "**.").queue(s -> {
							ctx.getJDA().retrieveUserById(ClansManager.getFundador(clan2)).queue(p -> {
								p.openPrivateChannel().queue(sd -> {
									sd.sendMessageEmbeds(eb.build()).queue(msg -> {
										msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
										Tiroteio.pedido = new Pedido(tag, clan2);
										msg.delete().queueAfter(5, TimeUnit.MINUTES);
									});
								}, f -> {
									Utils.enviarEmbed(ctx.getChannel(), uu, null, Utils.getEmote("nao").getFormatted()
											+ " **Erro:** Não foi possível enviar mensagem no privado do fundador do clan **"
											+ clan2 + "**.",
											((ConfigManager.temCfg(g, "cor")
													? Color.decode(ConfigManager.getConfig(g, "cor"))
													: null)),
											g.getName(), null, null, null, 0, true);
								});
							}, f -> {
								Utils.enviarEmbed(ctx.getChannel(), uu, null, Utils.getEmote("nao").getFormatted()
										+ " **Erro:** Não foi possível encontrar o fundador do clan **" + clan2 + "**.",
										((ConfigManager.temCfg(g, "cor")
												? Color.decode(ConfigManager.getConfig(g, "cor"))
												: null)),
										g.getName(), null, null, null, 0, true);
							});
							s.delete().queueAfter(5, TimeUnit.MINUTES);
						}, f -> {
							Utils.enviarEmbed(ctx.getChannel(), uu, null,
									Utils.getEmote("nao").getFormatted()
											+ " **Erro:** Não foi possível enviar mensagem no seu privado.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
						});
					}, f -> {
						Utils.enviarEmbed(ctx.getChannel(), uu, null,
								Utils.getEmote("nao").getFormatted()
										+ " **Erro:** Não foi possível enviar mensagem no seu privado.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
					});
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
						msg.editMessageComponents(ActionRow.of(sim, nao)).queue();
						msg.delete().queueAfter(30, TimeUnit.SECONDS);
					});
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Clan inexistente.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
			}**/
		}

	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Button b = event.getButton();
		String[] split = b.getId().split(";");
		Guild g = event.getGuild();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		System.out.println(split);
		if (split[0].equalsIgnoreCase("simclan") && event.getUser().getId().equals(split[1])) {
			System.out.println("teste");
			String tag = split[2];
			Member convidado = event.getMember();
			if (ClansManager.hasClan(convidado.getUser())) {
				Utils.enviarEmbed(tc, convidado.getUser(), null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você já está em um clan.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if ((ClansManager.getMembros(tag).size() + 1) >= 5) {
				Utils.enviarEmbed(tc, convidado.getUser(), null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Máximo de membros por clan (5) excedido.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			ClansManager.addMembro(tag, convidado.getUser(), 0);
			event.getMessage().delete().queue();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTimestamp(Instant.now());
			eb.setTitle("**" + tag + "** recrutou um novo membro!");
			eb.setDescription("Agora, " + convidado.getAsMention() + " faz parte do clan **"
					+ ClansManager.get(tag, "clan") + "**!");
			eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
			eb.setFooter(g.getName());
			try {
				eb.setImage(ClansManager.get(tag, "banner"));
				eb.setThumbnail(ClansManager.get(tag, "logo"));
				tc.sendMessageEmbeds(eb.build()).queue();
			} catch (IllegalArgumentException e) {
				tc.sendMessageEmbeds(eb.build()).queue();
			}

		} else if (split[0].equalsIgnoreCase("naoclan") && event.getUser().getId().equals(split[1])) {
			event.getMessage().delete().queue();
			Member convidado = event.getMember();
			String tag = split[2];
			Utils.enviarEmbed(tc, convidado.getUser(), null,
					convidado.getAsMention() + " recusou o convite para entrar no clan **" + tag + "**.",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
		} else if (split[0].equalsIgnoreCase("simclans") && event.getUser().getId().equals(split[1])) {
			event.getMessage().delete().queue();
			if (ClansManager.hasClan(event.getUser().getId())) {
				ClansManager.removerMembro(split[2], event.getUser().getId(), 1);
				Utils.enviarEmbed(tc, event.getUser(), null, "Você saiu do clan **" + split[2] + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else {
				Utils.enviarEmbed(tc, event.getUser(), null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		} else if (split[0].equalsIgnoreCase("naoclans") && event.getUser().getId().equals(split[1])) {
			event.getMessage().delete().queue();
		} /**else if (split[0].equalsIgnoreCase("aceitardsf")
				&& event.getUser().getId().equals(ClansManager.getFundador(split[2]))) {
			Guild tiroteio = Tiroteio.tiroteio;
			tiroteio.retrieveInvites().complete().forEach(i -> {
				i.delete().queue();
			});
			
			for (String s : ClansManager.getMembros(split[1])) {
				if (!EconomiaManager.hasBanco(event.getJDA().getGuildById(ClansManager.get(split[1], "guild")),
						event.getJDA().retrieveUserById(s.split(";")[0]).complete(), 100000)) {
					event.getPrivateChannel().sendMessage("Um dos membros participantes do clan **" + split[1]
							+ "** não possui **" + Utils.getDinheiro(100000) + "** no banco.").queue();
					return;
				}
				if (!EconomiaManager.hasArma(event.getJDA().getGuildById(ClansManager.get(split[1], "guild")),
						event.getJDA().retrieveUserById(s.split(";")[0]).complete())) {
					event.getPrivateChannel()
							.sendMessage(
									"Um dos membros participantes do clan **" + split[1] + "** não possui uma arma.")
							.queue();
					return;
				}
			}
			for (String s : ClansManager.getMembros(split[2])) {
				if (!EconomiaManager.hasBanco(event.getJDA().getGuildById(ClansManager.get(split[2], "guild")),
						event.getJDA().retrieveUserById(s.split(";")[0]).complete(), 100000)) {
					event.getPrivateChannel().sendMessage("Um dos membros participantes do clan **" + split[2]
							+ "** não possui **" + Utils.getDinheiro(100000) + "** no banco.").queue();
					return;
				}
				if (!EconomiaManager.hasArma(event.getJDA().getGuildById(ClansManager.get(split[2], "guild")),
						event.getJDA().retrieveUserById(s.split(";")[0]).complete())) {
					event.getPrivateChannel()
							.sendMessage(
									"Um dos membros participantes do clan **" + split[2] + "** não possui uma arma.")
							.queue();
					return;
				}
			}
			event.getPrivateChannel()
					.sendMessage("Entre no servidor abaixo e envie este link para seus outros 4 membros.\n\nConvite: "
							+ tiroteio.getTextChannels().get(0).createInvite().complete().getUrl()
							+ "\n\nVocês tem **2 minutos** para entrar.")
					.queue();
			User user = event.getJDA().retrieveUserById(ClansManager.getFundador(split[1])).complete();
			user.openPrivateChannel().complete()
					.sendMessage("Entre no servidor abaixo e envie este link para seus outros 4 membros.\n\nConvite: "
							+ tiroteio.getTextChannels().get(0).createInvite().complete().getUrl()
							+ "\n\nVocês tem **2 minutos** para entrar.")
					.queue();
			Tiroteio.acontecendo = true;
			Tiroteio.pedido.aceitar();
			Tiroteio.contar();
			System.out.println(Tiroteio.clan1 + " - " + Tiroteio.clan2 + " --- " + split[1] + " " + split[2]);
			Tiroteio.clan1 = split[1];
			Tiroteio.clan2 = split[2];
			System.out.println(Tiroteio.clan1 + " - " + Tiroteio.clan2 + " --- " + split[1] + " " + split[2]);
			Tiroteio.clan1g = event.getJDA().getGuildById(ClansManager.get(Tiroteio.clan1, "guild"));
			Tiroteio.clan2g = event.getJDA().getGuildById(ClansManager.get(Tiroteio.clan2, "guild"));
		}**/
	}

	@Override
	public String getName() {
		return "c!clan";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
