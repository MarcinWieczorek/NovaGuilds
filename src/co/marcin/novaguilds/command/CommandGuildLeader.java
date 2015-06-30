package co.marcin.novaguilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;

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
					
					if(guild.getLeader().getName().equals(sender.getName())) {
						if(!newLeader.getName().equals(sender.getName())) {
							if(newLeader.hasGuild() && newLeader.getGuild().getName().equals(guild.getName())) {
								//set guild leader
								guild.setLeader(newLeader);
								plugin.getGuildManager().saveGuild(guild);
								
								HashMap<String,String> vars = new HashMap<>();
								vars.put("PLAYERNAME",newLeader.getName());
								vars.put("GUILDNAME",guild.getName());
								plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.leader.success", vars);
								plugin.getMessageManager().broadcastMessage("broadcast.guild.setleader", vars);
								
								//Tab and tags
								plugin.tagUtils.refreshAll();
							}
							else {
								plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.leader.notsameguild");
							}
						}
						else {
							plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.leader.samenick");
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
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notexists");
			}
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.entername");
		}
		
		return true;
	}
}
