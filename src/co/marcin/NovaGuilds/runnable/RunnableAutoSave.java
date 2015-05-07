package co.marcin.NovaGuilds.runnable;

import co.marcin.NovaGuilds.NovaGuilds;
import org.bukkit.entity.Player;

public class RunnableAutoSave implements Runnable {
	private final NovaGuilds plugin;

	public RunnableAutoSave(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public void run() {
		plugin.getGuildManager().saveAll();
		plugin.getRegionManager().saveAll();
		plugin.getPlayerManager().saveAll();
		plugin.info("Saved data.");

		//send message to admins
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			if(player.hasPermission("novaguilds.admin.save.notify")) {
				plugin.sendMessagesMsg(player,"chat.admin.save.autosave");
			}
		}
	}
}
