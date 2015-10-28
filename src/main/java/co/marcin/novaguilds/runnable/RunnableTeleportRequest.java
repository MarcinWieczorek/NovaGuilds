/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.runnable;

import co.marcin.novaguilds.enums.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RunnableTeleportRequest implements Runnable {
	private final Player player;
	private final Location location;

	private final Location startLocation;
	private final Message message;

	public RunnableTeleportRequest(Player p, Location l, Message message) {
		player = p;
		location = l;
		startLocation = player.getLocation();
		this.message = message;
	}

	@Override
	public void run() {
		if(player.getLocation().distance(startLocation) < 1) {
			player.teleport(location);
			message.send(player);
		}
		else {
			Message.CHAT_DELAYEDTPMOVED.send(player);
		}
	}
}
