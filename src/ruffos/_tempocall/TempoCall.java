package ruffos._tempocall;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ruffos.ConsoleColors;
import ruffos._config.ConfigManager;
import ruffos._economia.EconomiaManager;
import ruffos.quitandjoingame.QuitAndJoinManager;
import ruffos.utils.Utils;

public class TempoCall extends ListenerAdapter {

	public static Map<Member, Long> tempo = new HashMap<>();
	public static Map<Member, Long> tempoMutado = new HashMap<>();
	public static Map<Member, Long> tempoMutadoSalvo = new HashMap<>();

	static Map<Member, Long> tempoCall = new HashMap<>();
	public static boolean liberado = false;

	public static void carregar(Guild g) {
		int i = 0;
		for (Member m : g.getMembers()) {
			if (m.getVoiceState().inAudioChannel()
					&& ConfigManager.isCategoriaTempo(g,
							g.getVoiceChannelById(m.getVoiceState().getChannel().getId()).getParentCategory())
					&& !QuitAndJoinManager.isQuited(g, m)) {
				tempoCall.put(m, System.currentTimeMillis());
				i++;
			}
		}
		System.out.println(Utils.getTime() + ConsoleColors.BRIGHT_GREEN + "Usuários carregados em call: "
				+ ConsoleColors.BRIGHT_CYAN + i + ConsoleColors.BRIGHT_GREEN + ".");
	}

	public static boolean contaHora(Category c) {
		if (ConfigManager.isCategoriaTempo(c.getGuild(), c)) {
			return true;
		}
		return false;
	}

	@Override
	public void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {
		Member m = event.getMember();
		if (event.getVoiceState().getChannel() != null) {
			VoiceChannel vc = event.getGuild().getVoiceChannelById(event.getVoiceState().getChannel().getId());
			if (m.getVoiceState().getChannel() != null && vc.getParentCategory() != null
					&& (contaHora(vc.getParentCategory()))) {
				if (!m.getVoiceState().isSelfMuted() && tempoMutado.containsKey(m)) {
					long tempoSalvo = 0;
					if (tempoMutadoSalvo.containsKey(m)) {
						tempoSalvo = tempoMutadoSalvo.get(m);
					}
					tempoMutadoSalvo.put(m, (tempoSalvo + (System.currentTimeMillis() - tempoMutado.get(m))));
					tempoMutado.remove(m);
				} else if (m.getVoiceState().isSelfMuted() && (tempo.containsKey(m))) {
					tempoMutado.put(m, System.currentTimeMillis());
				}
			}
		}
	}

	public static Member staff(Member m) {
		Member a = null;
		if (m.getRoles().contains(m.getGuild().getRoleById("862515827412697119"))) {
			a = m;
		}
		return a;
	}

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		VoiceChannel join = null;
		VoiceChannel left = null;
		if (event.getChannelJoined() != null) {
			join = event.getGuild().getVoiceChannelById(event.getChannelJoined().getId());
		}
		if (event.getChannelLeft() != null) {
			left = event.getGuild().getVoiceChannelById(event.getChannelLeft().getId());
		}
		Member m = event.getEntity();
		if (join != null && join.getParentCategory() != null && left == null) {
			if (contaHora(join.getParentCategory())) {
				tempo.put(m, System.currentTimeMillis());
				if (m.getVoiceState().isSelfMuted()) {
					tempoMutado.put(m, System.currentTimeMillis());
				}
			}
		} else if (join != null && join.getParentCategory() != null && left != null) {
			if (left.getParentCategory() != null) {
				if (contaHora(left.getParentCategory()) && tempo.containsKey(m)) {
					int dinheiro = Integer
							.parseInt(String.valueOf(300 * (System.currentTimeMillis() - tempo.get(m)) / 1800000));
					EconomiaManager.addDinheiroMaos(event.getGuild(), m.getUser(), dinheiro);
					tempo.remove(m);
				}
			}
			if (contaHora(join.getParentCategory())) {
				tempo.put(m, System.currentTimeMillis());
				if (m.getVoiceState().isSelfMuted()) {
					tempoMutado.put(m, System.currentTimeMillis());
				}
			}
		} else if (join == null && left != null && left.getParentCategory() != null) {
			if (contaHora(left.getParentCategory()) && tempo.containsKey(m)) {
				int dinheiro = Integer
						.parseInt(String.valueOf(300 * (System.currentTimeMillis() - tempo.get(m)) / 1800000));
				EconomiaManager.addDinheiroMaos(event.getGuild(), m.getUser(), dinheiro);
				tempo.remove(m);
			}
		}
	}

}
