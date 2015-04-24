package co.marcin.NovaGuilds.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandAdminGuild implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandAdminGuild(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String[] newargs = Utils.parseArgs(args,1);
		
		//TODO permissions
		if(args.length>0) {
			if(args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) { //TP to guild
				new CommandAdminGuildTeleport(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("abandon")) { //abandon
				new CommandAdminGuildAbandon(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("setname")) { //set name
				new CommandAdminGuildSetName(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("settag")) { //set tag
				new CommandAdminGuildSetTag(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("kick")) { //kick from guild
				new CommandAdminGuildKick(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("invite")) { //invite somebody to a guild
				new CommandAdminGuildInvite(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("promote")) { //promote to leader
				new CommandAdminGuildSetLeader(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("list")) { //list guilds
				new CommandAdminGuildList(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("pay")) { //list guilds
				new CommandAdminGuildBankPay(plugin).onCommand(sender, cmd, label, newargs);
			}
			else if(args[0].equalsIgnoreCase("withdraw")) { //list guilds
				new CommandAdminGuildBankWithdraw(plugin).onCommand(sender, cmd, label, newargs);
			}
			else {
				plugin.sendMessagesMsg(sender, "chat.unknowncmd");
			}
		}
		else {
			plugin.sendMessagesMsg(sender, "chat.commands.admin.guild.header");
			
			for(String citem : plugin.getMessages().getStringList("chat.commands.admin.guild.items")) {
				sender.sendMessage(Utils.fixColors(citem));
			}
		}
		return true;
	}
}
