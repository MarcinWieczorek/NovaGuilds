package co.marcin.NovaGuilds.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.NovaRegion;

public class CommandAdminGuildSetName implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminGuildSetName(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.setname")) {
			plugin.sendMessagesMsg(sender, "chat.nopermissions");
			return true;
		}
		
		if(args.length==0) { //no guild name
			plugin.sendMessagesMsg(sender, "chat.guild.entername");
			return true;
		}
		
		if(args.length==1) { //no new name
			plugin.sendMessagesMsg(sender, "chat.admin.guild.setname.enternewname");
			return true;
		}
		
		String guildname = args[0];
		String newname = args[1];
		
		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
		
		if(!(guild instanceof NovaGuild)) { //guild doesn't exist
			plugin.sendMessagesMsg(sender, "chat.guild.namenotexist");
			return true;
		}
		
		if(newname.length() < plugin.getConfig().getInt("guild.settings.name.min")) { //too short name
			plugin.sendMessagesMsg(sender, "chat.createguild.name.tooshort");
			return true;
		}
		
		if(newname.length() > plugin.getConfig().getInt("guild.settings.name.max")) { //too long name
			plugin.sendMessagesMsg(sender, "chat.createguild.name.toolong");
			return true;
		}
		
		if(plugin.getGuildManager().exists(newname)) { //name exists
			plugin.sendMessagesMsg(sender, "chat.createguild.nameexists");
			return true;
		}
		
		
		//all passed
		if(guild.hasRegion()) {
			NovaRegion region = plugin.getRegionManager().getRegionByGuild(guild);
			region.setGuildName(newname);
			plugin.getRegionManager().saveRegion(region);
		}
		
		guild.setName(newname);
		plugin.getGuildManager().changeName(guild, newname);
		
		//update players
		for(NovaPlayer nP : guild.getPlayers()) {
			nP.setGuild(guild);
			plugin.getPlayerManager().updateLocalPlayer(nP);
		}
		
		plugin.sendMessagesMsg(sender, "chat.admin.guild.setname.success");
		
		return true;
	}
}
