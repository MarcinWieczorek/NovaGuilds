package co.marcin.novaguilds.command;

import java.util.HashMap;

import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;

public class CommandGuildLeader implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandGuildLeader(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1) {
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		NovaPlayer newLeader = plugin.getPlayerManager().getPlayer(args[0]);

		if(newLeader == null) {
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return true;
		}

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}

		if(newLeader.equals(nPlayer)) {
			Message.CHAT_GUILD_LEADER_SAMENICK.send(sender);
			return true;
		}

		if(!newLeader.hasGuild() || !guild.isMember(newLeader)) {
			Message.CHAT_GUILD_LEADER_NOTSAMEGUILD.send(sender);
			return true;
		}

		//set guild leader
		guild.setLeader(newLeader);
		plugin.getGuildManager().saveGuild(guild);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYERNAME",newLeader.getName());
		vars.put("GUILDNAME",guild.getName());
		Message.CHAT_GUILD_LEADER_SUCCESS.vars(vars).send(sender);
		Message.BROADCAST_GUILD_SETLEADER.vars(vars).broadcast();

		//Tab and tags
		plugin.tagUtils.refreshAll();
		
		return true;
	}
}
