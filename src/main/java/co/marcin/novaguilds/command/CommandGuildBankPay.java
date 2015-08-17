package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildBankPay implements Executor {
	private final Commands command;

	public CommandGuildBankPay(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		Player player = (Player)sender;
		
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length==0 || !NumberUtils.isNumeric(args[0])) {
			Message.CHAT_GUILD_BANK_ENTERAMOUNT.send(sender);
			return;
		}

		Double money = Double.parseDouble(args[0]);

		money = NumberUtils.roundOffTo2DecPlaces(money);

		if(money < 0) {
			Message.CHAT_BASIC_NEGATIVENUMBER.send(sender);
			return;
		}

		if(!nPlayer.hasMoney(money)) {
			Message.CHAT_GUILD_BANK_PAY_NOTENOUGH.send(sender);
			return;
		}

		nPlayer.takeMoney(money);
		guild.addMoney(money);
		HashMap<String,String> vars = new HashMap<>();
		vars.put("AMOUNT",money+"");
		Message.CHAT_GUILD_BANK_PAY_PAID.vars(vars).send(sender);
	}
}
