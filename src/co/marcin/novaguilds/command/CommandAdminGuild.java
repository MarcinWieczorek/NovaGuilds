package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandAdminGuild implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdminGuild(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String[] newArgs = StringUtils.parseArgs(args, 1);
		String[] newArgs2 = StringUtils.parseArgs(args, 2);

		List<String> noguildcmds = new ArrayList<>();
		noguildcmds.add("list");
		noguildcmds.add("kick");
		noguildcmds.add("promote");

		if(sender.hasPermission("novaguilds.admin.guild.access")) {
			//command list
			if(args.length == 0) {
				plugin.sendMessagesMsg(sender, "chat.commands.admin.guild.header");

				for(String cItem : plugin.getMessages().getStringList("chat.commands.admin.guild.items")) {
					sender.sendMessage(StringUtils.fixColors(cItem));
				}

				return true;
			}

			String subCmd = args[0];
			NovaGuild guild = null;
			if(!noguildcmds.contains(subCmd)) {
				guild = plugin.getGuildManager().getGuildFind(args[0]);

				if(args.length > 1) {
					subCmd = args[1];
				}
			}

			if(guild != null || noguildcmds.contains(subCmd)) {
				if(subCmd.equalsIgnoreCase("tp") || subCmd.equalsIgnoreCase("teleport")) { //TP to guild
					new CommandAdminGuildTeleport(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("abandon")) { //abandon
					new CommandAdminGuildAbandon(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("setname")) { //set name
					new CommandAdminGuildSetName(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("settag")) { //set tag
					new CommandAdminGuildSetTag(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("kick")) { //kick from guild
					new CommandAdminGuildKick(plugin).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("invite")) { //invite somebody to a guild
					new CommandAdminGuildInvite(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("promote")) { //promote to leader
					new CommandAdminGuildSetLeader(plugin).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("list")) { //list guilds
					new CommandAdminGuildList(plugin).onCommand(sender, cmd, label, newArgs);
				}
				else if(subCmd.equalsIgnoreCase("pay")) { //list guilds
					new CommandAdminGuildBankPay(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("withdraw")) { //list guilds
					new CommandAdminGuildBankWithdraw(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("timerest")) { //list guilds
					new CommandAdminGuildSetTimerest(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("liveregentime")) { //list guilds
					new CommandAdminGuildSetLiveRegenerationTime(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else if(subCmd.equalsIgnoreCase("lives")) { //list guilds
					new CommandAdminGuildSetLives(plugin,guild).onCommand(sender, cmd, label, newArgs2);
				}
				else {
					plugin.sendMessagesMsg(sender, "chat.unknowncmd");
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}

		return true;
	}
}
