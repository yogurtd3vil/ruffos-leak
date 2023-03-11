package ruffos._lojatags;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.PermissionException;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.menu2.Paginator;
import ruffos.utils.Utils;

public class LojaTagsManager {

	public static void addTag(Guild g, String id, int preco) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `lojatags` (`tag`, `preco`) VALUES (?,?)");
			ps.setString(1, id);
			ps.setInt(2, preco);
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

	public static void removerTag(Guild g, String id) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("DELETE FROM `lojatags` WHERE `tag` = '" + id + "'");
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

	private static Paginator.Builder pbuilder;
	public static Map<String, Integer> tags = new HashMap<>();

	public static void atualizarTags(Guild g) {
		if (!Utils.isIgnoredServer(g)) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			tags.clear();
			try {
				Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
				ps = conn.prepareStatement("SELECT * FROM `lojatags`");
				rs = ps.executeQuery();

				Map<String, Integer> tags = new HashMap<>();
				while (rs.next()) {
					if (g.getRoleById(rs.getString("tag")) != null) {
						tags.put(rs.getString("tag"), rs.getInt("preco"));
					}
				}

				List<Map.Entry<String, Integer>> sorted = new ArrayList<>(tags.entrySet());
				Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
					@Override
					public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
						return Integer.compare(oo2.getValue(), oo1.getValue());
					}
				});

				if (sorted.isEmpty()) {
					return;
				}

				for (Map.Entry<String, Integer> tag : sorted) {
					LojaTagsManager.tags.put(tag.getKey(), tag.getValue());
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
	}

	public static String getTagById(Guild g, int id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
			ps = conn.prepareStatement("SELECT * FROM `lojatags`");
			rs = ps.executeQuery();

			Map<String, Integer> tags = new HashMap<>();
			while (rs.next()) {
				if (g.getRoleById(rs.getString("tag")) != null) {
					tags.put(rs.getString("tag"), rs.getInt("preco"));
				}
			}

			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(tags.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});

			if (sorted.isEmpty()) {
				return null;
			}
			Map<Integer, String> tagById = new HashMap<>();
			int i = 0;
			for (Map.Entry<String, Integer> tag : sorted) {
				i++;
				tagById.put(i, tag.getKey());
			}
			return tagById.get(id);
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

	public static String getPrecoById(Guild g, int id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
			ps = conn.prepareStatement("SELECT * FROM `lojatags`");
			rs = ps.executeQuery();

			Map<String, Integer> tags = new HashMap<>();
			while (rs.next()) {
				if (g.getRoleById(rs.getString("tag")) != null) {
					tags.put(rs.getString("tag"), rs.getInt("preco"));
				}
			}

			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(tags.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});

			if (sorted.isEmpty()) {
				return null;
			}
			Map<Integer, String> tagById = new HashMap<>();
			int i = 0;
			for (Map.Entry<String, Integer> tag : sorted) {
				i++;
				tagById.put(i, Utils.getDinheiro(tag.getValue()));
			}
			return tagById.get(id);
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

	public static void listarTags(TextChannel tc, User u) {
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
			ps = conn.prepareStatement("SELECT * FROM `lojatags`");
			rs = ps.executeQuery();

			Map<String, Integer> tags = new HashMap<>();
			while (rs.next()) {
				if (g.getRoleById(rs.getString("tag")) != null) {
					tags.put(rs.getString("tag"), rs.getInt("preco"));
				}
			}

			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(tags.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});

			if (sorted.isEmpty()) {
				Utils.enviarEmbed(tc, u, "🏪 Loja de TAGs", "Nenhum conteúdo adicionado à loja.",
						(ConfigManager.temCfg(tc.getGuild(), "cor")
								? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
								: null),
						g.getName(), null, null, g.getIconUrl(), 0, true);
			}

			int i = 0;
			for (Map.Entry<String, Integer> tag : sorted) {
				i++;
				pbuilder.addItems("TAG: <@&" + tag.getKey() + "> - Preço: **" + Utils.getDinheiro(tag.getValue())
						+ "** - ID para compra: **" + i + "**");
				LojaTagsManager.tags.put(tag.getKey(), tag.getValue());
			}
			Paginator p = pbuilder
					.setColor((ConfigManager.temCfg(tc.getGuild(), "cor")
							? Color.decode(ConfigManager.getConfig(tc.getGuild(), "cor"))
							: null))
					.setText(u.getAsMention()
							+ ", para adquirir alguma das tags abaixo use `c!comprartag [ID da compra]`.")
					.setUsers(u).build();
			p.paginate(tc, 1, "🏪 Loja de TAGs", g);
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
