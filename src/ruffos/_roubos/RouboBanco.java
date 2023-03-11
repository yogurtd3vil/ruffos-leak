package ruffos._roubos;

import java.awt.Color;
import java.time.Instant;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos._prisao.PresosManager;
import ruffos.utils.Utils;

public class RouboBanco {

	private User user;
	private int quantidade;
	private Timer timer;
	private TextChannel tc;
	private boolean pode = false;

	public RouboBanco(TextChannel tc, User user, Guild g) {
		this.user = user;
		this.quantidade = 0;
		this.tc = tc;
		pode = true;
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("🚓 ROUBO A BANCO!");
		eb.setFooter(g.getName());
		eb.setTimestamp(Instant.now());
		eb.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
		eb.setDescription(
				"Você está roubando um banco e deverá floodar a palavra `coletar` o máximo que puder para coletar receber um valor em dinheiro aleatório entre **"
						+ Utils.getDinheiro(100) + "** e **" + Utils.getDinheiro(300)
						+ "** durante **30s**. Antes do tempo de roubo acabar, você deverá digitar `fugir` ou será preso e perderá tudo.");
		eb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
		tc.sendMessageEmbeds(eb.build()).queue();
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (RoubarBancoCommand.rouboBancos.containsKey(user.getId())) {
					eb.setTitle("🚓 ROUBO A BANCO!");
					eb.setFooter(g.getName());
					eb.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
					eb.setTimestamp(Instant.now());
					eb.setDescription(
							"**A SUA CASA CAIU!** A polícia está próxima a você! Você acumulou valor total de **"
									+ Utils.getDinheiro(quantidade)
									+ "**. Tente escapar antes de ser preso digitando `escapar` para iniciar uma troca de tiros com a polícia. Você tem **5s** e gastará **5 munições**.");
					eb.setColor(
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
					tc.sendMessageEmbeds(eb.build()).queue();
					pode = false;
					RouboListener.fugir.put(g.getMember(user), RoubarBancoCommand.rouboBancos.get(user.getId()));
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							if (RouboListener.fugir.containsKey(g.getMember(user))) {
								Random r = new Random();
								int randomPrisao = r.nextInt(30);
								while (randomPrisao < 15) {
									randomPrisao = r.nextInt(30);
								}
								eb.setTitle("🚓 ROUBO A BANCO!");
								eb.setFooter(g.getName());
								eb.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
								eb.setTimestamp(Instant.now());
								eb.setDescription(
										"**A SUA CASA CAIU!** Você demorou muito para fugir e a polícia lhe prendeu! Você tinha acumulado o valor de **"
												+ Utils.getDinheiro(quantidade) + "**. Você foi preso por **"
												+ randomPrisao
												+ "** minuto(s)\n\nVocê poderá digitar apenas um comando preso.\n`c!fianca` » Para pagar sua fiança de **"
												+ Utils.getDinheiro(10000) + "**.");
								EconomiaManager.setUsos(g, user, 0);
								eb.setColor((ConfigManager.temCfg(g, "cor")
										? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null));
								PresosManager.prender(g, user, randomPrisao, "Tentativa de roubo à banco.");
								tc.sendMessageEmbeds(eb.build()).queue();
								RouboListener.fugir.remove(g.getMember(user));
								RoubarBancoCommand.rouboBancos.remove(user.getId());
							}
						}
					}, 5000);
				}
			}
		}, 32000);
	}

	public boolean pode() {
		return pode;
	}

	public User getUser() {
		return user;
	}

	public TextChannel getTc() {
		return tc;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

}
