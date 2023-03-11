package ruffos._economia;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.menu2.Paginator;
import ruffos.utils.Utils;

public class EconomiaManager {

	public static int getUsuarios(Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> users = new ArrayList<>();
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `dinheiro`");
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getInt("banco") > 1000) {
					if (!users.contains(rs.getString("usuario"))) {
						users.add(rs.getString("usuario"));
					}
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
		return users.size();
	}

	public static void setTrabalho(Guild g, User u, String trabalho) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `trabalhos` (`usuario`, `trabalho`, `tempo`) VALUES ('" + u.getId()
							+ "', '" + trabalho + "', '" + 0 + "')");
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
	}

	public static void updateTrabalho(Guild g, User u, String trabalho) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"UPDATE `trabalhos` SET `trabalho` = '" + trabalho + "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static boolean hasTrabalho(Guild g, User u) {
		return getTrabalho(g, u) != null;
	}

	public static String getTrabalho(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `trabalhos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("trabalho");
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
		return null;
	}

	public static Map<String, Member> lastMagnata = new HashMap<>();
	public static Member lastMagnata2 = null;
	public static Member lastMagnata3 = null;
	private static Paginator.Builder pbuilder;

	public static void enviarTopMoney(TextChannel tc, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Guild g = tc.getGuild();
		pbuilder = new Paginator.Builder().setColumns(1).setItemsPerPage(10).showPageNumbers(true)
				.waitOnSinglePage(false).useNumberedItems(false).setFinalAction(msg -> {
					try {
						msg.clearReactions().queue();
					} catch (PermissionException e) {
						msg.delete().queue();
					}
				}).setEventWaiter(Main.waiter).setTimeout(1, TimeUnit.MINUTES);
		pbuilder.clearItems();
		try {
			Connection conn = Main.getDatabase().getConnectionByGuildId(tc.getGuild().getId());
			ps = conn.prepareStatement("SELECT * FROM `dinheiro`");
			rs = ps.executeQuery();

			Map<String, Integer> dinheiro = new HashMap<>();
			while (rs.next()) {
				double quantidade = rs.getDouble("maos") + rs.getDouble("banco");
				if (quantidade >= 15000) {
					dinheiro.put(rs.getString("usuario"), (int) Math.round(quantidade));
				}
			}

			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(dinheiro.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});

			if (sorted.isEmpty()) {
				Utils.enviarEmbed(tc, u, "Ranking: Usuários com mais dinheiro no servidor",
						"Não há registros de usuários.",
						(ConfigManager.temCfg(tc.getGuild(), "cor")
								? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
								: null),
						g.getName(), null, null, g.getIconUrl(), 0, true);
			}

			int i = 0;
			for (Map.Entry<String, Integer> usuarios : sorted) {
				i++;
				if (i == 1 && ConfigManager.temCfg(tc.getGuild(), "magnata")) {
					try {
						Role magnata = Main.getJDA().getRoleById(ConfigManager.getConfig(tc.getGuild(), "magnata"));
						if (magnata != null) {
							pbuilder.addItems(" **" + i + ".** " + magnata.getAsMention() + " <@" + usuarios.getKey()
									+ "> - **" + Utils.getDinheiro(usuarios.getValue()) + "**");
							g.getMembersWithRoles(magnata).forEach(mm -> {
								if (!mm.getId().equals(usuarios.getKey())) {
									g.removeRoleFromMember(mm, magnata).queue(s -> {

									}, f -> {

									});
								}
							});
							g.retrieveMemberById(usuarios.getKey()).queue(m -> {
								g.addRoleToMember(m, magnata).queue(s -> {
								}, f -> {

								});
							}, f -> {

							});
						} else {
							pbuilder.addItems("**" + i + ".** <@" + usuarios.getKey() + "> - **"
									+ Utils.getDinheiro(usuarios.getValue()) + "**");
						}
					} catch (ErrorResponseException | PermissionException e) {
						pbuilder.addItems("**" + i + ".** <@" + usuarios.getKey() + "> - **"
								+ Utils.getDinheiro(usuarios.getValue()) + "**");
					}
				} else {
					pbuilder.addItems("**" + i + ".** <@" + usuarios.getKey() + "> - **"
							+ Utils.getDinheiro(usuarios.getValue()) + "**");
				}
			}
			Paginator p = pbuilder.setColor((ConfigManager.temCfg(tc.getGuild(), "cor")
					? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
					: null)).setText(u.getAsMention()).setUsers(u).build();
			p.paginate(tc, 1,
					Utils.getEmote("trofeu").getFormatted() + " Ranking: Usuários com mais dinheiro no servidor", g);
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

	public static void enviarTOPMoney(TextChannel tc) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = Main.getDatabase().getConnectionByGuildId(tc.getGuild().getId());
			ps = conn.prepareStatement("SELECT * FROM `dinheiro`");
			rs = ps.executeQuery();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(Utils.getEmote("trofeu").getFormatted() + " TOP 10 usuários mais ricos do servidor");
			Map<String, Integer> dinheiro = new HashMap<>();
			while (rs.next()) {
				double quantidade = rs.getDouble("maos") + rs.getDouble("banco");
				dinheiro.put(rs.getString("usuario"), (int) Math.round(quantidade));
			}

			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(dinheiro.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});
			String top1 = "Ninguém";
			int top1i = 0;
			Member topOne = null;
			Member topTwo = null;
			Member topThree = null;
			if (sorted.size() >= 1) {
				top1 = "<@" + sorted.get(0).getKey() + ">";
				top1i = sorted.get(0).getValue();
				try {
					topOne = tc.getGuild().retrieveMemberById(sorted.get(0).getKey()).complete();
				} catch (ErrorResponseException e) {

				}
			}
			String top2 = "Ninguém";
			int top2i = 0;
			if (sorted.size() >= 2) {
				top2 = "<@" + sorted.get(1).getKey() + ">";
				top2i = sorted.get(1).getValue();
				try {
					topTwo = tc.getGuild().retrieveMemberById(sorted.get(1).getKey()).complete();
				} catch (ErrorResponseException e) {

				}
			}
			String top3 = "Ninguém";
			int top3i = 0;
			if (sorted.size() >= 3) {
				top3 = "<@" + sorted.get(2).getKey() + ">";
				top3i = sorted.get(2).getValue();
				try {
					topThree = tc.getGuild().retrieveMemberById(sorted.get(2).getKey()).complete();
				} catch (ErrorResponseException e) {

				}
			}
			String top4 = "Ninguém";
			int top4i = 0;
			if (sorted.size() >= 4) {
				top4 = "<@" + sorted.get(3).getKey() + ">";
				top4i = sorted.get(3).getValue();
			}
			String top5 = "Ninguém";
			int top5i = 0;
			if (sorted.size() >= 5) {
				top5 = "<@" + sorted.get(4).getKey() + ">";
				top5i = sorted.get(4).getValue();
			}
			String top6 = "Ninguém";
			int top6i = 0;
			if (sorted.size() >= 6) {
				top6 = "<@" + sorted.get(5).getKey() + ">";
				top6i = sorted.get(5).getValue();
			}
			String top7 = "Ninguém";
			int top7i = 0;
			if (sorted.size() >= 7) {
				top7 = "<@" + sorted.get(6).getKey() + ">";
				top7i = sorted.get(6).getValue();
			}
			String top8 = "Ninguém";
			int top8i = 0;
			if (sorted.size() >= 8) {
				top8 = "<@" + sorted.get(7).getKey() + ">";
				top8i = sorted.get(7).getValue();
			}
			String top9 = "Ninguém";
			int top9i = 0;
			if (sorted.size() >= 9) {
				top9 = "<@" + sorted.get(8).getKey() + ">";
				top9i = sorted.get(8).getValue();
			}
			String top10 = "Ninguém";
			int top10i = 0;
			if (sorted.size() >= 10) {
				top10 = "<@" + sorted.get(9).getKey() + ">";
				top10i = sorted.get(9).getValue();
			}
			Guild g = tc.getGuild();
			eb.setFooter(g.getName() + " » O dinheiro contabilizando é o que contém em mãos e o do banco juntos.");
			eb.setTimestamp(Instant.now());
			if (!ConfigManager.temCfg(g, "magnata")) {
				eb.setColor((ConfigManager.temCfg(tc.getGuild(), "cor")
						? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
						: null));

				eb.setThumbnail(tc.getGuild().getIconUrl());
				if (topOne != null) {
					lastMagnata.put(tc.getGuild().getId(), topOne);
				}
				try {
					if (Main.getJDA().getRoleById(ConfigManager.getConfig(tc.getGuild(), "magnata")) == null) {
						eb.setDescription("**1º** " + top1 + " - Dinheiro total: **" + Utils.getDinheiro(top1i)
								+ "**\n**2º** "
								+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
								+ " " + top2 + " - Dinheiro total: **" + Utils.getDinheiro(top2i) + "**\n**3º** "
								+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
								+ " " + top3 + " - Dinheiro total: **" + Utils.getDinheiro(top3i) + "**\n**4º** " + top4
								+ " - Dinheiro total: **" + Utils.getDinheiro(top4i) + "**\n**5º** " + top5
								+ " - Dinheiro total: **" + Utils.getDinheiro(top5i) + "**\n**6º** " + top6
								+ " - Dinheiro total: **" + Utils.getDinheiro(top6i) + "**\n**7º** " + top7
								+ " - Dinheiro total: **" + Utils.getDinheiro(top7i) + "**\n**8º** " + top8
								+ " - Dinheiro total: **" + Utils.getDinheiro(top8i) + "**\n**9º** " + top9
								+ " - Dinheiro total: **" + Utils.getDinheiro(top9i) + "**\n**10º** " + top10
								+ " - Dinheiro total: **" + Utils.getDinheiro(top10i)
								+ "**\n\nEste servidor não está configurado com uma tag de MAGNATA ou eu estou sem a permissão de `Gerenciar cargos`. Contate a ADMINISTRAÇÃO.");
					} else {
						eb.setDescription("**1º** "
								+ Main.getJDA().getRoleById(ConfigManager.getConfig(tc.getGuild(), "magnata"))
										.getAsMention()
								+ " " + top1 + " - Dinheiro total: **" + Utils.getDinheiro(top1i) + "**\n**2º** "
								+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
								+ " " + top2 + " - Dinheiro total: **" + Utils.getDinheiro(top2i) + "**\n**3º** "
								+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
								+ " " + top3 + " - Dinheiro total: **" + Utils.getDinheiro(top3i) + "**\n**4º** " + top4
								+ " - Dinheiro total: **" + Utils.getDinheiro(top4i) + "**\n**5º** " + top5
								+ " - Dinheiro total: **" + Utils.getDinheiro(top5i) + "**\n**6º** " + top6
								+ " - Dinheiro total: **" + Utils.getDinheiro(top6i) + "**\n**7º** " + top7
								+ " - Dinheiro total: **" + Utils.getDinheiro(top7i) + "**\n**8º** " + top8
								+ " - Dinheiro total: **" + Utils.getDinheiro(top8i) + "**\n**9º** " + top9
								+ " - Dinheiro total: **" + Utils.getDinheiro(top9i) + "**\n**10º** " + top10
								+ " - Dinheiro total: **" + Utils.getDinheiro(top10i) + "**");
					}
				} catch (HierarchyException | IllegalArgumentException e) {
					eb.setDescription("**1º** " + top1 + " - Dinheiro total: **" + Utils.getDinheiro(top1i)
							+ "**\n**2º** "
							+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
							+ " " + top2 + " - Dinheiro total: **" + Utils.getDinheiro(top2i) + "**\n**3º** "
							+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
							+ " " + top3 + " - Dinheiro total: **" + Utils.getDinheiro(top3i) + "**\n**4º** " + top4
							+ " - Dinheiro total: **" + Utils.getDinheiro(top4i) + "**\n**5º** " + top5
							+ " - Dinheiro total: **" + Utils.getDinheiro(top5i) + "**\n**6º** " + top6
							+ " - Dinheiro total: **" + Utils.getDinheiro(top6i) + "**\n**7º** " + top7
							+ " - Dinheiro total: **" + Utils.getDinheiro(top7i) + "**\n**8º** " + top8
							+ " - Dinheiro total: **" + Utils.getDinheiro(top8i) + "**\n**9º** " + top9
							+ " - Dinheiro total: **" + Utils.getDinheiro(top9i) + "**\n**10º** " + top10
							+ " - Dinheiro total: **" + Utils.getDinheiro(top10i)
							+ "**\n\nEste servidor não está configurado com uma tag de MAGNATA ou eu estou sem a permissão de `Gerenciar cargos`. Contate a ADMINISTRAÇÃO.");
				}
				tc.sendMessageEmbeds(eb.build()).queue();
				return;
			}
			if (Main.getJDA().getRoleById(ConfigManager.getConfig(g, "magnata")) != null) {
				try {
					if (lastMagnata.get(g.getId()) != topOne) {
						if (lastMagnata.get(tc.getGuild().getId()) != null) {
							g.removeRoleFromMember(lastMagnata.get(g.getId()),
									Main.getJDA().getRoleById(ConfigManager.getConfig(g, "magnata"))).queue(s -> {

									}, f -> {

									});
						}
						if (topOne != null) {
							g.addRoleToMember(topOne, Main.getJDA().getRoleById(ConfigManager.getConfig(g, "magnata")))
									.queue(s -> {

									}, f -> {

									});
						}
					}
					if (Utils.isCDM(g)) {
						if (lastMagnata2 != topTwo) {
							if (lastMagnata2 != null) {
								g.removeRoleFromMember(lastMagnata.get(g.getId()), Main.getJDA().getRoleById(""))
										.queue(s -> {

										}, f -> {

										});
							}
							if (topTwo != null) {
								g.addRoleToMember(topTwo, Main.getJDA().getRoleById("713029605518475315")).queue();
							}
						}
						if (lastMagnata2 != topThree) {
							if (lastMagnata2 != null) {
								g.removeRoleFromMember(lastMagnata.get(g.getId()), Main.getJDA().getRoleById(""))
										.queue(s -> {

										}, f -> {

										});
							}
							if (topThree != null) {
								g.addRoleToMember(topThree, Main.getJDA().getRoleById("713029367252385843")).queue();
							}
						}
					}
				} catch (PermissionException | IllegalArgumentException e) {

				}
			}
			eb.setColor((ConfigManager.temCfg(tc.getGuild(), "cor")
					? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
					: null));

			eb.setThumbnail(tc.getGuild().getIconUrl());
			if (topOne != null) {
				lastMagnata.put(tc.getGuild().getId(), topOne);
			}
			try {
				if (Main.getJDA().getRoleById(ConfigManager.getConfig(tc.getGuild(), "magnata")) == null) {
					eb.setDescription("**1º** " + top1 + " - Dinheiro total: **" + Utils.getDinheiro(top1i)
							+ "**\n**2º** "
							+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
							+ " " + top2 + " - Dinheiro total: **" + Utils.getDinheiro(top2i) + "**\n**3º** "
							+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
							+ " " + top3 + " - Dinheiro total: **" + Utils.getDinheiro(top3i) + "**\n**4º** " + top4
							+ " - Dinheiro total: **" + Utils.getDinheiro(top4i) + "**\n**5º** " + top5
							+ " - Dinheiro total: **" + Utils.getDinheiro(top5i) + "**\n**6º** " + top6
							+ " - Dinheiro total: **" + Utils.getDinheiro(top6i) + "**\n**7º** " + top7
							+ " - Dinheiro total: **" + Utils.getDinheiro(top7i) + "**\n**8º** " + top8
							+ " - Dinheiro total: **" + Utils.getDinheiro(top8i) + "**\n**9º** " + top9
							+ " - Dinheiro total: **" + Utils.getDinheiro(top9i) + "**\n**10º** " + top10
							+ " - Dinheiro total: **" + Utils.getDinheiro(top10i)
							+ "**\n\nEste servidor não está configurado com uma tag de MAGNATA ou eu estou sem a permissão de `Gerenciar cargos`. Contate a ADMINISTRAÇÃO.");
				} else {
					eb.setDescription("**1º** "
							+ Main.getJDA().getRoleById(ConfigManager.getConfig(tc.getGuild(), "magnata"))
									.getAsMention()
							+ " " + top1 + " - Dinheiro total: **" + Utils.getDinheiro(top1i) + "**\n**2º** "
							+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
							+ " " + top2 + " - Dinheiro total: **" + Utils.getDinheiro(top2i) + "**\n**3º** "
							+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "")
							+ " " + top3 + " - Dinheiro total: **" + Utils.getDinheiro(top3i) + "**\n**4º** " + top4
							+ " - Dinheiro total: **" + Utils.getDinheiro(top4i) + "**\n**5º** " + top5
							+ " - Dinheiro total: **" + Utils.getDinheiro(top5i) + "**\n**6º** " + top6
							+ " - Dinheiro total: **" + Utils.getDinheiro(top6i) + "**\n**7º** " + top7
							+ " - Dinheiro total: **" + Utils.getDinheiro(top7i) + "**\n**8º** " + top8
							+ " - Dinheiro total: **" + Utils.getDinheiro(top8i) + "**\n**9º** " + top9
							+ " - Dinheiro total: **" + Utils.getDinheiro(top9i) + "**\n**10º** " + top10
							+ " - Dinheiro total: **" + Utils.getDinheiro(top10i) + "**");
				}
			} catch (HierarchyException | IllegalArgumentException e) {
				eb.setDescription("**1º** " + top1 + " - Dinheiro total: **" + Utils.getDinheiro(top1i) + "**\n**2º** "
						+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "") + " "
						+ top2 + " - Dinheiro total: **" + Utils.getDinheiro(top2i) + "**\n**3º** "
						+ (Utils.isCDM(g) ? Main.getJDA().getRoleById("713029367252385843").getAsMention() : "") + " "
						+ top3 + " - Dinheiro total: **" + Utils.getDinheiro(top3i) + "**\n**4º** " + top4
						+ " - Dinheiro total: **" + Utils.getDinheiro(top4i) + "**\n**5º** " + top5
						+ " - Dinheiro total: **" + Utils.getDinheiro(top5i) + "**\n**6º** " + top6
						+ " - Dinheiro total: **" + Utils.getDinheiro(top6i) + "**\n**7º** " + top7
						+ " - Dinheiro total: **" + Utils.getDinheiro(top7i) + "**\n**8º** " + top8
						+ " - Dinheiro total: **" + Utils.getDinheiro(top8i) + "**\n**9º** " + top9
						+ " - Dinheiro total: **" + Utils.getDinheiro(top9i) + "**\n**10º** " + top10
						+ " - Dinheiro total: **" + Utils.getDinheiro(top10i)
						+ "**\n\nEste servidor não está configurado com uma tag de MAGNATA ou eu estou sem a permissão de `Gerenciar cargos`. Contate a ADMINISTRAÇÃO.");
			}
			tc.sendMessageEmbeds(eb.build()).queue();
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

	public static void setBebida(Guild g, User u, String bebida, int copos) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `bebidas` (`usuario`, `bebida`, `copos`) VALUES ('" + u.getId()
							+ "', '" + bebida + "', '" + copos + "')");
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
	}

	public static void updateBebida(Guild g, User u, String bebida, int copos) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `bebidas` SET `bebida` = '" + bebida + "', `copos` = '" + copos
							+ "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static boolean hasBebida(Guild g, User u) {
		return getBebida(g, u) != null;
	}

	public static String getBebida(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `bebidas` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("bebida");
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
		return null;
	}

	public static void setCopos(Guild g, User u, int copos) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"UPDATE `bebidas` SET `copos` = '" + copos + "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static int getCopos(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `bebidas` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("copos");
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
		return 0;
	}

	public static void setArma(Guild g, User u, String arma) {
		PreparedStatement ps = null;
		int usos = 30;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `armas` (`usuario`, `arma`, `usos`) VALUES ('" + u.getId() + "', '"
							+ arma + "', '" + usos + "')");
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
	}

	public static void delArma(Guild g, User u) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("DELETE FROM `armas` WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static void updateArma(Guild g, User u, String arma) {
		PreparedStatement ps = null;
		int usos = 30;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("UPDATE `armas` SET `arma` = '"
					+ arma + "', `usos` = '" + usos + "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static boolean hasArma(Guild g, User u) {
		return getArma(g, u) != null;
	}

	public static String getArma(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `armas` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("arma");
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
		return null;
	}

	public static void setUsos(Guild g, User u, int usos) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"UPDATE `armas` SET `usos` = '" + usos + "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static int getUsos(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `armas` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("usos");
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
		return 0;
	}

	public static boolean inRecompensa(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `recompensa` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
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
		return false;
	}

	public static void addRecompensa(Guild g, User u) {
		PreparedStatement ps = null;
		try {
			Calendar saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
			Date d = new Date(saoPauloDate.getTimeInMillis());
			int hours = d.getHours();
			d.setHours(hours - 1);
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `recompensa` (`usuario`, `tempo`) VALUES ('" + u.getId() + "', '"
							+ d.getTime() + "')");
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
	}

	public static void updateRecompensa(Guild g, User u) {
		PreparedStatement ps = null;
		Calendar saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
		Date d = new Date(saoPauloDate.getTimeInMillis());
		int hours = d.getHours();
		d.setHours(hours - 1);
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"UPDATE `recompensa` SET `tempo` = '" + d.getTime() + "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static long getRecompensa(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `recompensa` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong("tempo");
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
		return 0;
	}

	public static double getDinheiroMaos(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `dinheiro` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("maos");
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
		return 0.0;
	}

	public static double getDinheiroMaos(Guild g, String id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `dinheiro` WHERE `usuario` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("maos");
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
		return 0.0;
	}

	public static double getDinheiroBanco(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `dinheiro` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("banco");
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
		return 0.0;
	}

	public static boolean hasMaos(Guild g, User u, double quantidade) {
		return getDinheiroMaos(g, u) >= quantidade;
	}

	public static boolean hasBanco(Guild g, User u, double quantidade) {
		return getDinheiroBanco(g, u) >= quantidade;
	}

	public static boolean existeUser(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `dinheiro` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
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
		return false;
	}

	public static void criarUser(Guild g, User u, double c) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `dinheiro` (`usuario`, `maos`, `banco`) VALUES ('" + u.getId()
							+ "', '" + c + "', '0.0')");
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
	}

	public static void addDinheiroMaos(Guild g, User u, double c) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `dinheiro` SET `maos` = '" + (getDinheiroMaos(g, u) + c)
							+ "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static void addDinheiroBanco(Guild g, User u, double c) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `dinheiro` SET `banco` = '" + (getDinheiroBanco(g, u) + c)
							+ "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static void removeDinheiroMaos(Guild g, User u, double c) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `dinheiro` SET `maos` = '" + (getDinheiroMaos(g, u) - c)
							+ "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

	public static void removeDinheiroMaos(Guild g, String id, double c) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `dinheiro` SET `maos` = '" + (getDinheiroMaos(g, id) - c)
							+ "' WHERE `usuario` = '" + id + "'");
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
	}

	public static void removeDinheiroBanco(Guild g, User u, double c) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `dinheiro` SET `banco` = '" + (getDinheiroBanco(g, u) - c)
							+ "' WHERE `usuario` = '" + u.getId() + "'");
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
	}

}
