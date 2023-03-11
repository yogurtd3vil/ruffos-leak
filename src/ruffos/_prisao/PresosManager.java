
package ruffos._prisao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos.Main;

public class PresosManager {

	public static void prender(Guild g, User u, int pena, String motivo) {
		PreparedStatement ps = null;
		if (!existePreso(g, u)) {
			Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
			try {
				ps = conn.prepareStatement(
						"INSERT INTO `presos` (`usuario`, `dia`, `pena`, `motivo`, `passagens`, `preso`, `pedras`) VALUES (?,?,?,?,?,?,?)");
				ps.setString(1, u.getId());
				Date d = new Date();
				ps.setLong(2, d.getTime());
				d.setMinutes(d.getMinutes() + pena);
				ps.setLong(3, d.getTime());
				ps.setString(4, motivo);
				ps.setInt(5, (getPassagens(g, u) + 1));
				ps.setBoolean(6, true);
				ps.setInt(7, 0);
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
			prenderDnv(g, u, pena, motivo);
		}
	}

	public static boolean estaPreso(Guild g, User u) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `presos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("preso");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void prenderDnv(Guild g, User u, int pena, String motivo) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					"UPDATE `presos` SET `dia` = ?, `pena` = ?, `motivo` = ?, `passagens` = ?, `preso` = ?, `pedras` = ? WHERE `usuario` = '"
							+ u.getId() + "'");
			Date d = new Date();
			ps.setLong(1, d.getTime());
			d.setMinutes(d.getMinutes() + pena);
			ps.setLong(2, d.getTime());
			ps.setString(3, motivo);
			ps.setInt(4, (getPassagens(g, u) + 1));
			ps.setBoolean(5, true);
			ps.setInt(6, 0);
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

	public static void soltar(Guild g, User u) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE `presos` SET `preso` = ? WHERE `usuario` = '" + u.getId() + "'");
			ps.setBoolean(1, false);
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

	public static boolean existePreso(Guild g, User u) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `presos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getPassagens(Guild g, User u) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `presos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("passagens");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getDiaPreso(Guild g, User u) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `presos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong("dia");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getPena(Guild g, User u) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `presos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getLong("pena");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getMotivo(Guild g, User u) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `presos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("motivo");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
