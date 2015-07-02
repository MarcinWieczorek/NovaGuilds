package co.marcin.novaguilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;

public class CommandGuildAlly implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildAlly(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		
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
										plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.ally.war");
										return true;
									}

									for(NovaPlayer allyP : allyGuild.getPlayers()) {
										if(allyP.isOnline()) {
											plugin.getMessageManager().sendMessagesMsg(allyP.getPlayer(), "chat.guild.ally.newinvite", vars);
										}
									}

									if(guild.isInvitedToAlly(allyGuild)) { //Accepting
										allyGuild.addAlly(guild);
										guild.addAlly(allyGuild);
										guild.removeAllyInvitation(allyGuild);
										plugin.getMessageManager().broadcastMessage("broadcast.guild.allied", vars);

										plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.ally.accepted", vars);

										//tags
										plugin.tagUtils.refreshAll();
									}
									else { //Inviting
										if(!allyGuild.isInvitedToAlly(guild)) {
											allyGuild.addAllyInvitation(guild);
											plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.ally.invited", vars);
											plugin.getMessageManager().broadcastGuild(allyGuild, "chat.guild.ally.notifyguild", vars);
										}
										else { //cancel inv
											allyGuild.removeAllyInvitation(guild);

											plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.ally.canceled",vars);
											plugin.getMessageManager().broadcastGuild(allyGuild,"chat.guild.ally.notifyguildcanceled",vars);
										}
									}
								}
								else { //UN-ALLY
									guild.removeAlly(allyGuild);
									allyGuild.removeAlly(guild);

									plugin.getMessageManager().broadcastMessage("broadcast.guild.endally",vars);

									plugin.tagUtils.refreshAll();
								}
							}
							else {
								plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notleader");
							}
						}
						else {
							plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.ally.samename");
						}
					}
					else {
						plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.namenotexist");
					}
				}
				else {
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notinguild");
				}
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.entername");
			}
		}
		else {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
		}
		
		return true;
	}
}
