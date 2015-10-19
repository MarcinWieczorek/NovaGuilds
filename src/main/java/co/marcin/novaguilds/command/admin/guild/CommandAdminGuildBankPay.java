package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.interfaces.ExecutorReversedAdminGuild;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildBankPay implements Executor, ExecutorReversedAdminGuild {
	private NovaGuild guild;
	private final Commands command;

	public CommandAdminGuildBankPay(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void guild(NovaGuild guild) {
		this.guild = guild;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(args.length != 1) {
			Message.CHAT_USAGE_NGA_GUILD_BANK_PAY.send(sender);
			return;
		}

		String money_str = args[0];

		if(!NumberUtils.isNumeric(money_str)) {
			Message.CHAT_ENTERINTEGER.send(sender);
			return;
		}

		double money = Double.parseDouble(money_str);

		if(money < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		money = NumberUtils.roundOffTo2DecPlaces(money);

		guild.addMoney(money);

		HashMap<String,String> vars = new HashMap<>();
		vars.put("MONEY",money_str);
		vars.put("GUILDNAME",guild.getName());
		Message.CHAT_ADMIN_GUILD_BANK_PAID.vars(vars).send(sender);
	}
}
