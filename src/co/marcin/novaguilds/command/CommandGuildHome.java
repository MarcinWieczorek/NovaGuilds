package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;

public class CommandGuildHome implements CommandExecutor {
private final NovaGuilds plugin;
	
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
				plugin.delayedTeleport(player,nPlayer.getGuild().getSpawnPoint(),"chat.guild.tp");
			}
		}
		else { //noguild
			plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
		}
		return true;
	}
}
