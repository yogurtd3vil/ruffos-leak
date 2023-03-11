package ruffos.utils;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import ruffos.ConsoleColors;
import ruffos.Main;
import ruffos._config.ConfigManager;

public class Utils {

	public static boolean isFakeGuild(Guild g) {
		return ConfigManager.temCfg(g, "fake") && Boolean.parseBoolean(ConfigManager.getConfig(g, "fake")) == true;
	}

	public static Guild getRuffosGuild() {
		return Main.getJDA().getGuildById("832995038740348998");
	}

	public static Member getMentionedMember(Guild g, String args) {
		String arg = args.replace("<", "").replace(">", "").replace("!", "").replace("@", "");
		return (args.length() == 18 && isLong(args) ? g.retrieveMemberById(args).complete()
				: g.retrieveMemberById(arg).complete());
	}

	public static Map<String, Integer> empregos = new HashMap<>();

	public static void loadEmpregos() {
		empregos.put(Utils.getEmote("lixeiro").getFormatted() + " Lixeiro", 0);
		empregos.put(Utils.getEmote("jardineiro").getFormatted() + " Jardineiro", 10);
		empregos.put(Utils.getEmote("puta").getFormatted() + " Prostituta", 15);
		empregos.put(Utils.getEmote("manobrista").getFormatted() + " Manobrista", 20);
		empregos.put(Utils.getEmote("motorista").getFormatted() + " Motorista", 25);
		empregos.put(Utils.getEmote("garcom").getFormatted() + " Garçom", 30);
		empregos.put(Utils.getEmote("recepcionista").getFormatted() + " Recepcionista", 35);
		empregos.put(Utils.getEmote("governanta").getFormatted() + " Governanta", 40);
		empregos.put(Utils.getEmote("cozinheiro").getFormatted() + " Chefe de cozinha", 50);
		empregos.put(Utils.getEmote("sommelier").getFormatted() + " Sommelier", 60);
		empregos.put(Utils.getEmote("bombeiro").getFormatted() + " Bombeiro", 70);
		empregos.put(Utils.getEmote("policial").getFormatted() + " Policial", 80);
		empregos.put(Utils.getEmote("medico").getFormatted() + " Médico", 90);
		empregos.put(Utils.getEmote("doleiro").getFormatted() + " Doleiro", 100);
	}

