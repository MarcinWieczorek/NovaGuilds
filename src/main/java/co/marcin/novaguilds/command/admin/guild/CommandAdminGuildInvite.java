package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildInvite implements Executor, ExecutorReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command;

	public CommandAdminGuildInvite(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
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
		
		if(args.length == 0) { //no player name
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return;
		}
		
		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playername);
		
		if(nPlayer == null) { //noplayer
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}
			
		if(nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASGUILD.send(sender);
			return;
		}
		
		if(nPlayer.isInvitedTo(guild)) {
			Message.CHAT_PLAYER_ALREADYINVITED.send(sender);
			return;
		}
		
		//all passed
		nPlayer.addInvitation(guild);
		Message.CHAT_PLAYER_INVITE_INVITED.send(sender);
		
		if(nPlayer.getPlayer() != null) {
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME",guild.getName());
			Message.CHAT_PLAYER_INVITE_NOTIFY.vars(vars).send(sender);
		}
	}
}