package co.marcin.novaguildss.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguildss.basic.NovaGuild;
import co.marcin.novaguildss.NovaGuilds;
import co.marcin.novaguildss.basic.NovaPlayer;

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
			plugin.sendMessagesMsg(sender,"chat.player.entername");
			return true;
		}
		
		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(playername);
		
		if(nPlayer == null) { //noplayer
			plugin.sendMessagesMsg(sender,"chat.player.notexists");
			return true;
		}
			
		if(nPlayer.hasGuild()) {
			plugin.sendMessagesMsg(sender,"chat.player.hasguild");
			return true;
		}
		
		if(nPlayer.isInvitedTo(guild)) {
			plugin.sendMessagesMsg(sender,"chat.player.alreadyinvited");
		}
		
		//all passed
		plugin.getPlayerManager().addInvitation(nPlayer, guild);
		plugin.getPlayerManager().updatePlayer(nPlayer);
		plugin.sendMessagesMsg(sender,"chat.player.invited");
		
		if(nPlayer.getPlayer() != null) {
			HashMap<String,String> vars = new HashMap<>();
			vars.put("GUILDNAME",guild.getName());
			plugin.sendMessagesMsg(nPlayer.getPlayer(),"chat.player.uvebeeninvited",vars);
		}
	
		return true;
	}
}