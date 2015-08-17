package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGroup;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildBuySlot implements Executor {
	private final Commands command;

	public CommandGuildBuySlot(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
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

		NovaPlayer nPlayer = NovaPlayer.get(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		NovaGroup group = NovaGroup.get(sender);
		double money = group.getGuildBuySlotMoney();
		List<ItemStack> items = group.getGuildBuySlotItems();

		if(money > 0 && !nPlayer.hasMoney(money)) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		if(items.size() > 0) {
			List<ItemStack> missing = InventoryUtils.getMissingItems(nPlayer.getPlayer().getInventory(), items);

			if(missing.size() > 0) {
				Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
				sender.sendMessage(StringUtils.getItemList(missing));
				return;
			}
		}

		nPlayer.getGuild().addSlot();
		Message.CHAT_GUILD_BUYSLOT.send(sender);
	}
}
