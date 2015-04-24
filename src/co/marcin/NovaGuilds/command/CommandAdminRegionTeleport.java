package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAdminRegionTeleport implements CommandExecutor {
	public final NovaGuilds plugin;

	public CommandAdminRegionTeleport(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("NovaGuilds.admin.region.delete")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length == 0) {
			plugin.sendMessagesMsg(sender,"chat.guild.entername");
			return true;
		}

		String guildname = args[0];

		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

		if(!(guild instanceof NovaGuild)) {
			plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}

		if(!guild.hasRegion()) {
			plugin.sendMessagesMsg(sender,"chat.guild.hasnoregion");
			return true;
		}

		NovaRegion region = plugin.getRegionManager().getRegionByGuild(guild);

		if(!(sender instanceof Player)) {
			plugin.sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		Player player = plugin.senderToPlayer(sender);
		player.teleport(region.getCorner(0));
		plugin.sendMessagesMsg(sender,"chat.admin.region.teleported");
		return true;
	}
}
