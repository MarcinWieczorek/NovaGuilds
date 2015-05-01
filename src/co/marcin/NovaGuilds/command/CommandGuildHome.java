package co.marcin.NovaGuilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.basic.NovaRegion;

public class CommandGuildHome implements CommandExecutor {
public final NovaGuilds plugin;
	
	public CommandGuildHome(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.sendMessagesMsg(sender,"chat.consolesender");
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);

		if(nPlayer.hasGuild()) {
			Player player = plugin.getServer().getPlayer(sender.getName());
			
			if(args.length>0 && args[0].equalsIgnoreCase("set")) {
				if(nPlayer.isLeader()) {
					NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(player.getLocation());
					//player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_CRYSTAL);
					
					if(rgatloc == null || rgatloc.getGuildName().equals(nPlayer.getGuild().getName())) {
						nPlayer.getGuild().setSpawnPoint(player.getLocation());
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
			
			if(nPlayer.getGuild().getSpawnPoint() != null) {
				player.teleport(nPlayer.getGuild().getSpawnPoint());
				plugin.sendMessagesMsg(sender,"chat.guild.tp");
			}
		}
		else { //noguild
			plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
		}
		return true;
	}
}
