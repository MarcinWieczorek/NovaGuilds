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

public class CommandGuildBankWithdraw implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildBankWithdraw(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String marg = null;
		
		if(args.length>0) {
			marg = args[0];
		}

		if(!(sender instanceof Player)) {
			plugin.sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		Player player = plugin.senderToPlayer(sender);
		
		if(sender.hasPermission("novaguilds.guild.bank.withdraw")) {
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
			
			if(nPlayer.hasGuild()) {
				NovaGuild guild = plugin.getGuildManager().getGuildByName(nPlayer.getGuild().getName());
				
				if(nPlayer.isLeader()) {
					if(marg != null && StringUtils.isNumeric(marg)) {
						Double money = Double.parseDouble(marg);
						
						if(guild.getMoney() >= money) {
							guild.takeMoney(money);
							plugin.econ.depositPlayer(player,money);
							
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
