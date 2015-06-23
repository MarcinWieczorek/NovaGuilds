package co.marcin.novaguilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.entity.Player;

public class CommandGuildBankPay implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildBankPay(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String marg = null;
		
		if(args.length>0) {
			marg = args[0];
		}

		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		Player player = plugin.senderToPlayer(sender);
		
		if(sender.hasPermission("novaguilds.guild.bank.pay")) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			
			if(nPlayer.hasGuild()) {
				NovaGuild guild = plugin.getGuildManager().getGuildByName(nPlayer.getGuild().getName());
				
				if(marg != null && StringUtils.isNumeric(marg)) {
					Double money = Double.parseDouble(marg);
					
					if(plugin.econ.getBalance(player) >= money) {
						plugin.econ.withdrawPlayer(player,money);
						guild.addMoney(money);
						HashMap<String,String> vars = new HashMap<>();
						vars.put("AMOUNT",money+"");
						plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.pay.paid",vars);
					}
					else {
						plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.pay.notenought");
					}
				}
				else {
					plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.bank.enteramount");
				}
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
			}
		}
		return true;
	}
}
