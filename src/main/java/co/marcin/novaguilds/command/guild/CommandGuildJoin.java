package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.InventoryUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class CommandGuildJoin implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildJoin(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		if(!sender.hasPermission("novaguilds.guild.join")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		List<NovaGuild> invitedTo = nPlayer.getInvitedTo();
		
		if(nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASGUILD.send(sender);
			return true;
		}

		if(invitedTo.isEmpty() && args.length != 1) {
			Message.CHAT_PLAYER_INVITE_LIST_NOTHING.send(sender);
			return true;
		}

		String guildname;

		//one or more guilds
		if(invitedTo.size()==1) {
			if(args.length == 0) {
				guildname = invitedTo.get(0).getName();
			}
			else {
				guildname = args[0];
			}
		}
		else {
			if(args.length == 0) {
				Message.CHAT_PLAYER_INVITE_LIST_HEADER.send(sender);

				String invitedlist = "";
				int i = 0;
				for(NovaGuild invitedGuild : invitedTo) {
					String itemrow = Message.CHAT_PLAYER_INVITE_LIST_ITEM.get();
					itemrow = StringUtils.replace(itemrow, "{GUILDNAME}", invitedGuild.getName());
					itemrow = StringUtils.replace(itemrow, "{TAG}", invitedGuild.getTag());

					invitedlist += itemrow;

					if(i<invitedTo.size()-1) {
						invitedlist += Message.CHAT_PLAYER_INVITE_LIST_SEPARATOR.get();
					}
					i++;
				}

				sender.sendMessage(StringUtils.fixColors(invitedlist));
				return true;
			}
			else {
				guildname = args[0];
			}
		}

		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);

		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return true;
		}

		if(!nPlayer.isInvitedTo(guild) && !guild.isOpenInvitation()) {
			Message.CHAT_PLAYER_INVITE_NOTINVITED.send(sender);
			return true;
		}

		//items
		List<ItemStack> joinItems = plugin.getGroupManager().getGroup(sender).getGuildJoinItems();
		if(!joinItems.isEmpty()) {
			List<ItemStack> missingItems = InventoryUtils.getMissingItems(((Player) sender).getInventory(), joinItems);
			if(!missingItems.isEmpty()) {
				//TODO: list missing items and test messages/make other msgs
				String itemlist = "";
				int i = 0;
				for(ItemStack missingItemStack : missingItems) {
					String itemrow = Message.CHAT_CREATEGUILD_ITEMLIST.get();
					itemrow = StringUtils.replace(itemrow, "{ITEMNAME}", missingItemStack.getType().name());
					itemrow = StringUtils.replace(itemrow, "{AMOUNT}", missingItemStack.getAmount() + "");

					itemlist += itemrow;

					if(i<missingItems.size()-1) {
						itemlist += Message.CHAT_CREATEGUILD_ITEMLISTSEP.get();
					}
					i++;
				}

				Message.CHAT_CREATEGUILD_NOITEMS.send(sender);
				sender.sendMessage(StringUtils.fixColors(itemlist));
				return true;
			}
		}

		HashMap<String, String> vars = new HashMap<>();

		//money
		double joinMoney = plugin.getGroupManager().getGroup(sender).getGuildJoinMoney();
		if(joinMoney > 0) {
			//if(plugin.econ.getBalance((Player) sender) < joinMoney) { //1.8
			if(!nPlayer.hasMoney(joinMoney)) {
				//TODO not enought money msg
				vars.put("{REQUIREDMONEY}", joinMoney + "");
				Message.CHAT_GUILD_NOTENOUGHMONEY.vars(vars).send(sender);
				return true;
			}
		}

		if(joinItems.size() > 0) {
			InventoryUtils.removeItems((Player) sender, joinItems);
		}

		if(joinMoney > 0) {
			nPlayer.takeMoney(joinMoney);
		}

		if(guild.isFull()) {
			Message.CHAT_GUILD_ISFULL.send(sender);
			return true;
		}

		guild.addPlayer(nPlayer);
		nPlayer.setGuild(guild);
		nPlayer.deleteInvitation(guild);
		plugin.tagUtils.refreshAll();
		Message.CHAT_GUILD_JOINED.send(sender);
		guild.showVaultHologram(nPlayer.getPlayer());

		vars.clear();
		vars.put("PLAYER",sender.getName());
		vars.put("GUILDNAME",guild.getName());
		Message.BROADCAST_GUILD_JOINED.vars(vars).broadcast();
		return true;
	}
}
