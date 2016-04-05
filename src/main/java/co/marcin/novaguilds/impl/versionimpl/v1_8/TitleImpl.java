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

package co.marcin.novaguilds.impl.versionimpl.v1_8;

import co.marcin.novaguilds.api.util.Title;
import co.marcin.novaguilds.impl.util.AbstractTitle;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class TitleImpl extends AbstractTitle {
	protected static final Class<?> packetTitle = Reflections.getCraftClass("PacketPlayOutTitle");
	protected static final Class<?> packetActions = Reflections.getCraftClass("PacketPlayOutTitle$EnumTitleAction");
	protected static final Class<?> chatBaseComponent = Reflections.getCraftClass("IChatBaseComponent");
	protected static final Class<?> nmsChatSerializer = Reflections.getCraftClass("IChatBaseComponent$ChatSerializer");

	public TitleImpl() {
		super("");
	}

	public TitleImpl(String title) {
		super(title);
	}

	public TitleImpl(String title, String subtitle) {
		super(title, subtitle);
	}

	public TitleImpl(Title title) {
		super(title);
	}

	public TitleImpl(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		super(title, subtitle, fadeInTime, stayTime, fadeOutTime);
	}

	@Override
	public void send(Player player) {
		if(packetTitle != null) {
			resetTitle(player);

			try {
				// Send timings first
				Object handle = Reflections.getHandle(player);
				Object connection = Reflections.getField(handle.getClass(), "playerConnection").get(handle);
				Object[] actions = packetActions.getEnumConstants();
				Method sendPacket = Reflections.getMethod(connection.getClass(), "sendPacket");

				Object packet = packetTitle.getConstructor(packetActions,
						chatBaseComponent, Integer.TYPE, Integer.TYPE,
						Integer.TYPE).newInstance(actions[2], null,
						fadeInTime * (ticks ? 1 : 20),
						stayTime * (ticks ? 1 : 20),
						fadeOutTime * (ticks ? 1 : 20));

				if(fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1) {
					sendPacket.invoke(connection, packet);
				}


				Object serialized = Reflections.getMethod(nmsChatSerializer, "a", String.class).invoke(null, "{text:\""
						+ StringUtils.fixColors(title) + "\",color:"
						+ titleColor.name().toLowerCase() + "}");
				packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[0], serialized);
				sendPacket.invoke(connection, packet);

				if(!subtitle.isEmpty()) {
					serialized = Reflections.getMethod(nmsChatSerializer, "a", String.class).invoke(null, "{text:\""
							+ StringUtils.fixColors(subtitle)
							+ "\",color:"
							+ subtitleColor.name()
							.toLowerCase() + "}");

					packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[1], serialized);
					sendPacket.invoke(connection, packet);
				}
			}
			catch(Exception e) {
				LoggerUtils.exception(e);
			}
		}
	}

	@Override
	public void clearTitle(Player player) {
		try {
			// Send timings first
			Object handle = Reflections.getHandle(player);
			Object connection = Reflections.getField(handle.getClass(), "playerConnection").get(handle);
			Object[] actions = packetActions.getEnumConstants();
			Method sendPacket = Reflections.getMethod(connection.getClass(), "sendPacket");
			Object packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[3], null);
			sendPacket.invoke(connection, packet);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void resetTitle(Player player) {
		try {
			// Send timings first
			Object handle = Reflections.getHandle(player);
			Object connection = Reflections.getField(handle.getClass(), "playerConnection").get(handle);
			Object[] actions = packetActions.getEnumConstants();
			Method sendPacket = Reflections.getMethod(connection.getClass(), "sendPacket");
			Object packet = packetTitle.getConstructor(packetActions, chatBaseComponent).newInstance(actions[4], null);
			sendPacket.invoke(connection, packet);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}
}
