package co.marcin.novaguilds.command;

import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandGuildHome implements CommandExecutor {
private final NovaGuilds plugin;
	
	public CommandGuildHome(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.home")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
			return true;
		}

		Player player = (Player)sender;

		if(args.length>0 && args[0].equalsIgnoreCase("set")) {
			if(!sender.hasPermission("novaguilds.guild.home.set")) {
				plugin.getMessageManager().sendNoPermissionsMessage(sender);
				return true;
			}

			if(!nPlayer.isLeader()) {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notleader");
				return true;
			}

			NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(player.getLocation());

			if(rgatloc==null && nPlayer.getGuild().hasRegion()) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.sethomeoutside");
				return true;
			}

			if(!nPlayer.getGuild().hasRegion() && rgatloc != null) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.guildatlocsp");
				return true;
			}

			nPlayer.getGuild().setSpawnPoint(player.getLocation());
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.setspawnpoint");
		}
		else {
			//items
			List<ItemStack> homeItems = plugin.getGroupManager().getGroup(sender).getGuildHomeItems();
			if(!homeItems.isEmpty()) {
				List<ItemStack> missingItems = ItemStackUtils.getMissingItems(player, homeItems);
				if(missingItems.size() > 0) {
					//TODO: list missing items and test messages/make other msgs
					String itemlist = "";
					int i = 0;
					for(ItemStack missingItemStack : missingItems) {
						String itemrow = plugin.getMessageManager().getMessagesString("chat.createguild.itemlist");
						itemrow = StringUtils.replace(itemrow, "{ITEMNAME}", missingItemStack.getType().name());
						itemrow = StringUtils.replace(itemrow, "{AMOUNT}", missingItemStack.getAmount() + "");

						itemlist += itemrow;

						if(i<missingItems.size()-1) itemlist+= plugin.getMessageManager().getMessagesString("chat.createguild.itemlistsep");
						i++;
					}

					plugin.getMessageManager().sendMessagesMsg(sender, "chat.createguild.noitems");
					sender.sendMessage(StringUtils.fixColors(itemlist));
					return true;
				}
			}

			//money
			double homeMoney = plugin.getGroupManager().getGroup(sender).getGuildHomeMoney();
			if(homeMoney > 0) {
				if(plugin.econ.getBalance((Player) sender) < homeMoney) {
					//TODO not enought money
					String rmmsg = plugin.getMessageManager().getMessagesString("chat.createguild.notenoughmoney");
					rmmsg = StringUtils.replace(rmmsg, "{REQUIREDMONEY}", homeMoney + "");
					plugin.getMessageManager().sendMessagesMsg(sender, rmmsg);
					return true;
				}
			}

			plugin.econ.withdrawPlayer((Player)sender,homeMoney);
			ItemStackUtils.takeItems(player, homeItems);
			plugin.delayedTeleport(player, nPlayer.getGuild().getSpawnPoint(), "chat.guild.tp");
		}
		return true;
	}
}
