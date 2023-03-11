package ruffos._rinha;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RinhaListener extends ListenerAdapter {

	public static boolean estaEmRinha(Member m) {
		return RinhaCommand.rinhas.stream().anyMatch(r -> r.getUm() == m || r.getDois() == m);
	}

	public static Rinha getRinha(Member m) {
		return RinhaCommand.rinhas.stream().filter(r -> r.getUm() == m || r.getDois() == m).findFirst().get();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Member m = event.getMember();
		Message msg = event.getMessage();
		if (event.isFromType(ChannelType.TEXT)) {
			if (estaEmRinha(m) && getRinha(m).getUm() == m && msg.getContentRaw().equalsIgnoreCase("brigar")) {
				getRinha(m).addUm();
			} else if (estaEmRinha(m) && getRinha(m).getDois() == m && msg.getContentRaw().equalsIgnoreCase("brigar")) {
				getRinha(m).addDois();
			}
		}
	}

}
