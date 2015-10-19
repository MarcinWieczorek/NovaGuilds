package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminRegion;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAdminRegionTeleport implements Executor, ExecutorReversedAdminRegion {
	private final Commands command;
	private NovaRegion region;

	public CommandAdminRegionTeleport(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void region(NovaRegion region) {
		this.region = region;
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

		NovaPlayer nPlayerOther;
		Player player;

		if(args.length > 1) { //other
			 nPlayerOther = plugin.getPlayerManager().getPlayer(args[1]);
			if(nPlayerOther == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			if(!nPlayerOther.isOnline()) {
				Message.CHAT_PLAYER_NOTONLINE.send(sender);
				return;
			}

			player = nPlayerOther.getPlayer();
		}
		else {
			player = (Player) sender;
		}

		HashMap<String,String> vars = new HashMap<>();
		vars.put("GUILDNAME", region.getGuild().getName());

		Location location = region.getCorner(0).clone();
		location.setY(location.getWorld().getHighestBlockYAt(location));

		if(!player.equals(sender)) {
			Message.CHAT_ADMIN_REGION_TELEPORT_OTHER.vars(vars).send(sender);
			Message.CHAT_ADMIN_REGION_TELEPORT_NOTIFYOTHER.vars(vars).send(player);
		}
		else {
			Message.CHAT_ADMIN_REGION_TELEPORT_SELF.vars(vars).send(sender);
		}

		player.teleport(location);
	}
}
