package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.util.NumberUtils;
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
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length != 1) { //invalid arguments
			plugin.getMessageManager().sendUsageMessage(sender,"nga.guild.bank.withdraw");
			return true;
		}

		String money_str = args[0];

		if(!NumberUtils.isNumeric(money_str)) { //money not int
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		double money = Double.parseDouble(money_str);

		if(money < 0) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.basic.negativenumber");
			return true;
		}

		if(guild.getMoney() < money) { //guild has not enough money
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.withdraw.notenough");
			return true;
		}

		money = NumberUtils.roundOffTo2DecPlaces(money);

		guild.takeMoney(money);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("MONEY",money_str);
		vars.put("GUILDNAME",guild.getName());
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.bank.withdrew",vars);

		return true;
	}
}
