package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandAdminGuildSetTag implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;

	public CommandAdminGuildSetTag(NovaGuilds plugin, NovaGuild guild) {
		this.plugin = plugin;
		this.guild = guild;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.settag")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length==0) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.entertag");
			return true;
		}

		String newtag = args[0];

		if(plugin.getGuildManager().getGuildFind(newtag) != null) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.tagexists");
			return true;
		}

		//all passed
		guild.setTag(newtag);

		plugin.tagUtils.refreshAll();

		HashMap<String,String> vars = new HashMap<>();
		vars.put("TAG",newtag);
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.admin.guild.set.tag",vars);
		return true;
	}
}
