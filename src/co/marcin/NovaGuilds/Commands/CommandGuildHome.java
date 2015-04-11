package co.marcin.NovaGuilds.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.NovaRegion;

public class CommandGuildHome implements CommandExecutor {
public final NovaGuilds plugin;
	
	public CommandGuildHome(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		NovaPlayer nplayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
		
		if(nplayer.hasGuild()) {
			Player player = plugin.getServer().getPlayer(sender.getName());
			
			if(args.length>0 && args[0].equalsIgnoreCase("set")) {
				if(nplayer.getGuild().getLeaderName().equals(sender.getName())) {
					NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(player.getLocation());
					//player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_CRYSTAL);
					
					if(rgatloc == null || rgatloc.getGuildName().equals(nplayer.getGuild().getName())) {
						nplayer.getGuild().setSpawnPoint(player.getLocation());
						plugin.sendMessagesMsg(sender,"chat.guild.setspawnpoint");
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.guild.guildatlocsp");
					}
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.guild.notleader");
				}
				
				return true;
			}
			
			if(nplayer.getGuild().getSpawnPoint() instanceof Location) {
				player.teleport(nplayer.getGuild().getSpawnPoint());
				plugin.sendMessagesMsg(sender,"chat.guild.tp");
			}
		}
		else { //noguild
			plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
		}
		return true;
	}
}
