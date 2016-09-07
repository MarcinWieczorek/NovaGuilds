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

package co.marcin.novaguilds.impl.versionimpl.v1_9_R2.packet;

import co.marcin.novaguilds.api.util.BlockPositionWrapper;
import co.marcin.novaguilds.impl.util.AbstractPacket;
import co.marcin.novaguilds.impl.versionimpl.v1_9_R2.BlockPositionWrapperImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.reflect.Reflections;

import java.lang.reflect.Field;

public class PacketPlayInUpdateSign extends AbstractPacket {
	protected static Class<?> packetInUpdateSignClass;
	protected static Field blockPositionField;
	protected static Reflections.FieldAccessor<String[]> linesField;

	private String[] lines;
	private int x;
	private int y;
	private int z;

	static {
		try {
			packetInUpdateSignClass = Reflections.getCraftClass("PacketPlayInUpdateSign");
			blockPositionField = Reflections.getPrivateField(packetInUpdateSignClass, "a");
			linesField = Reflections.getField(packetInUpdateSignClass, String[].class, 0);
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Converts NMS packet
	 *
	 * @param packet NMS PacketPlayInUpdateSign object
	 * @throws IllegalAccessException when something goes wrong
	 * @throws NoSuchFieldException   when something goes wrong
	 */
	public PacketPlayInUpdateSign(Object packet) throws IllegalAccessException, NoSuchFieldException {
		BlockPositionWrapper blockPosition = new BlockPositionWrapperImpl(blockPositionField.get(packet));

		x = blockPosition.getX();
		y = blockPosition.getY();
		z = blockPosition.getZ();

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
