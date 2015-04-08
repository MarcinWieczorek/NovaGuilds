package co.marcin.NovaGuilds.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;

public class CommandAdminGuildInvite implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandAdminGuildInvite(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.setname")) { //no perms
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
		
		if(!(guild instanceof NovaGuild)) {
			plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}
		
		String playername = args[1];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(playername);
		
		if(!(nPlayer instanceof NovaPlayer)) { //noplayer
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
		
		if(nPlayer.getPlayer() instanceof Player) {
				plugin.sendMessagesMsg(nPlayer.getPlayer(),"chat.player.uvebeeninvited");
		}
	
		return true;
	}
}