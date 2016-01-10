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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.command.CommandConfirm;
import co.marcin.novaguilds.command.CommandNovaGuilds;
import co.marcin.novaguilds.command.CommandPlayerInfo;
import co.marcin.novaguilds.command.CommandToolGet;
import co.marcin.novaguilds.command.admin.CommandAdmin;
import co.marcin.novaguilds.command.admin.CommandAdminReload;
import co.marcin.novaguilds.command.admin.CommandAdminSave;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuild;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildAbandon;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildBankPay;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildBankWithdraw;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildInactive;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildInvite;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildKick;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildList;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildPurge;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetLeader;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetLiveRegenerationTime;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetLives;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetName;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetPoints;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetSlots;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetTag;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildSetTimerest;
import co.marcin.novaguilds.command.admin.guild.CommandAdminGuildTeleport;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologram;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramAddTop;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramDelete;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramList;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramTeleport;
import co.marcin.novaguilds.command.admin.hologram.CommandAdminHologramTeleportHere;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegion;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionBypass;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionDelete;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionList;
import co.marcin.novaguilds.command.admin.region.CommandAdminRegionTeleport;
import co.marcin.novaguilds.command.guild.CommandGuild;
import co.marcin.novaguilds.command.guild.CommandGuildAbandon;
import co.marcin.novaguilds.command.guild.CommandGuildAlly;
import co.marcin.novaguilds.command.guild.CommandGuildBankPay;
import co.marcin.novaguilds.command.guild.CommandGuildBankWithdraw;
import co.marcin.novaguilds.command.guild.CommandGuildBuyLife;
import co.marcin.novaguilds.command.guild.CommandGuildBuySlot;
import co.marcin.novaguilds.command.guild.CommandGuildChatMode;
import co.marcin.novaguilds.command.guild.CommandGuildCompass;
import co.marcin.novaguilds.command.guild.CommandGuildCreate;
import co.marcin.novaguilds.command.guild.CommandGuildEffect;
import co.marcin.novaguilds.command.guild.CommandGuildHome;
import co.marcin.novaguilds.command.guild.CommandGuildInfo;
import co.marcin.novaguilds.command.guild.CommandGuildInvite;
import co.marcin.novaguilds.command.guild.CommandGuildJoin;
import co.marcin.novaguilds.command.guild.CommandGuildKick;
import co.marcin.novaguilds.command.guild.CommandGuildLeader;
import co.marcin.novaguilds.command.guild.CommandGuildLeave;
import co.marcin.novaguilds.command.guild.CommandGuildMenu;
import co.marcin.novaguilds.command.guild.CommandGuildOpenInvitation;
import co.marcin.novaguilds.command.guild.CommandGuildPvpToggle;
import co.marcin.novaguilds.command.guild.CommandGuildRequiredItems;
import co.marcin.novaguilds.command.guild.CommandGuildTop;
import co.marcin.novaguilds.command.guild.CommandGuildWar;
import co.marcin.novaguilds.command.region.CommandRegion;
import co.marcin.novaguilds.command.region.CommandRegionBuy;
import co.marcin.novaguilds.command.region.CommandRegionDelete;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandManager {
	private final NovaGuilds plugin;
	private final Map<String,String> aliases = new HashMap<>();
	private final Map<ItemStack,String> guiCommands = new HashMap<>();
	private final Map<Command, Executor> executors = new HashMap<>();
	private ItemStack topItem;

	public CommandManager(NovaGuilds plugin) {
		this.plugin = plugin;
		plugin.setCommandManager(this);
		registerCommands();

		ConfigurationSection section = plugin.getConfig().getConfigurationSection("aliases");

		for(String key : section.getKeys(false)) {
			for(String alias : section.getStringList(key)) {
				aliases.put(alias,key);
			}
		}

		setupGuildMenu();

		LoggerUtils.info("Enabled");
	}

	public String getMainCommand(String alias) {
		return aliases.get(alias);
	}

	public boolean existsAlias(String alias) {
		return aliases.containsKey(alias);
	}

	private void registerCommands() {
		//register custom executors
		new CommandGuild();
		new CommandGuildAbandon();
		new CommandGuildBuyLife();
		new CommandGuildBuySlot();
		new CommandGuildChatMode();
		new CommandGuildCompass();
		new CommandGuildCreate();
		new CommandGuildAlly();
		new CommandGuildBankPay();
		new CommandGuildBankWithdraw();
		new CommandGuildEffect();
		new CommandGuildHome();
		new CommandGuildInfo();
		new CommandGuildInvite();
		new CommandGuildJoin();
		new CommandGuildKick();
		new CommandGuildLeader();
		new CommandGuildLeave();
		new CommandGuildMenu();
		new CommandGuildPvpToggle();
		new CommandGuildTop();
		new CommandGuildRequiredItems();
		new CommandGuildWar();
		new CommandGuildOpenInvitation();

		new CommandRegion();
		new CommandRegionDelete();
		new CommandRegionBuy();

		new CommandNovaGuilds();
		new CommandPlayerInfo();
		new CommandConfirm();
		new CommandToolGet();

		//Admin
		new CommandAdmin();
		new CommandAdminReload();
		new CommandAdminSave();

		//AdminRegion
		new CommandAdminRegion();
		new CommandAdminRegionDelete();
		new CommandAdminRegionList();
		new CommandAdminRegionTeleport();
		new CommandAdminRegionBypass();

		//AdminGuilds
		new CommandAdminGuild();
		new CommandAdminGuildList();
		new CommandAdminGuildAbandon();
		new CommandAdminGuildBankPay();
		new CommandAdminGuildBankWithdraw();
		new CommandAdminGuildInactive();
		new CommandAdminGuildInvite();
		new CommandAdminGuildKick();
		new CommandAdminGuildPurge();
		new CommandAdminGuildSetLeader();
		new CommandAdminGuildSetLiveRegenerationTime();
		new CommandAdminGuildSetLives();
		new CommandAdminGuildSetName();
		new CommandAdminGuildSetPoints();
		new CommandAdminGuildSetSlots();
		new CommandAdminGuildSetTag();
		new CommandAdminGuildSetTimerest();
		new CommandAdminGuildTeleport();

		//AdminHologram
		new CommandAdminHologram();
		new CommandAdminHologramList();
		new CommandAdminHologramAddTop();
		new CommandAdminHologramDelete();
		new CommandAdminHologramTeleport();
		new CommandAdminHologramTeleportHere();
	}

	public String getGuiCommand(ItemStack itemStack) {
		return guiCommands.get(itemStack);
	}

	public Set<ItemStack> getGuiItems() {
		return guiCommands.keySet();
	}

	public void updateGuiTop() {
		guiCommands.remove(topItem);

		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(topItem.getType());
		meta.setDisplayName(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		meta.setLore(plugin.getGuildManager().getTopGuilds());
		topItem.setItemMeta(meta);
		guiCommands.put(topItem, "g top");
	}

	public void setupGuildMenu() {
		guiCommands.clear();
		ConfigurationSection sectionGUI = plugin.getConfig().getConfigurationSection("gguicmd");

		for(String key : sectionGUI.getKeys(false)) {
			String gcmd = key.replaceAll("_", " ");
			ItemStack is = ItemStackUtils.stringToItemStack(sectionGUI.getString(key));

			if(is != null) {
				if(key.equalsIgnoreCase("top")) {
					topItem = is;
				}
				else {
					guiCommands.put(is, gcmd);
				}
			}
		}
	}

	public void registerExecutor(Command command, Executor executor) {
		if(!executors.containsKey(command)) {
			executors.put(command, executor);

			if(command.hasGenericCommand()) {
				if(!(executor instanceof CommandExecutor)) {
					throw new IllegalArgumentException("An executor has to implement CommandExecutor to allow having generic command.");
				}

				PluginCommand genericCommand = plugin.getCommand(command.getGenericCommand());
				genericCommand.setExecutor((CommandExecutor) executor);

				if(command.hasTabCompleter()) {
					genericCommand.setTabCompleter(command.getTabCompleter());
				}
			}
		}
	}

	public Executor getExecutor(Command command) {
		return executors.get(command);
	}
}
