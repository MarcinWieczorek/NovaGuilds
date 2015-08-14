package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Message;
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
		if(!sender.hasPermission("novaguilds.admin.region.tp")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length == 0) {
			Message.CHAT_GUILD_ENTERNAME.send(sender);
			return true;
		}

		String guildname = args[0];
		String playername;
		NovaPlayer nPlayerOther = null;

		if(args.length > 1) { //other
			playername = args[1];

			 nPlayerOther = plugin.getPlayerManager().getPlayer(playername);
			if(nPlayerOther == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return true;
			}

			if(!nPlayerOther.isOnline()) {
				Message.CHAT_PLAYER_NOTONLINE.send(sender);
				return true;
			}
		}

		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);

		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return true;
		}

		if(!guild.hasRegion()) {
			Message.CHAT_GUILD_HASNOREGION.send(sender);
			return true;
		}

		NovaRegion region = plugin.getRegionManager().getRegion(guild);

		if(!(sender instanceof Player) && nPlayerOther == null) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());

		Location location = region.getCorner(0).clone();
		location.setY(location.getWorld().getHighestBlockYAt(location));

		Player player;

		if(nPlayerOther != null) {
			player = nPlayerOther.getPlayer();
			Message.CHAT_ADMIN_REGION_TELEPORT_OTHER.vars(vars).send(sender);
			Message.CHAT_ADMIN_REGION_TELEPORT_NOTIFYOTHER.vars(vars).send(player);
		}
		else {
			player = (Player) sender;
			Message.CHAT_ADMIN_REGION_TELEPORT_SELF.vars(vars).send(sender);
		}

		player.teleport(location);
		return true;
	}
}
