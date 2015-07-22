package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGuild implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuild(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!Commands.GUILD_ACCESS.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if((!Commands.GUILD_ACCESS.allowedSender(sender) && args.length==0) || !args[0].equalsIgnoreCase("top")) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

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
				case "items":
					new CommandGuildRequiredItems(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "pvp":
					new CommandGuildPvpToggle(plugin).onCommand(sender, cmd, label, newargs);
					break;
				default:
					Message.CHAT_UNKNOWNCMD.send(sender);
					break;
			}
		}
		else {
			if(plugin.getPlayerManager().getPlayer(sender).hasGuild()) {
				plugin.getMessageManager().sendMessagesList(sender,"chat.commands.guild.hasguild",null,false);

				if(plugin.getPlayerManager().getPlayer(sender).isLeader()) {
					plugin.getMessageManager().sendMessagesList(sender,"chat.commands.guild.leader",null,false);
				}
			}
			else {
				plugin.getMessageManager().sendMessagesList(sender,"chat.commands.guild.noguild",null,false);
			}
		}
		return true;
	}
}
