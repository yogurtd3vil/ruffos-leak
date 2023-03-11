package ruffos._blackjack;

import java.awt.Color;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class BlackJackCommand implements ICommand {
	public static Map<User, Blackjack> blackjack = new HashMap<>();
	public static Map<Member, Integer> count = new HashMap<>();

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		User u = ctx.getAuthor();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			long time = new Date().getTime();
			if (CooldownsManager.getCooldownTime(g, u.getId(), "blackjack") >= time) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"<a:nao_ruffos:717039420389458021> Aguarde, você só poderá jogar BlackJack novamente em **"
								+ Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), "blackjack") - time)
								+ "**!",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (ctx.getArgs().size() == 0) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						"<a:nao_ruffos:717039420389458021> **Comando incompleto:** Use c!blackjack [valor].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			} else if (blackjack.containsKey(u)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted()
								+ " **Erro:** Você já está jogando BlackJack! Digite `comprar` ou `parar`.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			int valor = 0;
			if (!Utils.isInteger(ctx.getArgs().get(0))) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Comando errado:** Use c!blackjack [valor].",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			valor = Integer.parseInt(ctx.getArgs().get(0));
			if (valor < 100) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** O valor deve ser maior ou igual à **"
								+ Utils.getDinheiro(100) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (valor > 2000) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** O valor deve ser menor ou igual à **"
								+ Utils.getDinheiro(2000) + "**.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			if (!EconomiaManager.hasMaos(g, u, valor)) {
				Utils.enviarEmbed(ctx.getChannel(), u, null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você deve ter **" + Utils.getDinheiro(valor)
								+ "** em mãos para jogar BlackJack.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
				return;
			}
			EconomiaManager.removeDinheiroMaos(g, u, valor);
			Blackjack bj = new Blackjack(u, valor, g, ctx.getChannel());
			blackjack.put(u, bj);
			if (count.containsKey(ctx.getMember())) {
				count.remove(ctx.getMember());
				CooldownsManager.addCooldown(g, u.getId(), "blackjack", Long.parseLong("600000"));
			} else {
				count.put(ctx.getMember(), 1);
			}
		}
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("c!bj");
	}

	@Override
	public String getName() {
		return "c!blackjack";
	}

	@Override
	public String getHelp() {
		return "Jogue BlackJack.";
	}

}
