package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToolGet implements CommandExecutor {
	private final NovaGuilds plugin;
	private final Commands command = Commands.TOOL_GET;
	
	public CommandToolGet(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		Material tool = Material.getMaterial(plugin.getConfig().getString("region.tool.item").toUpperCase());

		if(tool != null) {

	        Player player = plugin.getServer().getPlayer(sender.getName());
			player.getInventory().addItem(plugin.getConfigManager().getToolItem());
		}
		return true;
	}
}
