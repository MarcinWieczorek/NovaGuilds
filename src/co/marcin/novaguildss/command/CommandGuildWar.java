package co.marcin.novaguildss.command;

import co.marcin.novaguildss.basic.NovaGuild;
import co.marcin.novaguildss.NovaGuilds;
import co.marcin.novaguildss.basic.NovaPlayer;
import co.marcin.novaguildss.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildWar implements CommandExecutor {
	private final NovaGuilds plugin;

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
				HashMap<String,String> vars = new HashMap<>();

				if(guild.isNoWarInvited(cmdGuild)) { //accepting no-war
					guild.removeNoWarInvitation(cmdGuild);
					guild.removeWar(cmdGuild);
					cmdGuild.removeWar(guild);

					//broadcast
					vars.put("GUILD1",guild.getName());
					vars.put("GUILD2",cmdGuild.getName());
					plugin.broadcastMessage("broadcast.guild.nowar",vars);
				}
				else { //inviting to no-war
					cmdGuild.addNoWarInvitation(guild);
					vars.put("GUILDNAME", cmdGuild.getName());
					plugin.sendMessagesMsg(sender, "chat.guild.war.nowarinv", vars);

					//notify the guild
					vars.clear();
					vars.put("GUILDNAME",guild.getName());
					plugin.broadcastGuild(cmdGuild,"chat.guild.war.nowarinvnotify",vars);
				}
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

				//broadcasts
				HashMap<String,String> vars = new HashMap<>();
				vars.put("GUILD1",guild.getName());
				vars.put("GUILD2",cmdGuild.getName());
				plugin.broadcastMessage("broadcast.guild.war",vars);

				plugin.tagUtils.refreshAll();
			}
		}
		else { //List wars
			plugin.sendMessagesMsg(sender,"chat.guild.war.list.warsheader");
			String separator = plugin.getMessagesString("chat.guild.war.list.separator");
			String guildnameformat = plugin.getMessagesString("chat.guild.war.list.item");

			if(guild.getWars().size() > 0) {
				String warsstr = StringUtils.join(guild.getWars(), guildnameformat, separator);
				plugin.sendPrefixMessage(sender,warsstr);
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.war.list.nowars");
			}

			if(!guild.getNoWarInvitations().isEmpty()) {
				plugin.sendMessagesMsg(sender,"chat.guild.war.list.nowarinvheader");

				String nowarinvs = StringUtils.join(guild.getNoWarInvitations(), guildnameformat, separator);

				plugin.sendPrefixMessage(sender,nowarinvs);
			}
		}
		return true;
	}
}
