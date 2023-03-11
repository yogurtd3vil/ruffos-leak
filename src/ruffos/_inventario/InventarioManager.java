package ruffos._inventario;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;

public class InventarioManager {

	public static boolean hasItem(Guild g, User u, String item, int qntMin) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"SELECT * FROM `inventario` WHERE `usuario` = '" + u.getId() + "' AND `item` = '" + item + "'");
			rs = ps.executeQuery();
			return rs.next() && rs.getInt("qnt") >= qntMin;
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

	public static String getRoupa(Guild g, User u, String roupa) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `inventario` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("item").contains(";")) {
					String[] split = rs.getString("item").split(";");
					if (split[0].equalsIgnoreCase(roupa)) {
						return split[1];
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
		return null;
	}

	public static boolean hasRoupa(Guild g, User u, String roupa) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `inventario` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("item").contains(";")) {
					String[] split = rs.getString("item").split(";");
					if (split[0].equalsIgnoreCase(roupa)) {
						return true;
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
		return false;
	}

	public static int getQntItem(Guild g, User u, String item) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"SELECT * FROM `inventario` WHERE `usuario` = '" + u.getId() + "' AND `item` = '" + item + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("qnt");
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

	public static void addItem(Guild g, User u, String item, int qnt) {
		PreparedStatement ps = null;
		if (hasItem(g, u, item, 1)) {
			try {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId())
						.prepareStatement("UPDATE `inventario` SET `qnt` = '" + (getQntItem(g, u, item) + qnt)
								+ "' WHERE `usuario` = '" + u.getId() + "' AND `item` = '" + item + "'");
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
		} else {
			try {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId())
						.prepareStatement("INSERT INTO `inventario` (`usuario`, `item`, `qnt`) VALUES ('" + u.getId()
								+ "', '" + item + "', '" + qnt + "')");
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

	public static void removeItem(Guild g, User u, String item, int qnt) {
		PreparedStatement ps = null;
		if (hasItem(g, u, item, 1)) {
			try {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId())
						.prepareStatement("UPDATE `inventario` SET `qnt` = '" + (getQntItem(g, u, item) - qnt)
								+ "' WHERE `usuario` = '" + u.getId() + "' AND `item` = '" + item + "'");
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
			if (getQntItem(g, u, item) < 1) {
				try {
					ps = Main.getDatabase().getConnectionByGuildId(g.getId())
							.prepareStatement("DELETE FROM `inventario` WHERE `usuario` = '" + u.getId()
									+ "' AND `item` = '" + item + "'");
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
	}

	public static void showInventario(Guild g, User u, TextChannel tc) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `inventario` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			List<String> itens = new ArrayList<>();
			while (rs.next()) {
				if (!rs.getString("item").contains(";")) {
					itens.add("`" + rs.getString("qnt") + "x " + rs.getString("item") + "`");
				} else {
					String[] split = rs.getString("item").split(";");
					itens.add("`" + rs.getString("qnt") + "x " + split[0] + "`");
				}
			}
			if (itens.size() > 0) {
				String inv = "";
				for (String s : itens) {
					inv = inv + s + "\n";
				}
				inv = inv.substring(0, inv.length() - 1);
				Utils.enviarEmbed(tc, u, null, "**Seu inventário:**\n\n" + inv,
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			} else {
				Utils.enviarEmbed(tc, u, null, "**Seu inventário:**\n\n`Inventário vazio.`",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
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
