package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;

public class CommandAdminGuildKick  implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdminGuildKick(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.kick")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}
		
		if(args.length == 0) { //no playername
			plugin.sendMessagesMsg(sender,"chat.player.entername");
			return true;
		}
		
		NovaPlayer nPlayerKick = plugin.getPlayerManager().getPlayerByName(args[0]);
		NovaGuild guild = plugin.getGuildManager().getGuildByPlayer(nPlayerKick);
		
		if(nPlayerKick == null) { //no player
			plugin.sendMessagesMsg(sender,"chat.player.notexists");
			return true;
		}

		if(!nPlayerKick.hasGuild()) {
			plugin.sendMessagesMsg(sender,"chat.player.hasnoguild");
			return true;
		}

		if(nPlayerKick.isLeader()) {
			plugin.sendMessagesMsg(sender,"chat.admin.kick.leader");
			return true;
		}
		
		//all passed
		nPlayerKick.setGuild(null);
		nPlayerKick.setHasGuild(false);
		
		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYERNAME",nPlayerKick.getName());
		vars.put("GUILDNAME",guild.getName());
		plugin.broadcastMessage("broadcast.guild.kicked", vars);
		
		//tab/tag
		plugin.updateTabAll();
		plugin.tagUtils.refreshAll();
		
		return true;
	}
}
