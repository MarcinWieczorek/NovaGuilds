package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class CommandGuildBankPay implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandGuildBankPay(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String marg = null;
		
		if(args.length>0) {
			marg = args[0];
		}
		
		if(sender.hasPermission("NovaGuilds.guild.bank.pay")) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			
			if(nPlayer.hasGuild()) {
				NovaGuild guild = plugin.getGuildManager().getGuildByName(nPlayer.getGuild().getName());
				
				if(marg != null && Utils.isNumeric(marg)) {
					Double money = Double.parseDouble(marg);
					
					if(plugin.econ.getBalance(sender.getName()) >= money) {
						guild.addMoney(money);
						plugin.econ.withdrawPlayer(sender.getName(),money);
						plugin.getGuildManager().saveGuildLocal(guild);
						HashMap<String,String> vars = new HashMap<>();
						vars.put("AMOUNT",money+"");
						plugin.sendMessagesMsg(sender,"chat.guild.bank.pay.paid",vars);
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.guild.bank.pay.notenought");
					}
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.guild.bank.enteramount");
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
			}
		}
		return true;
	}
}
