package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.util.reflect.PacketSender;
import org.bukkit.entity.Player;

public class AbstractPacket {
	protected Object packet;

	public void send(Player player) {
		PacketSender.sendPacket(player, packet);
	}
}
