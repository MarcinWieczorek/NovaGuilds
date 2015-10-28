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
import co.marcin.novaguilds.command.*;
import co.marcin.novaguilds.command.admin.CommandAdmin;
import co.marcin.novaguilds.command.admin.CommandAdminReload;
import co.marcin.novaguilds.command.admin.CommandAdminSave;
import co.marcin.novaguilds.command.admin.guild.*;
import co.marcin.novaguilds.command.admin.hologram.*;
import co.marcin.novaguilds.command.admin.region.*;
import co.marcin.novaguilds.command.guild.*;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CommandManager {
	private final NovaGuilds plugin;
	private final HashMap<String,String> aliases = new HashMap<>();
	private final HashMap<ItemStack,String> guiCommands = new HashMap<>();
	private ItemStack topItem;
	private final HashMap<Commands, Executor> executors = new HashMap<>();

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
		plugin.getCommand("novaguilds").setExecutor(new CommandNovaGuilds(plugin));
		plugin.getCommand("ng").setExecutor(new CommandNovaGuilds(plugin));
		plugin.getCommand("nga").setExecutor(new CommandAdmin(plugin));

		plugin.getCommand("abandon").setExecutor(new CommandGuildAbandon(plugin));
		plugin.getCommand("guild").setExecutor(new CommandGuild(plugin));
		plugin.getCommand("gi").setExecutor(new CommandGuildInfo(plugin));
		plugin.getCommand("create").setExecutor(new CommandGuildCreate(plugin));
		plugin.getCommand("join").setExecutor(new CommandGuildJoin(plugin));
		plugin.getCommand("leave").setExecutor(new CommandGuildLeave(plugin));

		plugin.getCommand("invite").setExecutor(new CommandGuildInvite(plugin));
		plugin.getCommand("guildmenu").setExecutor(new CommandGuildMenu(plugin));

		plugin.getCommand("playerinfo").setExecutor(new CommandPlayerInfo(plugin));

		//register custom executors
		new CommandGuildBuyLife(Commands.GUILD_BUYLIFE);
		new CommandGuildBuySlot(Commands.GUILD_BUYSLOT);
		new CommandGuildCompass(Commands.GUILD_COMPASS);
		new CommandGuildAlly(Commands.GUILD_ALLY);
		new CommandGuildBankPay(Commands.GUILD_BANK_PAY);
		new CommandGuildBankWithdraw(Commands.GUILD_BANK_WITHDRAW);
		new CommandGuildEffect(Commands.GUILD_EFFECT);
		new CommandGuildHome(Commands.GUILD_HOME);
		new CommandGuildKick(Commands.GUILD_KICK);
		new CommandGuildLeader(Commands.GUILD_LEADER);
		new CommandGuildPvpToggle(Commands.GUILD_PVPTOGGLE);
		new CommandGuildTop(Commands.GUILD_TOP);
		new CommandGuildRequiredItems(Commands.GUILD_REQUIREDITEMS);
		new CommandGuildWar(Commands.GUILD_WAR);
		new CommandAdminGuildList(Commands.ADMIN_GUILD_LIST);
		new CommandGuildOpenInvitation(Commands.GUILD_OPENINVITATION);

		new CommandToolGet(Commands.TOOL_GET);

		//Admin
		new CommandAdminReload(Commands.ADMIN_RELOAD);
		new CommandAdminSave(Commands.ADMIN_SAVE);

		//AdminRegion
		new CommandAdminRegion(Commands.ADMIN_REGION_ACCESS);
		new CommandAdminRegionDelete(Commands.ADMIN_REGION_DELETE);
		new CommandAdminRegionList(Commands.ADMIN_REGION_LIST);
		new CommandAdminRegionTeleport(Commands.ADMIN_REGION_TELEPORT);

		//AdminGuilds
		new CommandAdminGuild(Commands.ADMIN_GUILD_ACCESS);
		new CommandAdminGuildAbandon(Commands.ADMIN_GUILD_ABANDON);
		new CommandAdminGuildBankPay(Commands.ADMIN_GUILD_BANK_PAY);
		new CommandAdminGuildBankWithdraw(Commands.ADMIN_GUILD_BANK_WITHDRAW);
		new CommandAdminGuildInactive(Commands.ADMIN_GUILD_INACTIVE);
		new CommandAdminGuildInvite(Commands.ADMIN_GUILD_INVITE);
		new CommandAdminGuildKick(Commands.ADMIN_GUILD_KICK);
		new CommandAdminGuildPurge(Commands.ADMIN_GUILD_PURGE);
		new CommandAdminGuildSetLeader(Commands.ADMIN_GUILD_SET_LEADER);
		new CommandAdminGuildSetLiveRegenerationTime(Commands.ADMIN_GUILD_SET_LIVEREGENERATIONTIME);
		new CommandAdminGuildSetLives(Commands.ADMIN_GUILD_SET_LIVES);
		new CommandAdminGuildSetName(Commands.ADMIN_GUILD_SET_NAME);
		new CommandAdminGuildSetPoints(Commands.ADMIN_GUILD_SET_POINTS);
		new CommandAdminGuildSetSlots(Commands.ADMIN_GUILD_SET_SLOTS);
		new CommandAdminGuildSetTag(Commands.ADMIN_GUILD_SET_TAG);
		new CommandAdminGuildSetTimerest(Commands.ADMIN_GUILD_SET_TIMEREST);
		new CommandAdminGuildTeleport(Commands.ADMIN_GUILD_TELEPORT);

		//AdminHologram
		new CommandAdminHologram(Commands.ADMIN_HOLOGRAM_ACCESS);
		new CommandAdminHologramList(Commands.ADMIN_HOLOGRAM_LIST);
		new CommandAdminHologramAddTop(Commands.ADMIN_HOLOGRAM_ADDTOP);
		new CommandAdminHologramDelete(Commands.ADMIN_HOLOGRAM_DELETE);
		new CommandAdminHologramTeleport(Commands.ADMIN_HOLOGRAM_TELEPORT);
		new CommandAdminHologramTeleportHere(Commands.ADMIN_HOLOGRAM_TELEPORT_HERE);

		//AdminRegion
		new CommandAdminRegionBypass(Commands.ADMIN_REGION_BYPASS);

		plugin.getCommand("nga").setTabCompleter(new TabCompleter() {
			@Override
			public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
				List<String> list = new ArrayList<>();
				Set<String> keys = null;

				if(args.length > 1) {
					switch(args[0].toLowerCase()) {
						case "g":
						case "guild":
							keys = CommandAdminGuild.commandsMap.keySet();
							break;

						case "rg":
						case "region":
							keys = CommandAdminRegion.commandsMap.keySet();
							break;

						case "h":
						case "hologram":
							keys = CommandAdminHologram.commandsMap.keySet();
							break;
					}
				}
				else {
					keys = CommandAdmin.commandsMap.keySet();
				}

				if(keys != null) {
					for(String key : keys) {
						if(key.startsWith(args[args.length - 1])) {
							list.add(key);
						}
					}
				}

				return list;
			}
		});
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
			LoggerUtils.debug(key);
			String gcmd = key.replaceAll("_", " ");
			LoggerUtils.debug(gcmd);
			ItemStack is = ItemStackUtils.stringToItemStack(sectionGUI.getString(key));
			if(is != null) LoggerUtils.debug(is.toString());

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

	public void registerExecutor(Commands command, Executor executor) {
		executors.put(command, executor);
	}

	public Executor getExecutor(Commands command) {
		return executors.get(command);
	}
}
