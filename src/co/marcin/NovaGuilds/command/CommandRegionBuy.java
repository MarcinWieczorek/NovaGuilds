package co.marcin.NovaGuilds.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.basic.NovaRegion;
import org.bukkit.entity.Player;

public class CommandRegionBuy implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandRegionBuy(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("NovaGuilds.region.create")) {
			if(!(sender instanceof Player)) {
				//TODO cmdsender msg
				return false;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			
			if(nPlayer.hasGuild()) {
				NovaGuild guild = nPlayer.getGuild();
				
				if(guild.getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
					if(!guild.hasRegion()) {
						if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
							Location sl1 = nPlayer.getSelectedLocation(0);
							Location sl2 = nPlayer.getSelectedLocation(1);

							if(plugin.getRegionManager().checkRegionSelect(sl1, sl2).equals("valid")) {
								double createprice = plugin.getConfig().getDouble("region.createprice");
								double pricepb = plugin.getConfig().getDouble("region.pricepb");
								int regionsize = plugin.getRegionManager().checkRegionSize(nPlayer.getSelectedLocation(0),nPlayer.getSelectedLocation(1));
								double price = pricepb * regionsize + createprice;
								
								if(guild.getMoney() >= price) {
									NovaRegion region = new NovaRegion();
									
									region.setCorner(0,nPlayer.getSelectedLocation(0));
									region.setCorner(1,nPlayer.getSelectedLocation(1));
									region.setWorld(nPlayer.getPlayer().getWorld());
									
									plugin.getRegionManager().addRegion(region, guild);
									guild.takeMoney(price);
									plugin.getGuildManager().saveGuild(guild);
									plugin.sendMessagesMsg(sender,"chat.region.created");
								}
								else {
									plugin.sendMessagesMsg(sender,"chat.guild.notenoughtmoney");
								}
							}
							else {
								plugin.sendMessagesMsg(sender,"chat.region.notvalid");
							}
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.region.areanotselected");
						}
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.guild.hasregionalready");
					}
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.guild.notleader");
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}
