package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildBankPay implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildBankPay(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		Player player = (Player)sender;
		
		if(!sender.hasPermission("novaguilds.guild.bank.pay")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length==0 || !NumberUtils.isNumeric(args[0])) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.enteramount");
			return true;
		}

		Double money = Double.parseDouble(args[0]);

		if(plugin.econ.getBalance(player) < money) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.pay.notenough");
			return true;
		}

		plugin.econ.withdrawPlayer(player,money);
		guild.addMoney(money);
		HashMap<String,String> vars = new HashMap<>();
		vars.put("AMOUNT",money+"");
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.pay.paid",vars);

		return true;
	}
}
