package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandGuildMenu implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildMenu(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {

			return true;
		}

		if(!sender.hasPermission("novaguilds.guild.gui")) {

			return true;
		}

		Player player = (Player)sender;

		Inventory inv = Bukkit.createInventory(null,9,plugin.getMessageManager().getMessagesString("inventory.ggui.name"));
		for(ItemStack item : plugin.getCommandManager().getGuiItems()) {
			inv.addItem(item);
		}
		player.openInventory(inv);
		return true;
	}
}
