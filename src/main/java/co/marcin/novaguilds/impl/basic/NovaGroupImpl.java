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

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.util.Schematic;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.util.SchematicImpl;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NovaGroup - a configurable group for players
 *
 * @author Marcin Wieczorek
 */
public class NovaGroupImpl implements NovaGroup {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();

	private final Map<Key, Object> values = new HashMap<>();

	private static final Map<Key, String> paths = new HashMap<Key, String>() {{
		put(Key.CREATE_MONEY, "guild.create.money");
		put(Key.CREATE_ITEMS, "guild.create.items");
		put(Key.HOME_DELAY, "guild.home.tpdelay");
		put(Key.HOME_MONEY, "guild.home.money");
		put(Key.HOME_ITEMS, "guild.home.items");
		put(Key.JOIN_MONEY, "guild.join.money");
		put(Key.JOIN_ITEMS, "guild.join.items");
		put(Key.EFFECT_MONEY, "guild.effect.money");
		put(Key.EFFECT_ITEMS, "guild.effect.items");
		put(Key.BUY_LIFE_MONEY, "guild.buylife.money");
		put(Key.BUY_LIFE_ITEMS, "guild.buylife.items");
		put(Key.BUY_SLOT_MONEY, "guild.buyslot.money");
		put(Key.BUY_SLOT_ITEMS, "guild.buyslot.items");
		put(Key.BUY_BANNER_MONEY, "guild.banner.money");
		put(Key.BUY_BANNER_ITEMS, "guild.banner.items");
		put(Key.REGION_CREATE_MONEY, "region.createmoney");
		put(Key.REGION_PRICEPERBLOCK, "region.ppb");
		put(Key.REGION_AUTOSIZE, "region.autoregionsize");
	}};

	private final String name;
	private Schematic schematic;

	/**
	 * The constructor
	 *
	 * @param group group name
	 */
	public NovaGroupImpl(String group) {
		name = group;
		LoggerUtils.info("Loading group '" + name + "'...");

		//setting all values
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("groups." + group);

		for(Key key : Key.values()) {
			if(!section.contains(paths.get(key)) && values.get(key) != null) {
				continue;
			}

			Object value = null;

			switch(key.getType()) {
				case DOUBLE:
					value = section.getDouble(paths.get(key));
					break;
				case INTEGER:
					value = section.getInt(paths.get(key));
					break;
				case ITEMSTACKLIST:
					value = ItemStackUtils.stringToItemStackList(section.getStringList(paths.get(key)));

					if(value == null) {
						value = new ArrayList<ItemStack>();
					}
					break;
			}

			values.put(key, value);
		}

		//Schematic
		String schematicName = section.getString("guild.create.schematic");
		if(schematicName != null && !schematicName.isEmpty()) {
			try {
				schematic = new SchematicImpl(schematicName);
			}
			catch(FileNotFoundException e) {
				LoggerUtils.error("Schematic not found: schematic/" + schematicName);
			}
		}

		int autoRegionWidth = getInt(Key.REGION_AUTOSIZE) * 2 + 1;
		if(autoRegionWidth > Config.REGION_MAXSIZE.getInt()) {
			values.put(Key.REGION_AUTOSIZE, Config.REGION_MAXSIZE.getInt() / 2 - 1);
			LoggerUtils.error("Group " + name + " has too big autoregion. Reset to " + getInt(Key.REGION_AUTOSIZE));
		}

		if(autoRegionWidth < Config.REGION_MINSIZE.getInt()) {
			values.put(Key.REGION_AUTOSIZE, Config.REGION_MINSIZE.getInt() / 2);
			LoggerUtils.error("Group " + name + " has too small autoregion. Reset to " + getInt(Key.REGION_AUTOSIZE));
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Schematic getCreateSchematic() {
		return schematic;
	}

	public Object get(Key key) {
		return values.get(key);
	}

	@Override
	public double getDouble(Key key) {
		return (double) get(key);
	}

	@Override
	public int getInt(Key key) {
		return (int) get(key);
	}

	@Override
	public List<ItemStack> getItemStackList(Key key) {
		return (List<ItemStack>) get(key);
	}
}
