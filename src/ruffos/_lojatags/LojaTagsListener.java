package ruffos._lojatags;

import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LojaTagsListener extends ListenerAdapter {

	@Override
	public void onRoleDelete(RoleDeleteEvent event) {
		LojaTagsManager.atualizarTags(event.getGuild());
	}

}
