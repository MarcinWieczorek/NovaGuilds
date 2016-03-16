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

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

public class Meta {
	private static NovaGuilds plugin = NovaGuilds.getInstance();

	public static void setMetadata(Metadatable obj, String key, Object value) {
		obj.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	public static MetadataValue getMetadata(Metadatable obj, String key) {
		for(MetadataValue value : obj.getMetadata(key)) {
			if(value.getOwningPlugin().getDescription().getName().equals(plugin.getDescription().getName())) {
				return value;
			}
		}
		return null;
	}

	public static void removeMetadata(Metadatable obj, String key) {
		obj.removeMetadata(key, plugin);
	}

	public static void protect(Block block) {
		setMetadata(block, "protected", true);
	}

	public static void unprotect(Block block) {
		removeMetadata(block, "protected");
	}

	public static boolean isProtected(Block block) {
		return getMetadata(block, "protected") != null && getMetadata(block, "protected").asBoolean();
	}
}
