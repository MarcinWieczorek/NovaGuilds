package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildKick  implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdminGuildKick(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.kick")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}
		
		if(args.length == 0) { //no playername
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return true;
		}
		
		NovaPlayer nPlayerKick = plugin.getPlayerManager().getPlayer(args[0]);
		
		if(nPlayerKick == null) { //no player
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return true;
		}

		if(!nPlayerKick.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayerKick.getGuild();

		if(nPlayerKick.isLeader()) {
			Message.CHAT_ADMIN_GUILD_KICK_LEADER.send(sender);
			return true;
		}
		
		//all passed
		nPlayerKick.setGuild(null);
		
		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYERNAME",nPlayerKick.getName());
		vars.put("GUILDNAME",guild.getName());
		Message.BROADCAST_GUILD_KICKED.vars(vars).broadcast();
		
		//tab/tag
		plugin.tagUtils.refreshAll();
		
		return true;
	}
}
