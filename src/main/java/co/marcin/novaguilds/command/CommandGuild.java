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

		if(!Commands.GUILD_ACCESS.allowedSender(sender) && (args.length==0 || !args[0].equalsIgnoreCase("top"))) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		if(args.length>0) {
			String command = args[0].toLowerCase();
			String[] newargs = StringUtils.parseArgs(args, 1);

			switch(command) {
				case "pay":
					plugin.getCommandManager().getExecutor(Commands.GUILD_BANK_PAY).execute(sender, newargs);
					break;
				case "withdraw":
					plugin.getCommandManager().getExecutor(Commands.GUILD_BANK_WITHDRAW).execute(sender, newargs);
					break;
				case "leader":
					plugin.getCommandManager().getExecutor(Commands.GUILD_LEADER).execute(sender, newargs);
					break;
				case "info":
					new CommandGuildInfo(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "leave":
					new CommandGuildLeave(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "home":
					plugin.getCommandManager().getExecutor(Commands.GUILD_HOME).execute(sender, newargs);
					break;
				case "buyregion":
					new CommandRegionBuy(plugin).onCommand(sender, cmd, label, newargs);
					break;
				case "ally":
					plugin.getCommandManager().getExecutor(Commands.GUILD_ALLY).execute(sender, newargs);
					break;
				case "kick":
					plugin.getCommandManager().getExecutor(Commands.GUILD_KICK).execute(sender, newargs);
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
					plugin.getCommandManager().getExecutor(Commands.GUILD_COMPASS).execute(sender, newargs);
					break;
				case "effect":
					plugin.getCommandManager().getExecutor(Commands.GUILD_EFFECT).execute(sender, newargs);
					break;
				case "top":
					new CommandGuildTop(Commands.GUILD_TOP).execute(sender, newargs);
					break;
				case "items":
					plugin.getCommandManager().getExecutor(Commands.GUILD_REQUIREDITEMS).execute(sender, newargs);
					break;
				case "pvp":
					plugin.getCommandManager().getExecutor(Commands.GUILD_PVPTOGGLE).execute(sender, newargs);
					break;
				case "buylife":
					plugin.getCommandManager().getExecutor(Commands.GUILD_BUYLIFE).execute(sender, newargs);
					break;
				default:
					Message.CHAT_UNKNOWNCMD.send(sender);
					break;
			}
		}
		else {
			if(plugin.getPlayerManager().getPlayer(sender).hasGuild()) {
				Message.CHAT_COMMANDS_GUILD_HASGUILD.prefix(false).send(sender);

				if(plugin.getPlayerManager().getPlayer(sender).isLeader()) {
					Message.CHAT_COMMANDS_GUILD_LEADER.prefix(false).send(sender);
				}
			}
			else {
				Message.CHAT_COMMANDS_GUILD_NOGUILD.prefix(false).send(sender);
			}
		}
		return true;
	}
}
