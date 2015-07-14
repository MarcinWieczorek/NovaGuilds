package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.event.GuildCreateEvent;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class CommandGuildCreate implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildCreate(NovaGuilds pl) {
		plugin = pl;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.create")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessage(sender, "chat.cmdfromconsole");
			return true;
		}

		if(args.length != 2) {
			plugin.getMessageManager().sendUsageMessage(sender, "guild.create");
			return true;
		}

		Player player = (Player)sender;

		String tag = args[0];
		String guildname = args[1];
		
		//remove colors
		guildname = StringUtils.removeColors(guildname);
		if(!plugin.getConfig().getBoolean("guild.settings.tag.color")) {
			tag = StringUtils.removeColors(tag);
		}
			
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		HashMap<String,String> vars = new HashMap<>();
		
		if(nPlayer.hasGuild()) { //has guild already
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.hasguild");
			return true;
		}

		if(plugin.getGuildManager().getGuildByName(guildname) != null) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.nameexists");
			return true;
		}

		if(plugin.getGuildManager().getGuildByTag(tag) != null) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.tagexists");
			return true;
		}

		if(plugin.getRegionManager().getRegionAtLocation(player.getLocation()) != null) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.regionhere");
			return true;
		}

		//tag length
		if(tag.length() > plugin.getConfig().getInt("guild.settings.tag.max")) { //too long
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.tag.toolong");
			return true;
		}

		if(StringUtils.removeColors(tag).length() < plugin.getConfig().getInt("guild.settings.tag.min")) { //too short
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.tag.tooshort");
			return true;
		}

		//name length
		if(guildname.length() > plugin.getConfig().getInt("guild.settings.name.max")) { //too long
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.name.toolong");
			return true;
		}

		if(guildname.length() < plugin.getConfig().getInt("guild.settings.name.min")) { //too short
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.name.tooshort");
			return true;
		}

		//allowed strings (tag, name)
		if(!StringUtils.isStringAllowed(tag) || !StringUtils.isStringAllowed(guildname)) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.name.notallowedstring");
			return true;
		}

		//distance from spawn
		if(player.getWorld().getSpawnLocation().distance(player.getLocation()) < plugin.getConfigManager().getGuildDistanceFromSpawn()) {
			vars.put("DISTANCE",plugin.getConfigManager().getGuildDistanceFromSpawn()+"");
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.tooclosespawn", vars);
			return true;
		}

		//items required
		List<ItemStack> items = plugin.getGroupManager().getGroup(sender).getGuildCreateItems();
		double requiredmoney = plugin.getGroupManager().getGroup(sender).getGuildCreateMoney();

		if(requiredmoney>0 && plugin.econ.getBalance(player.getName()) < requiredmoney) {
			String rmmsg = plugin.getMessageManager().getMessagesString("chat.createguild.notenoughmoney");
			rmmsg = StringUtils.replace(rmmsg, "{REQUIREDMONEY}", requiredmoney + "");
			plugin.getMessageManager().sendMessagesMsg(sender, rmmsg);
			return true;
		}

		if(!ItemStackUtils.hasAllRequiredItems(player,items)) {
			String itemlist = "";
			int i = 0;
			for(ItemStack missingItemStack : ItemStackUtils.getMissingItems(player,items)) {
				String itemrow = plugin.getMessageManager().getMessagesString("chat.createguild.itemlist");
				itemrow = StringUtils.replace(itemrow, "{ITEMNAME}", missingItemStack.getType().name());
				itemrow = StringUtils.replace(itemrow, "{AMOUNT}", missingItemStack.getAmount() + "");

				itemlist += itemrow;

				if(i<items.size()-1) itemlist+= plugin.getMessageManager().getMessagesString("chat.createguild.itemlistsep");
				i++;
			}

			plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.noitems");
			sender.sendMessage(StringUtils.fixColors(itemlist));
			return true;
		}

		RegionValidity regionValid = RegionValidity.VALID;
		NovaRegion region = null;

		//Automatic Region
		if(plugin.getConfig().getBoolean("region.autoregion")) {
			int size = plugin.getGroupManager().getGroup(sender).getRegionAutoSize();
			Location playerLocation = player.getLocation();
			Location c1 = new Location(player.getWorld(), playerLocation.getBlockX() - size+1, 0, playerLocation.getBlockZ() - size+1);
			Location c2 = new Location(player.getWorld(), playerLocation.getBlockX() + size, 0, playerLocation.getBlockZ() + size);

			region = new NovaRegion();

			region.setCorner(0, c1);
			region.setCorner(1, c2);
			region.setWorld(playerLocation.getWorld());

			regionValid = plugin.getRegionManager().checkRegionSelect(c1, c2);
			LoggerUtils.debug(regionValid.name());
		}

		switch(regionValid) {
			case VALID:
				//Guild object
				NovaGuild newGuild = new NovaGuild();
				newGuild.setName(guildname);
				newGuild.setTag(tag);
				newGuild.setLeader(nPlayer);
				newGuild.setSpawnPoint(player.getLocation());
				newGuild.addPlayer(nPlayer);
				newGuild.updateInactiveTime();
				newGuild.setLives(plugin.getConfig().getInt("guild.startlives"));
				newGuild.setPoints(plugin.getConfig().getInt("guild.startpoints"));
				newGuild.setMoney(plugin.getConfig().getDouble("guild.startmoney"));

				//fire event
				GuildCreateEvent guildCreateEvent = new GuildCreateEvent(newGuild,(Player)sender);
				plugin.getServer().getPluginManager().callEvent(guildCreateEvent);

				if(!guildCreateEvent.isCancelled()) {
					//Add the guild
					plugin.getGuildManager().addGuild(newGuild);

					//nPlayer
					nPlayer.setGuild(newGuild);

					//taking money away
					plugin.econ.withdrawPlayer(sender.getName(), requiredmoney);

					//taking items away
					ItemStackUtils.takeItems(player,items);

					//update tag and tabs
					plugin.tagUtils.updatePrefix((Player)sender);

					//autoregion
					if(region != null) {
						region.setGuild(nPlayer.getGuild());
						plugin.getRegionManager().addRegion(region, nPlayer.getGuild());
						LoggerUtils.debug("AutoRegion created!");

						for(Player playerCheck : plugin.getServer().getOnlinePlayers()) {
							if(region.equals(plugin.getRegionManager().getRegionAtLocation(playerCheck.getLocation()))) {
								plugin.getRegionManager().playerEnteredRegion(playerCheck,playerCheck.getLocation());
							}
						}
					}

					//homefloor
					plugin.getGuildManager().createHomeFloor(newGuild);

					//messages
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.success");

					vars.put("GUILDNAME", newGuild.getName());
					vars.put("PLAYER", sender.getName());
					plugin.getMessageManager().broadcastMessage("broadcast.guild.created", vars);
				}
				break;
			case OVERLAPS:
				plugin.getMessageManager().sendMessagesMsg(player, "chat.region.overlaps");
				break;
			case TOOCLOSE:
				plugin.getMessageManager().sendMessagesMsg(player, "chat.guild.tooclose");
				break;
			default:
				LoggerUtils.debug("Not expected RegionValidity result.");
				break;
		}
		return true;
	}
}
