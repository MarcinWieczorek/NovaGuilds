package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class CommandGuildTop implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildTop(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.top")) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.nopermissions");
			return true;
		}

		Collection<NovaGuild> guilds = plugin.getGuildManager().getGuilds();

		if(guilds.size() == 0) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.noguilds");
			return true;
		}

		plugin.mysqlReload();
		int limit = plugin.getMessageManager().getMessages().getInt("holographicdisplays.topguilds.toprows");

		try {
			Statement statement = plugin.c.createStatement();
			plugin.getMessageManager().sendMessagesMsg(sender, "holographicdisplays.topguilds.header");

			ResultSet res = statement.executeQuery("SELECT `name`,`points` FROM `"+plugin.sqlp+"guilds` ORDER BY `points` DESC LIMIT "+limit);

			int i=1;
//			while(res.next()) {
//				String rowmsg = plugin.getMessageManager().getMessagesString("holographicdisplays.topguilds.row");
//				rowmsg = StringUtils.replace(rowmsg, "{GUILDNAME}", res.getString("name"));
//				rowmsg = StringUtils.replace(rowmsg, "{N}", i + "");
//				rowmsg = StringUtils.replace(rowmsg, "{POINTS}", res.getString("points"));
//				sender.sendMessage(StringUtils.fixColors(rowmsg));
//				i++;
//			}

			for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
				String rowmsg = plugin.getMessageManager().getMessagesString("holographicdisplays.topguilds.row");
				rowmsg = StringUtils.replace(rowmsg, "{GUILDNAME}", guild.getName());
				rowmsg = StringUtils.replace(rowmsg, "{N}", i + "");
				rowmsg = StringUtils.replace(rowmsg, "{POINTS}", guild.getPoints()+"");
				sender.sendMessage(StringUtils.fixColors(rowmsg));
				i++;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
