package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;

public class RunnableInactiveCleaner implements Runnable {
	private final NovaGuilds plugin;

	public RunnableInactiveCleaner(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public void run() {
		for(NovaGuild guild : plugin.getGuildManager().getMostInactiveGuilds()) {
			if(NumberUtils.systemSeconds()-guild.getInactiveTime() < plugin.getConfigManager().getCleanupInactiveTime()) {
				break;
			}

			//TODO deleting
			LoggerUtils.debug("Fake removing guild " + guild.getName());
		}
	}
}
