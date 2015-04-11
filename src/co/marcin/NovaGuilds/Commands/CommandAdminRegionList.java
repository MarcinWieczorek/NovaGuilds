package co.marcin.NovaGuilds.Commands;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaRegion;

public class CommandAdminRegionList implements CommandExecutor {
	public NovaGuilds plugin;

	public CommandAdminRegionList(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.region.list")) {
			plugin.sendMessagesMsg(sender,"chat.region.list.header");
			HashMap<String,String> vars = new HashMap<String,String>();
			for(Entry<String, NovaRegion> r : plugin.getRegionManager().getRegions()) {
				NovaRegion region = r.getValue();
				vars.put("GUILDNAME",region.getGuildName());
				vars.put("X",region.getCorner(0).getBlockX()+"");
				vars.put("Z",region.getCorner(0).getBlockZ()+"");
				plugin.sendMessagesMsg(sender,"chat.region.list.item", vars);
				vars.clear();
			}
		}
		else {
			plugin.sendMessagesMsg(sender, "chat.nopermissions");
		}
		return true;
	}
}
