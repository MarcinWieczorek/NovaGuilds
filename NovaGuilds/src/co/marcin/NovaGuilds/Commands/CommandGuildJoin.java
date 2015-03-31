package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class CommandGuildJoin implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandGuildJoin(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
		List<String> invitedTo = nPlayer.getInvitedTo();
		
		if(!nPlayer.hasGuild()) {
			sender.sendMessage("1");
			if(invitedTo.size() > 0) {sender.sendMessage("2");
				String guildname;
				
				if(invitedTo.size()==1) {sender.sendMessage("3");
					guildname = invitedTo.get(0);
				}
				else {
					if(args.length == 0) {
						plugin.sendMessagesMsg(sender,"chat.player.ureinvitedto");
						return true;
					}
					else {
						guildname = args[0];
					}
				}
				
				if(plugin.getGuildManager().exists(guildname)) {
					sender.sendMessage("4");
					NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
					
					if(nPlayer.isInvitedTo(guild)) {
						guild.addPlayer(nPlayer);
						nPlayer.setGuild(guild);
						nPlayer.deleteInvitation(guild);
						plugin.getPlayerManager().updateLocalPlayer(nPlayer);
						plugin.getPlayerManager().updatePlayer(nPlayer);
						plugin.updateTab(nPlayer.getPlayer());
						plugin.sendMessagesMsg(sender,"chat.guild.joined");
						
						HashMap<String,String> vars = new HashMap<String,String>();
						vars.put("PLAYER",sender.getName());
						vars.put("GUILDNAME",guild.getName());
						plugin.broadcastMessage("broadcast.guild.joined", vars);
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.player.notinvitedtoguild");
					}
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.player.invitedtonothing");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.createguild.hasguild");
		}
		return true;
	}
}
