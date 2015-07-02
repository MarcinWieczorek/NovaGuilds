package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;

import java.util.HashMap;

public class CommandAdminGuildInvite implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildInvite(NovaGuilds novaGuilds, NovaGuild guild) {
		plugin = novaGuilds;
		this.guild = guild;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.setname")) { //no perms
			return true;
		}
		
		if(args.length == 0) { //no player name
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.entername");
			return true;
		}
		
		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(playername);
		
		if(nPlayer == null) { //noplayer
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notexists");
			return true;
		}
			
		if(nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.hasguild");
			return true;
		}
		
		if(nPlayer.isInvitedTo(guild)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.alreadyinvited");
		}
		
		//all passed
		plugin.getPlayerManager().addInvitation(nPlayer, guild);
		plugin.getPlayerManager().updatePlayer(nPlayer);
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.invited");
		
		if(nPlayer.getPlayer() != null) {
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME",guild.getName());
			plugin.getMessageManager().sendMessagesMsg(nPlayer.getPlayer(),"chat.player.uvebeeninvited",vars);
		}
	
		return true;
	}
}