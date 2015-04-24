package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.utils.StringUtils;

public class CommandGuildBankWithdraw implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandGuildBankWithdraw(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String marg = null;
		
		if(args.length>0) {
			marg = args[0];
		}
		
		if(sender.hasPermission("NovaGuilds.guild.bank.withdraw")) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			
			if(nPlayer.hasGuild()) {
				NovaGuild guild = plugin.getGuildManager().getGuildByName(nPlayer.getGuild().getName());
				
				if(guild.getLeaderName().equals(sender.getName())) {
					if(marg != null && StringUtils.isNumeric(marg)) {
						Double money = Double.parseDouble(marg);
						
						if(guild.getMoney() >= money) {
							guild.takeMoney(money);
							plugin.econ.depositPlayer(sender.getName(),money);
							
							plugin.getGuildManager().saveGuild(guild);
							
							HashMap<String,String> vars = new HashMap<>();
							vars.put("AMOUNT",money+"");
							plugin.sendMessagesMsg(sender,"chat.guild.bank.withdraw.success",vars);
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.guild.bank.withdraw.notenought");
						}
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.guild.bank.enteramount");
					}
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.guild.bank.withdraw.notleader");
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}

}
