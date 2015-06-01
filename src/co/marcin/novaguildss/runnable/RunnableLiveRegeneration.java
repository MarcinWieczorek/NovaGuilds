package co.marcin.novaguildss.runnable;

import co.marcin.novaguildss.NovaGuilds;
import co.marcin.novaguildss.basic.NovaGuild;

public class RunnableLiveRegeneration implements Runnable {
	private final NovaGuilds plugin;

	public RunnableLiveRegeneration(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public void run() {
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			long lostLiveTime = guild.getLostLiveTime();

			if(lostLiveTime > 0) {
				if(NovaGuilds.systemSeconds() - lostLiveTime > plugin.liveRegenerationTime) {
					guild.addLive();
					guild.resetLostLiveTime();
					plugin.debug("live regenerated for guild: "+guild.getName());
				}
			}
		}

		plugin.runLiveRegenerationTask();
	}
}
