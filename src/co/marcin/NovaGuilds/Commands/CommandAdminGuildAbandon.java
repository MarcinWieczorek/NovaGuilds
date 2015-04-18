package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class CommandAdminGuildAbandon implements CommandExecutor {
	private static NovaGuilds plugin;
	
	public CommandAdminGuildAbandon(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.abandon")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length == 0) {
			plugin.sendMessagesMsg(sender,"chat.usage.nga.guild.abandon");
			return true;
		}

		String guildname = args[0];
		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);

		if(guild != null) {
			if(guild.hasRegion()) {
				plugin.getRegionManager().removeRegion(guild.getRegion());
			}

			plugin.getGuildManager().deleteGuild(guild);
			plugin.updateTabAll();
			plugin.updateTagAll();

			HashMap<String,String> vars = new HashMap<>();
			vars.put("PLAYERNAME",sender.getName());
			vars.put("GUILDNAME",guild.getName());
			plugin.broadcastMessage("broadcast.admin.guild.abandon", vars);
		}
		else {
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.guild.couldnotfind")));
		}
		return true;
	}
	
}
