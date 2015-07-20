package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RunnableTeleportRequest implements Runnable {
	private final Player player;
	private final Location location;

	private final Location startLocation;
	private final NovaGuilds plugin;
	private final Message message;

	public RunnableTeleportRequest(NovaGuilds novaGuilds, Player p, Location l, Message message) {
		player = p;
		location = l;
		startLocation = player.getLocation();
		plugin = novaGuilds;
		this.message = message;
	}

	@Override
	public void run() {
		if(player.getLocation().distance(startLocation) == 0) {
			player.teleport(location);
			message.send(player);
		}
		else {
			Message.CHAT_DELAYEDTPMOVED.send(player);
			plugin.getMessageManager().sendMessagesMsg(player,"chat.delayedtpmoved");
		}
	}
}
