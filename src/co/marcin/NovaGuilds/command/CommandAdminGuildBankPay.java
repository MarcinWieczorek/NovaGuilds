package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildBankPay implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandAdminGuildBankPay(NovaGuilds pl) {
		plugin = pl;
	}

	/*
	* args:
	*  0 - guildname
	*  1 - money
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.bank.pay")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length != 2) {
			plugin.sendMessagesMsg(sender,"chat.usage.nga.guild.bank.pay");
			return true;
		}

		String guildname = args[0];
		String money_str = args[1];

		if(!StringUtils.isNumeric(money_str)) {
			plugin.sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		double money = Double.parseDouble(money_str);

		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

		if(guild == null) {
			guild = plugin.getGuildManager().getGuildByTag(guildname);
		}

		if(guild == null) {
			plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			return true;
		}

		guild.addMoney(money);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("MONEY",money_str);
		vars.put("GUILDNAME",guild.getName());
		plugin.sendMessagesMsg(sender,"chat.admin.guild.bank.paid",vars);

		return true;
	}
}
