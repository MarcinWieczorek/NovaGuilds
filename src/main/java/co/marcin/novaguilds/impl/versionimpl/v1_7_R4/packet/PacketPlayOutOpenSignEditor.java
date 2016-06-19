package co.marcin.novaguilds.impl.versionimpl.v1_7_R4.packet;

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PacketPlayOutOpenSignEditor extends AbstractPacket {
	protected static Class<?> packetOpenSignEditorClass;
	protected static Constructor<?> packetOpenSignEditorConstructor;

	static {
		try {
			packetOpenSignEditorClass = Reflections.getCraftClass("PacketPlayOutOpenSignEditor");
			packetOpenSignEditorConstructor = packetOpenSignEditorClass.getConstructor(
					int.class,
					int.class,
					int.class
			);
		}
		catch(NoSuchMethodException | ClassNotFoundException e) {
			LoggerUtils.exception(e);
		}
	}

	public PacketPlayOutOpenSignEditor(Location location) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		packet = packetOpenSignEditorConstructor.newInstance(
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ()
		);
	}
}
