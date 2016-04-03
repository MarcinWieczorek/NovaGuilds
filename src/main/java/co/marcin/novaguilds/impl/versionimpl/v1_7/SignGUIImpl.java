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

package co.marcin.novaguilds.impl.versionimpl.v1_7;

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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SignGUIImpl extends AbstractSignGui {
	protected final Class<?> packetInUpdateSignClass = Reflections.getCraftClass("PacketPlayInUpdateSign");
	protected final Class<?> packetOutUpdateSignClass = Reflections.getCraftClass("PacketPlayOutUpdateSign");
	protected final Class<?> packetBlockChangeClass = Reflections.getCraftClass("PacketPlayOutBlockChange");
	protected final Class<?> packetOpenSignEditorClass = Reflections.getCraftClass("PacketPlayOutOpenSignEditor");
	protected final Class<?> blockClass = Reflections.getCraftClass("Block");
	protected final Class<?> worldClass = Reflections.getCraftClass("World");

	public SignGUIImpl() {
		registerUpdateHandling();
	}

	protected void registerUpdateHandling() {
		new AbstractPacketHandler("PacketPlayInUpdateSign") {
			@Override
			public void handle(PacketReceiveEvent event) {
				Object packet = event.getPacket();

				Reflections.FieldAccessor<String[]> linesField = Reflections.getField(packetInUpdateSignClass, String[].class, 0);
				Reflections.FieldAccessor<Integer> xField = Reflections.getField(packetInUpdateSignClass, int.class, 0);
				Reflections.FieldAccessor<Integer> yField = Reflections.getField(packetInUpdateSignClass, int.class, 1);
				Reflections.FieldAccessor<Integer> zField = Reflections.getField(packetInUpdateSignClass, int.class, 2);


				final Player player = event.getPlayer();
				Location v = plugin.getSignGUI().getSignLocations().remove(player.getUniqueId());

				if(v == null) {
					return;
				}

				int x = xField.get(packet);
				int y = yField.get(packet);
				int z = zField.get(packet);

				if(x != v.getBlockX() || y != v.getBlockY() || z != v.getBlockZ()) {
					return;
				}

				final String[] lines = linesField.get(packet);
				final SignGUIListener response = plugin.getSignGUI().getListeners().remove(event.getPlayer().getUniqueId());

				if(response != null) {
					event.setCancelled(true);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							response.onSignDone(player, lines);
						}
					});
				}
			}
		};
	}

	@Override
	public void open(Player player, String[] defaultText, SignGUIListener response) {
		List<Object> packets = new ArrayList<>();
		Location location = player.getLocation().clone();
		location.setY(0);

		for(int i = 0; i < 4; i++) {
			if(defaultText[i].length() > 15) {
				defaultText[i] = defaultText[i].substring(0, 15);
			}
		}

		if(defaultText != null) {
			packets.add(packetBlockChange(location, Material.SIGN_POST, 0));
			packets.add(packetSignChange(location, defaultText));
		}

		packets.add(packetOpenSignEditor(location));

		if(defaultText != null) {
			packets.add(packetBlockChange(location, null, 0));
		}

		signLocations.put(player.getUniqueId(), location);
		listeners.put(player.getUniqueId(), response);
		PacketSender.sendPacket(player, packets.toArray());
	}

	@SuppressWarnings("deprecation")
	protected Object packetBlockChange(Location location, Material material, int data) {
		try {
			Object packet = packetBlockChangeClass.newInstance();
			Field aField = Reflections.getPrivateField(packetBlockChangeClass, "a");
			Field bField = Reflections.getPrivateField(packetBlockChangeClass, "b");
			Field cField = Reflections.getPrivateField(packetBlockChangeClass, "c");
			Field blockField = Reflections.getPrivateField(packetBlockChangeClass, "block");
			Field dataField = Reflections.getPrivateField(packetBlockChangeClass, "data");

			aField.set(packet, location.getBlockX());
			bField.set(packet, location.getBlockY());
			cField.set(packet, location.getBlockZ());

			Object block;
			if(material == null) {
				Method getBlockAtMethod = Reflections.getMethod(worldClass, "getType", int.class, int.class, int.class);
				block = getBlockAtMethod.invoke(Reflections.getHandle(location.getWorld()), location.getBlockX(), location.getBlockY(), location.getBlockZ());
			}
			else {
				Object id = material.getId();
				Method getByIdMethod = Reflections.getMethod(blockClass, "getById");
				block = getByIdMethod.invoke(null, id);
			}

			blockField.set(packet, block);

			dataField.set(packet, data);

			return packet;
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	protected Object packetSignChange(Location location, String[] lines) {
		try {
			Object packet = packetOutUpdateSignClass.newInstance();

			Field xField = Reflections.getPrivateField(packetOutUpdateSignClass, "x");
			Field yField = Reflections.getPrivateField(packetOutUpdateSignClass, "y");
			Field zField = Reflections.getPrivateField(packetOutUpdateSignClass, "z");
			Field linesField = Reflections.getPrivateField(packetOutUpdateSignClass, "lines");

			xField.set(packet, location.getBlockX());
			yField.set(packet, location.getBlockY());
			zField.set(packet, location.getBlockZ());
			linesField.set(packet, lines);

			return packet;
		}
		catch(InstantiationException | IllegalAccessException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	protected Object packetOpenSignEditor(Location location) {
		try {
			return packetOpenSignEditorClass.getConstructor(
					int.class,
					int.class,
					int.class
			).newInstance(
					location.getBlockX(),
					location.getBlockY(),
					location.getBlockZ()
			);
		}
		catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			LoggerUtils.exception(e);
			return null;
		}

	}
}
