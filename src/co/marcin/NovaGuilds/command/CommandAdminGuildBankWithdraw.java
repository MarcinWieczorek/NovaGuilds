package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildBankWithdraw implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminGuildBankWithdraw(NovaGuilds pl) {
		plugin = pl;
	}

	/*
	* args:
	*  0 - guildname
	*  1 - money
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("NovaGuilds.admin.guild.bank.withdraw")) { //no permissions
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length != 2) { //invalid arguments
			plugin.sendMessagesMsg(sender,"chat.usage.nga.guild.bank.withdraw");
			return true;
		}

		String guildname = args[0];
		String money_str = args[1];

		if(!Utils.isNumeric(money_str)) { //money not int
			plugin.sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		double money = Double.parseDouble(money_str);

		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

		if(guild == null) { //trying to find by tag
			guild = plugin.getGuildManager().getGuildByTag(guildname);
		}

		if(guild == null) { //still no guild
			plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}

		if(guild.getMoney() < money) { //guild has not enought money
			plugin.sendMessagesMsg(sender,"chat.guild.bank.withdraw.notenought");
			return true;
		}

		guild.takeMoney(money);

		plugin.getGuildManager().saveGuildLocal(guild);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("MONEY",money_str);
		vars.put("GUILDNAME",guild.getName());
		plugin.sendMessagesMsg(sender,"chat.admin.guild.bank.withdrew",vars);

		return true;
	}
}
