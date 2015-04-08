package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;

public class CommandAdminGuildSetTag implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminGuildSetTag(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.admin.guild.settag")) {
			if(args.length>0) {
				String guildname = args[0];
				if(args.length==1) {
					plugin.sendMessagesMsg(sender,"chat.guild.entertag");
					return true;
				}
			
				String newtag = args[1];
				
				NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
				
				if(guild instanceof NovaGuild) {
					if(plugin.getGuildManager().getGuildByTag(newtag) == null) {
						guild.setTag(newtag);
						
						plugin.getGuildManager().saveGuildLocal(guild);
						
						plugin.updateTabAll();
						plugin.updateTagAll();
						
						HashMap<String,String> vars = new HashMap<String,String>();
						vars.put("TAG",newtag);
						plugin.sendMessagesMsg(sender,"chat.admin.guild.settag",vars);
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.guild.tagexists");
					}
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
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
