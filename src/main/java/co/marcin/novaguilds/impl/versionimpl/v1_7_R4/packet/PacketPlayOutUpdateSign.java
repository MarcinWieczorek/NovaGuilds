package co.marcin.novaguilds.impl.versionimpl.v1_7_R4.packet;

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;

import java.lang.reflect.Field;

public class PacketPlayOutUpdateSign extends AbstractPacket {
	protected static Class<?> packetOutUpdateSignClass;
	protected static Field xField;
	protected static Field yField;
	protected static Field zField;
	protected static Field linesField;

	static {
		try {
			packetOutUpdateSignClass = Reflections.getCraftClass("PacketPlayOutUpdateSign");

			xField = Reflections.getPrivateField(packetOutUpdateSignClass, "x");
			yField = Reflections.getPrivateField(packetOutUpdateSignClass, "y");
			zField = Reflections.getPrivateField(packetOutUpdateSignClass, "z");
			linesField = Reflections.getPrivateField(packetOutUpdateSignClass, "lines");
		}
		catch(ClassNotFoundException | NoSuchFieldException e) {
			LoggerUtils.exception(e);
		}
	}

	public PacketPlayOutUpdateSign(Location location, String[] lines) throws IllegalAccessException, InstantiationException {
		packet = packetOutUpdateSignClass.newInstance();
		xField.set(packet, location.getBlockX());
		yField.set(packet, location.getBlockY());
		zField.set(packet, location.getBlockZ());
		linesField.set(packet, lines);
	}
}
