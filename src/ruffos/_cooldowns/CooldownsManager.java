package ruffos._cooldowns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import net.dv8tion.jda.api.entities.Guild;
import ruffos.Main;

public class CooldownsManager {

	public static void addCooldown(Guild g, String usuario, String cooldown, long tempo) {
		Connection con = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		long time = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo")).getTimeInMillis();
		if(usuario.equals("380570412314001410"))return;
		if (existeCooldown(g, usuario)) {
			List<String> cooldowns = new LinkedList<String>(Arrays.asList(getCooldown(g, usuario).split(";")));
			if (getCooldownTime(g, usuario, cooldown) != 0) {
				String valor = getCooldown(g, usuario);
				String[] split = valor.split(";");
				for (String s : split) {
					String[] split2 = s.split(":");
					if (split2[0].equals(cooldown)) {
						cooldowns.remove(s);
					}
				}
			}
			cooldowns.add(cooldown + ":" + (time + tempo));
			String novo = "";
			for (String c : cooldowns) {
				novo = novo + c + ";";
			}
			try {
				ps = con.prepareStatement(
						"UPDATE `cooldowns` SET `valor` = '" + novo + "' WHERE `usuario` = '" + usuario + "'");
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
				ps = con.prepareStatement("INSERT INTO `cooldowns` (`usuario`, `valor`) VALUES ('" + usuario + "', '"
						+ cooldown + ":" + (time + tempo) + "')");
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

	public static long getCooldownTime(Guild g, String usuario, String cooldown) {
		if (getCooldown(g, usuario) == null) {
			return 0;
		}
		String valor = getCooldown(g, usuario);
		String[] split = valor.split(";");
		for (String s : split) {
			String[] split2 = s.split(":");
			if (split2[0].equals(cooldown)) {
				return Long.parseLong(split2[1]);
			}
		}
		return 0;
	}

	public static String getCooldown(Guild g, String usuario) {
		Connection con = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("SELECT * FROM `cooldowns` WHERE `usuario` = '" + usuario + "'");
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

	public static boolean existeCooldown(Guild g, String usuario) {
		Connection con = Main.getDatabase().getConnectionByGuildId(g.getId());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("SELECT * FROM `cooldowns` WHERE `usuario` = '" + usuario + "'");
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

}
