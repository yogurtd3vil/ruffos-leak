package ruffos._clans;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.EventWaiter;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class ClanCommand implements ICommand {

	Map<Member, String> conv = new HashMap<>();

	private EventWaiter waiter;

	public ClanCommand(EventWaiter waiter) {
		this.waiter = waiter;
	}

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ClansManager.isClanGuild(g) && ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Comando incompleto:**\n\nPara ver o rank global de clans, use `c!clan rank`.\nPara criar um clan, use `c!clan criar [tag (3 caractéres)] [nome]`.\nPara sair de um clan, use `c!clan sair`.\nPara deletar seu clan, use `c!clan deletar`.\nPara convidar pessoas para seu clan, use `c!clan convidar @usuário`.\nPara expulsar pessoas do seu clan, use `c!clan expulsar [ID]`.\nPara conferir as informações do seu clan ou do clan de outros usuários e a renda nesta temporada, use `c!clan perfil` ou  `c!clan perfil [@usuário ou TAG do clan]`.\nPara definir uma logo para seu clan, use `c!clan logo [URL]`.\nPara definir um banner para seu clan, use `c!clan banner [URL]`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
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
						+ "`.\n\nConvide membros usando `c!clan convidar @usuário`.\nAdicione um banner ao seu clan usando `c!clan banner [URL]`.\nAdicione uma logo ao seu clan usando `c!clan logo [URL]`.\nVeja as informações do seu clan usando `c!clan info`.",
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
					eb.addField("**Membros:**", "**" + ((ClansManager.get(tag, "membros").equals("nenhum") ? 1
							: ClansManager.getMembros(tag).size() + 1)) + "/5**", true);
					eb.addField("**Lucro total:**", "**" + ClansManager.get(tag, "farm") + "**", false);
					String mem = "<@" + founderSplit[0] + "> - Lucro: **"
							+ Utils.getDinheiro(Integer.parseInt(founderSplit[1])) + "**\n";
					if (!ClansManager.get(tag, "membros").equals("nenhum")) {
						for (String s : ClansManager.getMembros(tag)) {
							String[] split = s.split(";");
							mem = mem + "<@" + split[0] + "> - Lucro: **"
									+ Utils.getDinheiro(Integer.parseInt(split[1])) + "**\n";
						}
					}
					eb.addField("**Lucros individuais:**", mem, false);
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
						eb.setAuthor(ctx.getAuthor().getName(), ctx.getAuthor().getAvatarUrl(),
								ctx.getAuthor().getAvatarUrl());
						eb.setTitle("CLAN: **" + tag + "** - _" + ClansManager.get(tag, "clan") + "_");
						eb.addField("**Fundação:**",
								"Fundado por " + (fund == null ? founderSplit[0] : fund.getAsMention()) + " em **"
										+ ClansManager.get(tag, "criado") + "** no servidor `"
										+ ctx.getJDA().getGuildById(ClansManager.get(tag, "guild")).getName() + "`.",
								false);
						eb.addField("**Membros:**", "**" + ((ClansManager.get(tag, "membros").equals("nenhum") ? 1
								: ClansManager.getMembros(tag).size() + 1)) + "/5**", true);
						eb.addField("**Lucro total:**", "**" + ClansManager.get(tag, "farm") + "**", false);
						String mem = "<@" + founderSplit[0] + "> - Lucro: **"
								+ Utils.getDinheiro(Integer.parseInt(founderSplit[1])) + "**\n";
						if (!ClansManager.get(tag, "membros").equals("nenhum")) {
							for (String s : ClansManager.getMembros(tag)) {
								String[] split = s.split(";");
								mem = mem + "<@" + split[0] + "> - Lucro: **"
										+ Utils.getDinheiro(Integer.parseInt(split[1])) + "**\n";
							}
						}
						eb.addField("**Lucros individuais:**", mem, false);
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
						eb.addField("**Membros:**", "**" + ((ClansManager.get(tag, "membros").equals("nenhum") ? 1
								: ClansManager.getMembros(tag).size() + 1)) + "/5**", true);
						eb.addField("**Lucro total:**", "**" + ClansManager.get(tag, "farm") + "**", false);
						String mem = "<@" + founderSplit[0] + "> - Lucro: **"
								+ Utils.getDinheiro(Integer.parseInt(founderSplit[1])) + "**\n";
						if (!ClansManager.get(tag, "membros").equals("nenhum")) {
							for (String s : ClansManager.getMembros(tag)) {
								String[] split = s.split(";");
								mem = mem + "<@" + split[0] + "> - Lucro: **"
										+ Utils.getDinheiro(Integer.parseInt(split[1])) + "**\n";
							}
						}
						eb.addField("**Lucros individuais:**", mem, false);
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
				if (conv.containsKey(convite) && conv.get(convite).equals(tag)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Aguarde, você já enviou um convite para este(a) usuário(a).",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("**[" + tag + "]** " + u.getAsMention() + " está convidando " + convite.getAsMention()
						+ " para entrar em seu clan.\nPara aceitar o convite, clique em: "
						+ Utils.getEmote("sim").getFormatted() + ".");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				User uu = u;
				try {
					eb.setThumbnail(ClansManager.get(tag, "logo"));
					eb.setImage(ClansManager.get(tag, "banner"));
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
						msg.addReaction(Utils.getEmote("sim")).queue();
						msg.addReaction(Utils.getEmote("nao")).queue();
						conv.put(convite, tag);
						convite(uu, convite, msg, tag);
					});
				} catch (IllegalArgumentException e) {
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
						msg.addReaction(Utils.getEmote("sim")).queue();
						msg.addReaction(Utils.getEmote("nao")).queue();
						conv.put(convite, tag);
						convite(uu, convite, msg, tag);
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
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue((msg) -> {
					msg.addReaction(Utils.getEmote("sim")).queue();
					msg.addReaction(Utils.getEmote("nao")).queue();
					sair(ctx.getMember(), msg, tag);
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
									+ " **Comando incompleto:** Use c!clan convidar @usuário.",
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
			}
		}
	}

	private void sair(Member sair, Message msg, String tag) {
		waiter.waitForEvent(MessageReactionAddEvent.class, (event) -> {
			if (event.getMessageId().equals(msg.getId())) {
				Guild g = msg.getGuild();
				TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
				Member react = event.getMember();
				if (react == sair) {
					if (event.getReaction().getEmoji().getName().equals("sim_ruffos")) {
						msg.delete().queue();
						if (ClansManager.hasClan(sair.getId())) {
							ClansManager.removerMembro(tag, sair.getId(), 1);
							Utils.enviarEmbed(tc, sair.getUser(), null, "Você saiu do clan **" + tag + "**.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
						} else {
							Utils.enviarEmbed(tc, sair.getUser(), null,
									Utils.getEmote("nao").getFormatted() + " **Erro:** Você não está em um clan.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
						}
					} else {
						msg.delete().queue();
					}
				}
			}
			return false;
		}, (event) -> {

		}, 1, TimeUnit.MINUTES, () -> {
			if (msg != null) {
				msg.delete().queue((m) -> {

				}, (f) -> {

				});
			}
		});
	}

	private void convite(User convidou, Member convidado, Message msg, String tag) {
		waiter.waitForEvent(MessageReactionAddEvent.class, (event) -> {
			if (event.getMessageId().equals(msg.getId())) {
				Guild g = msg.getGuild();
				TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
				Member react = event.getMember();
				if (react.getIdLong() == convidado.getIdLong()) {
					if (event.getReaction().getEmoji().getName().equals("sim_ruffos")) {
						if (conv.get(convidado).equals(tag)) {
							if (ClansManager.hasClan(convidado.getUser())) {
								Utils.enviarEmbed(tc, convidado.getUser(), null,
										Utils.getEmote("nao").getFormatted() + " **Erro:** Você já está em um clan.",
										((ConfigManager.temCfg(g, "cor")
												? Color.decode(ConfigManager.getConfig(g, "cor"))
												: null)),
										g.getName(), null, null, null, 0, true);
								return true;
							}
							if ((ClansManager.getMembros(tag).size() + 1) >= 5) {
								Utils.enviarEmbed(tc, convidado.getUser(), null,
										Utils.getEmote("nao").getFormatted()
												+ " **Erro:** Máximo de membros por clan (5) excedido.",
										((ConfigManager.temCfg(g, "cor")
												? Color.decode(ConfigManager.getConfig(g, "cor"))
												: null)),
										g.getName(), null, null, null, 0, true);
								return true;
							}
							ClansManager.addMembro(tag, convidado.getUser(), 0);
							msg.delete().queue();
							EmbedBuilder eb = new EmbedBuilder();
							eb.setTimestamp(Instant.now());
							eb.setTitle("**" + tag + "** recrutou um novo membro!");
							eb.setDescription("Agora, " + convidado.getAsMention() + " faz parte do clan **"
									+ ClansManager.get(tag, "clan") + "**!");
							eb.setColor(
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)));
							eb.setFooter(g.getName());
							try {
								eb.setImage(ClansManager.get(tag, "banner"));
								eb.setThumbnail(ClansManager.get(tag, "logo"));
								tc.sendMessageEmbeds(eb.build()).queue();
							} catch (IllegalArgumentException e) {
								tc.sendMessageEmbeds(eb.build()).queue();
							}
							conv.remove(convidado);
						}
					} else {
						if (conv.get(convidado).equals(tag)) {
							msg.delete().queue();
							conv.remove(convidado);
						}
					}
				}
			}
			return false;
		}, (event) -> {

		}, 1, TimeUnit.MINUTES, () -> {
			if (msg != null) {
				msg.delete().queue((m) -> {

				}, (falha) -> {

				});
				if (conv.containsKey(convidado) && conv.get(convidado).equals(tag)) {
					conv.remove(convidado);
				}
			}
		});
	}

	@Override
	public String getName() {
		return "c!clan";
	}

	@Override
	public String getHelp() {
		return "Clans";
	}

}
