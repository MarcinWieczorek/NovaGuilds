package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildBankWithdraw implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildBankWithdraw(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.bank.withdraw")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		if(args.length != 1) {
			Message.CHAT_GUILD_BANK_ENTERAMOUNT.send(sender);
			return true;
		}

		String moneyString = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_BANK_WITHDRAW_NOTLEADER.send(sender);
			return true;
		}

		if(!NumberUtils.isNumeric(moneyString)) {
			Message.CHAT_ENTERINTEGER.send(sender);
			return true;
		}

		double money = Double.parseDouble(moneyString);
		money = NumberUtils.roundOffTo2DecPlaces(money);

		if(guild.getMoney() < money) {
			Message.CHAT_GUILD_BANK_WITHDRAW_NOTENOUGH.send(sender);
			return true;
		}

		guild.takeMoney(money);
		plugin.econ.depositPlayer((Player) sender, money);
		HashMap<String,String> vars = new HashMap<>();
		vars.put("AMOUNT",money+"");
		Message.CHAT_GUILD_BANK_WITHDRAW_SUCCESS.vars(vars).send(sender);
		return true;
	}

}
