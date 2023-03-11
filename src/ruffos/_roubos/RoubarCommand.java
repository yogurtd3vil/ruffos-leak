package ruffos._roubos;

import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos._prisao.PresosManager;
import ruffos._rankings.TopRoubosCommand;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class RoubarCommand implements ICommand {

	public static Map<User, Roubo> reagir = new HashMap<>();

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (ctx.getArgs().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando incompleto:** Use c!roubar @usuário.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			Message m = ctx.getMessage();
			if (m.getMentions().getMembers().size() != 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!roubar @usuário.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			User roubado = m.getMentions().getMembers().get(0).getUser();
			if (u == roubado) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você não pode roubar a si mesmo.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!EconomiaManager.hasArma(g, u)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você precisa de uma arma para efetuar um roubo. Use: `c!loja`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			} else if (EconomiaManager.getUsos(g, u) < 1) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você precisa de munição para efetuar um roubo. Use: `c!loja`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			} else if (!EconomiaManager.existeUser(g, roubado)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Este(a) usuário(a) não existe no banco de dados do Ruffos.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "roubo") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " Aguarde, você só poderá efetuar um roubo novamente em **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "roubo") - time) + "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}

			int porcentagem = 30;
			int chance = 25;
			String arma = EconomiaManager.getArma(g, u);
			if (arma.equals("glock")) {
				porcentagem = 32;
				chance = 23;
			} else if (arma.equals("deagle")) {
				porcentagem = 35;
				chance = 20;
			} else if (arma.equals("ump45")) {
				porcentagem = 40;
				chance = 15;
			} else if (arma.equals("shotgun")) {
				porcentagem = 45;
				chance = 10;
			} else if (arma.equals("ak47")) {
				porcentagem = 55;
				chance = 3;
			}

			Random r = new Random();

			Date d = new Date();
			d.setHours(d.getHours() - 1);

			if (Utils.chance(chance)) {
				CooldownsManager.addCooldown(g, u.getId(), "roubo", Long.parseLong("1800000"));
				int balas = r.nextInt(6);
				while (balas <= 1) {
					balas = r.nextInt(6);
				}
				int randomPrisao = r.nextInt(30);
				while (randomPrisao < 15) {
					randomPrisao = r.nextInt(30);
				}
				EconomiaManager.setUsos(g, u, (EconomiaManager.getUsos(g, u) - balas));
				Utils.enviarEmbed(ctx.getChannel(), u, null, Utils.getEmote("sirene2").getFormatted()
						+ " O roubo foi interrompido pela polícia antes que você roubasse " + roubado.getAsMention()
						+ "! Você perdeu **" + balas + "** munição(ões) na trocação de tiro (**"
						+ EconomiaManager.getUsos(g, u) + "/30**) e foi preso por **" + randomPrisao
						+ " minuto(s)**\n\nVocê poderá digitar apenas um comando preso.\n`c!fianca` » Para pagar sua fiança de **"
						+ Utils.getDinheiro(10000) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				PresosManager.prender(g, u, randomPrisao, "Tentativa de roubo.");
				return;
			}
			int balas = r.nextInt(4);
			while (balas <= 1) {
				balas = r.nextInt(4);
			}
			int remover = (int) Math.round(porcentagem((EconomiaManager.getDinheiroMaos(g, roubado)), porcentagem));
			if (remover < 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Este(a) usuário(a) não tem dinheiro para ser roubado(a).",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			CooldownsManager.addCooldown(g, u.getId(), "roubo", Long.parseLong("1800000"));
			EconomiaManager.setUsos(g, u, (EconomiaManager.getUsos(g, u) - balas));
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					"Você roubou " + roubado.getAsMention() + " e levou **" + Utils.getDinheiro(remover)
							+ "** de suas mãos! (**-" + balas + " munição(ões) » " + EconomiaManager.getUsos(g, u)
							+ "/30**)\n\n" + roubado.getAsMention()
							+ " deverá digitar `reagir` em **5s** para pegar uma parte de volta!",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName() + " » Para ver quem mais roubou nas últimas horas ou dias: c!roubos.", null, null, null,
					0, true);
			TopRoubosCommand.addRoubo(g, u, remover);
			EconomiaManager.removeDinheiroMaos(g, roubado, remover);
			EconomiaManager.addDinheiroMaos(g, u, remover);
			/**
			 * TextChannel logs = ctx.getJDA().getTextChannelById("838845314454651001");
			 * logs.sendMessage(Utils.getEmote("roubo").getAsMention() + "
			 * **ROUBO**\n\n**Ladrão:** `" + u.getAsTag() + "` - `" + u.getId() +
			 * "`.\n**Roubado:** `" + roubado.getAsTag() + "` - `" + roubado.getId() +
			 * "`.\n**Dinheiro roubado:** `" + Utils.getDinheiro(remover) + "`.\n**Arma
			 * utilizada:** `" + arma + "` - `(-" + balas + " munição(ões))` - `" +
			 * EconomiaManager.getUsos(g, u) + "/30`.\n**Servidor:** `" + g.getName() + "` -
			 * `" + g.getId() + "`.").queue();
			 **/
			reagir.put(u, new Roubo(ctx.getChannel(), u, roubado, remover));
		}
	}

	public static Roubo getRouboDe(User u) {
		for (Map.Entry<User, Roubo> roubos : reagir.entrySet()) {
			if (roubos.getValue().getRoubado().equals(u)) {
				return roubos.getValue();
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "c!roubar";
	}

	@Override
	public String getHelp() {
		return "Roube um usuário.";
	}

	double porcentagem(double valor, int porcentagem) {
		return (valor * porcentagem) / 100.0;
	}

}
