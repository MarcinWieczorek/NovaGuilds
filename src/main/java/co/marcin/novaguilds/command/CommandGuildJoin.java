package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.util.ItemStackUtils;
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
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		if(!sender.hasPermission("novaguilds.guild.join")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		List<String> invitedTo = nPlayer.getInvitedTo();
		
		if(nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.createguild.hasguild");
			return true;
		}

		if(invitedTo.size() == 0) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.invitedtonothing");
			return true;
		}

		String guildname;

		//one or more guilds
		if(invitedTo.size()==1) {
			if(args.length == 0) {
				guildname = invitedTo.get(0);
			}
			else {
				guildname = args[0];
			}
		}
		else {
			if(args.length == 0) {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.ureinvitedto");
				return true;
			}
			else {
				guildname = args[0];
			}
		}

		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);

		if(guild == null) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.namenotexist");
			return true;
		}

		if(nPlayer.isInvitedTo(guild)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.player.notinvitedtoguild");
			return true;
		}

		//items
		List<ItemStack> joinItems = plugin.getGroupManager().getGroup(sender).getGuildJoinItems();
		if(!joinItems.isEmpty()) {
			List<ItemStack> missingItems = ItemStackUtils.getMissingItems((Player)sender, joinItems);
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
			else {
				ItemStackUtils.takeItems((Player)sender,joinItems);
			}
		}

		//money
		double joinMoney = plugin.getGroupManager().getGroup(sender).getGuildJoinMoney();
		if(joinMoney > 0) {
			if(plugin.econ.getBalance((Player) sender) >= joinMoney) {
				plugin.econ.withdrawPlayer((Player)sender,joinMoney);
			}
			else {
				//TODO not enought money msg
				String rmmsg = plugin.getMessageManager().getMessagesString("chat.createguild.notenoughmoney");
				rmmsg = StringUtils.replace(rmmsg, "{REQUIREDMONEY}", joinMoney + "");
				plugin.getMessageManager().sendMessagesMsg(sender, rmmsg);
				return true;
			}
		}

		guild.addPlayer(nPlayer);
		nPlayer.setGuild(guild);
		nPlayer.deleteInvitation(guild);
		plugin.tagUtils.refreshAll();
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.joined");

		HashMap<String,String> vars = new HashMap<>();
		vars.put("PLAYER",sender.getName());
		vars.put("GUILDNAME",guild.getName());
		plugin.getMessageManager().broadcastMessage("broadcast.guild.joined", vars);
		return true;
	}
}
