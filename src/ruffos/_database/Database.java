package ruffos._database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import ruffos.ConsoleColors;
import ruffos.Main;
import ruffos.utils.Utils;

public class Database {

	private HashMap<String, Connection> connections = new HashMap<>();

	public HashMap<String, Connection> getConnections() {
		return connections;
	}

	private Connection globalConnection = null;

	public void setGlobalConnection() {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		System.out.println("a");
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://200.9.154.111/global", "root", "13062003tdp");
			globalConnection = con;
			ps = globalConnection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `clans` (`guild` VARCHAR(255) NOT NULL, `tag` VARCHAR(255) NOT NULL, `clan` VARCHAR(255) NOT NULL, `fundador` VARCHAR(255) NOT NULL, `membros` VARCHAR(255) NOT NULL, `farmado` INTEGER NOT NULL, `banner` VARCHAR(255) NOT NULL, `logo` VARCHAR(255) NOT NULL, `criado` VARCHAR(255) NOT NULL)");
			ps.executeUpdate();
			ps2 = globalConnection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `quitados` (`usuario` VARCHAR(255) NOT NULL, `guild` VARCHAR(255) NOT NULL)");
			ps2.executeUpdate();
			System.out.println("oi");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				ps2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Connection getGlobalConnection() {
		return globalConnection;
	}

	public Connection getConnectionByGuildId(String id) {
		return connections.get("ruffos" + id);
	}

	public Database() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://200.9.154.111/", "root", "13062003tdp");
			Statement statement = connection.createStatement();
			ResultSet databases = statement.executeQuery("SHOW DATABASES;");
			System.out.println("show");
			int i = 0;
			int b = 0;
			while (databases.next()) {
				b++;
				System.out.println("aa " + b);
				String db = databases.getString(1);
				if (db.startsWith("ruffos") && !db.equals("ruffos")) {
					Connection c = DriverManager.getConnection("jdbc:mysql://200.9.154.111/" + db, "root",
							"13062003tdp");
					connections.put(db, c);
					i++;
				}
			}
			System.out.println("bb");
			statement.close();
			connection.close();
			databases.close();
			System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_CYAN + i + ConsoleColors.BRIGHT_GREEN
					+ " databases cacheadas." + ConsoleColors.RESET);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		System.out.println("das");
	}

	public void criarGuild(Guild g) {
		try {
			System.out.println("eaeeeeeeeeeeeee");
			long bots = g.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
			long members = g.getMembers().stream().map(m -> m.getUser()).filter(u -> !u.isBot()).count();
			String time = Utils.getTime();
			System.out.println(time + ConsoleColors.BRIGHT_GREEN + "O Ruffos foi adicionado em um servidor! Guild ID: "
					+ ConsoleColors.BRIGHT_CYAN + g.getId() + ConsoleColors.BRIGHT_GREEN + " - Posse: "
					+ ConsoleColors.BRIGHT_CYAN + g.retrieveOwner().complete().getUser().getName()
					+ ConsoleColors.BRIGHT_GREEN + " - Total de membros: " + ConsoleColors.BRIGHT_CYAN
					+ g.getMembers().size() + ConsoleColors.BRIGHT_GREEN + " - Humanos: " + ConsoleColors.BRIGHT_CYAN
					+ members + ConsoleColors.BRIGHT_GREEN + " - BOTs: " + ConsoleColors.BRIGHT_CYAN + bots);
			System.out.println(time + ConsoleColors.BRIGHT_GREEN + "Criando database e tabelas...");
			Class.forName("com.mysql.jdbc.Driver");
			Connection c = DriverManager.getConnection("jdbc:mysql://200.9.154.111/", "root", "13062003tdp");
			Statement statement = c.createStatement();

			statement.executeUpdate("CREATE DATABASE ruffos" + g.getId());
			TextChannel tc = Main.getJDA().getTextChannelById("668649934421229581");
			List<Field> fields = new ArrayList<>();
			fields.add(new Field("Guild ID", g.getId(), true));
			fields.add(new Field("Nome da Guild: ", g.getName(), true));
			fields.add(new Field("Posse: ", "<@" + g.retrieveOwner().complete().getUser().getId() + ">", true));
			fields.add(new Field("Total de membros: ", "" + g.getMembers().size(), true));
			fields.add(new Field("Humanos: ", "" + members, true));
			fields.add(new Field("BOTs: ", "" + bots, true));
			if (g.getVanityUrl() != null) {
				fields.add(new Field("VanityURL: ", g.getVanityUrl(), true));
			}

			// Utils.enviarEmbed(tc, null, "Ruffos adicionado em nova Guild", null,
			// Color.GREEN, null, fields,
			// g.getIconUrl(), null, 0, false);
			Connection connection = DriverManager.getConnection("jdbc:mysql://200.9.154.111/ruffos" + g.getId(), "root",
					"13062003tdp");
			connections.put("ruffos" + g.getId(), connection);
			statement.close();
			c.close();
			criarTabelas(g);
			System.out.println(time + ConsoleColors.BRIGHT_GREEN + "Tudo feito e conexão estabelecida!");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void deletarGuild(Guild g) {
		try {
			long bots = g.getMembers().stream().map(m -> m.getUser()).filter(u -> u.isBot()).count();
			long members = g.getMembers().stream().map(m -> m.getUser()).filter(u -> !u.isBot()).count();
			String time = Utils.getTime();
			System.out.println(time + ConsoleColors.BRIGHT_RED + "O Ruffos foi removido de um servidor! Guild ID: "
					+ ConsoleColors.BRIGHT_CYAN + g.getId() + ConsoleColors.BRIGHT_RED + " - Posse: "
					+ ConsoleColors.BRIGHT_CYAN + g.retrieveOwner().complete().getUser().getName()
					+ ConsoleColors.BRIGHT_RED + " - Total de membros: " + ConsoleColors.BRIGHT_CYAN
					+ g.getMembers().size() + ConsoleColors.BRIGHT_RED + " - Humanos: " + ConsoleColors.BRIGHT_CYAN
					+ members + ConsoleColors.BRIGHT_RED + " - BOTs: " + ConsoleColors.BRIGHT_CYAN + bots);
			System.out.println(time + ConsoleColors.RED + "Deletando database...");
			Class.forName("com.mysql.jdbc.Driver");
			Connection c = DriverManager.getConnection("jdbc:mysql://200.9.154.111/", "root", "13062003tdp");
			Statement statement = c.createStatement();

			statement.executeUpdate("DROP DATABASE ruffos" + g.getId());

			TextChannel tc = Main.getJDA().getTextChannelById("668650038603415592");
			List<Field> fields = new ArrayList<>();
			fields.add(new Field("Guild ID", g.getId(), true));
			fields.add(new Field("Nome da Guild: ", g.getName(), true));
			fields.add(new Field("Posse: ", "<@" + g.retrieveOwner().complete().getUser().getId() + ">", true));
			fields.add(new Field("Total de membros: ", "" + g.getMembers().size(), true));
			fields.add(new Field("Humanos: ", "" + members, true));
			fields.add(new Field("BOTs: ", "" + bots, true));
			if (g.getVanityUrl() != null) {
				fields.add(new Field("VanityURL: ", g.getVanityUrl(), true));
			}

			// Utils.enviarEmbed(tc, null, "Ruffos removido de uma Guild", null, Color.RED,
			// null, fields, g.getIconUrl(),
			// null, 0, false);
			connections.get("ruffos" + g.getId()).close();
			connections.remove("ruffos" + g.getId());
			statement.close();
			c.close();
			System.out.println(time + ConsoleColors.RED + "Tudo feito e conexão fechada!");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void criarTabelas(Guild g) {
		PreparedStatement cooldowns = null;
		PreparedStatement dinheiro = null;
		PreparedStatement recompensa = null;
		PreparedStatement arma = null;
		PreparedStatement bebidas = null;
		PreparedStatement trabalhos = null;
		PreparedStatement leveis = null;
		PreparedStatement personagem = null;
		PreparedStatement tempocall = null;
		PreparedStatement casamento = null;
		PreparedStatement presos = null;
		PreparedStatement filhos = null;
		PreparedStatement config = null;
		PreparedStatement pd = null;
		PreparedStatement amantes = null;
		PreparedStatement roubos = null;
		PreparedStatement inventario = null;
		PreparedStatement lojatags = null;
		PreparedStatement lvlexa = null;

		try {
			Connection con = getConnectionByGuildId(g.getId());
			if (con == null) {
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			dinheiro = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `dinheiro` (`usuario` LONG NOT NULL, `maos` DOUBLE NOT NULL, `banco` DOUBLE NOT NULL)");
			dinheiro.executeUpdate();
			recompensa = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `recompensa` (`usuario` LONG NOT NULL, `tempo` LONG NOT NULL)");
			recompensa.executeUpdate();
			arma = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `armas` (`usuario` LONG NOT NULL, `arma` VARCHAR(255) NOT NULL, `usos` INTEGER NOT NULL)");
			arma.executeUpdate();
			bebidas = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `bebidas` (`usuario` LONG NOT NULL, `bebida` VARCHAR(255) NOT NULL, `copos` INTEGER NOT NULL)");
			bebidas.executeUpdate();
			trabalhos = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `trabalhos` (`usuario` VARCHAR(255) NOT NULL, `trabalho` VARCHAR(255) NOT NULL, `tempo` LONG NOT NULL)");
			trabalhos.executeUpdate();
			leveis = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `leveis` (`id` VARCHAR(255) NOT NULL, `level` INTEGER NOT NULL, `xp` LONG NOT NULL)");
			leveis.executeUpdate();
			personagem = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `personagem` (`usuario` VARCHAR(255) NOT NULL, `valor` LONGTEXT)");
			personagem.executeUpdate();
			tempocall = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `tempocall` (`id` VARCHAR(255) NOT NULL, `tempo` INTEGER)");
			tempocall.executeUpdate();
			casamento = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `casamentos` (`idUm` VARCHAR(255) NOT NULL, `idDois` VARCHAR(255) NOT NULL, `dia` VARCHAR(255) NOT NULL)");
			casamento.executeUpdate();
			presos = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `presos` (`usuario` VARCHAR(255) NOT NULL, `dia` LONG NOT NULL, `pena` LONG NOT NULL, `motivo` VARCHAR(255) NOT NULL, `passagens` INTEGER NOT NULL, `preso` BOOLEAN NOT NULL, `pedras` INTEGER NOT NULL)");
			presos.executeUpdate();
			cooldowns = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `cooldowns` (`usuario` VARCHAR(255), `valor` LONGTEXT NOT NULL)");
			cooldowns.executeUpdate();
			filhos = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `filhos` (`filhoID` INTEGER NOT NULL, `nome` VARCHAR(255) NOT NULL, `sexo` VARCHAR(255) NOT NULL, `pai` VARCHAR(255) NOT NULL, `mae` VARCHAR(255) NOT NULL, `saude` INTEGER NOT NULL, `fome` INTEGER NOT NULL, `sede` INTEGER NOT NULL, `felicidade` INTEGER NOT NULL, `avatar` VARCHAR(255) NOT NULL)");
			filhos.executeUpdate();
			config = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `config` (`config` VARCHAR(255) NOT NULL, `valor` LONGTEXT NOT NULL)");
			config.executeUpdate();
			amantes = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `amantes` (`usuario` VARCHAR(255) NOT NULL, `amanteslista` LONGTEXT NOT NULL)");
			amantes.executeUpdate();
			roubos = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `roubos` (`usuario` VARCHAR(255) NOT NULL, `roubos` LONG NOT NULL)");
			roubos.executeUpdate();
			inventario = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `inventario` (`usuario` VARCHAR(255) NOT NULL, `item` VARCHAR(255) NOT NULL, `qnt` INTEGER NOT NULL)");
			inventario.executeUpdate();
			lojatags = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `lojatags` (`tag` VARCHAR(255) NOT NULL, `preco` INTEGER NOT NULL)");
			lojatags.executeUpdate();
			pd = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `pd` (`adm` VARCHAR(255) NOT NULL, `membro` VARCHAR(255) NOT NULL)");
			pd.executeUpdate();
			lvlexa = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `leveisexa` (`id` VARCHAR(255) NOT NULL, `level` INTEGER NOT NULL, `xp` LONG NOT NULL)");
			lvlexa.executeUpdate();
		} catch (ClassNotFoundException |

				SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				dinheiro.close();
				recompensa.close();
				arma.close();
				bebidas.close();
				trabalhos.close();
				leveis.close();
				personagem.close();
				tempocall.close();
				casamento.close();
				presos.close();
				cooldowns.close();
				filhos.close();
				config.close();
				amantes.close();
				roubos.close();
				inventario.close();
				lojatags.close();
				pd.close();
				lvlexa.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
