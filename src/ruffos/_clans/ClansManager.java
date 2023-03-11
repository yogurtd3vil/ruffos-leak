package ruffos._clans;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;

public class ClansManager {

	public static boolean isClanGuild(Guild g) {
		return ConfigManager.temCfg(g, "clans") && Boolean.parseBoolean(ConfigManager.getConfig(g, "clans")) == true;
	}

	public static void criarClan(Guild g, String nome, String tag, User u) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getGlobalConnection().prepareStatement(
					"INSERT INTO `clans` (`guild`, `tag`, `clan`, `fundador`, `membros`, `farmado`, `banner`, `logo`, criado) VALUES (?,?,?,?,?,?,?,?,?)");
			ps.setString(1, g.getId());
			ps.setString(2, tag.toUpperCase());
			ps.setString(3, nome);
			ps.setString(4, u.getId() + ";0");
			ps.setString(5, "nenhum");
			ps.setInt(6, 0);
			ps.setString(7, "https://i.imgur.com/Ht4Jlkd.png");
			ps.setString(8, g.getJDA().getSelfUser().getAvatarUrl());
			ps.setString(9, new SimpleDateFormat("dd/MM/yy").format(new Date()));
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

	public static int getPontos(String tag) {
		int pontos = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getGlobalConnection()
					.prepareStatement("SELECT * FROM `clans` WHERE `tag` = '" + tag + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				pontos = rs.getInt("farmado") / 500000;
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
		return pontos;
	}

	public static void sendRank(TextChannel tc) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getGlobalConnection().prepareStatement("SELECT * FROM `clans`");
			rs = ps.executeQuery();
			Map<String, Integer> pontos = new HashMap<>();
			while (rs.next()) {
				pontos.put(rs.getString("tag"), rs.getInt("farmado") / 500000);
			}
			List<Map.Entry<String, Integer>> sortedd = new ArrayList<>(pontos.entrySet());
			Collections.sort(sortedd, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return Integer.compare(o2.getValue(), o1.getValue());
				}
			});
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(Utils.getEmote("trofeu").getFormatted() + " TOP 5 clans com mais pontos acumulados");
			eb.setFooter(tc.getGuild().getName() + " » Para ver as informações dos clans, use c!clan perfil [TAG].");
			eb.setTimestamp(Instant.now());
			eb.setThumbnail(tc.getGuild().getIconUrl());
			String top1 = sortedd.size() < 1 ? "N/A\n"
					: "**" + sortedd.get(0).getKey() + "** com **" + sortedd.get(0).getValue() + "** pontos!\n";
			String top2 = sortedd.size() < 2 ? "N/A\n"
					: "**" + sortedd.get(1).getKey() + "** com **" + sortedd.get(1).getValue() + "** pontos!\n";
			String top3 = sortedd.size() < 3 ? "N/A\n"
					: "**" + sortedd.get(2).getKey() + "** com **" + sortedd.get(2).getValue() + "** pontos!\n";
			String top4 = sortedd.size() < 4 ? "N/A\n"
					: "**" + sortedd.get(3).getKey() + "** com **" + sortedd.get(3).getValue() + "** pontos!\n";
			String top5 = sortedd.size() < 5 ? "N/A\n"
					: "**" + sortedd.get(4).getKey() + "** com **" + sortedd.get(4).getValue() + "** pontos!\n";
			String top = top1 + top2 + top3 + top4 + top5
					+ "\nEste rank é globalizado, ou seja, os servidores que autorizaram o sistema de Clans estão contando e podem aparecer aqui.";
			eb.setDescription(top);
			eb.setColor(((ConfigManager.temCfg(tc.getGuild(), "cor")
					? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
					: null)));
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

	public static void addFarm(String tag, int farm) {
		int atual = Integer.parseInt(get(tag, "farmn"));
		atual += farm;
		update(tag, "farm", String.valueOf(atual));
	}

	public static void addFarm(User u, int farm) {
		String tag = getClan(u);
		int atual = Integer.parseInt(get(tag, "farmn"));
		atual += farm;
		update(tag, "farm", String.valueOf(atual));
		String[] found = get(tag, "fundador").split(";");
		if (get(tag, "fundador").split(";")[0].equals(u.getId())) {
			int i = Integer.parseInt(found[1]);
			i += farm;
			update(tag, "fundador", u.getId() + ";" + i);
		} else {
			for (String s : getMembros(tag)) {
				String[] split = s.split(";");
				if (split[0].equals(u.getId())) {
					int i = Integer.parseInt(split[1]);
					i += farm;
					removerMembro(tag, u.getId(), 0);
					addMembro(tag, u, i);
					break;
				}
			}
		}
	}

	public static void deletarClan(String tag) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getGlobalConnection()
					.prepareStatement("DELETE FROM `clans` WHERE `tag` = '" + tag.toUpperCase() + "'");
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

	public static boolean hasClan(User u) {
		return getClan(u) != null;
	}

	public static boolean hasClan(String id) {
		return getClan(id) != null;
	}

	public static String getFundador(String tag) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getGlobalConnection()
					.prepareStatement("SELECT * FROM `clans` WHERE `tag` = '" + tag + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("fundador").split(";")[0];
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

	public static String getClan(String id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getGlobalConnection().prepareStatement("SELECT * FROM `clans`");
			rs = ps.executeQuery();
			while (rs.next()) {
				String[] split = rs.getString("fundador").split(";");
				if (split[0].equals(id)) {
					return rs.getString("tag");
				}
				for (String membros : getMembros(rs.getString("tag"))) {
					String[] split2 = membros.split(";");
					if (split2[0].equals(id)) {
						return rs.getString("tag");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getClan(User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getGlobalConnection().prepareStatement("SELECT * FROM `clans`");
			rs = ps.executeQuery();
			while (rs.next()) {
				String[] split = rs.getString("fundador").split(";");
				if (split[0].equals(u.getId())) {
					return rs.getString("tag");
				}
				for (String membros : getMembros(rs.getString("tag"))) {
					String[] split2 = membros.split(";");
					if (split2[0].equals(u.getId())) {
						return rs.getString("tag");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static List<String> getMembros(String tag) {
		List<String> lista = new ArrayList<>();
		for (String s : get(tag, "membros").split(",")) {
			lista.add(s);
		}
		return lista;
	}

	public static void addMembro(String tag, User u, int qnt) {
		if (get(tag, "membros").equals("nenhum")) {
			update(tag, "membros", u.getId() + ";" + qnt);
		} else {
			List<String> lista = getMembros(tag);
			lista.add(u.getId() + ";" + qnt);
			String novaLista = "";
			for (String s : lista) {
				novaLista = novaLista + s + ",";
			}
			novaLista = StringUtils.chop(novaLista);
			update(tag, "membros", novaLista);
		}
	}

	public static void removerMembro(String tag, String id, int motivo) {
		List<String> lista = getMembros(tag);
		List<String> novaL = new ArrayList<>();
		for (String s : lista) {
			String[] split = s.split(";");
			if (split[0].equals(id)) {
				if (motivo == 1) {
					int i = Integer.parseInt(split[1]);
					int farm = Integer.parseInt(get(tag, "farmn"));
					String completo = String.valueOf((farm - i));
					update(tag, "farm", completo);
				}
				continue;
			}
			novaL.add(s);
		}
		String novaLista = "";
		for (String s : novaL) {
			novaLista = novaLista + s + ",";
		}
		novaLista = StringUtils.chop(novaLista);
		if (novaLista.equals("")) {
			novaLista = "nenhum";
		}
		update(tag, "membros", novaLista);
	}

	public static void update(String tag, String oq, String novo) {
		PreparedStatement ps = null;
		try {
			if (oq.equals("farm")) {
				ps = Main.getDatabase().getGlobalConnection().prepareStatement("UPDATE `clans` SET `farmado` = '"
						+ Integer.parseInt(novo) + "' WHERE `tag` = '" + tag.toUpperCase() + "'");
				ps.executeUpdate();
			} else {
				ps = Main.getDatabase().getGlobalConnection().prepareStatement(
						"UPDATE `clans` SET `" + oq + "` = '" + novo + "' WHERE `tag` = '" + tag.toUpperCase() + "'");
				ps.executeUpdate();
			}
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

	public static String get(String tag, String oq) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getGlobalConnection()
					.prepareStatement("SELECT * FROM `clans` WHERE `tag` = '" + tag.toUpperCase() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				if (oq.equals("farm")) {
					return Utils.getDinheiro(rs.getInt("farmado"));
				} else if (oq.equals("farmn")) {
					return String.valueOf(rs.getInt("farmado"));
				} else {
					return rs.getString(oq);
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
		return null;
	}

}
