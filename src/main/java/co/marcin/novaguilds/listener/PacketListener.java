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

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.event.PacketReceiveEvent;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class PacketListener extends AbstractListener {
	private static final Map<String, PacketExtension.PacketHandler> packetHandlers = new HashMap<>();

	/**
	 * Registers a handler
	 *
	 * @param packetHandler the handler
	 */
	public static void register(PacketExtension.PacketHandler packetHandler) {
		if(!Config.PACKETS_ENABLED.getBoolean()) {
			LoggerUtils.info("Could not register " + packetHandler.getPacketName() + " handler. Packets are disabled.");
			return;
		}

		PacketExtension.PacketHandler existingHandler = getHandler(packetHandler.getPacketName());
		if(existingHandler != null && existingHandler.getPriority().getSlot() >= packetHandler.getPriority().getSlot()) {
			return;
		}

		packetHandlers.put(packetHandler.getPacketName(), packetHandler);
	}

	/**
	 * Unregisters a handler
	 *
	 * @param packetHandler the handler
	 */
	public static void unregister(PacketExtension.PacketHandler packetHandler) {
		if(packetHandlers.containsKey(packetHandler.getPacketName())) {
			packetHandlers.remove(packetHandler.getPacketName());
		}
	}

	/**
	 * Gets a handler
	 *
	 * @param packetName packet name
	 * @return the handler
	 */
	public static PacketExtension.PacketHandler getHandler(String packetName) {
		return packetHandlers.get(packetName);
	}

	@EventHandler
	public void onPacketReceive(PacketReceiveEvent event) {
		PacketExtension.PacketHandler packetHandler = getHandler(event.getPacketName());

		if(packetHandler == null) {
			return;
		}

		packetHandler.handle(event);
	}

	public Map<String, PacketExtension.PacketHandler> getPacketHandlers() {
		return packetHandlers;
	}
}
