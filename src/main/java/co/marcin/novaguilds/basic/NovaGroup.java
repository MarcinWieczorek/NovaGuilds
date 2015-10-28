/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NovaGroup {

	private final String name;
	private double guildCreateMoney = 0;
	private final List<ItemStack> guildCreateItems = new ArrayList<>();

	private final List<ItemStack> guildHomeItems = new ArrayList<>();
	private double guildHomeMoney = 0;

	private final List<ItemStack> guildJoinItems = new ArrayList<>();
	private double guildJoinMoney;

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
	public NovaGroup(NovaGuilds plugin, String group) {
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
		guildEffectPrice = section.getDouble("effectprice");
		regionAutoSize = section.getInt("region.autoregionsize");

		guildHomeMoney = section.getDouble("guild.home.money");
		guildJoinMoney = section.getDouble("guild.join.money");

		guildBuylifeItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.buylife.items")));
		guildBuylifeMoney = section.getDouble("guild.buylife.money");

		guildBuySlotItems.addAll(ItemStackUtils.stringToItemStackList(section.getStringList("guild.buyslot.items")));
		guildBuySlotMoney = section.getDouble("guild.buyslot.money");
	}

	public static NovaGroup get(CommandSender sender) {
		return NovaGuilds.getInstance().getGroupManager().getGroup(sender);
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

	public double getGuildEffectPrice() {
		return guildEffectPrice;
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

	//guild home
	public double getGuildHomeMoney() {
		return guildHomeMoney;
	}

	public List<ItemStack> getGuildHomeItems() {
		return guildHomeItems;
	}

	//guild join
	public double getGuildJoinMoney() {
		return guildJoinMoney;
	}

	public List<ItemStack> getGuildJoinItems() {
		return guildJoinItems;
	}

	//buylife
	public double getGuildBuylifeMoney() {
		return guildBuylifeMoney;
	}

	public List<ItemStack> getGuildBuylifeItems() {
		return guildBuylifeItems;
	}

	//buySlot
	public List<ItemStack> getGuildBuySlotItems() {
		return guildBuySlotItems;
	}

	public double getGuildBuySlotMoney() {
		return guildBuySlotMoney;
	}
}
