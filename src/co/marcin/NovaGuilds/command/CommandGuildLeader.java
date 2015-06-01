package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;

public class CommandGuildLeader implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandGuildLeader(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			NovaPlayer newLeader = plugin.getPlayerManager().getPlayerByName(args[0]);
			
			if(newLeader != null) {
				if(nPlayer.hasGuild()) {
					NovaGuild guild = nPlayer.getGuild();
					
					if(guild.getLeaderName().equals(sender.getName())) {
						if(!newLeader.getName().equals(sender.getName())) {
							if(newLeader.hasGuild() && newLeader.getGuild().getName().equals(guild.getName())) {
								//set guild leader
								guild.setLeaderName(newLeader.getName());
								plugin.getGuildManager().saveGuild(guild);

								newLeader.setLeader(true);
								nPlayer.setLeader(false);
								
								HashMap<String,String> vars = new HashMap<>();
								vars.put("PLAYERNAME",newLeader.getName());
								vars.put("GUILDNAME",guild.getName());
								plugin.sendMessagesMsg(sender,"chat.guild.leader.success", vars);
								plugin.broadcastMessage("broadcast.guild.setleader", vars);
								
								//Tab and tags
								plugin.tagUtils.refreshAll();
							}
							else {
								plugin.sendMessagesMsg(sender,"chat.guild.leader.notsameguild");
							}
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.guild.leader.samenick");
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
				plugin.sendMessagesMsg(sender,"chat.player.notexists");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.player.entername");
		}
		
		return true;
	}
}
