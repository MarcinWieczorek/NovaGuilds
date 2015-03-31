package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class CommandAdmin implements CommandExecutor {
	NovaGuilds plugin;
	
	public CommandAdmin(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.access")) {
			if(args.length>0) {
				if(args[0].equalsIgnoreCase("region") || args[0].equalsIgnoreCase("rg")) {
					if(args.length>1) {
						if(args[1].equalsIgnoreCase("bypass")) { //togglebypass
							if(args.length==2) {
								if(sender.hasPermission("novaguilds.admin.region.bypass")) {
									NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
									
									nPlayer.toggleBypass();
									plugin.getPlayerManager().updateLocalPlayer(nPlayer);
									HashMap<String,String> vars = new HashMap<String,String>();
									vars.put("BYPASS",nPlayer.getBypass()+"");
									plugin.sendMessagesMsg(sender,"chat.admin.rgbypass.toggled",vars);
								}
								else {
									plugin.sendMessagesMsg(sender,"chat.nopermissions");
								}
							}
							else { //for other
								if(sender.hasPermission("novaguilds.admin.region.bypass.other")) {
									NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(args[2]);

									if(nPlayer == null) {
										plugin.sendMessagesMsg(sender,"chat.player.notexists");
										return true;
									}
									
									nPlayer.toggleBypass();
									plugin.getPlayerManager().updateLocalPlayer(nPlayer);
									HashMap<String,String> vars = new HashMap<String,String>();
									vars.put("PLAYER",nPlayer.getName());
									vars.put("BYPASS",nPlayer.getBypass()+"");
									plugin.sendMessagesMsg(sender,"chat.admin.rgbypass.toggledother",vars);
								}
								else {
									plugin.sendMessagesMsg(sender,"chat.nopermissions");
								}
							}
						}
						else if(args[1].equalsIgnoreCase("list")) { //list regions
							new CommandRegionList(plugin).onCommand(sender, cmd, label, args);
						}
						else {
							plugin.sendMessagesMsg(sender, "chat.unknowncmd");
						}
					}
					else {
						plugin.sendPrefixMessage(plugin.senderToPlayer(sender),"NovaGuilds Admin: Region commands");
					}
				}
				else if(args[0].equalsIgnoreCase("reload")) { //reload
					if(sender.hasPermission("novaguilds.admin.reload")) {
						new CommandReload(plugin).onCommand(sender, cmd, label, args);
					}
				}
				else if(args[0].equalsIgnoreCase("save")) { //reload
					new CommandAdminSave(plugin).onCommand(sender, cmd, label, Utils.parseArgs(args,1));
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.unknowncmd");
				}
			}
			else {
				plugin.sendPrefixMessage(plugin.senderToPlayer(sender),"NovaGuilds Admin info");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		
		return true;
	}

}
