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
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * NovaGroup - a configurable group for players
 *
 * @author Marcin Wieczorek
 */
public class NovaGroupImpl implements NovaGroup {

	private final String name;
	private double guildCreateMoney = 0;
	private final List<ItemStack> guildCreateItems = new ArrayList<>();

	private final List<ItemStack> guildHomeItems = new ArrayList<>();
	private double guildHomeMoney = 0;

	private final List<ItemStack> guildJoinItems = new ArrayList<>();
	private double guildJoinMoney;

	private final List<ItemStack> guildEffectItems = new ArrayList<>();
	private double guildEffectPrice = 0;

	private int guildTeleportDelay = 0;

	private final List<ItemStack> guildBuylifeItems = new ArrayList<>();
	private double guildBuylifeMoney = 0;

	private final List<ItemStack> guildBuySlotItems = new ArrayList<>();
	private double guildBuySlotMoney = 0;

	private double regionPricePerBlock = 0;
	private double regionCreateMoney = 0;
	private int regionAutoSize = 0;

	@SuppressWarnings("deprecation")
	public NovaGroupImpl(NovaGuilds plugin, String group) {
		name = group;
		LoggerUtils.info("Loading group '" + name + "'...");

		if(name.equalsIgnoreCase("admin")) {
			regionAutoSize = Config.REGION_ADMINAUTOSIZE.getInt();
			return;
		}

		//setting all values
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("groups." + group);
		guildCreateItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.create.items")));
		guildCreateMoney = section.getDouble("guild.create.money");

		guildTeleportDelay = section.getInt("guild.home.tpdelay");

		guildHomeItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.home.items")));
		guildJoinItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.join.items")));

		regionPricePerBlock = section.getDouble("region.ppb");
		regionCreateMoney = section.getDouble("region.createmoney");
		regionAutoSize = section.getInt("region.autoregionsize");

		guildHomeMoney = section.getDouble("guild.home.money");
		guildJoinMoney = section.getDouble("guild.join.money");

		guildEffectItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.effect.items")));
		guildEffectPrice = section.getDouble("guild.effect.money");

		guildBuylifeItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.buylife.items")));
		guildBuylifeMoney = section.getDouble("guild.buylife.money");

		guildBuySlotItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.buyslot.items")));
		guildBuySlotMoney = section.getDouble("guild.buyslot.money");
	}

	public String getName() {
		return name;
	}

	public int getGuildTeleportDelay() {
		return guildTeleportDelay;
	}

	public int getRegionAutoSize() {
		return regionAutoSize;
	}

	public double getGuildCreateMoney() {
		return guildCreateMoney;
	}

	public double getRegionPricePerBlock() {
		return regionPricePerBlock;
	}

	public double getRegionCreateMoney() {
		return regionCreateMoney;
	}

	public List<ItemStack> getGuildCreateItems() {
		return guildCreateItems;
	}

	public double getGuildHomeMoney() {
		return guildHomeMoney;
	}

	public List<ItemStack> getGuildHomeItems() {
		return guildHomeItems;
	}

	public double getGuildJoinMoney() {
		return guildJoinMoney;
	}

	public List<ItemStack> getGuildJoinItems() {
		return guildJoinItems;
	}

	public double getGuildBuylifeMoney() {
		return guildBuylifeMoney;
	}

	public List<ItemStack> getGuildBuylifeItems() {
		return guildBuylifeItems;
	}

	public List<ItemStack> getGuildBuySlotItems() {
		return guildBuySlotItems;
	}

	public double getGuildBuySlotMoney() {
		return guildBuySlotMoney;
	}

	public List<ItemStack> getGuildEffectItems() {
		return guildEffectItems;
	}

	public double getGuildEffectPrice() {
		return guildEffectPrice;
	}
}
