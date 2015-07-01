package co.marcin.novaguilds.command;

import co.marcin.novaguilds.enums.RegionValidity;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.entity.Player;

public class CommandRegionBuy implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandRegionBuy(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.region.create")) {
			if(!(sender instanceof Player)) {
				//TODO cmdsender msg
				return false;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			
			if(nPlayer.hasGuild()) {
				NovaGuild guild = nPlayer.getGuild();
				
				if(guild.getLeader().getName().equalsIgnoreCase(nPlayer.getName())) {
					if(!guild.hasRegion()) {
						if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
							Location sl1 = nPlayer.getSelectedLocation(0);
							Location sl2 = nPlayer.getSelectedLocation(1);

							if(plugin.getRegionManager().checkRegionSelect(sl1, sl2) == RegionValidity.VALID) {
								int regionsize = plugin.getRegionManager().checkRegionSize(nPlayer.getSelectedLocation(0),nPlayer.getSelectedLocation(1));

								//region's price
								double price = plugin.getGroup(sender).getPricePerBlock() * regionsize + plugin.getGroup(sender).getCreateRegionMoney();
								
								if(guild.getMoney() >= price) {
									NovaRegion region = new NovaRegion();
									
									region.setCorner(0,nPlayer.getSelectedLocation(0));
									region.setCorner(1,nPlayer.getSelectedLocation(1));
									region.setWorld(nPlayer.getPlayer().getWorld());
									region.setGuild(nPlayer.getGuild());
									
									plugin.getRegionManager().addRegion(region, guild);
									guild.takeMoney(price);
									plugin.getGuildManager().saveGuild(guild); //TODO check this
									plugin.getMessageManager().sendMessagesMsg(sender,"chat.region.created");
								}
								else {
									plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notenoughmoney");
								}
							}
							else {
								plugin.getMessageManager().sendMessagesMsg(sender,"chat.region.notvalid");
							}
						}
						else {
							plugin.getMessageManager().sendMessagesMsg(sender,"chat.region.areanotselected");
						}
					}
					else {
						plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.hasregionalready");
					}
				}
				else {
					plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notleader");
				}
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
			}
		}
		else {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
		}
		return true;
	}
}
