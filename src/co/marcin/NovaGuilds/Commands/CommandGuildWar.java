package co.marcin.NovaGuilds.Commands;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildWar implements CommandExecutor {
	public final NovaGuilds plugin;

	public CommandGuildWar(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.war")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(!(sender instanceof Player)) {
			plugin.sendMessagesMsg(sender,"chat.cmdfromconsole");
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);

		if(!nPlayer.hasGuild()) {
			plugin.sendMessagesMsg(sender,"chat.player.hasnoguild");
			return true;
		}

		NovaGuild guild = plugin.getGuildManager().getGuildByPlayer(nPlayer);

		if(args.length > 0) { //adding wars and no war invs
			if(!nPlayer.isLeader()) {
				plugin.sendMessagesMsg(sender,"chat.guild.notleader");
				return true;
			}

			String guildname = args[0];

			NovaGuild cmdGuild = plugin.getGuildManager().getGuildFind(guildname);

			if(cmdGuild == null) {
				plugin.sendMessagesMsg(sender,"chat.guild.couldnotfind");
				return true;
			}


			if(guild.isWarWith(cmdGuild)) { //no war inv
				//TODO
			}
			else { //new war
				if(guild.getName().equalsIgnoreCase(cmdGuild.getName())) {
					plugin.sendMessagesMsg(sender,"chat.guild.war.yourguildwar");
					return true;
				}

				if(guild.isAlly(cmdGuild)) {
					plugin.sendMessagesMsg(sender,"chat.guild.war.ally");
					return true;
				}

				guild.addWar(cmdGuild);
				cmdGuild.addWar(guild);

				plugin.getGuildManager().saveGuildLocal(guild);
				plugin.getGuildManager().saveGuildLocal(cmdGuild);

				//broadcasts
				HashMap<String,String> vars = new HashMap<>();
				vars.put("GUILD1",guild.getName());
				vars.put("GUILD2",cmdGuild.getName());
				plugin.broadcastMessage("broadcast.guild.war",vars);
			}
		}
		else { //List wars
			plugin.sendMessagesMsg(sender,"chat.guild.war.list.header");

			if(guild.getWars().size() > 0) {
				String warsstr = "";
				String sep = plugin.getMessagesString("chat.guild.war.list.separator");
				String guildnameformat = plugin.getMessagesString("chat.guild.war.list.item");

				for(String guildwar : guild.getWars()) {
					guildwar = Utils.replace(guildnameformat,"{GUILDNAME}",plugin.getGuildManager().getRealName(guildwar));
					warsstr = warsstr + guildwar + sep;
				}

				warsstr = warsstr.substring(0,warsstr.length()-2);
				plugin.sendPrefixMessage(sender,warsstr);
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.war.list.nowars");
			}
		}
		return true;
	}
}
