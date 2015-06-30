package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildBankPay implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildBankPay(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	/*
	* args:
	*  0 - guildname
	*  1 - money
	* */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.bank.pay")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length != 1) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.usage.nga.guild.bank.pay");
			return true;
		}

		String money_str = args[0];

		if(!StringUtils.isNumeric(money_str)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		double money = Double.parseDouble(money_str);

		guild.addMoney(money);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("MONEY",money_str);
		vars.put("GUILDNAME",guild.getName());
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.bank.paid",vars);

		return true;
	}
}
