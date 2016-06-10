/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.util.reflect;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.entity.Player;

public class PacketSender {
	/**
	 * Sends packets to players
	 *
	 * @param players array of players
	 * @param packets packets
	 */
	public static void sendPacket(Player[] players, Object... packets) {
		for(Player player : players) {
			sendPacket(player, packets);
		}
	}

	/**
	 * Sends packets to a player
	 *
	 * @param player  player
	 * @param packets packets
	 */
	public static void sendPacket(Player player, Object... packets) {
		NovaGuilds.getInstance().getPacketExtension().sendPacket(player, packets);
	}
}
