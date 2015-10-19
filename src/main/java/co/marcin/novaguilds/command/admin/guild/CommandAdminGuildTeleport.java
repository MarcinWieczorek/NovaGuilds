package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAdminGuildTeleport implements Executor, ExecutorReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command;

	public CommandAdminGuildTeleport(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		Location home = guild.getSpawnPoint();

		Player player = (Player)sender;
		boolean other = false;

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME",guild.getName());

		if(args.length == 1) {
			if(!Permission.NOVAGUILDS_ADMIN_GUILD_TELEPORT_OTHER.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			String playerName = args[0];
			NovaPlayer nPlayerOther = plugin.getPlayerManager().getPlayer(playerName);

			if(nPlayerOther == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			if(!nPlayerOther.isOnline()) {
				Message.CHAT_PLAYER_NOTONLINE.send(sender);
				return;
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
	}
}
