package co.marcin.novaguilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;

public class CommandGuildHome implements CommandExecutor {
private final NovaGuilds plugin;
	
	public CommandGuildHome(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.consolesender");
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
		}

		Player player = (Player)sender;

		if(args.length>0 && args[0].equalsIgnoreCase("set")) {
			if(!nPlayer.isLeader()) {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notleader");
			}

			NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(player.getLocation());

			if(rgatloc==null && nPlayer.getGuild().hasRegion()) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.sethomeoutside");
				return true;
			}

			if(!nPlayer.getGuild().hasRegion() && rgatloc != null) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.guildatlocsp");
				return true;
			}

			nPlayer.getGuild().setSpawnPoint(player.getLocation());
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.setspawnpoint");
		}
		else {
			if(nPlayer.getGuild().getSpawnPoint() != null) {
				plugin.delayedTeleport(player, nPlayer.getGuild().getSpawnPoint(), "chat.guild.tp");
			}
		}
		return true;
	}
}
