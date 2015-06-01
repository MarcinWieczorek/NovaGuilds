package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;

public class CommandGuildAlly implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildAlly(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);
		
		if(sender.hasPermission("novaguilds.guild.ally")) {
			if(args.length==1) {
				String allyname = args[0];

				if(nPlayer.hasGuild()) {
					NovaGuild guild = nPlayer.getGuild();
					NovaGuild allyGuild = plugin.getGuildManager().getGuildFind(allyname);

					if(allyGuild != null) {
						if(!allyGuild.equals(guild)) {
							if(guild.isLeader(sender)) {
								HashMap<String,String> vars = new HashMap<>();
								vars.put("GUILDNAME",guild.getName());
								vars.put("ALLYNAME", allyGuild.getName());

								if(!guild.isAlly(allyGuild)) {
									if(guild.isWarWith(allyGuild)) {
										plugin.sendMessagesMsg(sender,"chat.guild.ally.war");
										return true;
									}

									for(NovaPlayer allyP : allyGuild.getPlayers()) {
										if(allyP.isOnline()) {
											plugin.sendMessagesMsg(allyP.getPlayer(),"chat.guild.ally.newinvite",vars);
										}
									}

									if(guild.isInvitedToAlly(allyGuild)) { //Accepting
										allyGuild.addAlly(guild);
										guild.addAlly(allyGuild);
										guild.removeAllyInvitation(allyGuild);
										plugin.broadcastMessage("broadcast.guild.allied",vars);

										plugin.sendMessagesMsg(sender,"chat.guild.ally.accepted",vars);

										//tags
										plugin.tagUtils.refreshAll();
									}
									else { //Inviting
										if(!allyGuild.isInvitedToAlly(guild)) {
											allyGuild.addAllyInvitation(guild);
											plugin.sendMessagesMsg(sender,"chat.guild.ally.invited",vars);
											plugin.broadcastGuild(allyGuild,"chat.guild.ally.notifyguild",vars);
										}
										else { //cancel inv
											allyGuild.removeAllyInvitation(guild);

											plugin.sendMessagesMsg(sender,"chat.guild.ally.canceled",vars);
											plugin.broadcastGuild(allyGuild,"chat.guild.ally.notifyguildcanceled",vars);
										}
									}
								}
								else { //UN-ALLY
									guild.removeAlly(allyGuild);
									allyGuild.removeAlly(guild);

									plugin.broadcastMessage("broadcast.guild.endally",vars);

									plugin.tagUtils.refreshAll();
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
				plugin.sendMessagesMsg(sender,"chat.guild.entername");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		
		return true;
	}
}
