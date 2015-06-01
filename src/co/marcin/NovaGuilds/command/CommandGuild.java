package co.marcin.NovaGuilds.command;


import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.entity.Player;

public class CommandGuild implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuild(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length>0) {
			String command = args[0].toLowerCase();
			String[] newargs = StringUtils.parseArgs(args, 1);

			switch(command) {
				case "pay":
					new CommandGuildBankPay(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "withdraw":
					new CommandGuildBankWithdraw(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "leader":
					new CommandGuildLeader(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "info":
					new CommandGuildInfo(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "leave":
					new CommandGuildLeave(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "home":
					new CommandGuildHome(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "buyregion":
					new CommandRegionBuy(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "ally":
					new CommandGuildAlly(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "kick":
					new CommandGuildKick(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "abandon":
					new CommandGuildAbandon(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "invite":
					new CommandGuildInvite(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "join":
					new CommandGuildJoin(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "create":
					new CommandGuildCreate(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "war":
					new CommandGuildWar(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "compass":
					new CommandGuildCompass(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "effect":
					new CommandGuildEffect(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "top":
					new CommandGuildTop(plugin).onCommand(sender, cmd, label, newargs);
					break;
				default:
					plugin.sendMessagesMsg(sender, "chat.unknowncmd");
					plugin.info("cmd = " + command);
					break;
			}
		}
		else {
			List<String> cmdlist = plugin.getMessages().getStringList("chat.commands.guild.noguild");

			if(sender instanceof Player) {
				if(plugin.getPlayerManager().getPlayerBySender(sender).hasGuild()) {
					cmdlist = plugin.getMessages().getStringList("chat.commands.guild.hasguild");
				}
			}

			for(String cmdinfo : cmdlist) {
				sender.sendMessage(StringUtils.fixColors(cmdinfo));
			}
		}
		return true;
	}
}
