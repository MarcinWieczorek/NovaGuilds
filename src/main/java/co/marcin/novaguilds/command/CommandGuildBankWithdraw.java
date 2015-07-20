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
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.cmdfromconsole");
			return true;
		}

		if(args.length != 1) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.enteramount");
			return true;
		}

		String moneyString = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notinguild");
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.isLeader()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.withdraw.notleader");
			return true;
		}

		if(!NumberUtils.isNumeric(moneyString)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.enterinteger");
			return true;
		}

		double money = Double.parseDouble(moneyString);
		money = NumberUtils.roundOffTo2DecPlaces(money);

		if(guild.getMoney() < money) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.withdraw.notenough");
			return true;
		}

		guild.takeMoney(money);
		plugin.econ.depositPlayer((Player)sender, money);
		HashMap<String,String> vars = new HashMap<>();
		vars.put("AMOUNT",money+"");
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.withdraw.success",vars);
		return true;
	}

}
