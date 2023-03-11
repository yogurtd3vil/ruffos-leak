package ruffos.quitandjoingame;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import ruffos.Main;
import ruffos.utils.Utils;

public class QuitAndJoinManager {

	public static boolean isQuited(Guild g, Member m) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		if (g != null && m != null && !Utils.isIgnoredServer(g)) {
			try {
				ps = Main.getDatabase().getGlobalConnection()
						.prepareStatement("SELECT * FROM `quitados` WHERE `usuario` = '" + m.getId()
								+ "' AND `guild` = '" + g.getId() + "'");
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
		}
		return false;
	}

	public static void setQuited(Guild g, Member m) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getGlobalConnection().prepareStatement(
					"INSERT INTO `quitados` (`usuario`, `guild`) VALUES ('" + m.getId() + "', '" + g.getId() + "')");
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

	public static void setJoined(Guild g, Member m) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getGlobalConnection().prepareStatement(
					"DELETE FROM `quitados` WHERE `usuario` = '" + m.getId() + "' OR `guild` = '" + g.getId() + "'");
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
