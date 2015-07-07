package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;

import java.util.concurrent.TimeUnit;

public class RunnableInactiveCleaner implements Runnable {
	private final NovaGuilds plugin;
	private boolean fake = false;

	public RunnableInactiveCleaner(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public RunnableInactiveCleaner(NovaGuilds novaGuilds, boolean fake) {
		plugin = novaGuilds;
		this.fake = fake;
	}

	public void run() {
		for(NovaGuild guild : plugin.getGuildManager().getMostInactiveGuilds()) {
			if(NovaGuilds.systemSeconds()-guild.getInactiveTime() < plugin.getConfigManager().getCleanupInactiveTime()) {
				break;
			}

			//TODO deleting
			plugin.debug("Fake removing guild "+guild.getName());
		}

		if(!fake) {
			Runnable task = new RunnableLiveRegeneration(plugin);
			plugin.worker.schedule(task,plugin.getConfigManager().getCleanupInterval(), TimeUnit.MINUTES);
		}
	}
}
