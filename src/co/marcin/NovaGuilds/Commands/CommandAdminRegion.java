package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class CommandAdminRegion implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandAdminRegion(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length>0) {
			if(args[0].equalsIgnoreCase("bypass")) { //togglebypass
				if(args.length==1) {
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
						NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(args[1]);

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
			else if(args[0].equalsIgnoreCase("list")) { //list regions
				new CommandAdminRegionList(plugin).onCommand(sender, cmd, label, args);
			}
			else {
				plugin.sendMessagesMsg(sender, "chat.unknowncmd");
			}
		}
		else {
			plugin.sendMessagesMsg(sender, "chat.commands.admin.region.header");
			
			for(String citem : plugin.getMessages().getStringList("chat.commands.admin.region.items")) {
				sender.sendMessage(Utils.fixColors(citem));
			}
		}
		return true;
	}
}