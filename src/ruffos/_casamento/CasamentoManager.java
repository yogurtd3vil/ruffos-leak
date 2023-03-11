package ruffos._casamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.dv8tion.jda.api.entities.Guild;
import ruffos.Main;

public class CasamentoManager {

	public static void casar(Guild g, String idUm, String idDois) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO `casamentos` (`idUm`, `idDois`, `dia`) VALUES ('" + idUm + "', '"
					+ idDois + "', '" + System.currentTimeMillis() + "')");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void divorciar(Guild g, String id) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM `casamentos` WHERE `idUm` = '" + id + "'");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			ps = conn.prepareStatement("DELETE FROM `casamentos` WHERE `idDois` = '" + id + "'");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getParceiro(Guild g, String id) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `casamentos` WHERE `idUm` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("idDois");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			ps = conn.prepareStatement("SELECT * FROM `casamentos` WHERE `idDois` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("idUm");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getData(Guild g, String id) {
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `casamentos` WHERE `idUm` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return new SimpleDateFormat("dd/MM/yyyy").format(new Date(Long.parseLong(rs.getString("dia"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			ps = conn.prepareStatement("SELECT * FROM `casamentos` WHERE `idDois` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return new SimpleDateFormat("dd/MM/yyyy").format(new Date(Long.parseLong(rs.getString("dia"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean casado(Guild g, String id) {
		// ||
		Connection conn = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM `casamentos` WHERE `idUm` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			ps = conn.prepareStatement("SELECT * FROM `casamentos` WHERE `idDois` = '" + id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
