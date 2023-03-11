package ruffos.quitandjoingame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import ruffos._config.ConfigManager;
import ruffos._cooldowns.CooldownsManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class SairCommand implements ICommand {

	List<String> cooldown = new ArrayList<>();

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		Member m = ctx.getMember();
		if (ConfigManager.hasChat(g, ctx.getChannel())) {
			if (!QuitAndJoinManager.isQuited(g, m)) {
				long time = new Date().getTime();
				if (CooldownsManager.getCooldownTime(g, g.getId(), "sair") >= time) {
					Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Aguarde, alguém saiu do Rufos recentemente neste servidor. Você só poderá sair em **"
							+ Utils.getTime(CooldownsManager.getCooldownTime(g, g.getId(), "crime") - time) + "**!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
				if (ConfigManager.temCfg(g, "canal")) {
					List<TextChannel> canaisRuffos = new ArrayList<>();
					String[] split = ConfigManager.getConfig(g, "canal").split(",");
					for (String s : split) {
						if (g.getTextChannelById(s) != null) {
							canaisRuffos.add(g.getTextChannelById(s));
						}
					}
					for (TextChannel tc : canaisRuffos) {
						try {
							tc.upsertPermissionOverride(m).deny(Permission.VIEW_CHANNEL).queue(s -> {
							}, (f) -> {
								Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null, Utils.getEmote("nao")
										.getFormatted()
										+ " **Erro:** Este servidor não suporta este sistema. Contate a administração e solicite para que configure os canais do Ruffos corretamente adicionando a permissão de `Gerenciar permissões`.",
										((ConfigManager.temCfg(g, "cor")
												? Color.decode(ConfigManager.getConfig(g, "cor"))
												: null)),
										g.getName(), null, null, null, 0, true);
								return;
							});
						} catch (PermissionException e) {
							Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null, Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Este servidor não suporta este sistema. Contate a administração e solicite para que configure os canais do Ruffos corretamente adicionando a permissão de `Gerenciar permissões`.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
							return;
						}
					}
					QuitAndJoinManager.setQuited(g, m);
					CooldownsManager.addCooldown(g, g.getId(), "sair", 60000);
					Utils.enviarEmbed(m.getUser().openPrivateChannel().complete(), m.getUser(), null,
							"Você saiu do Ruffos no servidor `" + g.getName()
									+ "`! Você não receberá mais dinheiro, os comandos do mesmo foram suspensos de você e o chat foi escondido de você. Espero que volte logo!",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0);
				}
			} else {
				Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null,
						Utils.getEmote("nao").getFormatted() + " **Erro:** Você já saiu do Ruffos neste servidor.",
						((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
						g.getName(), null, null, null, 0, true);
			}
		}
	}

	@Override
	public String getName() {
		return "c!sair";
	}

	@Override
	public String getHelp() {
		return "sair";
	}

}
