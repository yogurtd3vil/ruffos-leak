package ruffos._blackjack;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import ruffos._clans.ClansManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;

public class Blackjack {

	private List<String> cartas = new ArrayList<>();
	private List<String> cartasBOT = new ArrayList<>();
	private int valor = 0;
	private User u;
	private int botPontos = 0;
	private int seusPontos = 0;
	private TextChannel tc = null;

	public Blackjack(User u, int valor, Guild g, TextChannel tc) {
		this.tc = tc;
		this.u = u;
		this.valor = valor;
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
		eb.setTitle("🃏 BLACKJACK: " + Utils.getDinheiro(valor) + "!");
		eb.setFooter(g.getName());
		eb.setTimestamp(Instant.now());
		eb.setDescription(
				"Você deverá chegar o mais próximo de 21 pontos possível.\nSe você passar disso ou o Ruffos chegar primeiro, você perderá.\nDigite `comprar` para comprar cartas ou `parar` para parar o jogo.");
		eb.setImage("https://i.imgur.com/950t3YN.png");
		Random r = new Random();
		cartas.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
		cartas.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
		cartasBOT.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
		cartasBOT.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
		String suasCartas = "";
		seusPontos = 0;
		botPontos = 0;
		for (String carta : cartas) {
			suasCartas = suasCartas + carta + "\n";
			seusPontos += Utils.getVale(carta);
		}
		String botCartas = "";
		for (String carta : cartasBOT) {
			botCartas = botCartas + carta + "\n";
			botPontos += Utils.getVale(carta);
		}
		eb.addField("✋ Cartas em sua mão:", suasCartas, true);
		eb.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
		eb.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);
		eb.setTimestamp(Instant.now());
		tc.sendMessageEmbeds(eb.build()).queue();

	}

	public boolean jogar(Guild g) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
		eb.setTitle("🃏 BLACKJACK: " + Utils.getDinheiro(valor) + "!");
		eb.setFooter(g.getName());
		eb.setTimestamp(Instant.now());
		eb.setDescription(
				"Você deverá chegar o mais próximo de 21 pontos possível.\nSe você passar disso ou o Ruffos chegar primeiro, você perderá.\nDigite `comprar` para comprar cartas ou `parar` para parar o jogo.");
		eb.setImage("https://i.imgur.com/950t3YN.png");
		Random r = new Random();
		cartas.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
		String suasCartas = "";
		seusPontos = 0;
		botPontos = 0;
		for (String carta : cartas) {
			suasCartas = suasCartas + carta + "\n";
			seusPontos += Utils.getVale(carta);
		}
		String botCartas = "";
		for (String carta : cartasBOT) {
			botCartas = carta + "\n";
			botPontos += Utils.getVale(carta);
		}
		if (seusPontos > 21) {
			EmbedBuilder perdeu = new EmbedBuilder();
			perdeu.setTitle("🃏 BLACKJACK: PERDEU!");
			perdeu.setFooter(g.getName());
			perdeu.setTimestamp(Instant.now());
			perdeu.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			perdeu.setDescription("Você perdeu o valor de **" + Utils.getDinheiro(valor) + "**!");
			perdeu.addField("✋ Cartas em sua mão:", suasCartas, true);
			perdeu.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
			perdeu.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

			perdeu.setColor(Color.RED);
			perdeu.setImage("https://i.imgur.com/950t3YN.png");
			tc.sendMessageEmbeds(perdeu.build()).queue();
			return true;
		}
		cartasBOT.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
		int i = Utils.getVale(cartasBOT.get(cartasBOT.size() - 1));
		String s = cartasBOT.get(cartasBOT.size() - 1);
		while (i >= 9) {
			cartasBOT.remove(s);
			cartasBOT.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size() - 1)));
			s = cartasBOT.get(cartasBOT.size() - 1);
			i = Utils.getVale(cartasBOT.get(cartasBOT.size() - 1));
		}

		for (String carta : cartasBOT) {
			botCartas = botCartas + carta + "\n";
			botPontos += Utils.getVale(carta);
		}
		if (botPontos > 21) {
			EmbedBuilder ganhou = new EmbedBuilder();
			ganhou.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			ganhou.setTitle("🃏 BLACKJACK: VITÓRIA!");
			ganhou.setFooter(g.getName());
			ganhou.setTimestamp(Instant.now());
			ganhou.setDescription("Você ganhou o valor de **" + Utils.getDinheiro(valor * 2) + "**!");
			ganhou.addField("✋ Cartas em sua mão:", suasCartas, true);
			ganhou.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
			ganhou.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

			ganhou.setColor(Color.GREEN);
			ganhou.setImage("https://i.imgur.com/950t3YN.png");
			EconomiaManager.addDinheiroMaos(g, u, (valor * 2));
			if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
				ClansManager.addFarm(u, (valor));
			}
			tc.sendMessageEmbeds(ganhou.build()).queue();
			return true;
		} else if (botPontos == 21) {
			EmbedBuilder perdeu = new EmbedBuilder();
			perdeu.setTitle("🃏 BLACKJACK: PERDEU!");
			perdeu.setFooter(g.getName());
			perdeu.setTimestamp(Instant.now());
			perdeu.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			perdeu.setDescription("Você perdeu o valor de **" + Utils.getDinheiro(valor) + "**!");
			perdeu.addField("✋ Cartas em sua mão:", suasCartas, true);
			perdeu.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
			perdeu.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

			perdeu.setColor(Color.RED);
			perdeu.setImage("https://i.imgur.com/950t3YN.png");
			tc.sendMessageEmbeds(perdeu.build()).queue();
			return true;
		} else if (botPontos > seusPontos) {
			cartas.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
			seusPontos = 0;
			botPontos = 0;
			suasCartas = "";
			botCartas = "";
			for (String carta : cartas) {
				suasCartas = suasCartas + carta + "\n";
				seusPontos += Utils.getVale(carta);
			}
			for (String carta : cartasBOT) {
				botCartas = carta + "\n";
				botPontos += Utils.getVale(carta);
			}
			if (seusPontos > 21) {
				EmbedBuilder perdeu = new EmbedBuilder();
				perdeu.setTitle("🃏 BLACKJACK: PERDEU!");
				perdeu.setFooter(g.getName());
				perdeu.setTimestamp(Instant.now());
				perdeu.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				perdeu.setDescription("Você perdeu o valor de **" + Utils.getDinheiro(valor) + "**!");
				perdeu.addField("✋ Cartas em sua mão:", suasCartas, true);
				perdeu.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
				perdeu.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

				perdeu.setColor(Color.RED);
				perdeu.setImage("https://i.imgur.com/950t3YN.png");
				tc.sendMessageEmbeds(perdeu.build()).queue();
				return true;
			} else if (seusPontos == 21) {
				EmbedBuilder ganhou = new EmbedBuilder();
				ganhou.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				ganhou.setTitle("🃏 BLACKJACK: VITÓRIA!");
				ganhou.setFooter(g.getName());
				ganhou.setTimestamp(Instant.now());
				ganhou.setDescription("Você ganhou o valor de **" + Utils.getDinheiro(valor * 2) + "**!");
				ganhou.addField("✋ Cartas em sua mão:", suasCartas, true);
				ganhou.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
				ganhou.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

				ganhou.setColor(Color.GREEN);
				ganhou.setImage("https://i.imgur.com/950t3YN.png");
				EconomiaManager.addDinheiroMaos(g, u, (valor * 2));
				if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
					ClansManager.addFarm(u, (valor));
				}
				tc.sendMessageEmbeds(ganhou.build()).queue();
				return true;
			} else if (seusPontos > botPontos) {
				EmbedBuilder ganhou = new EmbedBuilder();
				ganhou.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				ganhou.setTitle("🃏 BLACKJACK: VITÓRIA!");
				ganhou.setFooter(g.getName());
				ganhou.setTimestamp(Instant.now());
				ganhou.setDescription("Você ganhou o valor de **" + Utils.getDinheiro(valor * 2) + "**!");
				ganhou.addField("✋ Cartas em sua mão:", suasCartas, true);
				ganhou.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
				ganhou.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

				ganhou.setColor(Color.GREEN);
				ganhou.setImage("https://i.imgur.com/950t3YN.png");
				EconomiaManager.addDinheiroMaos(g, u, (valor * 2));
				if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
					ClansManager.addFarm(u, (valor));
				}
				tc.sendMessageEmbeds(ganhou.build()).queue();
				return true;
			}
			EmbedBuilder perdeu = new EmbedBuilder();
			perdeu.setTitle("🃏 BLACKJACK: PERDEU!");
			perdeu.setFooter(g.getName());
			perdeu.setTimestamp(Instant.now());
			perdeu.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			perdeu.setDescription("Você perdeu o valor de **" + Utils.getDinheiro(valor) + "**!");
			perdeu.addField("✋ Cartas em sua mão:", suasCartas, true);
			perdeu.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
			perdeu.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

			perdeu.setColor(Color.RED);
			perdeu.setImage("https://i.imgur.com/950t3YN.png");
			tc.sendMessageEmbeds(perdeu.build()).queue();
			return true;
		}
		eb.addField("✋ Cartas em sua mão:", suasCartas, true);
		eb.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
		eb.addField("Seus pontos:", "**" + seusPontos + "**", true);
		eb.addField("Pontos do ruffos:", "**" + botPontos + "**", true);
		eb.setTimestamp(Instant.now());
		tc.sendMessageEmbeds(eb.build()).queue();
		return false;
	}

	public void parar(Guild g) {
		Random r = new Random();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
		eb.setTitle("🃏 BLACKJACK: " + Utils.getDinheiro(valor) + "!");
		eb.setFooter(g.getName());
		eb.setTimestamp(Instant.now());
		eb.setDescription(
				"Você deverá chegar o mais próximo de 21 pontos possível.\nSe você passar disso ou o Ruffos chegar primeiro, você perderá.\nDigite `comprar` para comprar cartas ou `parar` para parar o jogo.");
		eb.setImage("https://i.imgur.com/950t3YN.png");
		String suasCartas = "";
		seusPontos = 0;
		botPontos = 0;
		for (String carta : cartas) {
			suasCartas = suasCartas + carta + "\n";
			seusPontos += Utils.getVale(carta);
		}
		String botCartas = "";
		for (String carta : cartasBOT) {
			botCartas = carta + "\n";
			botPontos += Utils.getVale(carta);
		}
		for (String carta : cartasBOT) {
			botCartas = botCartas + carta + "\n";
			botPontos += Utils.getVale(carta);
		}
		if (botPontos > 21) {
			EmbedBuilder ganhou = new EmbedBuilder();
			ganhou.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			ganhou.setTitle("🃏 BLACKJACK: VITÓRIA!");
			ganhou.setFooter(g.getName());
			ganhou.setTimestamp(Instant.now());
			ganhou.setDescription("Você ganhou o valor de **" + Utils.getDinheiro(valor * 2) + "**!");
			ganhou.addField("✋ Cartas em sua mão:", suasCartas, true);
			ganhou.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
			ganhou.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

			ganhou.setColor(Color.GREEN);
			ganhou.setImage("https://i.imgur.com/950t3YN.png");
			EconomiaManager.addDinheiroMaos(g, u, (valor * 2));
			if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
				ClansManager.addFarm(u, (valor));
			}
			tc.sendMessageEmbeds(ganhou.build()).queue();
			return;
		} else if (seusPontos > botPontos) {
			suasCartas = "";
			seusPontos = 0;
			botPontos = 0;
			for (String carta : cartas) {
				suasCartas = suasCartas + carta + "\n";
				seusPontos += Utils.getVale(carta);
			}
			botCartas = "";
			for (String carta : cartasBOT) {
				botCartas = carta + "\n";
				botPontos += Utils.getVale(carta);
			}
			for (String carta : cartasBOT) {
				botCartas = botCartas + carta + "\n";
				botPontos += Utils.getVale(carta);
			}
			cartasBOT.add(Utils.getCartas().get(r.nextInt(Utils.getCartas().size())));
			if (seusPontos > botPontos) {
				EmbedBuilder ganhou = new EmbedBuilder();
				ganhou.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				ganhou.setTitle("🃏 BLACKJACK: VITÓRIA!");
				ganhou.setFooter(g.getName());
				ganhou.setTimestamp(Instant.now());
				ganhou.setDescription("Você ganhou o valor de **" + Utils.getDinheiro(valor * 2) + "**!");
				ganhou.addField("✋ Cartas em sua mão:", suasCartas, true);
				ganhou.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
				ganhou.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

				ganhou.setColor(Color.GREEN);
				ganhou.setImage("https://i.imgur.com/950t3YN.png");
				EconomiaManager.addDinheiroMaos(g, u, (valor * 2));
				if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
					ClansManager.addFarm(u, (valor));
				}
				tc.sendMessageEmbeds(ganhou.build()).queue();
			} else {
				EmbedBuilder perdeu = new EmbedBuilder();
				perdeu.setTitle("🃏 BLACKJACK: PERDEU!");
				perdeu.setFooter(g.getName());
				perdeu.setTimestamp(Instant.now());
				perdeu.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				perdeu.setDescription("Você perdeu o valor de **" + Utils.getDinheiro(valor) + "**!");
				perdeu.addField("✋ Cartas em sua mão:", suasCartas, true);
				perdeu.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
				perdeu.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

				perdeu.setColor(Color.RED);
				eb.setImage("https://i.imgur.com/950t3YN.png");
				tc.sendMessageEmbeds(perdeu.build()).queue();
			}
			return;
		} else if (botPontos > seusPontos) {
			EmbedBuilder perdeu = new EmbedBuilder();
			perdeu.setTitle("🃏 BLACKJACK: PERDEU!");
			perdeu.setFooter(g.getName());
			perdeu.setTimestamp(Instant.now());
			perdeu.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			perdeu.setDescription("Você perdeu o valor de **" + Utils.getDinheiro(valor) + "**!");
			perdeu.addField("✋ Cartas em sua mão:", suasCartas, true);
			perdeu.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
			perdeu.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

			perdeu.setColor(Color.RED);
			perdeu.setImage("https://i.imgur.com/950t3YN.png");
			tc.sendMessageEmbeds(perdeu.build()).queue();
			return;
		} else {
			EmbedBuilder perdeu = new EmbedBuilder();
			perdeu.setTitle("🃏 BLACKJACK: PERDEU!");
			perdeu.setFooter(g.getName());
			perdeu.setTimestamp(Instant.now());
			perdeu.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
			perdeu.setDescription("Você perdeu o valor de **" + Utils.getDinheiro(valor) + "**!");
			perdeu.addField("✋ Cartas em sua mão:", suasCartas, true);
			perdeu.addField("🐶 Cartas na pata do Ruffos:", botCartas, true);
			perdeu.addField("Pontos:", "**Você:** " + seusPontos + " | **Ruffos:** " + botPontos, false);

			perdeu.setColor(Color.RED);
			perdeu.setImage("https://i.imgur.com/950t3YN.png");
			tc.sendMessageEmbeds(perdeu.build()).queue();
			return;
		}
	}

}
