package ruffos._roubos;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos._clans.ClansManager;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos._prisao.PresosManager;
import ruffos._rankings.TopRoubosCommand;
import ruffos.utils.Utils;

public class RouboListener extends ListenerAdapter {

	public static Map<Member, RouboBanco> fugir = new HashMap<>();

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		User u = event.getAuthor();
		Guild g = event.getGuild();
		TextChannel tc = event.getGuild().getTextChannelById(event.getChannel().getId());
		if (event.isFromType(ChannelType.TEXT)) {
			if (RoubarCofreCommand.roubosCofre.containsKey(event.getMember())) {
				RouboCofre rc = RoubarCofreCommand.roubosCofre.get(event.getMember());
				String valor = event.getMessage().getContentRaw();
				if (valor.length() != 4) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setDescription(
							"Você errou a senha do cofre e perdeu **R$ 5.000,00**!\n\nA senha digitada contém mais ou menos que **4** caracteres.\nSenha correta: **"
									+ rc.getSenha() + "**.");
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setColor(((ConfigManager.temCfg(event.getGuild(), "cor")
							? Color.decode(ConfigManager.getConfig(event.getGuild(), "cor"))
							: null)));
					eb.setFooter(event.getGuild().getName());
					eb.setTimestamp(Instant.now());
					eb.setImage("https://i.imgur.com/mrnNXjg.png");
					tc.retrieveMessageById(rc.getMensagemId()).complete().editMessageEmbeds(eb.build()).queue(m -> {
						m.clearReactions().queue();
					});
					RoubarCofreCommand.roubosCofre.remove(event.getMember());
					return;
				}
				for (char s : valor.toCharArray()) {
					if (rc.getSenha().indexOf(String.valueOf(s)) > -1) {
						rc.addAcerto();
					}
				}
				if (rc.getAcertos() == 4) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setDescription("Você acertou os **4** números que formam a senha do cofre e levou **"
							+ Utils.getDinheiro(rc.getContem()) + "**!");
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setColor(((ConfigManager.temCfg(event.getGuild(), "cor")
							? Color.decode(ConfigManager.getConfig(event.getGuild(), "cor"))
							: null)));
					eb.setFooter(event.getGuild().getName());
					eb.setTimestamp(Instant.now());
					eb.setImage("https://i.imgur.com/ODX5bk9.png");
					tc.retrieveMessageById(rc.getMensagemId()).complete().editMessageEmbeds(eb.build()).queue(m -> {
						m.clearReactions().queue();
					});
					if (ClansManager.hasClan(u)
							&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
						ClansManager.addFarm(u, rc.getContem());
					}
					TopRoubosCommand.addRoubo(event.getGuild(), u, rc.getContem());
					EconomiaManager.addDinheiroMaos(event.getGuild(), u, rc.getContem());
					RoubarCofreCommand.roubosCofre.remove(event.getMember());
				} else {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setDescription("Você errou a senha do cofre e perdeu **R$ 5.000,00**!\n\nSenha digitada **"
							+ valor + "**.\nSenha correta: **" + rc.getSenha() + "**.");
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setColor(((ConfigManager.temCfg(event.getGuild(), "cor")
							? Color.decode(ConfigManager.getConfig(event.getGuild(), "cor"))
							: null)));
					eb.setFooter(event.getGuild().getName());
					eb.setTimestamp(Instant.now());
					eb.setImage("https://i.imgur.com/mrnNXjg.png");
					tc.retrieveMessageById(rc.getMensagemId()).complete().editMessageEmbeds(eb.build()).queue(m -> {
						m.clearReactions().queue();
					});
					RoubarCofreCommand.roubosCofre.remove(event.getMember());
				}
			}
			if (RoubarBancoCommand.rouboBancos.containsKey(u.getId())
					&& event.getMessage().getContentRaw().equalsIgnoreCase("coletar") && (ConfigManager.hasChat(g, tc))
					&& RoubarBancoCommand.rouboBancos.get(u.getId()).pode()) {
				RouboBanco rouboBanco = RoubarBancoCommand.rouboBancos.get(u.getId());
				Random r = new Random();
				int random = r.nextInt(300);
				while (random < 100) {
					random = r.nextInt(300);
				}
				rouboBanco.setQuantidade(rouboBanco.getQuantidade() + random);
				RoubarBancoCommand.rouboBancos.put(u.getId(), rouboBanco);
			} else if (RoubarBancoCommand.rouboBancos.containsKey(u.getId())
					&& event.getMessage().getContentRaw().equalsIgnoreCase("fugir") && (ConfigManager.hasChat(g, tc))
					&& RoubarBancoCommand.rouboBancos.get(u.getId()).pode()) {
				RouboBanco rouboBanco = RoubarBancoCommand.rouboBancos.get(u.getId());
				RoubarBancoCommand.rouboBancos.remove(u.getId());
				Utils.enviarEmbed(rouboBanco.getTc(), u, "🚓 ROUBO A BANCO!",
						"Você conseguiu fugir e acumulou o total de **" + Utils.getDinheiro(rouboBanco.getQuantidade())
								+ "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, false);
				EconomiaManager.addDinheiroMaos(g, u, rouboBanco.getQuantidade());
				if (ClansManager.hasClan(u) && g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
					ClansManager.addFarm(u, rouboBanco.getQuantidade());
				}
				TopRoubosCommand.addRoubo(g, u, rouboBanco.getQuantidade());
				rouboBanco.getTimer().cancel();
			}
			if (RoubarCommand.getRouboDe(u) != null && event.getMessage().getContentRaw().equalsIgnoreCase("reagir")) {
				ruffos._roubos.Roubo roubo = RoubarCommand.getRouboDe(u);
				if (!EconomiaManager.hasArma(g, u)) {
					Utils.enviarEmbed(roubo.getTextChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você precisa de uma arma para reagir à um roubo! Use: `c!loja`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, false);
					return;
				}
				if (EconomiaManager.getUsos(g, u) < 1) {
					Utils.enviarEmbed(roubo.getTextChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro: **Você está sem munições! Use: `c!loja`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, false);
					return;
				}
				String arma = EconomiaManager.getArma(g, u);
				int porcentagem = 10;
				if (arma.equals("glock")) {
					porcentagem = 20;
				} else if (arma.equals("deagle")) {
					porcentagem = 25;
				} else if (arma.equals("ump45")) {
					porcentagem = 30;
				} else if (arma.equals("shotgun")) {
					porcentagem = 35;
				} else if (arma.equals("ak47")) {
					porcentagem = 40;
				}
				double remover = porcentagem(roubo.getValorRoubado(), porcentagem);
				Utils.enviarEmbed(roubo.getTextChannel(), roubo.getRoubou(), null,
						"O roubo que você efetuou em " + roubo.getRoubado().getAsMention()
								+ " falhou pois o mesmo reagiu com uma **" + Utils.getArma(arma)
								+ " (-1 munição)** e recuperou **" + Utils.getDinheiro(remover) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName() + " » Para ver quem mais roubou nas últimas horas ou dias: c!roubos.", null, null,
						roubo.getRoubado().getAvatarUrl(), 0, true);
				EconomiaManager.setUsos(g, u, (EconomiaManager.getUsos(g, u) - 1));
				EconomiaManager.removeDinheiroMaos(g, roubo.getRoubou(), remover);
				EconomiaManager.addDinheiroMaos(g, roubo.getRoubado(), remover);
				RoubarCommand.reagir.remove(roubo.getRoubou());
			}
			if (RoubarBancoCommand.rouboBancos.containsKey(u.getId())
					&& event.getMessage().getContentRaw().equalsIgnoreCase("escapar") && (ConfigManager.hasChat(g, tc))
					&& !RoubarBancoCommand.rouboBancos.get(u.getId()).pode() && fugir.containsKey(event.getMember())) {
				User user = event.getAuthor();
				int quantidade = fugir.get(event.getMember()).getQuantidade();
				String arma = EconomiaManager.getArma(g, user);
				if (arma == null) {
					EmbedBuilder ebb = new EmbedBuilder();
					Random r = new Random();
					int randomPrisao = r.nextInt(30);
					while (randomPrisao < 15) {
						randomPrisao = r.nextInt(30);
					}
					ebb.setTitle("🚓 ROUBO A BANCO!");
					ebb.setFooter(g.getName());
					ebb.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
					ebb.setTimestamp(Instant.now());
					ebb.setDescription(
							"**A SUA CASA CAIU!** Você não tem uma arma! A polícia lhe prendeu! Você tinha acumulado o valor de **"
									+ Utils.getDinheiro(quantidade) + "**. Você foi preso por **" + randomPrisao
									+ "** minuto(s)\n\nVocê poderá digitar apenas 2 comandos presos.\n`c!fianca` » Para pagar sua fiança de **"
									+ Utils.getDinheiro(15000) + "**.");
					ebb.setColor(
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
					PresosManager.prender(g, user, randomPrisao,
							"Tentativa de roubo à banco e troca de tiro com policiais.");
					tc.sendMessageEmbeds(ebb.build()).queue();
					RouboListener.fugir.remove(g.retrieveMemberById(user.getId()).complete());
					RoubarBancoCommand.rouboBancos.remove(user.getId());
					return;
				}
				int chance = 0;
				switch (arma) {
				case "ak47":
					chance = 30;
					break;
				case "shotgun":
					chance = 25;
					break;
				case "ump45":
					chance = 20;
					break;
				case "deagle":
					chance = 15;
					break;
				case "glock":
					chance = 10;
					break;
				case "faca":
					chance = 5;
					break;

				default:
					break;
				}
				if (Utils.chance(chance)) {
					if (EconomiaManager.getUsos(g, user) >= 5) {
						EconomiaManager.setUsos(g, user, (EconomiaManager.getUsos(g, user) - 5));
						RouboBanco rouboBanco = RoubarBancoCommand.rouboBancos.get(u.getId());
						Utils.enviarEmbed(rouboBanco.getTc(), u, "🚓 ROUBO A BANCO!",
								"Você conseguiu escapar e acumulou o total de **"
										+ Utils.getDinheiro(rouboBanco.getQuantidade())
										+ "**! Você perdeu **5** munição(ões) na trocação de tiro (**"
										+ EconomiaManager.getUsos(g, user) + "/30**) ",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, false);
						EconomiaManager.addDinheiroMaos(g, u, rouboBanco.getQuantidade());
						if (ClansManager.hasClan(u)
								&& g.getId().equals(ClansManager.get(ClansManager.getClan(u), "guild"))) {
							ClansManager.addFarm(u, rouboBanco.getQuantidade());
						}
						TopRoubosCommand.addRoubo(g, u, rouboBanco.getQuantidade());
						rouboBanco.getTimer().cancel();
						RoubarBancoCommand.rouboBancos.remove(u.getId());
						fugir.remove(event.getMember());
					} else {
						EmbedBuilder ebb = new EmbedBuilder();
						Random r = new Random();
						int randomPrisao = r.nextInt(30);
						while (randomPrisao < 15) {
							randomPrisao = r.nextInt(30);
						}
						ebb.setTitle("🚓 ROUBO A BANCO!");
						ebb.setFooter(g.getName());
						ebb.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
						ebb.setTimestamp(Instant.now());
						ebb.setDescription(
								"**A SUA CASA CAIU!** Você não tinha mais munições e a troca de tiro foi mal sucedida! A polícia lhe prendeu! Você tinha acumulado o valor de **"
										+ Utils.getDinheiro(quantidade) + "**. Você foi preso por **" + randomPrisao
										+ "** minuto(s)\n\nVocê poderá digitar apenas 2 comandos presos.\n`c!fianca` » Para pagar sua fiança de **"
										+ Utils.getDinheiro(15000)
										+ "**.\n`c!fugir` » Para iniciar uma fuga da prisão.");
						EconomiaManager.setUsos(g, user, 0);
						ebb.setColor((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
								: null));
						PresosManager.prender(g, user, randomPrisao,
								"Tentativa de roubo à banco e troca de tiro com policiais.");
						tc.sendMessageEmbeds(ebb.build()).queue();
						RouboListener.fugir.remove(g.retrieveMemberById(user.getId()).complete());
						RoubarBancoCommand.rouboBancos.remove(user.getId());
					}
				} else {
					EmbedBuilder ebb = new EmbedBuilder();
					Random r = new Random();
					int randomPrisao = r.nextInt(30);
					while (randomPrisao < 15) {
						randomPrisao = r.nextInt(30);
					}
					ebb.setTitle("🚓 ROUBO A BANCO!");
					ebb.setFooter(g.getName());
					ebb.setAuthor(user.getName(), user.getAvatarUrl(), user.getAvatarUrl());
					ebb.setTimestamp(Instant.now());
					ebb.setDescription(
							"**A SUA CASA CAIU!** Troca de tiro mal sucedida! A polícia lhe prendeu! Você tinha acumulado o valor de **"
									+ Utils.getDinheiro(quantidade) + "**. Você foi preso por **" + randomPrisao
									+ "** minuto(s)\n\nVocê poderá digitar apenas 2 comandos presos.\n`c!fianca` » Para pagar sua fiança de **"
									+ Utils.getDinheiro(15000) + "**.\n`c!fugir` » Para iniciar uma fuga da prisão.");
					ebb.setColor(
							(ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null));
					PresosManager.prender(g, user, randomPrisao,
							"Tentativa de roubo à banco e troca de tiro com policiais.");
					EconomiaManager.setUsos(g, user, 0);
					tc.sendMessageEmbeds(ebb.build()).queue();
					RouboListener.fugir.remove(g.retrieveMemberById(user.getId()).complete());
					RoubarBancoCommand.rouboBancos.remove(user.getId());
				}
			}
		}
	}

	double porcentagem(double valor, int porcentagem) {
		return (valor * porcentagem) / 100.0;
	}

}
