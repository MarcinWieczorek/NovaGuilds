package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildKick implements Executor {
	private final Commands command;

	public CommandAdminGuildKick(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}
		
		if(args.length == 0) { //no playername
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return;
		}
		
		NovaPlayer nPlayerKick = plugin.getPlayerManager().getPlayer(args[0]);
		
		if(nPlayerKick == null) { //no player
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}

		if(!nPlayerKick.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayerKick.getGuild();

		if(nPlayerKick.isLeader()) {
			Message.CHAT_ADMIN_GUILD_KICK_LEADER.send(sender);
			return;
		}
		
		//all passed
		nPlayerKick.setGuild(null);
		
		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYERNAME",nPlayerKick.getName());
		vars.put("GUILDNAME",guild.getName());
		Message.BROADCAST_GUILD_KICKED.vars(vars).broadcast();
		
		//tab/tag
		plugin.tagUtils.refreshAll();
	}
}
