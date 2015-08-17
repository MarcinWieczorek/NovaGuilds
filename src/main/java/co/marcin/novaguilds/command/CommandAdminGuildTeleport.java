package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		Location home = guild.getSpawnPoint();

		Player player = (Player)sender;
		boolean other = false;

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());

		if(args.length==1) {
			if(!sender.hasPermission("novaguilds.admin.guild.other")) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return true;
			}

			String playerName = args[0];
			NovaPlayer nPlayerOther = plugin.getPlayerManager().getPlayer(playerName);

			if(nPlayerOther == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return true;
			}

			if(!nPlayerOther.isOnline()) {
				Message.CHAT_PLAYER_NOTONLINE.send(sender);
				return true;
			}

			player = nPlayerOther.getPlayer();
			other = true;
		}

		if(other) {
			vars.put("PLAYERNAME", player.getName());
			Message.CHAT_ADMIN_GUILD_TELEPORTED_OTHER.vars(vars).send(sender);
		}
		else {
			Message.CHAT_ADMIN_GUILD_TELEPORTED_SELF.vars(vars).send(sender);
		}

		player.teleport(home);
		return true;
	}
}
