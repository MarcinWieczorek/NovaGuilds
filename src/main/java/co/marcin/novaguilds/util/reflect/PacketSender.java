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

import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class PacketSender {
	public static void sendPacket(Player player, Object... packets) {
		sendPacket(new Player[]{player}, packets);
	}

	public static void sendPacket(Player[] players, Object... packets) {
		try {
			Class<?> packetClass = Reflections.getCraftClass("Packet");
			Class<?> craftPlayerClass = Reflections.getBukkitClass("CraftPlayer");

			for(Player player : players) {
				Object craftPlayer = craftPlayerClass.cast(player);
				Object handle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
				Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
				Method sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", packetClass);

				for(Object packet : packets) {
					if(packet == null) {
						continue;
					}

					sendPacketMethod.invoke(playerConnection, packet);
				}
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}
}
