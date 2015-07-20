package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class CommandGuildTop implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildTop(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.top")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		Collection<NovaGuild> guilds = plugin.getGuildManager().getGuilds();

		if(guilds.isEmpty()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.noguilds");
			return true;
		}

		int limit = plugin.getMessageManager().getMessages().getInt("holographicdisplays.topguilds.toprows");
		int i=1;

		plugin.getMessageManager().sendMessagesMsg(sender, "holographicdisplays.topguilds.header");

		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			String rowmsg = plugin.getMessageManager().getMessagesString("holographicdisplays.topguilds.row");
			rowmsg = StringUtils.replace(rowmsg, "{GUILDNAME}", guild.getName());
			rowmsg = StringUtils.replace(rowmsg, "{N}", i + "");
			rowmsg = StringUtils.replace(rowmsg, "{POINTS}", guild.getPoints()+"");
			sender.sendMessage(StringUtils.fixColors(rowmsg));
			i++;
		}

		return true;
	}
}
