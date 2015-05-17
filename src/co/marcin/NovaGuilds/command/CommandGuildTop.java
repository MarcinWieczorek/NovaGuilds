package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.utils.StringUtils;
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
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		Collection<NovaGuild> guilds = plugin.getGuildManager().getGuilds();

		if(guilds.size() == 0) {
			sender.sendMessage("no guilds"); //TODO
			return true;
		}

		Statement statement;

		try {
			statement = plugin.c.createStatement();
			plugin.sendMessagesMsg(sender,"holographicdisplays.topguilds.header");

			ResultSet res = statement.executeQuery("SELECT `name`,`points` FROM `"+plugin.sqlp+"guilds` ORDER BY `points` DESC LIMIT "+plugin.getMessages().getInt("holographicdisplays.topguilds.toprows"));

			int i=1;
			while(res.next()) {
				String rowmsg = plugin.getMessages().getString("holographicdisplays.topguilds.row");
				rowmsg = StringUtils.replace(rowmsg, "{GUILDNAME}", res.getString("name"));
				rowmsg = StringUtils.replace(rowmsg, "{N}", i + "");
				rowmsg = StringUtils.replace(rowmsg, "{POINTS}", res.getString("points"));
				sender.sendMessage(StringUtils.fixColors(rowmsg));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
