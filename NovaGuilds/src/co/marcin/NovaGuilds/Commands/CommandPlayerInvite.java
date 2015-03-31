package co.marcin.NovaGuilds.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class CommandPlayerInvite implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandPlayerInvite(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			String playername = args[0];
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			
			if(nPlayer.hasGuild()) {
				if(plugin.getPlayerManager().exists(playername)) {
					NovaPlayer inPlayer = plugin.getPlayerManager().getPlayerByName(playername);
					
					if(!inPlayer.hasGuild()) {
						NovaGuild guild = nPlayer.getGuild();
						if(!inPlayer.isInvitedTo(guild)) {
							plugin.getPlayerManager().addInvitation(inPlayer, guild);
							plugin.getPlayerManager().updateLocalPlayer(inPlayer);
							plugin.getPlayerManager().updatePlayer(inPlayer);
							plugin.sendMessagesMsg(sender,"chat.player.invited");
							
							if(inPlayer.getPlayer() instanceof Player) {
								if(inPlayer.getPlayer().isOnline()) {
									plugin.sendMessagesMsg(inPlayer.getPlayer(),"chat.player.uvebeeninvited");
								}
							}
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.player.alreadyinvited");
						}
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.player.hasguild");
					}
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.player.notexists");
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
			}
			return true;
		}
		return false;
	}
}