	public static String getLink(String sexo, String corPessoa) {
		if (sexo.equals("masculino") && corPessoa.equals("branco")) {
			return "https://i.imgur.com/clEL2uo.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro")) {
			return "https://i.imgur.com/QZGHwuy.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco")) {
			return "https://i.imgur.com/lwf9zCw.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro")) {
			return "https://i.imgur.com/HDjeq0J.png";
		}
		return null;
	}

	public static String getLink(String sexo, String corPessoa, String marca) {
		if (sexo.equals("masculino") && corPessoa.equals("branco") && marca.equals("nike")) {
			return "https://i.imgur.com/Qnbunam.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro") && marca.equals("nike")) {
			return "https://i.imgur.com/Aar8I55.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("branco") && marca.equals("adidas")) {
			return "https://i.imgur.com/123hiEI.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro") && marca.equals("adidas")) {
			return "https://i.imgur.com/kHU3o7g.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("branco") && marca.equals("lacoste")) {
			return "https://i.imgur.com/PNhkvHS.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro") && marca.equals("lacoste")) {
			return "https://i.imgur.com/NWWWe02.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("branco") && marca.equals("gucci")) {
			return "https://i.imgur.com/bvjsGJh.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro") && marca.equals("gucci")) {
			return "https://i.imgur.com/OWtbyvH.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco") && marca.equals("versace")) {
			return "https://i.imgur.com/0oIlwwc.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro") && marca.equals("versace")) {
			return "https://i.imgur.com/CpggnhS.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco") && marca.equals("prada")) {
			return "https://i.imgur.com/y6nMUXc.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro") && marca.equals("prada")) {
			return "https://i.imgur.com/Qp7NNJd.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco") && marca.equals("louisvuitton")) {
			return "https://i.imgur.com/fIrXu7s.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro") && marca.equals("louisvuitton")) {
			return "https://i.imgur.com/0S72h0K.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco") && marca.equals("gucci")) {
			return "https://i.imgur.com/VpEJq8a.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro") && marca.equals("gucci")) {
			return "https://i.imgur.com/9d7u2kp.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("branco") && marca.equals("boost")) {
			return "https://i.imgur.com/Hx4pWPb.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro") && marca.equals("boost")) {
			return "https://i.imgur.com/ZJkivVK.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco") && marca.equals("boost")) {
			return "https://i.imgur.com/XbK3fuX.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro") && marca.equals("boost")) {
			return "https://i.imgur.com/EqCZGjw.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("branco") && marca.equals("natal2020")) {
			return "https://i.imgur.com/oGW1ktH.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro") && marca.equals("natal2020")) {
			return "https://i.imgur.com/tEjdFtV.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco") && marca.equals("natal2020")) {
			return "https://i.imgur.com/W6rjweE.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro") && marca.equals("natal2020")) {
			return "https://i.imgur.com/7AGjFBZ.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("branco") && marca.equals("rena2020")) {
			return "https://i.imgur.com/7Oopx3F.png";
		} else if (sexo.equals("masculino") && corPessoa.equals("negro") && marca.equals("rena2020")) {
			return "https://i.imgur.com/Btro6WW.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("branco") && marca.equals("rena2020")) {
			return "https://i.imgur.com/MaHzdOD.png";
		} else if (sexo.equals("feminino") && corPessoa.equals("negro") && marca.equals("rena2020")) {
			return "https://i.imgur.com/WoAgJL5.png";
		}
		return null;
	}

	public static List<String> getCartas() {
		return Arrays.asList("A de Ouros", "2 de Ouros", "3 de Ouros", "4 de Ouros", "5 de Ouros", "6 de Ouros",
				"7 de Paus", "8 de Paus", "9 de Paus", "J de Paus", "Q de Paus", "K de Paus", "A de Paus", "2 de Paus",
				"3 de Paus", "4 de Paus", "5 de Paus", "6 de Paus", "7 de Paus", "8 de Paus", "9 de Paus", "J de Paus",
				"Q de Paus", "K de Paus", "A de Copas", "2 de Copas", "3 de Copas", "4 de Copas", "5 de Copas",
				"6 de Copas", "7 de Copas", "8 de Copas", "9 de Copas", "J de Copas", "Q de Copas", "K de Copas",
				"A de Espadas", "2 de Espadas", "3 de Espadas", "4 de Espadas", "5 de Espadas", "6 de Espadas",
				"7 de Espadas", "8 de Espadas", "9 de Espadas", "J de Espadas", "Q de Espadas", "K de Espadas");
	}

	public static boolean isEXA(Guild g) {
		return g.getId().equals("869353363609899038");
	}

	public static int getVale(String args) {
		String n = args.split(" ")[0];
		if (isInteger(n)) {
			return Integer.parseInt(n);
		}
		if (n.equals("A")) {
			return 1;
		} else if (n.equals("J") || n.equals("Q") || n.equals("K")) {
			return 10;
		}
		return 0;
	}

	public static String getArma(String arma) {
		if (arma.equals("glock")) {
			return "Glock";
		} else if (arma.equals("deagle")) {
			return "Desert Eagle";
		} else if (arma.equals("ump45")) {
			return "UMP45";
		} else if (arma.equals("shotgun")) {
			return "Shotgun";
		} else if (arma.equals("ak47")) {
			return "AK-47";
		} else if (arma.equals("faca")) {
			return "Faca";
		}
		return null;
	}

	public static String getBebida(String bebida) {
		if (bebida.equals("cerveja")) {
			return "Cerveja";
		} else if (bebida.equals("vodka")) {
			return "Vodka";
		} else if (bebida.equals("gin")) {
			return "Gin";
		}
		return null;
	}

	public static int getMaxBebidas(String bebida) {
		if (bebida.equals("cerveja")) {
			return 6;
		} else if (bebida.equals("vodka")) {
			return 8;
		} else if (bebida.equals("gin")) {
			return 10;
		}
		return 0;
	}

	public static String getTime(long ms) {
		long segundos = (ms / 1000) % 60;
		long minutos = (ms / 60000) % 60;
		long horas = (ms / 3600000) % 24;
		long dias = ms / 86400000;
		StringBuilder sb = new StringBuilder();
		if (dias > 0) {
			sb.append(dias + "d ");
		}
		if (horas > 0) {
			sb.append(horas + "h ");
		}
		if (minutos > 0) {
			sb.append(minutos + "m ");
		}
		if (segundos > 0) {
			sb.append(segundos + "s");
		}
		return sb.toString();
	}

	public static Emoji getEmote(String emote) {
		if (Main.getJDA() != null && Main.getJDA().getGuildById("717003376037986314") != null && Main.getJDA()
				.getGuildById("717003376037986314").getEmojisByName(emote + "_ruffos", true).get(0) != null) {
			return Main.getJDA().getGuildById("717003376037986314").getEmojisByName(emote + "_ruffos", false).get(0);
		}
		return null;
	}

	public static boolean isTDP(Guild g) {
		return g.getId().equals("828053766955073606");
	}

	public static boolean isFakeWorld(Guild g) {
		return g.getId().equals("770445728530825226");
	}

	public static boolean isLP(Guild g) {
		return g.getId().equals("710604971590156414");
	}

	public static boolean isIgnoredServer(Guild g) {
		return g.getId().equals("717003376037986314") || g.getId().equals("936599806674087976");
	}

	public static boolean isDouble(String args) {
		try {
			Double.parseDouble(args);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public static boolean isLong(String args) {
		try {
			Long.parseLong(args);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public static boolean isInteger(String args) {
		try {
			Integer.parseInt(args);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public static void enviarEmbed(TextChannel tc, User autor, String titulo, String texto, Color cor, String footer,
			List<Field> fields, String img, String thumbnail, int delete, boolean queue) {
		EmbedBuilder eb = new EmbedBuilder();

		if (autor != null) {
			eb.setAuthor(autor.getName(), null, autor.getAvatarUrl());
		}
		if (titulo != null) {
			eb.setTitle(titulo);
		}
		if (texto != null) {
			eb.setDescription(texto);
		}
		if (img != null) {
			eb.setImage(img);
		}
		if (cor != null) {
			eb.setColor(cor);
		}
		if (footer != null) {
			eb.setFooter(footer);
		}
		eb.setTimestamp(Instant.now());
		if (fields != null) {
			for (Field field : fields) {
				eb.addField(field);
			}
		}
		if (thumbnail != null) {
			eb.setThumbnail(thumbnail);
		}
		if (delete == 0) {
			tc.sendMessageEmbeds(eb.build()).queue((s) -> {

			}, (f) -> {
				System.out.println("Erro: " + texto + " -*> " + footer);
			});
		} else {
			tc.sendMessageEmbeds(eb.build()).queue(m -> m.delete().queueAfter(delete, TimeUnit.SECONDS), (f) -> {
				System.out.println("Erro: " + texto + " -*> " + footer);
			});
		}
	}

	public static void enviarEmbed(PrivateChannel pc, User autor, String titulo, String texto, Color cor, String footer,
			List<Field> fields, String img, String thumbnail, int delete) {
		EmbedBuilder eb = new EmbedBuilder();
		if (autor != null) {
			eb.setAuthor(autor.getName(), null, autor.getAvatarUrl());
		}
		if (titulo != null) {
			eb.setTitle(titulo);
		}
		if (texto != null) {
			eb.setDescription(texto);
		}
		if (img != null) {
			eb.setImage(img);
		}
		if (cor != null) {
			eb.setColor(cor);
		}
		if (footer != null) {
			eb.setFooter(footer);
		}
		if (thumbnail != null) {
			eb.setThumbnail(thumbnail);
		}
		eb.setTimestamp(Instant.now());
		if (fields != null) {
			for (Field field : fields) {
				eb.addField(field);
			}
		}
		try {
			if (delete == 0) {
				pc.sendMessageEmbeds(eb.build()).queue();
			} else {
				pc.sendMessageEmbeds(eb.build()).queue(msg -> msg.delete().queueAfter(delete, TimeUnit.SECONDS));
			}
		} catch (ErrorResponseException e) {
			System.out.println(getTime() + ConsoleColors.RED + " Não foi possível enviar uma mensagem no privado de "
					+ ConsoleColors.BRIGHT_WHITE + pc.getUser().getName() + ConsoleColors.RED + ".");
		}
	}

	public static boolean isCDM(Guild g) {
		return false;
	}

	public static String getTime() {
		Date d = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		return ConsoleColors.BRIGHT_WHITE + "[" + fmt.format(d.getTime()) + "] " + ConsoleColors.RESET;
	}

	public static boolean chance(double e) {
		double d = Math.random();
		if (d < e / 100.0D) {
			return true;
		}
		return false;
	}

	public static String getDinheiro(double d) {
		return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(d);
	}

	public static boolean isFakeEmpire(Guild g) {
		return g.getId().equals("734918084665802874");
	}

}
