package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class CommandGuildHome implements CommandExecutor {
private final NovaGuilds plugin;
	
	public CommandGuildHome(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.home")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		Player player = (Player)sender;

		if(args.length>0 && args[0].equalsIgnoreCase("set")) {
			if(!sender.hasPermission("novaguilds.guild.home.set")) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return true;
			}

			if(!nPlayer.isLeader()) {
				Message.CHAT_GUILD_NOTLEADER.send(sender);
				return true;
			}

			NovaRegion rgatloc = plugin.getRegionManager().getRegion(player.getLocation());

			if(rgatloc==null && nPlayer.getGuild().hasRegion()) {
				Message.CHAT_GUILD_SETHOME_OUTSIDEREGION.send(sender);
				return true;
			}

			if(!nPlayer.getGuild().hasRegion() && rgatloc != null) {
				Message.CHAT_GUILD_SETHOME_OVERLAPS.send(sender);
				return true;
			}

			nPlayer.getGuild().setSpawnPoint(player.getLocation());
			Message.CHAT_GUILD_SETHOME_SUCCESS.send(sender);
		}
		else {
			//items
			List<ItemStack> homeItems = plugin.getGroupManager().getGroup(sender).getGuildHomeItems();
			if(!homeItems.isEmpty()) {
				List<ItemStack> missingItems = ItemStackUtils.getMissingItems(player, homeItems);
				if(!missingItems.isEmpty()) {
					//TODO: list missing items and test messages/make other msgs
					String itemlist = "";
					int i = 0;
					for(ItemStack missingItemStack : missingItems) {
						String itemrow = plugin.getMessageManager().getMessagesString("chat.createguild.itemlist");
						itemrow = StringUtils.replace(itemrow, "{ITEMNAME}", missingItemStack.getType().name());
						itemrow = StringUtils.replace(itemrow, "{AMOUNT}", missingItemStack.getAmount() + "");

						itemlist += itemrow;

						if(i<missingItems.size()-1) {
							itemlist += plugin.getMessageManager().getMessagesString("chat.createguild.itemlistsep");
						}

						i++;
					}

					Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
					sender.sendMessage(StringUtils.fixColors(itemlist));
					return true;
				}
			}

			//money
			double homeMoney = plugin.getGroupManager().getGroup(sender).getGuildHomeMoney();
			if(homeMoney > 0) {
//				if(plugin.econ.getBalance((Player)sender) < homeMoney) { //1.8
				if(plugin.econ.getBalance(sender.getName()) < homeMoney) { //1.7
					//TODO not enought money
					HashMap<String, String> vars = new HashMap<>();
					vars.put("REQUIREDMONEY", String.valueOf(homeMoney));
					Message.CHAT_GUILD_NOTENOUGHMONEY.vars(vars).send(sender);
					return true;
				}
			}

			//plugin.econ.withdrawPlayer((Player) sender, homeMoney); //1.8
			plugin.econ.withdrawPlayer(sender.getName(), homeMoney); //1.7
			ItemStackUtils.takeItems(player, homeItems);
			Message.CHAT_GUILD_HOME.send(sender);
			plugin.getGuildManager().delayedTeleport(player, nPlayer.getGuild().getSpawnPoint(), Message.CHAT_DELAYEDTELEPORT);
		}
		return true;
	}
}
