package ruffos._config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import ruffos.Main;

public class ConfigManager {

	public static boolean isCategoriaTempo(Guild g, Category cat) {
		if (!ConfigManager.temCfg(g, "tempocall") || cat == null) {
			return false;
		}
		String id = cat.getId();
		String[] split = getConfig(g, "tempocall").split(",");
		for (String s : split) {
			if (s.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasChat(Guild g, TextChannel tc) {
		if (!ConfigManager.temCfg(g, "canal")) {
			return false;
		}
		String id = tc.getId();
		String[] split = getConfig(g, "canal").split(",");
		for (String s : split) {
			if (s.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public static void configurar(Guild g, String cfg, String valor) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `config` (`config`, `valor`) VALUES (?,?)");
			ps.setString(1, cfg);
			ps.setString(2, valor);
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

	public static boolean temCfg(Guild g, String cfg) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `config` WHERE `config` = '" + cfg + "'");
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

	public static void modificar(Guild g, String cfg, String valor) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `config` SET `valor` = '" + valor + "' WHERE `config` = '" + cfg + "'");
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

	public static void deletar(Guild g, String cfg) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("DELETE FROM `config` WHERE `config` = '" + cfg + "'");
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

	public static String getConfig(Guild g, String cfg) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `config` WHERE `config` = '" + cfg + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("valor");
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
