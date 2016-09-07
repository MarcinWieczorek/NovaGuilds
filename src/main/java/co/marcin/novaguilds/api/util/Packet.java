package co.marcin.novaguilds.api.util;

import org.bukkit.entity.Player;

public interface Packet {
	/**
	 * Sends NMS packet to a player
	 *
	 * @param player target player
	 */
	void send(Player player);

	/**
	 * Gets the NMS packet
	 *
	 * @return NMS packet instance
	 */
	Object getPacket();
}
