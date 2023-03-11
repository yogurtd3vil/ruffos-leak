package ruffos._leveis;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;

public class LeveisEXA {

	public static int getXP(String id, Guild g) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `leveisexa` WHERE `id` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("xp");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (!ps.isClosed()) {
					ps.close();
				}
				if (!rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static int getLevel(String id, Guild g) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `leveisexa` WHERE `id` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("level");
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

	public static void addXP(TextChannel tc, String id, String nome, int xpDoar) {
		Random r = new Random();
		int xp = r.nextInt(15);
		if (xpDoar != 0) {
			xp = xpDoar;
		}
		int xpAtual = getXP(id, tc.getGuild());
		Connection conn = Main.getDatabase().getConnectionByGuildId(tc.getGuild().getId());
		if (!existeLeveis(id, tc.getGuild())) {
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement(
						"INSERT INTO `leveisexa` (`id`, `level`, `xp`) VALUES ('" + id + "', '0', '" + xp + "')");
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
			return;
		}
		int nextLevel = getLevel(id, tc.getGuild()) + 1;
		if ((xpAtual + xp) >= getXPToNextLevel(nextLevel)) {
			int resto = (xpAtual + xp) - getXPToNextLevel(nextLevel);
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("UPDATE `leveisexa` SET `level` = '" + nextLevel + "', `xp` = '" + resto
						+ "' WHERE `id` = '" + id + "'");
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
			Role lvl100 = tc.getGuild().getRoleById("869353363731533900"),
					lvl90 = tc.getGuild().getRoleById("869353363731533899"),
					lvl80 = tc.getGuild().getRoleById("869353363731533897"),
					lvl70 = tc.getGuild().getRoleById("869353363731533898"),
					lvl60 = tc.getGuild().getRoleById("869353363731533896"),
					lvl50 = tc.getGuild().getRoleById("869353363731533895"),
					lvl40 = tc.getGuild().getRoleById("869353363731533894"),
					lvl30 = tc.getGuild().getRoleById("869353363714744339"),
					lvl20 = tc.getGuild().getRoleById("869353363714744338"),
					lvl10 = tc.getGuild().getRoleById("869353363714744337");
			if (Utils.isEXA(tc.getGuild())) {
				if (nextLevel == 10) {
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl10).queue();
				} else if (nextLevel == 20) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl10).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl20).queue();
				} else if (nextLevel == 30) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl20).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl30).queue();
				} else if (nextLevel == 40) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl30).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl40).queue();
				} else if (nextLevel == 50) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl40).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl50).queue();
				} else if (nextLevel == 60) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl50).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl60).queue();
				} else if (nextLevel == 70) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl60).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl70).queue();
				} else if (nextLevel == 80) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl70).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl80).queue();
				} else if (nextLevel == 90) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl80).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl90).queue();
				} else if (nextLevel == 100) {
					tc.getGuild().removeRoleFromMember(tc.getGuild().retrieveMemberById(id).complete(), lvl90).queue();
					tc.getGuild().addRoleToMember(tc.getGuild().retrieveMemberById(id).complete(), lvl100).queue();
				}
				EmbedBuilder eb = new EmbedBuilder();
				if (Utils.getEmote("parabens") == null) {
					return;
				}
				eb.setAuthor(tc.getGuild().getName() + " = Leveis: " + nome + " subiu de level!",
						tc.getGuild().getIconUrl(), tc.getGuild().getIconUrl());
				eb.setColor(((ConfigManager.temCfg(tc.getGuild(), "cor")
						? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
						: null)));
				eb.setDescription("Você subiu para o level **" + nextLevel + "**! Parabéns!");
				eb.setTimestamp(Instant.now());
				eb.setFooter(tc.getGuild().getName());
				tc.sendMessageEmbeds(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
			}
		} else {
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement(
						"UPDATE `leveisexa` SET `xp` = '" + (xpAtual + xp) + "' WHERE `id` = '" + id + "'");
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

	public static boolean existeLeveis(String id, Guild g) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `leveisexa` WHERE `id` = '" + id + "'");
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

	public static int getXPToNextLevel(int level) {
		return 25 * (level ^ 3) + 80 * level + 200;
	}

	public static void enviarTOPLevel(TextChannel tc) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = Main.getDatabase().getConnectionByGuildId(tc.getGuild().getId());
			ps = conn.prepareStatement("SELECT * FROM `leveisexa`");
			rs = ps.executeQuery();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setThumbnail(tc.getGuild().getIconUrl());

			eb.setTitle(Utils.getEmote("trofeu").getFormatted() + " TOP 10 maiores leveis do servidor");
			Map<String, Integer> leveis = new HashMap<>();
			while (rs.next()) {
				leveis.put(rs.getString("id"), rs.getInt("level"));
			}

			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(leveis.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});
			String top1 = "Ninguém";
			int top1i = 0;
			if (sorted.size() >= 1) {
				top1 = "<@" + sorted.get(0).getKey() + ">";
				top1i = sorted.get(0).getValue();
			}
			String top2 = "Ninguém";
			int top2i = 0;
			if (sorted.size() >= 2) {
				top2 = "<@" + sorted.get(1).getKey() + ">";
				top2i = sorted.get(1).getValue();
			}
			String top3 = "Ninguém";
			int top3i = 0;
			if (sorted.size() >= 3) {
				top3 = "<@" + sorted.get(2).getKey() + ">";
				top3i = sorted.get(2).getValue();
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
			eb.setColor(((ConfigManager.temCfg(tc.getGuild(), "cor")
					? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
					: null)));
			eb.setFooter(tc.getGuild().getName());
			eb.setTimestamp(Instant.now());
			eb.setDescription("**1º** " + top1 + " - Level: **" + top1i + "**\n**2º** " + top2 + " - Level: **" + top2i
					+ "**\n**3º** " + top3 + " - Level: **" + top3i + "**\n**4º** " + top4 + " - Level: **" + top4i
					+ "**\n**5º** " + top5 + " - Level: **" + top5i + "**\n**6º** " + top6 + " - Level: **" + top6i
					+ "**\n**7º** " + top7 + " - Level: **" + top7i + "**\n**8º** " + top8 + " - Level: **" + top8i
					+ "**\n**9º** " + top9 + " - Level: **" + top9i + "**\n**10º** " + top10 + " - Level: **" + top10i
					+ "**");
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

}
