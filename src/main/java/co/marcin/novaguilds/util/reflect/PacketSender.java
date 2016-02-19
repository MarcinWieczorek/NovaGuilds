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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class PacketSender {
	private static final String packageName = Bukkit.getServer().getClass().getPackage().getName();
	private static final String version = packageName.substring(packageName.lastIndexOf(".") + 1);

	public static void sendPacket(Player player, Object... os) {
		sendPacket(new Player[]{player}, os);
	}

	public static void sendPacket(Player[] players, Object... os) {
		try {
			Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");

			for(Player p : players) {
				Object cp = craftPlayer.cast(p);
				Object handle = craftPlayer.getMethod("getHandle").invoke(cp);
				Object con = handle.getClass().getField("playerConnection").get(handle);
				Method method = con.getClass().getMethod("sendPacket", packetClass);

				for(Object o : os) {
					if(o == null) continue;
					method.invoke(con, o);
				}
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

}