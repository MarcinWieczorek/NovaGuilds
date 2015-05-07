package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;

public class CommandGuildKick  implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildKick(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.kick")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}
		
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);
		
		if(!nPlayer.hasGuild()) {
			plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
			return true;
		}
		
		NovaGuild guild = plugin.getGuildManager().getGuildByPlayer(nPlayer);
		
		if(!guild.getLeaderName().equalsIgnoreCase(sender.getName())) {
			plugin.sendMessagesMsg(sender,"chat.guild.notleader");
			return true;
		}
		
		if(args.length == 0) {
			plugin.sendMessagesMsg(sender,"chat.player.entername");
			return true;
		}
		
		NovaPlayer nPlayerKick = plugin.getPlayerManager().getPlayerByName(args[0]);
		
		if(nPlayerKick == null) {
			plugin.sendMessagesMsg(sender,"chat.player.notexists");
			return true;
		}
		
		if(!nPlayerKick.getGuild().getName().equalsIgnoreCase(guild.getName())) {
			plugin.sendMessagesMsg(sender,"chat.player.notinyourguild");
			return true;
		}

		if(nPlayer.getName().equalsIgnoreCase(nPlayerKick.getName())) {
			plugin.sendMessagesMsg(sender,"chat.guild.kickyourself");
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
