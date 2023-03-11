package ruffos._pd;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import ruffos.Main;

public class PDManager {

	public static void sendPD(TextChannel tc) {
		Guild g = tc.getGuild();
		// verifyPD(g);
		EmbedBuilder embed = new EmbedBuilder();
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				embed.setAuthor("💍 Lista de Damas - " + g.getName(), g.getIconUrl(), g.getIconUrl());
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `pd`");
					rs = ps.executeQuery();
					Map<String, List<String>> pd = new HashMap<>();
					while (rs.next()) {
						if (!pd.containsKey(rs.getString("adm"))) {
							List<String> a = new ArrayList<>();
							a.add(rs.getString("membro"));
							pd.put(rs.getString("adm"), a);
						} else {
							List<String> a = pd.get(rs.getString("adm"));
							a.add(rs.getString("membro"));
							pd.put(rs.getString("adm"), a);
						}
					}
					for (String s : pd.keySet()) {
						String lista = "";
						for (String aa : pd.get(s)) {
							lista = lista + "<@" + aa + ">\n";
						}
						embed.addField(g.retrieveMemberById(s).complete().getUser().getAsTag(), lista, true);
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
				embed.setColor(Color.PINK);
				embed.setTimestamp(Instant.now());
				embed.setFooter(g.getName());
				tc.sendMessageEmbeds(embed.build()).queue();
			}
		}, 15000);
	}

	public static void verifyPD(Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `pd`");
			rs = ps.executeQuery();
			while (rs.next()) {
				String protetor = rs.getString("adm");
				String membro = rs.getString("membro");
				g.retrieveMemberById(protetor).queue(s -> {
				}, f -> {
					remover(protetor, membro, g);
				});
				g.retrieveMemberById(rs.getString("membro")).queue(s -> {

				}, f -> {
					remover(protetor, membro, g);
				});
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

	public static void add(String protetor, String protegida, Guild g) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `pd` (`adm`, `membro`) VALUES (?,?)");
			ps.setString(1, protetor);
			ps.setString(2, protegida);
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

	public static void remover(String protetor, String protegida, Guild g) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"DELETE FROM `pd` WHERE `adm` = '" + protetor + "' AND `membro` = '" + protegida + "'");
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

	public static int getPD(Member m, Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int i = 0;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `pd`");
			rs = ps.executeQuery();
			while (rs.next()) {
				i++;
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
		return i;
	}

	public static boolean hasPD(String id, String pd, Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `pd` WHERE `adm` = '" + id + "' AND `membro` = '" + pd + "'");
			rs = ps.executeQuery();
			return rs.next();
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

}
