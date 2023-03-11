package ruffos._eventos.porquinho;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import ruffos.ConsoleColors;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;

public class Porquinho {

	public boolean iniciado = false;
	Map<User, String> participantes = new HashMap<>();
	public long messageId = 0;
	public int msgs = 0;

	public void iniciar(Guild g) {
		iniciado = true;
		System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Iniciando PORQUINHO em: "
				+ ConsoleColors.BRIGHT_CYAN + g.getName() + ConsoleColors.RESET);
		Random r = new Random();
		int valor = r.nextInt(6000);
		while (valor < 2000) {
			valor = r.nextInt(6000);
		}
		final int v = valor;
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("🐷 Porquinho da Sorte!");
		eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
		eb.setDescription(
				"Clique nos porquinhos para participar do sorteio!\nOs **7** primeiros que reagirem (o primeiro de cada) serão sorteados.");
		eb.addField("Premiação do Sorteio:", "**" + Utils.getDinheiro(v) + "**", false);
		eb.setImage("https://i.imgur.com/5BPegG4.gif");
		eb.setFooter(g.getName());
		eb.setTimestamp(Instant.now());
		try {
			if (ConfigManager.temCfg(g, "canal")
					&& g.getTextChannelById(ConfigManager.getConfig(g, "canal").split(",")[0]) != null) {
				g.getTextChannelById(ConfigManager.getConfig(g, "canal").split(",")[0]).sendMessageEmbeds(eb.build())
						.queue((message) -> {
							messageId = message.getIdLong();
							message.addReaction(Utils.getEmote("porcolouco")).queue();
							message.addReaction(Utils.getEmote("porcotarado")).queue();
							message.addReaction(Utils.getEmote("porcosafado")).queue();
							message.addReaction(Utils.getEmote("porcoinstagrammer")).queue();
							message.addReaction(Utils.getEmote("porcofofo")).queue();
							message.addReaction(Utils.getEmote("porcofaminto")).queue();
							message.addReaction(Utils.getEmote("porcodancarino")).queue();
							message.delete().queueAfter(20, TimeUnit.SECONDS);
						});
			}
		} catch (InsufficientPermissionException e) {
			System.out.println(
					Utils.getTime() + "Erro ao enviar o porquinho. - Mensagem: " + e.getMessage() + " - Localizado: "
							+ e.getLocalizedMessage() + " - Causa: " + e.getCause() + " - Servidor: " + g.getName());
		}
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				if (participantes.isEmpty()) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("🐷 Porquinho da Sorte!");
					eb.setColor(
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
					eb.setDescription("O sorteio foi cancelado devido a falta de participantes.");
					eb.setImage("https://i.imgur.com/5BPegG4.gif");
					eb.setFooter(g.getName());
					eb.setTimestamp(Instant.now());
					g.getTextChannelById(ConfigManager.getConfig(g, "canal").split(",")[0]).sendMessageEmbeds(eb.build())
							.complete();
					iniciado = false;
					return;
				}

				EmbedBuilder eb = new EmbedBuilder();
				StringBuilder sb = new StringBuilder();
				eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
				for (User u : participantes.keySet()) {
					sb.append("\n" + u.getAsMention() + " - **" + getPorcoByName(participantes.get(u)) + "**");
				}
				eb.setTitle("🐷 Porquinho da Sorte!");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setDescription("Realizando sorteio em **5s**...");
				eb.addField("Participantes", sb.toString(), false);
				eb.addField("Premiação do Sorteio:", "**" + Utils.getDinheiro(v) + "**", false);
				eb.setImage("https://i.imgur.com/5BPegG4.gif");
				g.getTextChannelById(ConfigManager.getConfig(g, "canal").split(",")[0]).sendMessageEmbeds(eb.build())
						.complete();
				vencedor(v, g);
			}
		}, 20000);
	}

	void vencedor(int v, Guild g) {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				Random r = new Random();
				List<User> part = new ArrayList<>();
				for (User u : participantes.keySet()) {
					part.add(u);
				}
				User vencedor = part.get(r.nextInt(part.size()));
				EconomiaManager.addDinheiroMaos(g, vencedor, v);
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("🐷 Porquinho da Sorte!");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
				eb.addField("Vencedor:", vencedor.getAsMention(), false);
				eb.addField("Premiação do Sorteio:", "**" + Utils.getDinheiro(v) + "**", false);
				eb.setImage("https://i.imgur.com/5BPegG4.gif");
				if (ClansManager.hasClan(vencedor)
						&& g.getId().equals(ClansManager.get(ClansManager.getClan(vencedor), "guild"))) {
					ClansManager.addFarm(vencedor, v);
				}
				g.getTextChannelById(ConfigManager.getConfig(g, "canal").split(",")[0]).sendMessageEmbeds(eb.build())
						.complete();
				iniciado = false;
				participantes.clear();
				messageId = 0;
			}
		}, 5000);
	}

	public void addParticipante(User u, String porco) {
		if (!participantes.containsKey(u) && !participantes.containsValue(porco)) {
			participantes.put(u, porco);
		}
	}

	public String getPorcoByName(String porco) {
		if (porco.equals("porco1")) {
			return "Porco Louco";
		} else if (porco.equals("porco2")) {
			return "Porco Tarado";
		} else if (porco.equals("porco3")) {
			return "Porco Safado";
		} else if (porco.equals("porco4")) {
			return "Porco Instagrammer";
		} else if (porco.equals("porco5")) {
			return "Porco Fofo";
		} else if (porco.equals("porco6")) {
			return "Porco Faminto";
		} else if (porco.equals("porco7")) {
			return "Porco Dançarino";
		}
		return null;
	}

}
