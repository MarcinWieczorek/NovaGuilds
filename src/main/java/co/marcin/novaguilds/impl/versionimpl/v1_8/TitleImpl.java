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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.Title;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@SuppressWarnings("ConstantConditions")
public class TitleImpl implements Title {
	/* Title packet */
	private Class<?> packetTitle;

	/* Title packet actions ENUM */
	private Class<?> packetActions;

	/* Chat serializer */
	private Class<?> nmsChatSerializer;
	private Class<?> chatBaseComponent;

	/* Title text and color */
	private String title = "";
	private ChatColor titleColor = ChatColor.WHITE;

	/* Subtitle text and color */
	private String subtitle = "";
	private ChatColor subtitleColor = ChatColor.WHITE;

	/* Title timings */
	private int fadeInTime = -1;
	private int stayTime = -1;
	private int fadeOutTime = -1;
	private boolean ticks = false;

	/**
	 * Create a new 1.8 title
	 */
	public TitleImpl() {
		this("");
	}

	/**
	 * Create a new 1.8 title
	 *
	 * @param title Title
	 */
	public TitleImpl(String title) {
		this(title, null);
	}

	/**
	 * Create a new 1.8 title
	 *
	 * @param title    Title text
	 * @param subtitle Subtitle text
	 */
	public TitleImpl(String title, String subtitle) {
		this(title, subtitle, -1, -1, -1);
	}

	/**
	 * Copy 1.8 title
	 *
	 * @param title Title
	 */
	public TitleImpl(Title title) {
		// Copy title
		this.title = title.getTitle();
		subtitle = title.getSubtitle();
		titleColor = title.getTitleColor();
		subtitleColor = title.getSubtitleColor();
		fadeInTime = title.getFadeInTime();
		fadeOutTime = title.getFadeOutTime();
		stayTime = title.getStayTime();
		ticks = title.getTicks();
		loadClasses();
	}

	/**
	 * Create a new 1.8 title
	 *
	 * @param title       Title text
	 * @param subtitle    Subtitle text
	 * @param fadeInTime  Fade in time
	 * @param stayTime    Stay on screen time
	 * @param fadeOutTime Fade out time
	 */
	public TitleImpl(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime) {
		this.title = title;
		this.subtitle = subtitle;
		this.fadeInTime = fadeInTime;
		this.stayTime = stayTime;
		this.fadeOutTime = fadeOutTime;
		loadClasses();
	}

	/**
	 * Loads classes
	 */
	private void loadClasses() {
		packetTitle = Reflections.getCraftClass("PacketPlayOutTitle");
		packetActions = Reflections.getCraftClass("PacketPlayOutTitle$EnumTitleAction");
		chatBaseComponent = Reflections.getCraftClass("IChatBaseComponent");
		nmsChatSerializer = Reflections.getCraftClass("IChatBaseComponent$ChatSerializer");
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	@Override
	public String getSubtitle() {
		return subtitle;
	}

	@Override
	public ChatColor getTitleColor() {
		return titleColor;
	}

	@Override
	public ChatColor getSubtitleColor() {
		return subtitleColor;
	}

	@Override
	public int getFadeInTime() {
		return fadeInTime;
	}

	@Override
	public int getFadeOutTime() {
		return fadeOutTime;
	}

	@Override
	public int getStayTime() {
		return stayTime;
	}

	@Override
	public boolean getTicks() {
		return ticks;
	}

	@Override
	public void setTitleColor(ChatColor color) {
		titleColor = color;
	}

	@Override
	public void setSubtitleColor(ChatColor color) {
		subtitleColor = color;
	}

	@Override
	public void setFadeInTime(int time) {
		fadeInTime = time;
	}

	@Override
	public void setFadeOutTime(int time) {
		fadeOutTime = time;
	}

	@Override
	public void setStayTime(int time) {
		stayTime = time;
	}

	@Override
	public void setTimingsToTicks() {
		ticks = true;
	}

	@Override
	public void setTimingsToSeconds() {
		ticks = false;
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
	public void broadcast() {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			send(player);
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
