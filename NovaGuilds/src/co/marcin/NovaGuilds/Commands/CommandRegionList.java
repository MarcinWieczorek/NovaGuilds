package co.marcin.NovaGuilds.Commands;

import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaRegion;
import co.marcin.NovaGuilds.Utils;

public class CommandRegionList implements CommandExecutor {
	public NovaGuilds plugin;
	
	public CommandRegionList(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.region.list")) {
			for(Entry<String, NovaRegion> r : plugin.getRegionManager().getRegions()) {
				NovaRegion region = r.getValue();
				sender.sendMessage(region.getGuildName()+" - "+Utils.parseDBLocationCoords2D(region.getCorner(0)));
			}
		}
		else {
			plugin.sendMessagesMsg(sender, "chat.nopermissions");
		}
		return true;
	}
}
