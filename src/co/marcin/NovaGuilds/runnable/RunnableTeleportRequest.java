package co.marcin.NovaGuilds.runnable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RunnableTeleportRequest implements Runnable {
	private final Player player;
	private final Location location;

	private final Location startLocation;

	public RunnableTeleportRequest(Player p, Location l) {
		player = p;
		location = l;
		startLocation = player.getLocation();
	}

	@Override
	public void run() {
		if(player.getLocation().distance(startLocation) == 0) {
			player.teleport(location);
		}
		else {
			//moved
		}
	}
}
