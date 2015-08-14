package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildInvite implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildInvite(NovaGuilds novaGuilds, NovaGuild guild) {
		plugin = novaGuilds;
		this.guild = guild;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.invite")) { //no perms
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}
		
		if(args.length == 0) { //no player name
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return true;
		}
		
		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playername);
		
		if(nPlayer == null) { //noplayer
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return true;
		}
			
		if(nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASGUILD.send(sender);
			return true;
		}
		
		if(nPlayer.isInvitedTo(guild)) {
			Message.CHAT_PLAYER_ALREADYINVITED.send(sender);
			return true;
		}
		
		//all passed
		nPlayer.addInvitation(guild);
		Message.CHAT_PLAYER_INVITE_INVITED.send(sender);
		
		if(nPlayer.getPlayer() != null) {
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME",guild.getName());
			Message.CHAT_PLAYER_INVITE_NOTIFY.vars(vars).send(sender);
		}
	
		return true;
	}
}