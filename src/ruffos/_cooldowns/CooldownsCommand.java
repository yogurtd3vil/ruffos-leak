package ruffos._cooldowns;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class CooldownsCommand implements ICommand {

	String getCooldown(Guild g, User u, String nome) {
		long time = System.currentTimeMillis();
		if (CooldownsManager.getCooldownTime(g, u.getId(), nome) >= time) {
			return Utils.getTime(CooldownsManager.getCooldownTime(g, u.getId(), nome) - time) + ".";
		}
		return "não está em cooldown.";
	}

	String getCooldownRecompensa(Guild g, User u) {
		long time = System.currentTimeMillis();
		if (EconomiaManager.inRecompensa(g, u) && time < (EconomiaManager.getRecompensa(g, u) + (3600000L * 24))) {
			return Utils.getTime((EconomiaManager.getRecompensa(g, u) + (3600000L * 24) - System.currentTimeMillis()))
					+ ".";
		}
		return "não está em cooldown.";
	}

	@Override
	public void handle(CommandContext ctx) {
		User u = ctx.getAuthor();
		Guild g = ctx.getGuild();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			List<Field> fields = new ArrayList<>();
			fields.add(new Field("c!semanal:", getCooldown(g, u, "semanal"), true));
			fields.add(new Field("c!recompensa:", getCooldownRecompensa(g, u), true));
			fields.add(new Field("c!crime:", getCooldown(g, u, "crime"), true));
			fields.add(new Field("c!beber:", getCooldown(g, u, "beber"), true));
			fields.add(new Field("c!roubarbanco:", getCooldown(g, u, "roubobanco"), true));
			fields.add(new Field("c!roubar:", getCooldown(g, u, "roubo"), true));
			fields.add(new Field("c!trabalhar:", getCooldown(g, u, "trabalhar"), true));
			fields.add(new Field("c!blackjack:", getCooldown(g, u, "blackjack"), true));
			if (Utils.isFakeGuild(g)) {
				fields.add(new Field("c!sf:", getCooldown(g, u, "SF"), true));
				fields.add(new Field("c!amantes sf:", getCooldown(g, u, "amantes"), true));
			} else {
				fields.add(new Field("c!gf:", getCooldown(g, u, "gf"), true));
				fields.add(new Field("c!amantes gf:", getCooldown(g, u, "amantes"), true));
			}
			fields.add(new Field("c!roleta:", getCooldown(g, u, "roleta"), true));
			fields.add(new Field("c!cavalo:", getCooldown(g, u, "cavalo"), true));
			fields.add(new Field("c!roubarcofre:", getCooldown(g, u, "roubocofre"), true));
			if (ConfigManager.temCfg(g, "salariostaff")
					&& g.getRoleById(ConfigManager.getConfig(g, "salariostaff")) != null) {
				fields.add(new Field("c!salariostaff:", getCooldown(g, u, "salariostaff"), true));
			}
			fields.add(new Field("c!xcam", getCooldown(g, u, "xcam"), true));
			fields.add(new Field("c!venderpack", getCooldown(g, u, "venderpack"), true));
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
			String tim = fmt.format(new Date());
			StringBuilder sb = new StringBuilder(tim);
			sb.deleteCharAt(2);
			int t = Integer.parseInt(sb.toString());
			fields.add(new Field("c!madrugada",
					((t >= 200 && t <= 210) ? "Este comando já está disponível para ser utilizado! Limite: 1x."
							: "Este comando fica disponível apenas das **02:00** até às **02:10**!"),
					true));
			Utils.enviarEmbed(ctx.getChannel(), u, null,
					Utils.getEmote("tempo").getFormatted()
							+ " Confira quanto tempo falta para executar algumas das minhas ações:",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), fields, null, null, 0, true);
		}
	}

	@Override
	public String getName() {
		return "c!c";
	}

	@Override
	public String getHelp() {
		return "ver cooldowns";
	}

}
