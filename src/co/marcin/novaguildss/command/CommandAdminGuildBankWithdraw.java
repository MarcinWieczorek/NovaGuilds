package co.marcin.novaguildss.command;

import co.marcin.novaguildss.basic.NovaGuild;
import co.marcin.novaguildss.NovaGuilds;
import co.marcin.novaguildss.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildBankWithdraw implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildBankWithdraw(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	/*
	* args:
	*  0 - guildname
	*  1 - money
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.bank.withdraw")) { //no permissions
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length != 1) { //invalid arguments
			plugin.sendMessagesMsg(sender,"chat.usage.nga.guild.bank.withdraw");
			return true;
		}

		String money_str = args[0];

		if(!StringUtils.isNumeric(money_str)) { //money not int
			plugin.sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		double money = Double.parseDouble(money_str);

		if(guild.getMoney() < money) { //guild has not enought money
			plugin.sendMessagesMsg(sender,"chat.guild.bank.withdraw.notenought");
			return true;
		}

		guild.takeMoney(money);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("MONEY",money_str);
		vars.put("GUILDNAME",guild.getName());
		plugin.sendMessagesMsg(sender,"chat.admin.guild.bank.withdrew",vars);

		return true;
	}
}
