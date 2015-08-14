package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
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
		LoggerUtils.info("Saved data.");

		//send message to admins
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			if(player.hasPermission("novaguilds.admin.save.notify")) {
				Message.CHAT_ADMIN_SAVE_AUTOSAVE.send(player);
			}
		}
	}
}
