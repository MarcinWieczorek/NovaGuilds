package co.marcin.novaguilds.command.admin.region;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminRegionBypass implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminRegionBypass(NovaGuilds pl) {
		plugin = pl;
	}

	/*
	* Changing bypass
	* no args - for sender
	* args[1] - for specified player
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HashMap<String,String> vars = new HashMap<>();

		if(args.length==0) {
			if(!sender.hasPermission("novaguilds.admin.region.bypass")) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return true;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

			nPlayer.toggleBypass();
			vars.put("BYPASS",Message.getOnOff(nPlayer.getBypass()));
			Message.CHAT_ADMIN_REGION_BYPASS_TOGGLED_SELF.vars(vars).send(sender);
		}
		else { //for other
			if(sender.hasPermission("novaguilds.admin.region.bypass.other")) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return true;
			}

			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(args[0]);

			if(nPlayer == null) {
				Message.CHAT_PLAYER_NOTEXISTS.send(sender);
				return true;
			}

			nPlayer.toggleBypass();
			vars.put("PLAYER",nPlayer.getName());
			vars.put("BYPASS",Message.getOnOff(nPlayer.getBypass()));

			if(nPlayer.isOnline()) {
				Message.CHAT_ADMIN_REGION_BYPASS_NOTIFYOTHER.vars(vars).send(sender);
			}

			Message.CHAT_ADMIN_REGION_BYPASS_TOGGLED_OTHER.vars(vars).send(sender);
		}
		return true;
	}
}
