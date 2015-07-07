package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAdminRegionTeleport implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminRegionTeleport(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.region.delete")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length == 0) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.entername");
			return true;
		}

		String guildname = args[0];
		String playername;
		NovaPlayer nPlayerOther = null;

		if(args.length > 1) { //other
			playername = args[1];

			 nPlayerOther = plugin.getPlayerManager().getPlayer(playername);
			if(nPlayerOther == null) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notexists");
				return true;
			}

			if(!nPlayerOther.isOnline()) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notonline");
				return true;
			}
		}

		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);

		if(guild == null) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}

		if(!guild.hasRegion()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.hasnoregion");
			return true;
		}

		NovaRegion region = plugin.getRegionManager().getRegion(guild);

		if(!(sender instanceof Player) && nPlayerOther == null) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());

		Location location = region.getCorner(0).clone();
		location.setY(location.getWorld().getHighestBlockYAt(location));

		Player player;

		if(nPlayerOther != null) {
			player = nPlayerOther.getPlayer();
			plugin.getMessageManager().sendMessagesMsg(player,"chat.admin.region.teleport.notifyother",vars);
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.region.teleport.successother",vars);
		}
		else {
			player = (Player) sender;
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.region.teleport.success",vars);
		}

		player.teleport(location);
		return true;
	}
}
