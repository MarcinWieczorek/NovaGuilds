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

public class CommandGuildBankPay implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildBankPay(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		Player player = (Player)sender;
		
		if(!sender.hasPermission("novaguilds.guild.bank.pay")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length==0 || !NumberUtils.isNumeric(args[0])) {
			Message.CHAT_GUILD_BANK_ENTERAMOUNT.send(sender);
			return true;
		}

		Double money = Double.parseDouble(args[0]);

		money = NumberUtils.roundOffTo2DecPlaces(money);

		if(plugin.econ.getBalance(player) < money) {
			Message.CHAT_GUILD_BANK_PAY_NOTENOUGH.send(sender);
			return true;
		}

		plugin.econ.withdrawPlayer(player, money);
		guild.addMoney(money);
		HashMap<String,String> vars = new HashMap<>();
		vars.put("AMOUNT",money+"");
		Message.CHAT_ADMIN_GUILD_BANK_PAID.vars(vars).send(sender);
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.pay.paid",vars);

		return true;
	}
}
