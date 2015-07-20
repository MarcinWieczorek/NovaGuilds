package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandGuildKick  implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildKick(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.kick")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}
		
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		
		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}
		
		NovaGuild guild = nPlayer.getGuild();
		
		if(!guild.getLeader().getName().equalsIgnoreCase(sender.getName())) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}
		
		if(args.length == 0) {
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return true;
		}
		
		NovaPlayer nPlayerKick = plugin.getPlayerManager().getPlayer(args[0]);
		
		if(nPlayerKick == null) {
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return true;
		}

		if(!nPlayerKick.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return true;
		}
		
		if(!nPlayerKick.getGuild().getName().equalsIgnoreCase(guild.getName())) {
			Message.CHAT_PLAYER_NOTINYOURGUILD.send(sender);
			return true;
		}

		if(nPlayer.getName().equalsIgnoreCase(nPlayerKick.getName())) {
			Message.CHAT_GUILD_KICKYOURSELF.send(sender);
			return true;
		}
		
		//all passed
		nPlayerKick.setGuild(null);

		nPlayer.getGuild().removePlayer(nPlayerKick);
		
		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYERNAME",nPlayerKick.getName());
		vars.put("GUILDNAME",guild.getName());
		Message.BROADCAST_GUILD_KICKED.vars(vars).broadcast();
		
		//tab/tag
		plugin.tagUtils.refreshAll();
		
		return true;
	}
}
