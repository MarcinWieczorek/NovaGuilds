package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildRequiredItems implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildRequiredItems(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.requireditems")) {

			return true;
		}

		if(!(sender instanceof Player)) {

			return true;
		}

		Player player = (Player)sender;

		List<ItemStack> requiredItems = plugin.getGroupManager().getGroup(sender).getGuildCreateItems();
		int size = 9 * Math.round(requiredItems.size() / 9);
		if(size == 0) {
			size = 9;
		}

		Inventory inventory = plugin.getServer().createInventory(null,size,plugin.getMessageManager().getMessagesString("inventory.requireditems.name"));

		for(ItemStack item : requiredItems) {
			inventory.addItem(item);
		}

		player.openInventory(inventory);

		return true;
	}
}
