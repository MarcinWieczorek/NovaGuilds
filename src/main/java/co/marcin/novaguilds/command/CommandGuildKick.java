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
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
			return true;
		}
		
		NovaGuild guild = nPlayer.getGuild();
		
		if(!guild.getLeader().getName().equalsIgnoreCase(sender.getName())) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notleader");
			return true;
		}
		
		if(args.length == 0) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.entername");
			return true;
		}
		
		NovaPlayer nPlayerKick = plugin.getPlayerManager().getPlayer(args[0]);
		
		if(nPlayerKick == null) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notexists");
			return true;
		}

		if(!nPlayerKick.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.hasnoguild");
			return true;
		}
		
		if(!nPlayerKick.getGuild().getName().equalsIgnoreCase(guild.getName())) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notinyourguild");
			return true;
		}

		if(nPlayer.getName().equalsIgnoreCase(nPlayerKick.getName())) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.kickyourself");
			return true;
		}
		
		//all passed
		nPlayerKick.setGuild(null);

		nPlayer.getGuild().removePlayer(nPlayerKick);
		
		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYERNAME",nPlayerKick.getName());
		vars.put("GUILDNAME",guild.getName());
		plugin.getMessageManager().broadcastMessage("broadcast.guild.kicked", vars);
		
		//tab/tag
		plugin.tagUtils.refreshAll();
		
		return true;
	}
}
