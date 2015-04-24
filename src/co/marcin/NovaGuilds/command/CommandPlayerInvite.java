package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class CommandPlayerInvite implements CommandExecutor {
	public final NovaGuilds plugin;
	
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
									HashMap<String, String> vars = new HashMap<>();
									vars.put("GUILDNAME",guild.getName());
									plugin.sendMessagesMsg(inPlayer.getPlayer(),"chat.player.uvebeeninvited",vars);
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