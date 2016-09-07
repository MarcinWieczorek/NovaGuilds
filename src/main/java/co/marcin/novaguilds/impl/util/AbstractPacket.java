package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.api.util.Packet;
import co.marcin.novaguilds.util.reflect.PacketSender;
import org.bukkit.entity.Player;

public class AbstractPacket implements Packet {
	protected Object packet;

	@Override
	public void send(Player player) {
		PacketSender.sendPacket(player, packet);
	}

	@Override
	public Object getPacket() {
		return packet;
	}
}
