package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGroup;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.command.admin.CommandAdmin;
import co.marcin.novaguilds.command.guild.CommandGuild;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.VersionUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class CommandNovaGuilds implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandNovaGuilds(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			Map<String, String[]> langInfo = new HashMap<>();

			langInfo.put("en-en", new String[]{
					Message.CHAT_PREFIX.get()+"NovaGuilds Information",
					"&2NovaGuilds &6#&c"+ VersionUtils.buildCurrent,
					"&2Author: &6Marcin (CTRL) Wieczorek",
					"&22015 &4Pol&fand",
					"&bhttp://novaguilds.pl/",
					"&2Latest plugin build: &6#&c" + VersionUtils.buildLatest
			});

			langInfo.put("pl-pl", new String[]{
					Message.CHAT_PREFIX.get()+"NovaGuilds Informacje",
					"&2NovaGuilds &6#&c"+ VersionUtils.buildCurrent,
					"&2Autor: &6Marcin (CTRL) Wieczorek",
					"&22015 &4Pol&fska",
					"&bhttp://novaguilds.pl/",
					"&2Najnowsza wersja pluginu: &6#&c" + VersionUtils.buildLatest
			});

			String[] info = langInfo.get(Config.LANG.getString());

			for(String i : info) {
				sender.sendMessage(StringUtils.fixColors(i));
			}

			return true;
		}

		if(args[0].equalsIgnoreCase("tool")) { //TOOL
			plugin.getCommandManager().getExecutor(Commands.TOOL_GET).execute(sender, args);
		}
		else if(args[0].equalsIgnoreCase("bank")) { //bank
			if(!sender.hasPermission("novaguilds.test.bank")) {
				Message.CHAT_NOPERMISSIONS.send(sender);
				return true;
			}

			if(sender instanceof Player) {
				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
				if(nPlayer.hasGuild()) {
					((Player) sender).getInventory().addItem(plugin.getConfigManager().getGuildVaultItem());
				}
			}
		}
		else if(args[0].equalsIgnoreCase("admin")) { //Admin commands
			new CommandAdmin(plugin).onCommand(sender, cmd, label, StringUtils.parseArgs(args, 1));
		}
		else if(args[0].equalsIgnoreCase("group")) { //Admin commands
			NovaGroup group = plugin.getGroupManager().getGroup(sender);

			if(args.length > 1) {
				group = plugin.getGroupManager().getGroup(args[1]);
				if(group == null) {
					sender.sendMessage("Invalid group");
					return true;
				}
			}

			sender.sendMessage("name = "+group.getName());
			sender.sendMessage("guildCreateMoney = "+group.getGuildCreateMoney());
			sender.sendMessage("guildHomeMoney = "+group.getGuildHomeMoney());
			sender.sendMessage("guildJoinMoney = "+group.getGuildJoinMoney());
			sender.sendMessage("guildCreateItems = " + group.getGuildCreateItems().toString());
			sender.sendMessage("guildHomeItems = " + group.getGuildHomeItems().toString());
			sender.sendMessage("guildJoinItems = " + group.getGuildJoinItems().toString());
			sender.sendMessage("guildTeleportDelay = "+ group.getGuildTeleportDelay()+"s");
			sender.sendMessage("regionCreateMoney = "+ group.getRegionCreateMoney());
			sender.sendMessage("regionPricePerBlock = "+ group.getRegionPricePerBlock());
		}
		else if(args[0].equalsIgnoreCase("guild") || args[0].equalsIgnoreCase("g")) { // command /g
				new CommandGuild(plugin).onCommand(sender, cmd, label, StringUtils.parseArgs(args, 1));
		}
		else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { //help
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
			BookMeta bm = (BookMeta)book.getItemMeta();
			//plugin.getMessageManager().loadMessages();
			List<String> pages = plugin.getMessageManager().getMessages().getStringList("book.help.pages");
			List<String> pagesColor = new ArrayList<>();
			for(String page : pages) {
				pagesColor.add(StringUtils.fixColors(page));
			}

			bm.setPages(pagesColor);
			bm.setAuthor("CTRL");
			bm.setTitle(StringUtils.fixColors(MessageManager.getMessagesString("book.help.title")));
			book.setItemMeta(bm);
			Player player = (Player) sender;
			player.getInventory().addItem(book);
		}
		else {
			Message.CHAT_UNKNOWNCMD.send(sender);
		}
		
		return true;
	}
}
