package co.marcin.novaguilds.impl.versionimpl.v1_7.packet;

import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;

public class PacketPlayInUpdateSign extends AbstractPacket {
	protected static Class<?> packetInUpdateSignClass;
	protected static Reflections.FieldAccessor<String[]> linesField;
	protected static Reflections.FieldAccessor<Integer> xField;
	protected static Reflections.FieldAccessor<Integer> yField;
	protected static Reflections.FieldAccessor<Integer> zField;

	private final String[] lines;

	static {
		try {
			packetInUpdateSignClass = Reflections.getCraftClass("PacketPlayInUpdateSign");
			linesField = Reflections.getField(packetInUpdateSignClass, String[].class, 0);
			xField = Reflections.getField(packetInUpdateSignClass, int.class, 0);
			yField = Reflections.getField(packetInUpdateSignClass, int.class, 1);
			zField = Reflections.getField(packetInUpdateSignClass, int.class, 2);
		}
		catch(ClassNotFoundException e) {
			LoggerUtils.exception(e);
		}
	}

	private final int x;
	private final int y;
	private final int z;

	public PacketPlayInUpdateSign(Object packet) {
		x = xField.get(packet);
		y = yField.get(packet);
		z = zField.get(packet);
		lines = linesField.get(packet);
	}

	/**
	 * Gets x coordinate
	 *
	 * @return integer
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets y coordinate
	 *
	 * @return integer
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets z coordinate
	 *
	 * @return integer
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets sign lines
	 *
	 * @return array of 4 strings
	 */
	public String[] getLines() {
		return lines;
	}
}
