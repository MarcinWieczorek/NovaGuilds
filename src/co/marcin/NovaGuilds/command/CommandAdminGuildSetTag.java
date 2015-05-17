package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;

public class CommandAdminGuildSetTag implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetTag(NovaGuilds plugin, NovaGuild guild) {
		this.plugin = plugin;
		this.guild = guild;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.settag")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length==0) {
			plugin.sendMessagesMsg(sender,"chat.guild.entertag");
			return true;
		}

		String newtag = args[0];

		if(plugin.getGuildManager().getGuildFind(newtag) != null) {
			plugin.sendMessagesMsg(sender,"chat.guild.tagexists");
			return true;
		}

		//all passed
		guild.setTag(newtag);

		plugin.tagUtils.refreshAll();

		HashMap<String,String> vars = new HashMap<>();
		vars.put("TAG",newtag);
		plugin.sendMessagesMsg(sender,"chat.admin.guild.settag",vars);
		return true;
	}
}
