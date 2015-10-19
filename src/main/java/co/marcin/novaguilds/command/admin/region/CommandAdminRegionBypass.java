package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminRegionBypass implements Executor {
	private final Commands command;

	public CommandAdminRegionBypass(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	/*
	* Changing bypass
	* no args - for sender
	* args[0] - for specified player
	* */
	@Override
	public void execute(CommandSender sender, String[] args) {
		HashMap<String,String> vars = new HashMap<>();

		if(args.length==0 || args[0].equalsIgnoreCase(sender.getName())) {
			if(!command.hasPermission(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

			nPlayer.toggleBypass();
			vars.put("BYPASS", Message.getOnOff(nPlayer.getBypass()));
			Message.CHAT_ADMIN_REGION_BYPASS_TOGGLED_SELF.vars(vars).send(sender);
		}
		else { //for other
			if(!Permission.NOVAGUILDS_ADMIN_REGION_BYPASS_OTHER.has(sender)) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(args[0]);

			if(nPlayer == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return;
			}

			nPlayer.toggleBypass();
			vars.put("PLAYER", nPlayer.getName());
			vars.put("BYPASS", Message.getOnOff(nPlayer.getBypass()));

			if(nPlayer.isOnline()) {
				Message.CHAT_ADMIN_REGION_BYPASS_NOTIFYOTHER.vars(vars).send(sender);
			}

			Message.CHAT_ADMIN_REGION_BYPASS_TOGGLED_OTHER.vars(vars).send(sender);
		}
	}
}
