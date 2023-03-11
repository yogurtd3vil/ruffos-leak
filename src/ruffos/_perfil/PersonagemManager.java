package ruffos._perfil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import ruffos.ConsoleColors;
import ruffos.Main;
import ruffos.utils.Utils;

public class PersonagemManager {

	public static void criarPersonagem(Guild g, User u) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("INSERT INTO `personagem` (`usuario`, `valor`) VALUES (" + u.getId()
							+ ", 'avatar;Ruffos é o melhor web cachorro do mundo! » c!sobremim para editar (custo: 1000.0);cor;sexo;corpessoa;')");
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

	public static void deletar(Guild g, User u) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("DELETE FROM `personagem` WHERE `usuario` = '" + u.getId() + "'");
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

	public static boolean existe(Guild g, User u) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `personagem` WHERE `usuario` = '" + u.getId() + "'");
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String get(Guild g, User u, String get) {
		PreparedStatement ps = null;
		String s = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `personagem` WHERE `usuario` = '" + u.getId() + "'");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				s = rs.getString("valor");
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
		if (s == null) {
			return get;
		}
		String[] split = s.split(";");
		if (get.equalsIgnoreCase("avatar")) {
			return (split[0] == null) ? get : split[0];
		} else if (get.equalsIgnoreCase("sobremim")) {
			return (split[1] == null) ? get : split[1];
		} else if (get.equalsIgnoreCase("cor")) {
			return (split[2] == null) ? get : split[2];
		} else if (get.equalsIgnoreCase("sexo")) {
			try {
				return (split[3] == null) ? get : split[3];
			} catch (ArrayIndexOutOfBoundsException e) {
				return get;
			}
		} else if (get.equalsIgnoreCase("corpessoa")) {
			try {
				return (split[4] == null) ? get : split[4];
			} catch (ArrayIndexOutOfBoundsException e) {
				return get;
			}
		}
		return s;
	}

	public static void removerRoupasBoost(Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `personagem`");
			rs = ps.executeQuery();
			while (rs.next()) {
				g.retrieveMemberById(rs.getString("usuario")).queue(s -> {
					if (get(g, s.getUser(), "avatar").equals("https://i.imgur.com/Hx4pWPb.png")
							|| get(g, s.getUser(), "avatar").equals("https://i.imgur.com/ZJkivVK.png")
							|| get(g, s.getUser(), "avatar").equals("https://i.imgur.com/XbK3fuX.png")
							|| get(g, s.getUser(), "avatar").equals("https://i.imgur.com/EqCZGjw.png")) {
						Utils.getRuffosGuild().retrieveMemberById(s.getUser().getId()).queue(ss -> {
							if (ss.getTimeBoosted() == null) {
								mudar(g, ss.getUser(), "avatar",
										Utils.getLink(get(g, ss.getUser(), "sexo"), get(g, ss.getUser(), "corpessoa")));
								System.out.println(
										Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Roupa Boost removida de: "
												+ ConsoleColors.BRIGHT_WHITE + ss.getUser().getName()
												+ ConsoleColors.BRIGHT_GREEN + "." + ConsoleColors.RESET);
							} else {
								System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Roupa Boost 100%: "
										+ ConsoleColors.BRIGHT_WHITE + ss.getUser().getName()
										+ ConsoleColors.BRIGHT_GREEN + "." + ConsoleColors.RESET);
							}
						}, f -> {
							mudar(g, s.getUser(), "avatar",
									Utils.getLink(get(g, s.getUser(), "sexo"), get(g, s.getUser(), "corpessoa")));
							System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN
									+ "Roupa Boost removida de: " + ConsoleColors.BRIGHT_WHITE + s.getUser().getName()
									+ ConsoleColors.BRIGHT_GREEN + "." + ConsoleColors.RESET);
						});
					}
				}, f -> {

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

	public static void mudar(Guild g, User u, String oQ, String mudanca) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `personagem` SET `valor` = ? WHERE `usuario` = '" + u.getId() + "'");
			if (oQ.equalsIgnoreCase("avatar")) {
				ps.setString(1, mudanca + ";" + get(g, u, "sobremim") + ";" + get(g, u, "cor") + ";" + get(g, u, "sexo")
						+ ";" + get(g, u, "corpessoa") + ";");
			} else if (oQ.equalsIgnoreCase("sobremim")) {
				ps.setString(1, get(g, u, "avatar") + ";" + mudanca + ";" + get(g, u, "cor") + ";" + get(g, u, "sexo")
						+ ";" + get(g, u, "corpessoa") + ";");
			} else if (oQ.equalsIgnoreCase("cor")) {
				ps.setString(1, get(g, u, "avatar") + ";" + get(g, u, "sobremim") + ";" + mudanca + ";"
						+ get(g, u, "sexo") + ";" + get(g, u, "corpessoa") + ";");
			} else if (oQ.equalsIgnoreCase("sexo")) {
				ps.setString(1, get(g, u, "avatar") + ";" + get(g, u, "sobremim") + ";" + get(g, u, "cor") + ";"
						+ mudanca + ";" + get(g, u, "corpessoa") + ";");
			} else if (oQ.equalsIgnoreCase("corpessoa")) {
				ps.setString(1, get(g, u, "avatar") + ";" + get(g, u, "sobremim") + ";" + get(g, u, "cor") + ";"
						+ get(g, u, "sexo") + ";" + mudanca + ";");
			}
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
