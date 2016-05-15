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

import co.marcin.novaguilds.event.PacketReceiveEvent;
import co.marcin.novaguilds.impl.util.AbstractPacketHandler;
import co.marcin.novaguilds.impl.util.signgui.AbstractSignGui;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SignGUIImpl extends AbstractSignGui {
	protected static Class<?> iChatBaseComponentClass;
	protected static Class<?> packetInUpdateSignClass;
	protected static Class<?> packetOutUpdateSignClass;
	protected static Class<?> packetBlockChangeClass;
	protected static Class<?> packetOpenSignEditorClass;
	protected static Class<?> baseBlockPositionClass;
	protected static Class<?> blockPositionClass;
	protected static Class<?> worldClass;
	protected static Class<?> chatComponentTextClass;
	protected static Class<?> craftMagicNumbersClass;

	static {
		try {
			iChatBaseComponentClass = Reflections.getCraftClass("IChatBaseComponent");
			packetInUpdateSignClass = Reflections.getCraftClass("PacketPlayInUpdateSign");
			packetOutUpdateSignClass = Reflections.getCraftClass("PacketPlayOutUpdateSign");
			packetBlockChangeClass = Reflections.getCraftClass("PacketPlayOutBlockChange");
			packetOpenSignEditorClass = Reflections.getCraftClass("PacketPlayOutOpenSignEditor");
			baseBlockPositionClass = Reflections.getCraftClass("BaseBlockPosition");
			blockPositionClass = Reflections.getCraftClass("BlockPosition");
			worldClass = Reflections.getCraftClass("World");
			chatComponentTextClass = Reflections.getCraftClass("ChatComponentText");
			craftMagicNumbersClass = Reflections.getBukkitClass("util.CraftMagicNumbers");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	public SignGUIImpl() {
		registerUpdateHandling();
	}

	@Override
	public void open(Player player, String[] defaultText, SignGUIListener response) {
		try {
			List<Object> packets = new ArrayList<>();
			Location location = player.getLocation().clone();
			location.setY(0);

			Object blockPosition = blockPosition(location);

			if(defaultText != null) {
				packets.add(packetBlockChange(location, Material.SIGN_POST, 0));
				packets.add(packetSignChange(location, defaultText));
			}

			Object packetOpenSignEditor = packetOpenSignEditorClass.getConstructor(blockPositionClass).newInstance(blockPosition);
			packets.add(packetOpenSignEditor);

			if(defaultText != null) {
				packets.add(packetBlockChange(location, null, 0));
			}

			signLocations.put(player.getUniqueId(), location);
			listeners.put(player.getUniqueId(), response);
			PacketSender.sendPacket(player, packets.toArray());
		}
		catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			LoggerUtils.exception(e);
		}
	}

	protected void registerUpdateHandling() {
		new AbstractPacketHandler("PacketPlayInUpdateSign") {
			@Override
			public void handle(PacketReceiveEvent event) {
				try {
					Object packet = event.getPacket();

					Field blockPositionField = Reflections.getPrivateField(packetInUpdateSignClass, "a");
					Field linesField = Reflections.getPrivateField(packetInUpdateSignClass, "b");
					Field xField = Reflections.getPrivateField(baseBlockPositionClass, "a");
					Field yField = Reflections.getPrivateField(baseBlockPositionClass, "c");
					Field zField = Reflections.getPrivateField(baseBlockPositionClass, "d");

					final Player player = event.getPlayer();
					Location v = getSignLocations().remove(player.getUniqueId());

					if(v == null) {
						return;
					}

					Object blockPosition = blockPositionField.get(packet);
					int x = xField.getInt(blockPosition);
					int y = yField.getInt(blockPosition);
					int z = zField.getInt(blockPosition);

					if(x != v.getBlockX() || y != v.getBlockY() || z != v.getBlockZ()) {
						return;
					}

					Method getTextMethod = Reflections.getMethod(iChatBaseComponentClass, "getText");
					Object[] components = (Object[]) linesField.get(packet);

					int index = 0;
					final String[] lines = new String[4];
					for(Object component : components) {
						Object line = getTextMethod.invoke(component);
						lines[index] = (String) line;
						index++;
					}

					final SignGUIListener response = getListeners().remove(event.getPlayer().getUniqueId());

					if(response != null) {
						event.setCancelled(true);
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								response.onSignDone(player, lines);
							}
						});
					}
				}
				catch(IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
					LoggerUtils.exception(e);
				}
			}
		};
	}

	protected Object getData(Material material, int data) {
		try {
			Method getBlockMethod = Reflections.getMethod(craftMagicNumbersClass, "getBlock", Material.class);
			Object block = getBlockMethod.invoke(craftMagicNumbersClass, material);
			Method fromLegacyDataMethod = Reflections.getMethod(block.getClass(), "fromLegacyData");
			return fromLegacyDataMethod.invoke(block, data);
		}
		catch(IllegalAccessException | InvocationTargetException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	protected Object packetBlockChange(Location location, Material material, int data) {
		try {
			Object blockPosition = blockPosition(location);
			Object packet = packetBlockChangeClass.newInstance();
			Field blockPositionField = Reflections.getField(packetBlockChangeClass, "a");
			Field blockDataField = Reflections.getField(packetBlockChangeClass, "block");
			blockPositionField.setAccessible(true);
			blockDataField.setAccessible(true);

			Object blockData;
			if(material == null) {
				Object world = Reflections.getHandle(location.getWorld());
				Method getTypeMethod = Reflections.getMethod(worldClass, "getType");
				blockData = getTypeMethod.invoke(world, blockPosition);
			}
			else {
				blockData = getData(material, data);
			}

			blockPositionField.set(packet, blockPosition);
			blockDataField.set(packet, blockData);

			return packet;
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	protected Object packetSignChange(Location location, String[] lines) {
		try {
			Object blockPosition = blockPosition(location);
			Object packet = packetOutUpdateSignClass.newInstance();

			Field worldField = Reflections.getField(packetOutUpdateSignClass, "a");
			Field blockPositionField = Reflections.getField(packetOutUpdateSignClass, "b");
			Field linesField = Reflections.getField(packetOutUpdateSignClass, "c");

			worldField.setAccessible(true);
			blockPositionField.setAccessible(true);
			linesField.setAccessible(true);


			Constructor<?> chatComponentTextConstructor = chatComponentTextClass.getConstructor(String.class);

			worldField.set(packet, Reflections.getHandle(location.getWorld()));
			blockPositionField.set(packet, blockPosition);

			int n = 4;
			Object array = Array.newInstance(chatComponentTextClass, n);
			for(int i = 0; i < n; i++) {
				Object val = chatComponentTextConstructor.newInstance(lines[i]);

				Array.set(array, i, val);
			}

			linesField.set(packet, array);

			return packet;
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	protected Object blockPosition(Location location) {
		try {
			return blockPositionClass.getConstructor(
					int.class,
					int.class,
					int.class
			).newInstance(
					location.getBlockX(),
					location.getBlockY(),
					location.getBlockZ()
			);
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}
}
