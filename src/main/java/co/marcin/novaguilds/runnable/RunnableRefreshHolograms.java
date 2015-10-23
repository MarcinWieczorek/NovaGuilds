package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.LoggerUtils;

public class RunnableRefreshHolograms implements Runnable {
	private final NovaGuilds plugin;

	public RunnableRefreshHolograms(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	@Override
	public void run() {
		plugin.getHologramManager().refreshTopHolograms();
		LoggerUtils.info("Top holograms refreshed.");
	}
}
