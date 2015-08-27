package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildRequiredItems implements Executor {
	private final Commands command;

	public CommandGuildRequiredItems(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		Player player = (Player)sender;

		List<ItemStack> requiredItems = plugin.getGroupManager().getGroup(sender).getGuildCreateItems();
		int size = 9 * Math.round(requiredItems.size() / 9);
		if(size == 0) {
			size = 9;
		}

		Inventory inventory = plugin.getServer().createInventory(null, size, Message.INVENTORY_REQUIREDITEMS_NAME.get());

		for(ItemStack item : requiredItems) {
			inventory.addItem(item);
		}

		player.openInventory(inventory);
	}
}
