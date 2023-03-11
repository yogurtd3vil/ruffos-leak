package ruffos._amantes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos.Main;

public class AmantesManager {

	public static void verificarAmantes(Guild g, User u) {
		for (String s : getAmantes(g, u)) {
			if (g.retrieveMemberById(s) == null) {
				removerAmante(g, u, s);
			}
		}
	}

	public static List<String> getAmantes(Guild g, User u) {
		List<String> amantes = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `amantes` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				String[] split = rs.getString("amanteslista").split(",");
				for (String s : split) {
					amantes.add(s);
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
		return amantes;
	}

	public static boolean hasAmantes(Guild g, User u) {
		return !getAmantes(g, u).isEmpty();
	}

	public static void addAmante(Guild g, User u, User amante) {
		PreparedStatement ps = null;
		if (!hasAmantes(g, u)) {
			try {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId())
						.prepareStatement("INSERT INTO `amantes` (`usuario`, `amanteslista`) VALUES (?,?)");
				ps.setString(1, u.getId());
				ps.setString(2, amante.getId());
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
			String novo = "";
			for (String s : getAmantes(g, u)) {
				novo = novo + s + ",";
			}
			novo = novo + amante.getId();
			try {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId())
						.prepareStatement("UPDATE `amantes` SET `amanteslista` = ? WHERE `usuario` = ?");
				ps.setString(1, novo);
				ps.setString(2, u.getId());
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

	public static void removerAmante(Guild g, User u, String id) {
		PreparedStatement ps = null;
		if (hasAmantes(g, u)) {
			List<String> amantes = getAmantes(g, u);
			amantes.remove(id);
			String novo = "";
			for (String s : amantes) {
				novo = novo + s + ",";
			}
			novo = StringUtils.chop(novo);
			if (novo.equals("")) {
				try {
					ps = Main.getDatabase().getConnectionByGuildId(g.getId())
							.prepareStatement("DELETE FROM `amantes` WHERE `usuario` = '" + u.getId() + "'");
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
			try {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
						"UPDATE `amantes` SET `amanteslista` = '" + novo + "' WHERE `usuario` = '" + u.getId() + "'");
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
