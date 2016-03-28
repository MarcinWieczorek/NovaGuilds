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

package co.marcin.novaguilds.util.reflect.packet;

import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import com.google.common.base.Charsets;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

@SuppressWarnings("ALL")
public class PacketPlayOutPlayerInfo {

	private static final Class<?> packetClass = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
	private static Class<?> gameProfileClass;
	private static final Class<?>[] typesClass = new Class<?>[]{
			String.class,
			boolean.class,
			int.class
	};
	private static int type = 0;

	static {
		try {
			if(packetClass.getConstructor(typesClass) == null) {
				type = 1;
			}
		}
		catch(Exception e) {
			type = 1;
		}

		try {
			gameProfileClass = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
		}
		catch(ClassNotFoundException e) {
			LoggerUtils.exception(e);
		}
	}

	public static Object getPacket(String string, boolean b, int ping) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
		if(type == 0) {
			return packetClass.getConstructor(typesClass).newInstance(string, b, ping);
		}
		else if(type == 1) {
			UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(Charsets.UTF_8));
			Object profile = null;

			if(type == 2) {
				profile = gameProfileClass.getConstructor(new Class<?>[]{
						String.class,
						String.class
				}).newInstance(uuid.toString(), string);
			}
			else if(type == 1) {
				profile = gameProfileClass.getConstructor(new Class<?>[]{
						UUID.class,
						String.class
				}).newInstance(uuid, string);
			}

			Class<?> clazz = Reflections.getCraftClass("PacketPlayOutPlayerInfo");
			Object packet = packetClass.getConstructor().newInstance();
			Reflections.getPrivateField(clazz, "username").set(packet, string);
			Reflections.getPrivateField(clazz, "gamemode").set(packet, 1);
			Reflections.getPrivateField(clazz, "ping").set(packet, ping);
			Reflections.getPrivateField(clazz, "player").set(packet, profile);

			if(!b) {
				Reflections.getPrivateField(clazz, "action").set(packet, 4);
			}

			return packet;
		}

		return null;
	}

}
