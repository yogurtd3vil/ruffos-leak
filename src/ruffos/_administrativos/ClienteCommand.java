package ruffos._administrativos;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import ruffos.Main;
import ruffos.utils.EventWaiter;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class ClienteCommand implements ICommand {

	private EventWaiter waiter;

	public ClienteCommand(EventWaiter waiter) {
		this.waiter = waiter;
	}

	private Map<User, Cliente> clientes = new HashMap<>();

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ctx.getAuthor().getId().equals("380570412314001410")) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"`c!cliente add` - Adicionar cliente neste servidor.\n`c!cliente remover [server id]` - Remova um cliente de um servidor específico.\n`c!cliente info <id/*>` - Veja as informações de um servidor ou liste todas as informações de servidores.\n`c!cliente dias [id] [dias]` - Adiciona dias à um servidor.",
						Color.GREEN, g.getName(), null, null, null, 0, true);
				return;
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("add")) {
				if (u.getId().equals("380570412314001410") || u.getId().equals("680426267434418296")) {
					Cliente cliente = new Cliente();
					cliente.setServerId(g.getId());
					EmbedBuilder eb = new EmbedBuilder();
					eb.setDescription("Informe o id do comprador:");
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setColor(Color.GREEN);
					eb.setFooter(g.getName());
					eb.setTimestamp(Instant.now());
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
						clientes.put(u, cliente);
						usuario(ctx.getChannel(), u);
					});
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("remover")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"`c!cliente add` - Adicionar cliente neste servidor.\n`c!cliente remover [server id]` - Remova um cliente de um servidor específico.\n`c!cliente info <id/*>` - Veja as informações de um servidor ou liste todas as informações de servidores.\n`c!cliente dias [id] [dias]` - Adiciona dias à um servidor.",
							Color.GREEN, g.getName(), null, null, null, 0, true);
					return;
				}
				String serverId = ctx.getArgs().get(1);
				PreparedStatement ps = null;
				try {
					ps = Main.getDatabase().getGlobalConnection()
							.prepareStatement("DELETE FROM `clientes` WHERE `serverId` = '" + serverId + "'");
					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				ctx.getChannel().sendMessage("Servidor deletado, deseja que o Ruffos saia do servidor?")
						.queue((msg) -> {
							msg.addReaction(Emoji.fromFormatted("<a:sim_ruffos:717039486646616155>")).queue();
							sair(msg, u, serverId);
						});
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("ver")) {
				if (ctx.getArgs().size() == 1) {
					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
						ps = Main.getDatabase().getGlobalConnection()
								.prepareStatement("SELECT * FROM `clientes` WHERE `serverId` = '" + g.getId() + "'");
						rs = ps.executeQuery();
						if (rs.next()) {
							long atual = System.currentTimeMillis();
							long expirar = rs.getLong("expiracao");
							String exp = "**" + new SimpleDateFormat("dd/MM/yy").format(rs.getLong("expiracao")) + "**";
							if (atual >= expirar) {
								exp = "`" + new SimpleDateFormat("dd/MM/yy").format(rs.getLong("expiracao")) + "`";
							}
							ctx.getChannel()
									.sendMessage("Informações do cliente deste servidor:\nCliente: **"
											+ rs.getString("nome") + "**\nValor a pagar: **" + rs.getString("preco")
											+ "**\nAdições: **" + rs.getString("adicoes") + "**\nExpiração: " + exp)
									.queue();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							ps.close();
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				} else if (ctx.getArgs().size() == 2 && ctx.getArgs().get(1).equalsIgnoreCase("*")) {
					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
						ps = Main.getDatabase().getGlobalConnection().prepareStatement("SELECT * FROM `clientes`");
						rs = ps.executeQuery();
						while (rs.next()) {
							long atual = System.currentTimeMillis();
							long expirar = rs.getLong("expiracao");
							String exp = "**" + new SimpleDateFormat("dd/MM/yy").format(rs.getLong("expiracao")) + "**";
							if (atual >= expirar) {
								exp = "`" + new SimpleDateFormat("dd/MM/yy").format(rs.getLong("expiracao")) + "`";
							}
							if (Main.getJDA().getGuildById(rs.getString("serverId")) == null) {
								ctx.getChannel()
										.sendMessage("Informações do servidor `" + rs.getString("serverId")
												+ " NÃO ENCONTRADO`:\nCliente: **" + rs.getString("nome")
												+ "**\nValor a pagar: **" + rs.getString("preco") + "**\nAdições: **"
												+ rs.getString("adicoes") + "**\nExpiração: " + exp)
										.queue();
							} else {
								ctx.getChannel()
										.sendMessage("Informações do servidor `"
												+ Main.getJDA().getGuildById(rs.getString("serverId")).getName() + "` ["
												+ rs.getString("serverId") + "]:\nCliente: **" + rs.getString("nome")
												+ "**\nValor a pagar: **" + rs.getString("preco") + "**\nAdições: **"
												+ rs.getString("adicoes") + "**\nExpiração: " + exp)
										.queue();
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							ps.close();
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				} else if (ctx.getArgs().size() == 2) {
					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
						ps = Main.getDatabase().getGlobalConnection().prepareStatement(
								"SELECT * FROM `clientes` WHERE `serverId` = '" + ctx.getArgs().get(1) + "'");
						rs = ps.executeQuery();
						if (rs.next()) {
							long atual = System.currentTimeMillis();
							long expirar = rs.getLong("expiracao");
							String exp = "**" + new SimpleDateFormat("dd/MM/yy").format(rs.getLong("expiracao")) + "**";
							if (atual >= expirar) {
								exp = "`" + new SimpleDateFormat("dd/MM/yy").format(rs.getLong("expiracao")) + "`";
							}
							ctx.getChannel()
									.sendMessage("Informações do cliente deste servidor:\nCliente: **"
											+ rs.getString("nome") + "**\nValor a pagar: **" + rs.getString("preco")
											+ "**\nAdições: **" + rs.getString("adicoes") + "**\nExpiração: " + exp)
									.queue();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							ps.close();
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("dias")) {
				if (ctx.getArgs().size() != 3) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"`c!cliente add` - Adicionar cliente neste servidor.\n`c!cliente remover [server id]` - Remova um cliente de um servidor específico.\n`c!cliente info <id/*>` - Veja as informações de um servidor ou liste todas as informações de servidores.\n`c!cliente dias [id] [dias]` - Adiciona dias à um servidor.",
							Color.GREEN, g.getName(), null, null, null, 0, true);
					return;
				}
				String serverId = ctx.getArgs().get(1);
				PreparedStatement ps = null;
				try {
					ps = Main.getDatabase().getGlobalConnection().prepareStatement(
							"UPDATE `clientes` SET `expiracao` = ? WHERE `serverId` = '" + serverId + "'");
					Date d = new Date(getDias(serverId));
					d.setDate(d.getDate() + Integer.parseInt(ctx.getArgs().get(2)));
					ps.setLong(1, d.getTime());
					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("sair")) {
				ctx.getJDA().getGuildById(ctx.getArgs().get(1)).leave().queue(s -> {
					ctx.getChannel().sendMessage("Saiu do server ").queue();
				}, f -> {
					ctx.getChannel().sendMessage("Falha").queue();
				});

			}
		}
	}

	long getDias(String id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getGlobalConnection()
					.prepareStatement("SELECT * FROM `clientes` WHERE `serverId` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong("expiracao");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void sair(Message msg, User u, String serverId) {
		waiter.waitForEvent(MessageReactionAddEvent.class, (event) -> {
			if (event.getMessageId().equalsIgnoreCase(msg.getId()) && u.getId().equals(event.getUser().getId())) {
				if (event.getReaction().getEmoji().getName().equals("sim_ruffos")) {
					event.getJDA().getGuildById(serverId).leave().queue();
					TextChannel tc = msg.getGuild().getTextChannelById(event.getChannel().getId());
					Utils.enviarEmbed(tc, u, null, "O Ruffos saiu do servidor.", Color.GREEN, msg.getGuild().getName(),
							null, null, null, 0, true);
				}
			}
			return false;
		}, (event) -> {

		}, 1, TimeUnit.MINUTES, () -> {
			msg.removeReaction(Emoji.fromFormatted("<a:sim_ruffos:717039486646616155>")).queue();
		});
	}

	public void usuario(TextChannel tc, User u) {
		waiter.waitForEvent(MessageReceivedEvent.class, (event) -> {
			if (event.getChannel() == tc && event.getMember().getId().equals(u.getId())
					&& !event.getMessage().getContentRaw().equalsIgnoreCase("c!cliente add")) {
				System.out.println(event.getMessage().getContentRaw());
				clientes.get(u).setNome(event.getJDA().getUserById(event.getMessage().getContentRaw()).getAsTag() + " ("
						+ event.getMessage().getContentRaw() + ")");
				EmbedBuilder eb = new EmbedBuilder();
				eb.setDescription("Informe o valor cobrado:");
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setColor(Color.GREEN);
				eb.setFooter(event.getGuild().getName());
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
					valor(u);
				});
				return true;
			}
			return false;
		}, (event) -> {

		}, 2, TimeUnit.MINUTES, () -> {
			clientes.remove(u);
		});
	}

	public void valor(User u) {
		waiter.waitForEvent(MessageReceivedEvent.class, (event) -> {
			if (event.getMember().getId().equals(u.getId())) {
				System.out.println(event.getMessage().getContentRaw());
				clientes.get(u).setPreco(Integer.parseInt(event.getMessage().getContentRaw()));
				EmbedBuilder eb = new EmbedBuilder();
				eb.setDescription("Informe as adições:");
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setColor(Color.GREEN);
				eb.setFooter(event.getGuild().getName());
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
					adicoes(u);
				});
				return true;
			}
			return false;
		}, (event) -> {

		}, 2, TimeUnit.MINUTES, () -> {
			clientes.remove(u);
		});
	}

	public void adicoes(User u) {
		waiter.waitForEvent(MessageReceivedEvent.class, (event) -> {
			if (event.getMember().getId().equals(u.getId())) {
				System.out.println(event.getMessage().getContentRaw());
				clientes.get(u).setAdicoes(event.getMessage().getContentRaw());
				EmbedBuilder eb = new EmbedBuilder();
				eb.setDescription("Informe quantos dias o bot durará:");
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setColor(Color.GREEN);
				eb.setFooter(event.getGuild().getName());
				eb.setTimestamp(Instant.now());
				event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
					dias(u);
				});
				return true;
			}
			return false;
		}, (event) -> {

		}, 2, TimeUnit.MINUTES, () -> {
			clientes.remove(u);
		});
	}

	public void dias(User u) {
		waiter.waitForEvent(MessageReceivedEvent.class, (event) -> {
			if (event.getMember().getId().equals(u.getId())) {
				System.out.println(event.getMessage().getContentRaw());
				Date d = new Date();
				d.setDate(d.getDate() + Integer.parseInt(event.getMessage().getContentRaw()));
				clientes.get(u).setExpiracao(d.getTime());
				TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
				Utils.enviarEmbed(tc, u, null, "Cliente registrado.", Color.GREEN, event.getGuild().getName(), null,
						null, null, 0, true);
				PreparedStatement ps = null;
				try {
					ps = Main.getDatabase().getGlobalConnection().prepareStatement(
							"INSERT INTO `clientes` (`nome`, `serverId`, `preco`, `adicoes`, `expiracao`) VALUES (?,?,?,?,?)");
					ps.setString(1, clientes.get(u).getNome());
					ps.setString(2, clientes.get(u).getServerId());
					ps.setInt(3, clientes.get(u).getPreco());
					ps.setString(4, clientes.get(u).getAdicoes());
					ps.setLong(5, clientes.get(u).getExpiracao());
					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
			return false;
		}, (event) -> {

		}, 2, TimeUnit.MINUTES, () -> {
			clientes.remove(u);
		});
	}

	@Override
	public String getName() {
		return "c!cliente";
	}

	@Override
	public String getHelp() {
		return null;
	}

}
