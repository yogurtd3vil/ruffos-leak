package ruffos._filhos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import ruffos.Main;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;

public class FilhosManager {

	public static int criarFilho(Guild g, String nome, String sexo, String pai, String mae) {
		PreparedStatement ps = null;
		int filhoID = 0;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"INSERT INTO `filhos` (`filhoID`, `nome`, `sexo`, `pai`, `mae`, `saude`, `fome`, `sede`, `felicidade`, `avatar`) VALUES (?,?,?,?,?,?,?,?,?,?)");
			System.out.println(getLastId(g));
			filhoID = (getLastId(g) + 1);
			System.out.println(getLastId(g) + 1);
			System.out.println(filhoID);
			ps.setInt(1, filhoID);
			ps.setString(2, nome);
			ps.setString(3, sexo);
			ps.setString(4, pai);
			ps.setString(5, mae);
			ps.setInt(6, 100);
			ps.setInt(7, 100);
			ps.setInt(8, 100);
			ps.setInt(9, 100);
			ps.setString(10, "Nenhum");
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
		return filhoID;
	}

	public static int getLastId(Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `filhos` ORDER BY `filhoID` DESC LIMIT 1");
			rs = ps.executeQuery();
			if (rs.next()) {
				return Integer.parseInt(String.valueOf(rs.getLong("filhoID")));
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

	public static Map<String, Timer> salvarTimerFelicidade = new HashMap<>();

	public static void perderFelicidade(Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `filhos`");
			rs = ps.executeQuery();
			while (rs.next()) {
				String filhoID = rs.getString("filhoID");
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						retirar(g, filhoID, "felicidade");
						String pai = getInfo(g, filhoID, "pai").toString();
						String mae = getInfo(g, filhoID, "mae").toString();
						String nome = getInfo(g, filhoID, "nome").toString();
						int fome = Integer.parseInt(String.valueOf(getInfo(g, filhoID, "felicidade")));
						if (fome <= 20 && fome != 0) {
							if (g.getMemberById(pai) != null) {
								try {
									g.getMemberById(pai).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") está prestes a morrer de depressão! Caso ele morra você perderá **"
													+ Utils.getDinheiro(5000)
													+ "** em suas mãos possuindo o valor ou não.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							if (g.getMemberById(mae) != null) {
								try {
									g.getMemberById(mae).getUser().openPrivateChannel().complete()
											.sendMessage("Seu(ua) filho(a) de ID **" + filhoID + "** (" + nome
													+ ") está prestes a morrer de depressão! Caso ele morra você perderá **"
													+ Utils.getDinheiro(5000)
													+ "** em suas mãos possuindo o valor ou não.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
						} else if (fome == 0) {
							EconomiaManager.removeDinheiroMaos(g, pai, 5000);
							EconomiaManager.removeDinheiroMaos(g, mae, 5000);
							if (g.getMemberById(pai) != null) {
								try {
									g.getMemberById(pai).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") morreu de depressão! Você perdeu **" + Utils.getDinheiro(5000)
													+ "** em mãos.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							if (g.getMemberById(mae) != null) {
								try {
									g.getMemberById(mae).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") morreu de depressão! Você perdeu **" + Utils.getDinheiro(5000)
													+ "** em mãos.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							morrer(g, filhoID);
						}
					}
				}, 2100000, 2100000);
				salvarTimerFelicidade.put(rs.getString("filhoID"), timer);
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

	public static Map<String, Timer> salvarTimerSede = new HashMap<>();

	public static void ficarComSede(Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `filhos`");
			rs = ps.executeQuery();
			while (rs.next()) {
				String filhoID = rs.getString("filhoID");
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						retirar(g, filhoID, "sede");
						String pai = getInfo(g, filhoID, "pai").toString();
						String mae = getInfo(g, filhoID, "mae").toString();
						String nome = getInfo(g, filhoID, "nome").toString();
						int fome = Integer.parseInt(String.valueOf(getInfo(g, filhoID, "sede")));
						if (fome <= 20 && fome != 0) {
							if (g.getMemberById(pai) != null) {
								try {
									g.getMemberById(pai).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") está prestes a morrer de sede! Caso ele morra você perderá **"
													+ Utils.getDinheiro(5000)
													+ "** em suas mãos possuindo o valor ou não.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							if (g.getMemberById(mae) != null) {
								try {
									g.getMemberById(mae).getUser().openPrivateChannel().complete()
											.sendMessage("Seu(ua) filho(a) de ID **" + filhoID + "** (" + nome
													+ ") está prestes a morrer de sede! Caso ele morra você perderá **"
													+ Utils.getDinheiro(5000)
													+ "** em suas mãos possuindo o valor ou não.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
						} else if (fome == 0) {
							EconomiaManager.removeDinheiroMaos(g, pai, 5000);
							EconomiaManager.removeDinheiroMaos(g, mae, 5000);
							if (g.getMemberById(pai) != null) {
								try {
									g.getMemberById(pai).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") morreu de sede! Você perdeu **" + Utils.getDinheiro(5000)
													+ "** em mãos.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							if (g.getMemberById(mae) != null) {
								try {
									g.getMemberById(mae).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") morreu de sede! Você perdeu **" + Utils.getDinheiro(5000)
													+ "** em mãos.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							morrer(g, filhoID);
						}
					}
				}, 3600000, 3600000);
				salvarTimerSede.put(rs.getString("filhoID"), timer);
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

	public static Map<String, Timer> salvarTimerFome = new HashMap<>();

	public static void ficarComFome(Guild g) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `filhos`");
			rs = ps.executeQuery();
			while (rs.next()) {
				String filhoID = rs.getString("filhoID");
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						retirar(g, filhoID, "fome");
						String pai = getInfo(g, filhoID, "pai").toString();
						String mae = getInfo(g, filhoID, "mae").toString();
						String nome = getInfo(g, filhoID, "nome").toString();
						int fome = Integer.parseInt(String.valueOf(getInfo(g, filhoID, "fome")));
						if (fome <= 20 && fome != 0) {
							if (g.getMemberById(pai) != null) {
								try {
									g.getMemberById(pai).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") está prestes a morrer de fome! Caso ele morra você perderá **"
													+ Utils.getDinheiro(5000)
													+ "** em suas mãos possuindo o valor ou não.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							if (g.getMemberById(mae) != null) {
								try {
									g.getMemberById(mae).getUser()
											.openPrivateChannel().complete()
											.sendMessage("Seu(ua) filho(a) de ID **" + filhoID + "** (" + nome
													+ ") está prestes a morrer de fome! Caso ele morra você perderá **"
													+ Utils.getDinheiro(5000)
													+ "** em suas mãos possuindo o valor ou não.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
						} else if (fome == 0) {
							EconomiaManager.removeDinheiroMaos(g, pai, 5000);
							EconomiaManager.removeDinheiroMaos(g, mae, 5000);
							if (g.getMemberById(pai) != null) {
								try {
									g.getMemberById(pai).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") morreu de fome! Você perdeu **" + Utils.getDinheiro(5000)
													+ "** em mãos.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							if (g.getMemberById(mae) != null) {
								try {
									g.getMemberById(mae).getUser().openPrivateChannel().complete()
											.sendMessage("Seu filho de ID **" + filhoID + "** (" + nome
													+ ") morreu de fome! Você perdeu **" + Utils.getDinheiro(5000)
													+ "** em mãos.")
											.queue(s -> {

											}, f -> {

											});
								} catch (ErrorResponseException e) {

								}
							}
							morrer(g, filhoID);
						}
					}
				}, 1800000, 1800000);
				salvarTimerFome.put(rs.getString("filhoID"), timer);
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

	public static void retirar(Guild g, String filhoID, String oq) {
		PreparedStatement ps = null;
		int quantidade = 0;
		switch (oq) {
		case "sede":
			quantidade = 5;
			break;
		case "fome":
			quantidade = 5;
			break;
		case "felicidade":
			quantidade = 5;
			break;
		default:
			break;
		}
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("UPDATE `filhos` SET `" + oq + "` = '"
							+ (Integer.parseInt(getInfo(g, filhoID, oq).toString()) - quantidade)
							+ "' WHERE `filhoID` = '" + filhoID + "'");
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

	public static Object getInfo(Guild g, String filhoID, String oq) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `filhos` WHERE `filhoID` = '" + filhoID + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				if (oq.equals("sede")) {
					return rs.getInt(oq);
				} else if (oq.equals("pai")) {
					return rs.getString("pai");
				} else if (oq.equals("mae")) {
					return rs.getString("mae");
				} else if (oq.equals("saude")) {
					return rs.getInt("saude");
				} else if (oq.equals("fome")) {
					return rs.getInt("fome");
				} else if (oq.equals("felicidade")) {
					return rs.getInt("felicidade");
				} else if (oq.equals("nome")) {
					return rs.getString("nome");
				} else if (oq.equals("avatar")) {
					return rs.getString("avatar");
				} else if (oq.equals("sexo")) {
					return rs.getString("sexo");
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

	public static void update(Guild g, String filhoID, String oq, Object valor) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
					"UPDATE `filhos` SET `" + oq + "` = '" + valor + "' WHERE `filhoID` = '" + filhoID + "'");
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

	public static List<String> getFilhos(Guild g, User u) {
		List<String> filhos = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `filhos` WHERE `pai` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			while (rs.next()) {
				filhos.add("ID: **" + rs.getString("filhoID") + "** - Nome: **" + rs.getString("nome") + "** - Sexo: **"
						+ rs.getString("sexo") + "** - **VOCÊ É PAI DESTE(A) FILHO(A)**");
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
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `filhos` WHERE `mae` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			while (rs.next()) {
				filhos.add("ID: **" + rs.getString("filhoID") + "** - Nome: **" + rs.getString("nome") + "** - Sexo: **"
						+ rs.getString("sexo") + "** - **VOCÊ É MÃE DESTE(A) FILHO(A)**");
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
		return filhos;
	}

	public static void morrer(Guild g, String filhoId) {
		PreparedStatement ps = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("DELETE FROM `filhos` WHERE `filhoID` = '" + Integer.parseInt(filhoId) + "'");
			ps.executeUpdate();
			salvarTimerSede.get(filhoId).cancel();
			salvarTimerSede.remove(filhoId);
			salvarTimerFome.get(filhoId).cancel();
			salvarTimerFome.remove(filhoId);
			salvarTimerFelicidade.get(filhoId).cancel();
			salvarTimerFelicidade.remove(filhoId);
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
