package ruffos._rankings;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import ruffos.Main;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class TopRoubosCommand implements ICommand {

	public static boolean hasRoubo(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `roubos` WHERE `usuario` = '" + u.getId() + "'");
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

	public static int getRoubos(Guild g, User u) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId())
					.prepareStatement("SELECT * FROM `roubos` WHERE `usuario` = '" + u.getId() + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("roubos");
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

	public static void addRoubo(Guild g, User u, int i) {
		PreparedStatement ps = null;
		try {
			if (hasRoubo(g, u)) {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId())
						.prepareStatement("UPDATE `roubos` SET `roubos` = '" + (getRoubos(g, u) + i)
								+ "' WHERE `usuario` = '" + u.getId() + "'");
				ps.executeUpdate();
			} else {
				ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement(
						"INSERT INTO `roubos` (`usuario`, `roubos`) VALUES ('" + u.getId() + "', '" + i + "') ");
				ps.executeUpdate();
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
	}

	public Map<String, Integer> getRoubos(Guild g) {
		Map<String, Integer> roubos = new HashMap<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = Main.getDatabase().getConnectionByGuildId(g.getId()).prepareStatement("SELECT * FROM `roubos`");
			rs = ps.executeQuery();
			while (rs.next()) {
				try {
					roubos.put(rs.getString("usuario"), rs.getInt("roubos"));
				} catch (ErrorResponseException e) {
					continue;
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
		return roubos;
	}

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			Map<String, Integer> roubos = getRoubos(g);
			List<Map.Entry<String, Integer>> sorted = new ArrayList<>(roubos.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> oo1, Entry<String, Integer> oo2) {
					return Integer.compare(oo2.getValue(), oo1.getValue());
				}
			});
			String top1 = "Ninguém";
			int top1i = 0;
			if (sorted.size() >= 1) {
				top1 = "<@" + sorted.get(0).getKey() + ">";
				top1i = sorted.get(0).getValue();
			}
			String top2 = "Ninguém";
			int top2i = 0;
			if (sorted.size() >= 2) {
				top2 = "<@" + sorted.get(1).getKey() + ">";
				top2i = sorted.get(1).getValue();
			}
			String top3 = "Ninguém";
			int top3i = 0;
			if (sorted.size() >= 3) {
				top3 = "<@" + sorted.get(2).getKey() + ">";
				top3i = sorted.get(2).getValue();
			}
			String top4 = "Ninguém";
			int top4i = 0;
			if (sorted.size() >= 4) {
				top4 = "<@" + sorted.get(3).getKey() + ">";
				top4i = sorted.get(3).getValue();
			}
			String top5 = "Ninguém";
			int top5i = 0;
			if (sorted.size() >= 5) {
				top5 = "<@" + sorted.get(4).getKey() + ">";
				top5i = sorted.get(4).getValue();
			}
			EmbedBuilder eb = new EmbedBuilder();
			eb.setThumbnail(g.getIconUrl());
			eb.setDescription("**1º **" + top1 + " - Dinheiro roubado: **" + Utils.getDinheiro(top1i) + "**\n**2º **"
					+ top2 + " - Dinheiro roubado: **" + Utils.getDinheiro(top2i) + "**\n**3º **" + top3
					+ " - Dinheiro roubado: **" + Utils.getDinheiro(top3i) + "**\n**4º **" + top4
					+ " - Dinheiro roubado: **" + Utils.getDinheiro(top4i) + "**\n**5º **" + top5
					+ " - Dinheiro roubado: **" + Utils.getDinheiro(top5i) + "**");
			eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
			eb.setFooter(g.getName() + " » Roubos a membros e roubos a banco.");
			eb.setTimestamp(Instant.now());
			eb.setTitle(Utils.getEmote("trofeu").getFormatted() + " TOP 5 - Mais roubaram");
			ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
		}
	}

	@Override
	public String getName() {
		return "c!toproubos";
	}

	@Override
	public String getHelp() {
		return "Veja quem mais rouboBancos nos últimos tempos.";
	}

}
