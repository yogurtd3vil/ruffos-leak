package ruffos._filhos;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import ruffos._casamento.CasamentoManager;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos._leveis.LeveisManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class FilhoCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
						+ " **Comando incompleto:**\n\nPara ver as informações do filho, use `c!filho info [id]`.\nPara fazer um filho, use `c!filho fazer [masculino/feminino] [você é o pai? 's' ou 'n'] [nome]`.\nPara listar todos os seus filhos, use `c!filho listar`.\nPara alimentar seu filho, use `c!filho alimentar [id]`.\nPara dar água ao seu filho, use `c!filho daragua [id]`.\nPara brincar com seu filho, use `c!filho brincar [id]`.\nPara curar seu filho, use `c!filho curar [id]`.\nPara definir o avatar do seu filho, use `c!filho avatar [id] [link]`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getArgs().get(0).equalsIgnoreCase("fazer")) {
				if (FilhosManager.getFilhos(g, u).size() >= 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Você não pode possuir mais que **2** filhos. Caso você tenha trocado de casal e agora quer outro filho ou filha, contate <@478868013219577876>!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (!CasamentoManager.casado(g, u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Você deve estar casado(a) para ter um filho.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ctx.getArgs().size() < 3) {
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Comando incompleto:** Use c!filho fazer [masculino/feminino] [você é o pai? (responda com `s` ou `n`)] [nome].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String sexo = ctx.getArgs().get(1).toLowerCase();
				if (!sexo.equals("masculino") && !sexo.equals("feminino")) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Os únicos sexos disponíveis são **masculino** ou **feminino**.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				boolean pai = ctx.getArgs().get(2).toLowerCase().equals("s") ? true : false;
				String nome = "";
				for (int i = 3; i != ctx.getArgs().size(); i++) {
					nome = nome + ctx.getArgs().get(i) + " ";
				}
				nome = StringUtils.chop(nome);
				if (nome.length() > 33) {
					int apagar = nome.length() - 33;
					Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** O nome não pode conter mais de **33** caractéres. Você deverá apagar: **"
							+ apagar + "** caractéres.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (nome.equals(" ") || nome.equals("")) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Defina um nome para seu(ua) filho(a).",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				int l = FilhosManager.criarFilho(g, nome, sexo,
						(pai ? u.getId() : CasamentoManager.getParceiro(g, u.getId())),
						(pai ? CasamentoManager.getParceiro(g, u.getId()) : u.getId()));
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("🎠 VOCÊ FEZ " + (sexo.toUpperCase().equals("MASCULINO") ? "UM FILHO!" : "UMA FILHA!"));
				eb.setColor(
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)));
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("Você fez um(a) filho(a) com <@" + CasamentoManager.getParceiro(g, u.getId())
						+ ">!\n\n**__INFORMAÇÕES__**\n\nNome: **" + nome + "**\nSexo: **" + sexo.toUpperCase()
						+ "**\nPai: <@" + (pai ? u.getId() : CasamentoManager.getParceiro(g, u.getId())) + ">\nMãe: <@"
						+ (pai ? CasamentoManager.getParceiro(g, u.getId()) : u.getId())
						+ ">\nID do seu(ua) filho(a): **" + l
						+ "**\n\nConfira informações como esta e outras como a saúde, fome e sede digitando: **c!filho info "
						+ l + "**!");
				eb.setFooter(g.getName());
				eb.setTimestamp(Instant.now());
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						String filhoID = String.valueOf(l);
						FilhosManager.retirar(g, filhoID, "sede");
						String pai = FilhosManager.getInfo(g, filhoID, "pai").toString();
						String mae = FilhosManager.getInfo(g, filhoID, "mae").toString();
						String nome = FilhosManager.getInfo(g, filhoID, "nome").toString();
						int fome = Integer.parseInt(String.valueOf(FilhosManager.getInfo(g, filhoID, "sede")));
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
							FilhosManager.morrer(g, filhoID);
						}
					}
				}, 3600000, 3600000);
				FilhosManager.salvarTimerSede.put(String.valueOf(l), timer);
				Timer timer2 = new Timer();
				timer2.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						String filhoID = String.valueOf(l);
						FilhosManager.retirar(g, filhoID, "fome");
						String pai = FilhosManager.getInfo(g, filhoID, "pai").toString();
						String mae = FilhosManager.getInfo(g, filhoID, "mae").toString();
						String nome = FilhosManager.getInfo(g, filhoID, "nome").toString();
						int fome = Integer.parseInt(String.valueOf(FilhosManager.getInfo(g, filhoID, "fome")));
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
									g.getMemberById(mae).getUser().openPrivateChannel().complete()
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
							FilhosManager.morrer(g, filhoID);
						}
					}
				}, 1800000, 1800000);
				FilhosManager.salvarTimerFome.put(String.valueOf(l), timer2);
				Timer timer3 = new Timer();
				timer3.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						String filhoID = String.valueOf(l);
						FilhosManager.retirar(g, filhoID, "felicidade");
						String pai = FilhosManager.getInfo(g, filhoID, "pai").toString();
						String mae = FilhosManager.getInfo(g, filhoID, "mae").toString();
						String nome = FilhosManager.getInfo(g, filhoID, "nome").toString();
						int fome = Integer.parseInt(String.valueOf(FilhosManager.getInfo(g, filhoID, "felicidade")));
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
							FilhosManager.morrer(g, filhoID);
						}
					}
				}, 2100000, 2100000);
				FilhosManager.salvarTimerFelicidade.put(String.valueOf(l), timer3);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("listar")) {
				if (ctx.getArgs().size() == 0) {
					List<String> filhos = FilhosManager.getFilhos(g, u);
					if (filhos.isEmpty()) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Você não tem filhos.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					String desc = "";
					for (String s : filhos) {
						desc = desc + s + "\n";
					}
					desc = desc + "\nPara ver as informações de um(a) determinado(a) filho(a): **c!filho info [ID]**";
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setTimestamp(Instant.now());
					eb.setFooter(g.getName());
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					eb.addField("Sua lista de filhos e filhas:", desc, true);
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
					return;
				} else if (ctx.getMessage().getMentions().getMembers().size() == 1) {
					u = ctx.getMessage().getMentions().getMembers().get(0).getUser();
					List<String> filhos = FilhosManager.getFilhos(g, u);
					if (filhos.isEmpty()) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Este(a) usuário(a) não tem filhos.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					String desc = "";
					for (String s : filhos) {
						desc = desc + s + "\n";
					}
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(ctx.getAuthor().getName(), ctx.getAuthor().getAvatarUrl(),
							ctx.getAuthor().getAvatarUrl());
					eb.setTimestamp(Instant.now());
					eb.setFooter(g.getName());
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					eb.addField("Lista de filhos e filhas de " + u.getName() + ":", desc, true);
					return;
				} else {
					List<String> filhos = FilhosManager.getFilhos(g, u);
					if (filhos.isEmpty()) {
						Utils.enviarEmbed(ctx.getChannel(), u, null,
								Utils.getEmote("nao").getFormatted() + " **Erro:** Você não tem filhos.",
								((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
										: null)),
								g.getName(), null, null, null, 0, true);
						return;
					}
					String desc = "";
					for (String s : filhos) {
						desc = desc + s + "\n";
					}
					desc = desc + "\nPara ver as informações de um(a) determinado(a) filho(a): **c!filho info [ID]**";
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
					eb.setTimestamp(Instant.now());
					eb.setFooter(g.getName());
					eb.setColor(((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
							: null)));
					eb.addField("Sua lista de filhos e filhas:", desc, true);
					ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
				}
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("alimentar")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Comando incompleto:** Use c!filho alimentar [id].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoID = ctx.getArgs().get(1);
				if (FilhosManager.getInfo(g, filhoID, "nome") == null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** ID inválido.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				} else if (!FilhosManager.getInfo(g, filhoID, "pai").equals(u.getId())
						&& !FilhosManager.getInfo(g, filhoID, "mae").equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Este(a) filho(a) não pertence a você.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoNome = FilhosManager.getInfo(g, filhoID, "nome").toString();
				if (Integer.parseInt(String.valueOf(FilhosManager.getInfo(g, filhoID, "fome"))) >= 100) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** A fome de **" + filhoNome
									+ "** está no máximo.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				Random r = new Random();
				int randomXP = r.nextInt(30);
				while (randomXP < 5) {
					randomXP = r.nextInt(30);
				}
				boolean xp = false;
				if (xp) {
					randomXP = (randomXP * 2);
				}
				if (!EconomiaManager.hasMaos(g, u, 800)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(800) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), randomXP);
				FilhosManager.update(g, filhoID, "fome",
						Integer.parseInt((String.valueOf(FilhosManager.getInfo(g, filhoID, "fome")))) + 5);
				String avatar = FilhosManager.getInfo(g, filhoID, "avatar").toString();
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"**" + filhoNome + "** foi alimentado! Você gastou **" + Utils.getDinheiro(800)
								+ "** e recebeu **" + randomXP + "** de XP!" + ((xp) ? " **(x2)**" : "")
								+ "\n\nFome de **" + filhoNome + "**: **" + FilhosManager.getInfo(g, filhoID, "fome")
								+ "%**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, (avatar.equals("Nenhum") ? null : avatar), null, 0, true);
				EconomiaManager.removeDinheiroMaos(g, u, 800);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("daragua")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!filho daragua [id].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoID = ctx.getArgs().get(1);
				if (FilhosManager.getInfo(g, filhoID, "nome") == null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** ID inválido.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				} else if (!FilhosManager.getInfo(g, filhoID, "pai").equals(u.getId())
						&& !FilhosManager.getInfo(g, filhoID, "mae").equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Este(a) filho(a) não pertence a você.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoNome = FilhosManager.getInfo(g, filhoID, "nome").toString();
				if (Integer.parseInt(String.valueOf(FilhosManager.getInfo(g, filhoID, "sede"))) >= 100) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** A sede de **" + filhoNome
									+ "** está no máximo.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				Random r = new Random();
				int randomXP = r.nextInt(35);
				while (randomXP < 5) {
					randomXP = r.nextInt(35);
				}
				boolean xp = false;
				if (xp) {
					randomXP = (randomXP * 2);
				}
				if (!EconomiaManager.hasMaos(g, u, 1000)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(1000) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), randomXP);
				FilhosManager.update(g, filhoID, "sede",
						Integer.parseInt((String.valueOf(FilhosManager.getInfo(g, filhoID, "sede")))) + 5);
				String avatar = FilhosManager.getInfo(g, filhoID, "avatar").toString();
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"**" + filhoNome + "** tomou água! Você gastou **" + Utils.getDinheiro(800) + "** e recebeu **"
								+ randomXP + "** de XP!" + ((xp) ? " **(x2)**" : "") + "\n\nSede de **" + filhoNome
								+ "**: **" + FilhosManager.getInfo(g, filhoID, "sede") + "%**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, (avatar.equals("Nenhum") ? null : avatar), null, 0, true);
				EconomiaManager.removeDinheiroMaos(g, u, 1000);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("brincar")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!filho brincar [id].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoID = ctx.getArgs().get(1);
				if (FilhosManager.getInfo(g, filhoID, "nome") == null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** ID inválido.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				} else if (!FilhosManager.getInfo(g, filhoID, "pai").equals(u.getId())
						&& !FilhosManager.getInfo(g, filhoID, "mae").equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Este(a) filho(a) não pertence a você.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoNome = FilhosManager.getInfo(g, filhoID, "nome").toString();
				if (Integer.parseInt(String.valueOf(FilhosManager.getInfo(g, filhoID, "felicidade"))) >= 100) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** A felicidade de **" + filhoNome
									+ "** está no máximo.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				Random r = new Random();
				int randomXP = r.nextInt(15);
				while (randomXP < 5) {
					randomXP = r.nextInt(15);
				}
				boolean xp = false;
				if (xp) {
					randomXP = (randomXP * 2);
				}
				if (!EconomiaManager.hasMaos(g, u, 200)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(200) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), randomXP);
				FilhosManager.update(g, filhoID, "felicidade",
						Integer.parseInt((String.valueOf(FilhosManager.getInfo(g, filhoID, "felicidade")))) + 5);
				String avatar = FilhosManager.getInfo(g, filhoID, "avatar").toString();
				if (Utils.chance(30)) {
					FilhosManager.update(g, filhoID, "saude",
							Integer.parseInt((String.valueOf(FilhosManager.getInfo(g, filhoID, "saude")))) - 5);
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"**" + filhoNome + "** brincou! Você gastou **" + Utils.getDinheiro(200) + "** e recebeu **"
									+ randomXP + "** de XP!" + ((xp) ? " **(x2)**" : "") + "\n\nFelicidade de **"
									+ filhoNome + "**: **" + FilhosManager.getInfo(g, filhoID, "felicidade")
									+ "%**.\n\n" + Utils.getEmote("cruz").getFormatted()
									+ " **OPS!**\nEnquanto vocês brincavam, **" + filhoNome
									+ "** machucou-se!\n\nSaúde de **" + filhoNome + "**: **"
									+ FilhosManager.getInfo(g, filhoID, "saude") + "%**.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, (avatar.equals("Nenhum") ? null : avatar), null, 0, true);
				} else {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							"**" + filhoNome + "** brincou! Você gastou **" + Utils.getDinheiro(200) + "** e recebeu **"
									+ randomXP + "** de XP!" + ((xp) ? " **(x2)**" : "") + "\n\nFelicidade de **"
									+ filhoNome + "**: **" + FilhosManager.getInfo(g, filhoID, "felicidade") + "%**.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, (avatar.equals("Nenhum") ? null : avatar), null, 0, true);
				}
				EconomiaManager.removeDinheiroMaos(g, u, 200);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("curar")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!filho curar [id].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				int preco = 3000;
				if (EconomiaManager.hasTrabalho(g, u) && EconomiaManager.getTrabalho(g, u).equals("Médico")) {
					preco = 2000;
				}
				String filhoID = ctx.getArgs().get(1);
				if (FilhosManager.getInfo(g, filhoID, "nome") == null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** ID inválido.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				} else if (!FilhosManager.getInfo(g, filhoID, "pai").equals(u.getId())
						&& !FilhosManager.getInfo(g, filhoID, "mae").equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Este(a) filho(a) não pertence a você.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoNome = FilhosManager.getInfo(g, filhoID, "nome").toString();
				if (Integer.parseInt(String.valueOf(FilhosManager.getInfo(g, filhoID, "saude"))) >= 100) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** A saúde de **" + filhoNome
									+ "** está no máximo.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				Random r = new Random();
				int randomXP = r.nextInt(40);
				while (randomXP < 5) {
					randomXP = r.nextInt(40);
				}
				boolean xp = false;
				if (xp) {
					randomXP = (randomXP * 2);
				}
				if (!EconomiaManager.hasMaos(g, u, preco)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(preco) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				LeveisManager.addXP(ctx.getChannel(), u.getId(), u.getName(), randomXP);
				FilhosManager.update(g, filhoID, "saude",
						Integer.parseInt((String.valueOf(FilhosManager.getInfo(g, filhoID, "saude")))) + 5);
				String avatar = FilhosManager.getInfo(g, filhoID, "avatar").toString();
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"**" + filhoNome + "** foi ao médico! Você gastou **" + Utils.getDinheiro(preco)
								+ "** e recebeu **" + randomXP + "** de XP!" + ((xp) ? " **(x2)**" : "")
								+ "\n\nSaúde de **" + filhoNome + "**: **" + FilhosManager.getInfo(g, filhoID, "saude")
								+ "%**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, (avatar.equals("Nenhum") ? null : avatar), null, 0, true);
				EconomiaManager.removeDinheiroMaos(g, u, preco);
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("info")) {
				if (ctx.getArgs().size() != 2) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!filho info [id].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoID = ctx.getArgs().get(1);
				if (FilhosManager.getInfo(g, filhoID, "nome") == null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** ID inválido.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String nome = FilhosManager.getInfo(g, filhoID, "nome").toString();
				String sexo = FilhosManager.getInfo(g, filhoID, "sexo").toString();
				String pai = FilhosManager.getInfo(g, filhoID, "pai").toString();
				String mae = FilhosManager.getInfo(g, filhoID, "mae").toString();
				String avatar = FilhosManager.getInfo(g, filhoID, "avatar").toString();
				int saude = Integer.parseInt(FilhosManager.getInfo(g, filhoID, "saude").toString());
				int felicidade = Integer.parseInt(FilhosManager.getInfo(g, filhoID, "felicidade").toString());
				int fome = Integer.parseInt(FilhosManager.getInfo(g, filhoID, "fome").toString());
				int sede = Integer.parseInt(FilhosManager.getInfo(g, filhoID, "sede").toString());
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTimestamp(Instant.now());
				eb.setFooter(g.getName());
				eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
				eb.setDescription("Informações de **" + nome + "**:\n\nNome: **" + nome + "**.\nSexo: **" + sexo
						+ "**.\nPai e mãe: <@" + pai + "> ❤️ <@" + mae + ">.\nSaúde: **" + saude
						+ "/100**.\nFelicidade: **" + felicidade + "/100**.\nFome: **" + fome + "/100**.\nSede: **"
						+ sede + "/100**.");
				if (!avatar.equals("Nenhum")) {
					eb.setImage(avatar);
				}
				ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
			} else if (ctx.getArgs().get(0).equalsIgnoreCase("avatar")) {
				if (ctx.getArgs().size() != 3) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted()
									+ " **Comando incompleto:** Use c!filho avatar [id] [link].",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoID = ctx.getArgs().get(1);
				if (FilhosManager.getInfo(g, filhoID, "nome") == null) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** ID inválido.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				} else if (!FilhosManager.getInfo(g, filhoID, "pai").equals(u.getId())
						&& !FilhosManager.getInfo(g, filhoID, "mae").equals(u.getId())) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Este(a) filho(a) não pertence a você.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				String filhoNome = FilhosManager.getInfo(g, filhoID, "nome").toString();
				if (!EconomiaManager.hasMaos(g, u, 4000)) {
					Utils.enviarEmbed(ctx.getChannel(), u, null,
							Utils.getEmote("nao").getFormatted() + " **Erro:** Você precisa de **"
									+ Utils.getDinheiro(4000) + "** em mãos para isso.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				EconomiaManager.removeDinheiroMaos(g, u, 4000);
				FilhosManager.update(g, filhoID, "avatar", ctx.getArgs().get(2));
				Utils.enviarEmbed(ctx.getChannel(), u, null, "Você alterou o avatar de **" + filhoNome + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, ctx.getArgs().get(2), null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!filho";
	}

	@Override
	public String getHelp() {
		return "Manuseie, liste ou crie filhos com quem você está casado(a)!";
	}

}