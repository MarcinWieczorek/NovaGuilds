package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RunnableTeleportRequest implements Runnable {
	private final Player player;
	private final Location location;

	private final Location startLocation;
	private final NovaGuilds plugin;
	private final String msgPath;

	public RunnableTeleportRequest(NovaGuilds novaGuilds, Player p, Location l, String path) {
		player = p;
		location = l;
		startLocation = player.getLocation();
		plugin = novaGuilds;
		msgPath = path;
	}

	@Override
	public void run() {
		if(player.getLocation().distance(startLocation) == 0) {
			player.teleport(location);
			plugin.getMessageManager().sendMessagesMsg(player,msgPath);
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(player,"chat.delayedtpmoved");
		}
	}
}
