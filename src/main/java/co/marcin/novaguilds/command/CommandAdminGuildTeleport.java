package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAdminGuildTeleport implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;
	
	public CommandAdminGuildTeleport(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//args:
		// 0 - other player
		
		if(!(sender instanceof Player)) {
			LoggerUtils.info("You cannot tp to a guild from the console!");
			return true;
		}
		
		if(!sender.hasPermission("novaguilds.admin.guild.tp")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		Location home = guild.getSpawnPoint();

		Player player = (Player)sender;
		boolean other = false;

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());

		if(args.length==1) {
			String playerName = args[0];
			NovaPlayer nPlayerOther = plugin.getPlayerManager().getPlayer(playerName);

			if(nPlayerOther == null) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notexists");
				return true;
			}

			if(!nPlayerOther.isOnline()) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notonline");
				return true;
			}

			player = nPlayerOther.getPlayer();
			other = true;
		}

		if(other) {
			vars.put("PLAYERNAME",player.getName());
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.admin.guild.teleported.other", vars);
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.admin.guild.teleported.self", vars);
		}

		player.teleport(home);
		return true;
	}
}
