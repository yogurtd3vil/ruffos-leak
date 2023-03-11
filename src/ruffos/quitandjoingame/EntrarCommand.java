package ruffos.quitandjoingame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import ruffos._config.ConfigManager;
import ruffos.utils.Utils;
import ruffos.utils.commands.CommandContext;
import ruffos.utils.commands.ICommand;

public class EntrarCommand implements ICommand {

	@Override
	public void handle(CommandContext ctx) {
		Guild g = ctx.getGuild();
		Member m = ctx.getMember();
		if (QuitAndJoinManager.isQuited(g, m)) {
			List<TextChannel> canaisRuffos = new ArrayList<>();
			String[] split = ConfigManager.getConfig(g, "canal").split(",");
			for (String s : split) {
				if (g.getTextChannelById(s) != null) {
					canaisRuffos.add(g.getTextChannelById(s));
				}
			}
			for (TextChannel tc : canaisRuffos) {
				try {
					if (tc.getMemberPermissionOverrides().stream()
							.anyMatch(po -> po.isMemberOverride() && po.getId().equals(m.getId()))) {
						PermissionOverride po = tc.getMemberPermissionOverrides().stream()
								.filter(poo -> poo.isMemberOverride() && poo.getId().equals(m.getId())).findFirst()
								.get();
						po.delete().queue((s) -> {
						}, (f) -> {
							Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null, Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Este servidor não suporta este sistema. Contate a administração e solicite para que configure os canais do Ruffos corretamente adicionando a permissão de `Gerenciar permissões`.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
							return;
						});
					}
				} catch (PermissionException e) {
					Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Este servidor não suporta este sistema. Contate a administração e solicite para que configure os canais do Ruffos corretamente adicionando a permissão de `Gerenciar permissões`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
			}
			QuitAndJoinManager.setJoined(g, m);
			Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null,
					"Você voltou ao Ruffos no servidor `" + g.getName() + "`! Senti saudades!",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
		} else {
			List<TextChannel> canaisRuffos = new ArrayList<>();
			String[] split = ConfigManager.getConfig(g, "canal").split(",");
			for (String s : split) {
				if (g.getTextChannelById(s) != null) {
					canaisRuffos.add(g.getTextChannelById(s));
				}
			}
			for (TextChannel tc : canaisRuffos) {
				try {
					if (tc.getMemberPermissionOverrides().stream()
							.anyMatch(po -> po.isMemberOverride() && po.getId().equals(m.getId()))) {
						PermissionOverride po = tc.getMemberPermissionOverrides().stream()
								.filter(poo -> poo.isMemberOverride() && poo.getId().equals(m.getId())).findFirst()
								.get();
						po.delete().queue((s) -> {
						}, (f) -> {
							Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null, Utils.getEmote("nao").getFormatted()
									+ " **Erro:** Este servidor não suporta este sistema. Contate a administração e solicite para que configure os canais do Ruffos corretamente adicionando a permissão de `Gerenciar permissões`.",
									((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor"))
											: null)),
									g.getName(), null, null, null, 0, true);
							return;
						});
					}
				} catch (PermissionException e) {
					Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null, Utils.getEmote("nao").getFormatted()
							+ " **Erro:** Este servidor não suporta este sistema. Contate a administração e solicite para que configure os canais do Ruffos corretamente adicionando a permissão de `Gerenciar permissões`.",
							((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
							g.getName(), null, null, null, 0, true);
					return;
				}
			}
			Utils.enviarEmbed(ctx.getChannel(), m.getUser(), null,
					Utils.getEmote("nao").getFormatted() + " **Erro:** Você não saiu do Ruffos neste servidor.",
					((ConfigManager.temCfg(g, "cor") ? Color.decode(ConfigManager.getConfig(g, "cor")) : null)),
					g.getName(), null, null, null, 0, true);
		}
	}

	@Override
	public String getName() {
		return "c!entrar";
	}

	@Override
	public String getHelp() {
		return "entrar";
	}

}
