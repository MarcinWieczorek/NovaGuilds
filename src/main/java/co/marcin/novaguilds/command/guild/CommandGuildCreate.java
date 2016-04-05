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

package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.Schematic;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.event.GuildCreateEvent;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.ParticleUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandGuildCreate extends AbstractCommandExecutor implements CommandExecutor {
	private static final Command command = Command.GUILD_CREATE;

	public CommandGuildCreate() {
		super(command);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length != 2) {
			Message.CHAT_USAGE_GUILD_CREATE.send(sender);
			return;
		}

		Player player = (Player) sender;

		String tag = args[0];
		String guildName = args[1];

		//remove colors
		guildName = StringUtils.removeColors(guildName);
		if(!Config.GUILD_SETTINGS_TAG_COLOR.getBoolean()) {
			tag = StringUtils.removeColors(tag);
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
		Map<VarKey, String> vars = new HashMap<>();

		if(nPlayer.hasGuild()) { //has guild already
			Message.CHAT_CREATEGUILD_HASGUILD.send(sender);
			return;
		}

		//Check name and tag validity
		Message validName = validName(guildName);
		Message validTag = validTag(tag);

		if(validName != null) {
			validName.send(sender);
			return;
		}

		if(validTag != null) {
			validTag.send(sender);
			return;
		}


		if(RegionManager.get(player) != null) {
			Message.CHAT_CREATEGUILD_REGIONHERE.send(sender);
			return;
		}

		//distance from spawn
		if(player.getWorld().getSpawnLocation().distance(player.getLocation()) < Config.GUILD_FROMSPAWN.getInt()) {
			vars.put(VarKey.DISTANCE, String.valueOf(Config.GUILD_FROMSPAWN.getInt()));
			Message.CHAT_CREATEGUILD_TOOCLOSESPAWN.vars(vars).send(sender);
			return;
		}

		//Disabled worlds
		if(Config.GUILD_DISABLEDWORLDS.getStringList().contains(player.getWorld().getName())) {
			Message.CHAT_CREATEGUILD_DISABLEDWORLD.send(sender);
			return;
		}

		//items required
		NovaGroup group = GroupManager.getGroup(sender);
		List<ItemStack> items = group.getGuildCreateItems();
		double requiredMoney = group.getGuildCreateMoney();

		if(requiredMoney > 0 && !nPlayer.hasMoney(requiredMoney)) {
			vars.put(VarKey.REQUIREDMONEY, String.valueOf(requiredMoney));
			Message.CHAT_CREATEGUILD_NOTENOUGHMONEY.vars(vars).send(sender);
			return;
		}

		List<ItemStack> missingItemsList = InventoryUtils.getMissingItems(player.getInventory(), items);
		if(!missingItemsList.isEmpty()) {
			Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
			sender.sendMessage(StringUtils.getItemList(missingItemsList));

			return;
		}

		RegionValidity regionValid = RegionValidity.VALID;
		NovaRegion region = null;

		//Automatic Region
		if(Config.REGION_AUTOREGION.getBoolean()) {
			int size = group.getRegionAutoSize();
			Location playerLocation = player.getLocation();
			Location c1 = new Location(player.getWorld(), playerLocation.getBlockX() - size, 0, playerLocation.getBlockZ() - size);
			Location c2 = new Location(player.getWorld(), playerLocation.getBlockX() + size, 0, playerLocation.getBlockZ() + size);

			region = new NovaRegionImpl();

			region.setCorner(0, c1);
			region.setCorner(1, c2);
			region.setWorld(playerLocation.getWorld());

			regionValid = plugin.getRegionManager().checkRegionSelect(c1, c2);
		}

		switch(regionValid) {
			case VALID:
				//Guild object
				NovaGuild guild = new NovaGuildImpl(UUID.randomUUID());
				guild.setName(guildName);
				guild.setTag(tag);
				guild.setLeader(nPlayer);
				guild.setHome(player.getLocation());
				guild.addPlayer(nPlayer);
				guild.updateInactiveTime();
				guild.setLives(Config.GUILD_STARTLIVES.getInt());
				guild.setPoints(Config.GUILD_STARTPOINTS.getInt());
				guild.setMoney(Config.GUILD_STARTMONEY.getInt());
				guild.setSlots(Config.GUILD_SLOTS_START.getInt());
				guild.setTimeCreated(NumberUtils.systemSeconds());

				//fire event
				GuildCreateEvent guildCreateEvent = new GuildCreateEvent(guild, (Player) sender);
				plugin.getServer().getPluginManager().callEvent(guildCreateEvent);

				if(!guildCreateEvent.isCancelled()) {
					//Add the guild
					plugin.getGuildManager().add(guild);

					//taking money away
					nPlayer.takeMoney(requiredMoney);

					//taking items away
					InventoryUtils.removeItems(player, items);

					//update tag and tabs
					TagUtils.refresh((Player) sender);
					TabUtils.refresh(nPlayer);

					//Update holograms
					plugin.getHologramManager().refreshTopHolograms();

					//autoregion
					if(region != null) {
						nPlayer.getGuild().setRegion(region);

						for(Player playerCheck : NovaGuilds.getOnlinePlayers()) {
							if(region.equals(RegionManager.get(playerCheck))) {
								plugin.getRegionManager().playerEnteredRegion(playerCheck, playerCheck.getLocation());
							}
						}
					}

					//homefloor
					Schematic schematic = GroupManager.getGroup(guild.getLeader().getPlayer()).getCreateSchematic();

					if(schematic != null) {
						schematic.paste(guild.getHome());
					}

					//vault item
					if(Config.VAULT_ENABLED.getBoolean()) {
						if(!InventoryUtils.containsAtLeast(nPlayer.getPlayer().getInventory(), Config.VAULT_ITEM.getItemStack(), 1)) {
							nPlayer.getPlayer().getInventory().addItem(Config.VAULT_ITEM.getItemStack());
						}
					}

					//Supernova
					ParticleUtils.createSuperNova(player);

					//messages
					Message.CHAT_CREATEGUILD_SUCCESS.send(sender);

					vars.put(VarKey.GUILDNAME, guild.getName());
					vars.put(VarKey.PLAYER, sender.getName());
					Message.BROADCAST_GUILD_CREATED.vars(vars).broadcast();
				}
				break;
			case OVERLAPS:
				Message.CHAT_REGION_VALIDATION_OVERLAPS.send(sender);
				break;
			case TOOCLOSE:
				Message.CHAT_REGION_VALIDATION_TOOCLOSE.send(sender);
				break;
		}
	}

	public static Message validTag(String tag) {
		if(GuildManager.getGuildByTag(tag) != null) { //Check for an existing guild
			return Message.CHAT_CREATEGUILD_TAGEXISTS;
		}

		if(tag.length() > Config.GUILD_SETTINGS_TAG_MAX.getInt()) { //too long
			return Message.CHAT_CREATEGUILD_TAG_TOOLONG;
		}

		if(StringUtils.removeColors(tag).length() < Config.GUILD_SETTINGS_TAG_MIN.getInt()) { //too short
			return Message.CHAT_CREATEGUILD_TAG_TOOSHORT;
		}

		if(!StringUtils.isStringAllowed(tag)) { //Allowed characters
			return Message.CHAT_CREATEGUILD_NOTALLOWEDSTRING;
		}

		return null;
	}

	public static Message validName(String name) {
		if(GuildManager.getGuildByName(name) != null) { //Check for an existing guild
			return Message.CHAT_CREATEGUILD_NAMEEXISTS;
		}

		if(name.length() > Config.GUILD_SETTINGS_NAME_MAX.getInt()) { //too long
			return Message.CHAT_CREATEGUILD_NAME_TOOLONG;
		}

		if(name.length() < Config.GUILD_SETTINGS_NAME_MIN.getInt()) { //too short
			return Message.CHAT_CREATEGUILD_NAME_TOOSHORT;
		}

		if(!StringUtils.isStringAllowed(name)) { //Allowed characters
			return Message.CHAT_CREATEGUILD_NOTALLOWEDSTRING;
		}

		return null;
	}
}
