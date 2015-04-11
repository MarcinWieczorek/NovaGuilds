package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class CommandGuildAlly implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandGuildAlly(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);
		
		if(sender.hasPermission("novaguilds.guild.ally")) {
			if(args.length==1) {
				String allyname = args[0];
				
				if(!allyname.equals("")) {
					if(nPlayer.hasGuild()) {
						NovaGuild guild = nPlayer.getGuild();
						
						if(plugin.getGuildManager().exists(allyname)) {
							NovaGuild allyguild = plugin.getGuildManager().getGuildByName(allyname);
							
							if(!allyname.equalsIgnoreCase(guild.getName())) {
								if(guild.getLeaderName().equalsIgnoreCase(sender.getName())) {
									if(!guild.isAlly(allyguild)) {
										HashMap<String,String> vars = new HashMap<String,String>();
										vars.put("GUILDNAME",guild.getName());
										vars.put("ALLYNAME",allyguild.getName());
										
										for(NovaPlayer allyP : allyguild.getPlayers()) {
											if(allyP.isOnline()) {
												plugin.sendMessagesMsg(allyP.getPlayer(),"chat.guild.ally.newinvite",vars);
											}
										}
										
										if(guild.isInvitedToAlly(allyname)) { //Accepting
											allyguild.addAlly(guild.getName());
											guild.addAlly(allyguild.getName());
											guild.removeAllyInvitation(allyname);
											
											plugin.getGuildManager().saveGuildLocal(guild);
											plugin.getGuildManager().saveGuildLocal(allyguild);
											
											plugin.broadcastMessage("broadcast.guild.allied",vars);
											
											plugin.sendMessagesMsg(sender,"chat.guild.ally.accepted",vars);
										}
										else { //Inviting
											if(!allyguild.isInvitedToAlly(guild.getName())) {
												allyguild.addAllyInvitation(guild.getName());
												
												plugin.getGuildManager().saveGuildLocal(allyguild);
												
												plugin.sendMessagesMsg(sender,"chat.guild.ally.invited",vars);
												plugin.broadcastGuild(allyguild,"chat.guild.ally.notifyguild",vars);
											}
											else { //cancel inv
												allyguild.removeAllyInvitation(guild.getName());
												
												plugin.getGuildManager().saveGuildLocal(allyguild);
												
												plugin.sendMessagesMsg(sender,"chat.guild.ally.canceled",vars);
												plugin.broadcastGuild(allyguild,"chat.guild.ally.notifyguildcanceled",vars);
											}
										}
									}
									else { //UN-ALLY
										
									}
								}
								else {
									plugin.sendMessagesMsg(sender,"chat.guild.notleader");
								}
							}
							else {
								plugin.sendMessagesMsg(sender,"chat.guild.ally.samename");
							}
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
						}
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
					}
				}
				else {
					plugin.info("emptyname");
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.entername");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		
		return true;
	}
}
