package ruffos._config;

import java.awt.Color;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class ConfigCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Member m = ctx.getMember();
		Guild g = ctx.getGuild();
		if (m.isOwner() || m.getId().equals("380570412314001410")) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(
						ctx.getChannel(), null, Utils.getEmote("config")
								.getFormatted() + "  Configurações do Ruffos",
						"Chats: "
								+ (ConfigManager.temCfg(g, "canal") ? Utils.getEmote("ativado").getFormatted()
										: Utils.getEmote("desativado").getFormatted())
								+ "\nCor nas embeds: "
								+ (ConfigManager.temCfg(g, "cor") ? Utils.getEmote("ativado").getFormatted()
										: Utils.getEmote("desativado").getFormatted())
								+ "\nChat Geral: "
								+ (ConfigManager.temCfg(g, "chatgeral") ? Utils.getEmote("ativado").getFormatted()
										: Utils.getEmote("desativado").getFormatted())
								+ "\nDirigível: "
								+ ((ConfigManager.temCfg(g, "dirigivelmsgs")
										&& ConfigManager.temCfg(g, "dirigiveltime"))
												? Utils.getEmote("ativado").getFormatted()
												: Utils.getEmote("desativado").getFormatted())
								+ "\nCategoria de receber Dinheiro, XP e Tempo em Call: "
								+ ((ConfigManager.temCfg(g, "tempocall")) ? Utils.getEmote("ativado")
										.getFormatted() : Utils.getEmote("desativado").getFormatted())
								+ "\nMensagens do c!crime: "
								+ ((ConfigManager.temCfg(g, "crime")) ? Utils.getEmote("ativado")
										.getFormatted() : Utils.getEmote("desativado").getFormatted())
								+ "\nTag magnata: "
								+ ((ConfigManager.temCfg(g, "magnata")
										&& (g.getRoleById(ConfigManager.getConfig(g, "magnata")) != null))
												? Utils.getEmote("ativado").getFormatted()
												: Utils.getEmote("desativado").getFormatted())
								+ "\nClans: "
								+ (((ConfigManager.temCfg(g, "clans")
										&& Boolean.parseBoolean(ConfigManager.getConfig(g, "clans")) == true))
												? Utils.getEmote("ativado").getFormatted()
												: Utils.getEmote("desativado").getFormatted())
								+ "\nFAKE: "
								+ (((ConfigManager.temCfg(g, "fake")
										&& Boolean.parseBoolean(ConfigManager.getConfig(g, "fake")) == true))
												? Utils.getEmote("ativado").getFormatted()
												: Utils.getEmote("desativado").getFormatted())
								+ "\n\nPara configurar os chats, use: `c!config chat`.\nPara configurar a cor das embeds, use: `c!config cor`.\nPara definir o chat geral do servidor, use `c!config chatgeral #chat`.\nPara configurar o dirigível, use `c!config dirigivel`\nPara configurar as categorias de receber Dinheiro, XP e Tempo em Call, use `c!config tempocall`.\nPara configurar as mensagens do c!crime, use `c!config crime`\nPara definir se é um servidor 'fake' ou não, use `c!config fake`.",
						(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null),
						g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("chat")) {
				if (ctx.getArgs().size() < 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
							"Para adicionar chats, use `c!config chat add [id]`.\nPara remover chats, use `c!config chat remover [id]`.\nPara ver os chats em que o ruffos funciona, use `c!config chat ver`.\nPara desativar o ruffos (remover todos os chats), use `c!config chat desativar`.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ctx.getArgs().get(1).equalsIgnoreCase("desativar")) {
					if (ConfigManager.temCfg(g, "canal")) {
						ConfigManager.deletar(g, "canal");
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								Utils.getEmote("sim").getFormatted()
										+ " Os chats foram desativados. Para ativar novamente, será necessário configurar novamente do zero.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					} else {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								Utils.getEmote("nao").getFormatted() + " Os chats já estão desativados.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					}
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("add")) {
					if (ctx.getArgs().size() != 3) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								"Para adicionar chats, use `c!config chat add [id]`.\nPara remover chats, use `c!config chat remover [id]`.\nPara ver os chats em que o ruffos funciona, use `c!config chat ver`.\nPara desativar o ruffos (remover todos os chats), use `c!config chat desativar`.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					if (ctx.getMessage().getMentions().getChannels().size() != 1) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								"Para adicionar chats, use `c!config chat add [id]`.\nPara remover chats, use `c!config chat remover [id]`.\nPara ver os chats em que o ruffos funciona, use `c!config chat ver`.\nPara desativar o ruffos (remover todos os chats), use `c!config chat desativar`.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					TextChannel tc = (TextChannel) ctx.getMessage().getMentions().getChannels().stream()
							.filter(gc -> gc.getType() == ChannelType.TEXT).findFirst().get();
					String chat = tc.getId();
					if (ConfigManager.temCfg(g, "canal")) {
						String[] chats = ConfigManager.getConfig(g, "canal").split(",");
						String novo = "";
						for (String c : chats) {
							if (c.equals(chat)) {
								Utils.enviarEmbed(ctx.getChannel(), null,
										Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
										Utils.getEmote("nao").getFormatted() + " O chat " + tc.getAsMention()
												+ " já foi adicionado.",
										null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0,
										true);
								return;
							}
							novo = novo + c + ",";
						}
						novo = novo + chat;
						ConfigManager.modificar(g, "canal", novo);
					} else {
						ConfigManager.configurar(g, "canal", chat);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
							Utils.getEmote("sim").getFormatted() + " O chat " + tc.getAsMention() + " foi adicionado.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("remover")) {
					if (ctx.getArgs().size() != 3) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								"Para adicionar chats, use `c!config chat add [id]`.\nPara remover chats, use `c!config chat remover [id]`.\nPara ver os chats em que o ruffos funciona, use `c!config chat ver`.\nPara desativar o ruffos (remover todos os chats), use `c!config chat desativar`.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					TextChannel tc = (TextChannel) ctx.getMessage().getMentions().getChannels().stream()
							.filter(gc -> gc.getType() == ChannelType.TEXT).findFirst().get();
					String chat = tc.getId();
					if (ConfigManager.temCfg(g, "canal")) {
						String[] chats = ConfigManager.getConfig(g, "canal").split(",");
						String novo = "";
						for (String c : chats) {
							if (c.equals(chat)) {
								continue;
							}
							novo = novo + c + ",";
						}
						novo = StringUtils.chop(novo);
						if (novo.equals("")) {
							ConfigManager.deletar(g, "canal");
						} else {
							ConfigManager.modificar(g, "canal", novo);
						}
					} else {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								Utils.getEmote("nao").getFormatted() + " Os chats estão desativados.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
							Utils.getEmote("sim").getFormatted() + " O chat " + tc.getAsMention() + " foi removido.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("ver")) {
					if (ConfigManager.temCfg(g, "canal")) {
						String[] chats = ConfigManager.getConfig(g, "canal").split(",");
						String cc = "";
						for (String c : chats) {
							cc = cc + "<#" + c + ">, ";
						}
						cc = StringUtils.chop(cc);
						cc = StringUtils.chop(cc);
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								Utils.getEmote("sim").getFormatted() + " Chats configurados:\n" + cc, null, g.getName(),
								null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					} else {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Chat - Ruffos",
								Utils.getEmote("nao").getFormatted() + " Os chats estão desativados.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					}
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("cor")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Cor - Ruffos",
							"Para alterar a cor das embeds do Ruffos, use `c!config cor [nome da cor em inglês/id da cor]`. Para a cor, em vez de usar o nome ou o id da cor, utilize 'n'",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				String color = ctx.getArgs().get(1);
				if (color.equalsIgnoreCase("n")) {
					ConfigManager.deletar(g, "cor");
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Cor - Ruffos",
							Utils.getEmote("sim").getFormatted() + " Agora, nenhuma cor aparecerá mais nas embeds.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				Color cor;
				try {
					Field field = Class.forName("java.awt.Color").getField(color);
					cor = (Color) field.get(null);
					final Color c = cor;
					String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
					if (!ConfigManager.temCfg(g, "cor")) {
						ConfigManager.configurar(g, "cor", hex);
					} else {
						ConfigManager.modificar(g, "cor", hex);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Cor - Ruffos",
							Utils.getEmote("sim").getFormatted()
									+ " Você alterou a cor das Embeds do Ruffos. Todas as embeds enviadas (com exceção do `c!perfil`) parecerão com esta.",
							cor, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				} catch (Exception e) {

				}
				try {
					cor = Color.decode(color);
					final Color c = cor;
					if (!ConfigManager.temCfg(g, "cor")) {
						ConfigManager.configurar(g, "cor", color);
					} else {
						ConfigManager.modificar(g, "cor", color);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Cor - Ruffos",
							Utils.getEmote("sim").getFormatted()
									+ " Você alterou a cor das Embeds do Ruffos. Todas as embeds enviadas (com exceção do `c!perfil`) parecerão com esta.",
							cor, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				} catch (NumberFormatException e) {

				}
				Utils.enviarEmbed(ctx.getChannel(), null,
						Utils.getEmote("config").getFormatted() + " Configurações de Cor - Ruffos",
						"Para alterar a cor das embeds do Ruffos, use `c!config cor [nome da cor em inglês/id da cor]`.",
						null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("chatgeral")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Chat Geral - Ruffos",
							"Para definir o chat geral do Ruffos, use `c!config chatgeral #chat`.", null, g.getName(),
							null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ctx.getMessage().getMentions().getChannels().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Chat Geral - Ruffos",
							"Para definir o chat geral do Ruffos, use `c!config chatgeral #chat`.", null, g.getName(),
							null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				TextChannel tc = (TextChannel) ctx.getMessage().getMentions().getChannels().stream()
						.filter(gc -> gc.getType() == ChannelType.TEXT).findFirst().get();
				if (ConfigManager.temCfg(g, "chatgeral")) {
					ConfigManager.modificar(g, "chatgeral", tc.getId());
				} else {
					ConfigManager.configurar(g, "chatgeral", tc.getId());
				}
				Utils.enviarEmbed(ctx.getChannel(), null,
						Utils.getEmote("config").getFormatted() + " Configurações de Chat Geral - Ruffos",
						Utils.getEmote("sim").getFormatted() + " O chat geral foi definido para " + tc.getAsMention()
								+ ".",
						null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("dirigivel")) {
				if (ctx.getArgs().size() != 3) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações do Dirigível - Ruffos",
							"Para definir a quantidade de mensagens necessárias para o dirigível aparecer, use `c!config dirigivel msgs [quantidade]`.\nPara definir quanto tempo o dirigível ficará 'passando', use `c!config dirigivel tempo [tempo em segundos]`.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ctx.getArgs().get(1).equalsIgnoreCase("msgs")) {
					String s = ctx.getArgs().get(2);
					try {
						Integer.parseInt(s);
					} catch (NumberFormatException e) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações do Dirigível - Ruffos",
								"Para definir a quantidade de mensagens necessárias para o dirigível aparecer, use `c!config dirigivel msgs [quantidade]`.\nPara definir quanto tempo o dirigível ficará 'passando', use `c!config dirigivel tempo [tempo em segundos]`.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					if (ConfigManager.temCfg(g, "dirigivelmsgs")) {
						ConfigManager.modificar(g, "dirigivelmsgs", s);
					} else {
						ConfigManager.configurar(g, "dirigivelmsgs", s);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações do Dirigível - Ruffos",
							Utils.getEmote("sim").getFormatted() + " O dirigível passará agora de **" + s + "** em **"
									+ s + "** mensagem(ns) no servidor.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("tempo")) {
					String s = ctx.getArgs().get(2);
					try {
						Integer.parseInt(s);
					} catch (NumberFormatException e) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações do Dirigível - Ruffos",
								"Para definir a quantidade de mensagens necessárias para o dirigível aparecer, use `c!config dirigivel msgs [quantidade]`.\nPara definir quanto tempo o dirigível ficará 'passando', use `c!config dirigivel tempo [tempo em segundos]`.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					if (ConfigManager.temCfg(g, "dirigiveltime")) {
						ConfigManager.modificar(g, "dirigiveltime", s);
					} else {
						ConfigManager.configurar(g, "dirigiveltime", s);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações do Dirigível - Ruffos",
							Utils.getEmote("sim").getFormatted() + " O dirigível agora ficará 'passando' por **" + s
									+ "** segundo(s).",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("tempocall")) {
				if (ctx.getArgs().size() < 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted()
									+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
							"Para adicionar categorias, use `c!config tempocall add [id]`.\nPara remover categorias, use `c!config tempocall remover [id]`.\nPara ver as categorias em que o tempo call funciona, use `c!config tempocall ver`.\nPara desativar o tempo call (remover todas as categorias), use `c!config tempocall desativar`.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ctx.getArgs().get(1).equalsIgnoreCase("desativar")) {
					if (ConfigManager.temCfg(g, "tempocall")) {
						ConfigManager.deletar(g, "tempocall");
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								Utils.getEmote("sim").getFormatted()
										+ " As categorias foram desativadas. Para ativar novamente, será necessário configurar novamente do zero.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					} else {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								Utils.getEmote("nao").getFormatted() + " As categorias já estão desativadas.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					}
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("add")) {
					if (ctx.getArgs().size() != 3) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								"Para adicionar categorias, use `c!config tempocall add [id]`.\nPara remover categorias, use `c!config tempocall remover [id]`.\nPara ver as categorias em que o tempo call funciona, use `c!config tempocall ver`.\nPara desativar o tempo call (remover todas as categorias), use `c!config tempocall desativar`.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					String chat = ctx.getArgs().get(2);
					if (g.getCategoryById(chat) == null) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								Utils.getEmote("nao").getFormatted() + " Categoria inválida.", null, g.getName(), null,
								null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					if (ConfigManager.temCfg(g, "tempocall")) {
						String[] chats = ConfigManager.getConfig(g, "tempocall").split(",");
						String novo = "";
						for (String c : chats) {
							if (c.equals(chat)) {
								Utils.enviarEmbed(ctx.getChannel(), null,
										Utils.getEmote("config").getFormatted()
												+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
										Utils.getEmote("nao").getFormatted() + " A categoria `" + chat
												+ "` já foi adicionada.",
										null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0,
										true);
								return;
							}
							novo = novo + c + ",";
						}
						novo = novo + chat;
						ConfigManager.modificar(g, "tempocall", novo);
					} else {
						ConfigManager.configurar(g, "tempocall", chat);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted()
									+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
							Utils.getEmote("sim").getFormatted() + " A categoria `" + g.getCategoryById(chat).getName()
									+ "` foi adicionada.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("remover")) {
					if (ctx.getArgs().size() != 3) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								"Para adicionar categorias, use `c!config tempocall add [id]`.\nPara remover categorias, use `c!config tempocall remover [id]`.\nPara ver as categorias em que o tempo call funciona, use `c!config tempocall ver`.\nPara desativar o tempo call (remover todas as categorias), use `c!config tempocall desativar`.",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					String chat = ctx.getArgs().get(2);
					if (g.getCategoryById(chat) == null) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								Utils.getEmote("nao").getFormatted() + " Categoria inválida.", null, g.getName(), null,
								null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					if (ConfigManager.temCfg(g, "tempocall")) {
						String[] chats = ConfigManager.getConfig(g, "tempocall").split(",");
						String novo = "";
						for (String c : chats) {
							if (c.equals(chat)) {
								continue;
							}
							novo = novo + c + ",";
						}
						novo = StringUtils.chop(novo);
						if (novo.equals("")) {
							ConfigManager.deletar(g, "tempocall");
						} else {
							ConfigManager.modificar(g, "tempocall", novo);
						}
					} else {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								Utils.getEmote("nao").getFormatted() + " As categorias estão desativadas.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted()
									+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
							Utils.getEmote("sim").getFormatted() + " A categoria `" + g.getCategoryById(chat).getName()
									+ "` foi removida.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("ver")) {
					if (ConfigManager.temCfg(g, "tempocall")) {
						String[] chats = ConfigManager.getConfig(g, "tempocall").split(",");
						String cc = "";
						for (String c : chats) {
							if (g.getCategoryById(c) != null) {
								cc = cc + "`" + g.getCategoryById(c).getName() + " (" + c + ")`, ";
							} else {
								cc = cc + "`" + c + "`, ";
							}
						}
						cc = StringUtils.chop(cc);
						cc = StringUtils.chop(cc);
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								Utils.getEmote("sim").getFormatted() + " Categorias configuradas:\n" + cc, null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					} else {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações de receber Dinheiro, XP e Tempo em Call - Ruffos",
								Utils.getEmote("nao").getFormatted() + " As categorias estão desativadas.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					}
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("crime")) {
				if (ctx.getArgs().size() < 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações das mensagens de Crime - Ruffos",
							"Para adicionar uma mensagem, use `c!config crime add [mensagem]`.\nPara remover uma mensagem, use `c!config crime remover [id da mensagem]`.\nPara ver as mensagens e seus respectivos IDs, use `c!config crime ver`.\n\n**PRESTE ATENÇÃO:**\nPara representar o valor ganho no crime, use `@valor`.",
							null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ctx.getArgs().get(1).equalsIgnoreCase("add")) {
					String msg = "";
					for (int i = 2; i != ctx.getArgs().size(); i++) {
						msg = msg + " " + ctx.getArgs().get(i);
					}
					if (ConfigManager.temCfg(g, "crime")) {
						String[] msgs = ConfigManager.getConfig(g, "crime").split("/");
						String novo = "";
						for (String c : msgs) {
							novo = novo + c + "/";
						}
						novo = novo + msg;
						ConfigManager.modificar(g, "crime", novo);
					} else {
						ConfigManager.configurar(g, "crime", msg);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações das mensagens de Crime - Ruffos",
							"Mensagem adicionada:\n`" + msg + "`", null, g.getName(), null, null,
							ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("remover")) {
					String[] split = ConfigManager.getConfig(g, "crime").split("/");
					if (!ConfigManager.temCfg(g, "crime")) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações das mensagens de Crime - Ruffos",
								Utils.getEmote("nao").getFormatted() + " Não há mensagens configuradas.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					if (!Utils.isInteger(ctx.getArgs().get(2))) {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações das mensagens de Crime - Ruffos",
								Utils.getEmote("nao").getFormatted()
										+ " Para remover uma mensagem, use `c!config crime remover [id da mensagem]` (Para ver o ID das mensagens, use `c!config crime ver`).",
								null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
						return;
					}
					int id = Integer.parseInt(ctx.getArgs().get(2));
					String msg = split[id];
					String novo = "";
					for (String s : split) {
						if (!s.equals(msg)) {
							novo = novo + s + "/";
						}
					}
					if (!novo.equals("")) {
						novo = StringUtils.chop(novo);
					}
					if (novo.equals("")) {
						ConfigManager.deletar(g, "crime");
					}
					if (ConfigManager.temCfg(g, "crime")) {
						ConfigManager.modificar(g, "crime", novo);
					}
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações das mensagens de Crime - Ruffos",
							"Mensagem removida:\n`" + msg + "`", null, g.getName(), null, null,
							ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				} else if (ctx.getArgs().get(1).equalsIgnoreCase("ver")) {
					if (ConfigManager.temCfg(g, "crime")) {
						String[] split = ConfigManager.getConfig(g, "crime").split("/");
						int i = 0;
						String ss = "";
						for (String s : split) {
							ss = ss + "ID: **" + i + "** » Mensagem: `" + s + "`.\n";
							i++;
						}
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações das mensagens de Crime - Ruffos",
								"Segue abaixo as mensagens e seus respectivos IDs:\n\n" + ss, null, g.getName(), null,
								null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					} else {
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted()
										+ " Configurações das mensagens de Crime - Ruffos",
								Utils.getEmote("nao").getFormatted() + " Não há mensagens configuradas.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					}
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("magnata")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Magnata - Ruffos",
							"Para definir a tag Magnata do Ruffos, use `c!config magnata @cargo`.", null, g.getName(),
							null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ctx.getMessage().getMentions().getRoles().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Magnata - Ruffos",
							"Para definir a tag Magnata do Ruffos, use `c!config magnata @cargo`.", null, g.getName(),
							null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				Role r = ctx.getMessage().getMentions().getRoles().get(0);
				if (ConfigManager.temCfg(g, "magnata")) {
					ConfigManager.modificar(g, "magnata", r.getId());
				} else {
					ConfigManager.configurar(g, "magnata", r.getId());
				}
				Utils.enviarEmbed(ctx.getChannel(), null,
						Utils.getEmote("config").getFormatted() + " Configurações de Magnata - Ruffos",
						Utils.getEmote("sim").getFormatted() + " O cargo de magnata foi definido para "
								+ r.getAsMention() + ".",
						null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("temporario")) {
				if (!m.getId().equals("380570412314001410")) {
					return;
				}
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Canais Temporários - Ruffos",
							"Para definir o canal inicial, use `c!config temporario [ID]`.", null, g.getName(), null,
							null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				VoiceChannel vc = g.getVoiceChannelById(ctx.getArgs().get(1));
				if (vc == null) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Canais Temporários - Ruffos",
							Utils.getEmote("nao").getFormatted() + " Canal inválido.", null, g.getName(), null, null,
							ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ConfigManager.temCfg(g, "canal-temp")) {
					ConfigManager.modificar(g, "canal-temp", ctx.getArgs().get(1));
				} else {
					ConfigManager.configurar(g, "canal-temp", ctx.getArgs().get(1));
				}
				Utils.enviarEmbed(ctx.getChannel(), null,
						Utils.getEmote("config").getFormatted() + " Configurações de Canais Temporários - Ruffos",
						Utils.getEmote("sim").getFormatted() + " O canal inicial foi definido para `" + vc.getName()
								+ "`.",
						null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("clans")) {
				if (ConfigManager.temCfg(g, "clans")) {
					if (Boolean.parseBoolean(ConfigManager.getConfig(g, "clans"))) {
						ConfigManager.modificar(g, "clans", String.valueOf(false));
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Clans - Ruffos",
								Utils.getEmote("sim").getFormatted() + " Sistema de clans **DESATIVADO**.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					} else {
						ConfigManager.modificar(g, "clans", String.valueOf(true));
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Clans - Ruffos",
								Utils.getEmote("sim").getFormatted() + " Sistema de clans **ATIVADO**.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					}
				} else {
					ConfigManager.configurar(g, "clans", String.valueOf(true));
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Clans - Ruffos",
							Utils.getEmote("sim").getFormatted() + " Sistema de clans **ATIVADO**.", null, g.getName(),
							null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("fake")) {
				if (ConfigManager.temCfg(g, "fake")) {
					if (Boolean.parseBoolean(ConfigManager.getConfig(g, "fake"))) {
						ConfigManager.modificar(g, "fake", String.valueOf(false));
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Servidor Fake - Ruffos",
								Utils.getEmote("sim").getFormatted() + " Servidor definido como **OFF**.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					} else {
						ConfigManager.modificar(g, "fake", String.valueOf(true));
						Utils.enviarEmbed(ctx.getChannel(), null,
								Utils.getEmote("config").getFormatted() + " Configurações de Servidor Fake - Ruffos",
								Utils.getEmote("sim").getFormatted() + " Servidor definido como **FAKE**.", null,
								g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					}
				} else {
					ConfigManager.configurar(g, "fake", String.valueOf(true));
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Servidor Fake - Ruffos",
							Utils.getEmote("sim").getFormatted() + " Servidor definido como **FAKE**.", null,
							g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("salariostaff")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Salário STAFF - Ruffos",
							"Para definir a tag Magnata do Ruffos, use `c!config salariostaff @cargo`.", null,
							g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				if (ctx.getMessage().getMentions().getRoles().size() != 1) {
					Utils.enviarEmbed(ctx.getChannel(), null,
							Utils.getEmote("config").getFormatted() + " Configurações de Salário STAFF - Ruffos",
							"Para definir a tag Magnata do Ruffos, use `c!config salariostaff @cargo`.", null,
							g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
					return;
				}
				Role r = ctx.getMessage().getMentions().getRoles().get(0);
				if (ConfigManager.temCfg(g, "salariostaff")) {
					ConfigManager.modificar(g, "salariostaff", r.getId());
				} else {
					ConfigManager.configurar(g, "salariostaff", r.getId());
				}
				Utils.enviarEmbed(ctx.getChannel(), null,
						Utils.getEmote("config").getFormatted() + " Configurações de Salário STAFF - Ruffos",
						Utils.getEmote("sim").getFormatted() + " O cargo de staff foi definido para " + r.getAsMention()
								+ ".",
						null, g.getName(), null, null, ctx.getJDA().getSelfUser().getAvatarUrl(), 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!config";
	}

	@Override
	public String getHelp() {
		return "Configure o bot";
	}

}
