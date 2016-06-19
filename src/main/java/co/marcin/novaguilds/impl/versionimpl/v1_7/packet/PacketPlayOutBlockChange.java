package co.marcin.novaguilds.impl.versionimpl.v1_7.packet;

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import org.bukkit.Location;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketPlayOutBlockChange extends AbstractPacket {
	protected static Class<?> packetPlayOutBlockChangeClass;
	protected static Class<?> blockClass;
	protected static Class<?> worldClass;
	protected static Field xField;
	protected static Field yField;
	protected static Field zField;
	protected static Field blockField;
	protected static Field dataField;
	protected static Method getBlockAtMethod;
	protected static Method getByIdMethod;

	static {
		try {
			packetPlayOutBlockChangeClass = Reflections.getCraftClass("PacketPlayOutBlockChange");
			blockClass = Reflections.getCraftClass("Block");
			worldClass = Reflections.getCraftClass("World");

			xField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "a");
			yField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "b");
			zField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "c");
			blockField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "block");
			dataField = Reflections.getPrivateField(packetPlayOutBlockChangeClass, "data");

			getBlockAtMethod = Reflections.getMethod(worldClass, "getType", int.class, int.class, int.class);
			getByIdMethod = Reflections.getMethod(blockClass, "getById");
		}
		catch(NoSuchFieldException | ClassNotFoundException e) {
			LoggerUtils.exception(e);
		}
	}

	public PacketPlayOutBlockChange(Location location, Material material, int data) throws IllegalAccessException, InstantiationException, InvocationTargetException {
		packet = packetPlayOutBlockChangeClass.newInstance();

		xField.set(packet, location.getBlockX());
		yField.set(packet, location.getBlockY());
		zField.set(packet, location.getBlockZ());

		Object block;
		if(material == null) {
			block = getBlockAtMethod.invoke(
					Reflections.getHandle(location.getWorld()),
					location.getBlockX(),
					location.getBlockY(),
					location.getBlockZ()
			);
		}
		else {
			Object id = material.getId();
			block = getByIdMethod.invoke(null, id);
		}

		blockField.set(packet, block);
		dataField.set(packet, data);
	}
}
