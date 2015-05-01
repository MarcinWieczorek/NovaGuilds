package co.marcin.NovaGuilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;

import java.util.HashMap;

public class CommandAdminGuildInvite implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdminGuildInvite(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("NovaGuilds.admin.guild.setname")) { //no perms
			return true;
		}
		
		if(args.length == 0) { //no guild name
			plugin.sendMessagesMsg(sender,"chat.guild.entername");
			return true;
		}
		
		if(args.length == 1) { //no player name
			plugin.sendMessagesMsg(sender,"chat.player.entername");
			return true;
		}
		
		String guildname = args[0];
		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
		
		if(guild == null) {
			plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}
		
		String playername = args[1];
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
		plugin.getPlayerManager().updateLocalPlayer(nPlayer);
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