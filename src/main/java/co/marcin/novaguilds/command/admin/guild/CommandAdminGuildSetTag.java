package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Message;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(args.length==0) {
			Message.CHAT_GUILD_ENTERTAG.send(sender);
			return true;
		}

		String newtag = args[0];

		if(plugin.getGuildManager().getGuildFind(newtag) != null) {
			Message.CHAT_CREATEGUILD_TAGEXISTS.send(sender);
			return true;
		}

		//all passed
		guild.setTag(newtag);

		plugin.tagUtils.refreshAll();

		HashMap<String,String> vars = new HashMap<>();
		vars.put("TAG",newtag);
		Message.CHAT_ADMIN_GUILD_SET_TAG.vars(vars).send(sender);
		return true;
	}
}
