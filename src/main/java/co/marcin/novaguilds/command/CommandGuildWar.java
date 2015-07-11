package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.util.StringUtils;
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
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.cmdfromconsole");
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.player.hasnoguild");
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length > 0) { //adding wars and no war invs
			if(!nPlayer.isLeader()) {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notleader");
				return true;
			}

			String guildname = args[0];

			NovaGuild cmdGuild = plugin.getGuildManager().getGuildFind(guildname);

			if(cmdGuild == null) {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.couldnotfind");
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
					plugin.getMessageManager().broadcastMessage("broadcast.guild.nowar", vars);
				}
				else { //inviting to no-war
					cmdGuild.addNoWarInvitation(guild);
					vars.put("GUILDNAME", cmdGuild.getName());
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.war.nowarinv", vars);

					//notify the guild
					vars.clear();
					vars.put("GUILDNAME",guild.getName());
					plugin.getMessageManager().broadcastGuild(cmdGuild, "chat.guild.war.nowarinvnotify", vars,true);
				}
			}
			else { //new war
				if(guild.getName().equalsIgnoreCase(cmdGuild.getName())) {
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.war.yourguildwar");
					return true;
				}

				if(guild.isAlly(cmdGuild)) {
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.war.ally");
					return true;
				}

				guild.addWar(cmdGuild);
				cmdGuild.addWar(guild);

				//broadcasts
				HashMap<String,String> vars = new HashMap<>();
				vars.put("GUILD1",guild.getName());
				vars.put("GUILD2",cmdGuild.getName());
				plugin.getMessageManager().broadcastMessage("broadcast.guild.war",vars);

				plugin.tagUtils.refreshAll();
			}
		}
		else { //List wars
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.war.list.warsheader");
			String separator = plugin.getMessageManager().getMessagesString("chat.guild.war.list.separator");
			String guildnameformat = plugin.getMessageManager().getMessagesString("chat.guild.war.list.item");

			if(guild.getWars().size() > 0) {
				String warsstr = StringUtils.join(guild.getWarsNames(), guildnameformat, separator);
				plugin.getMessageManager().sendPrefixMessage(sender,warsstr);
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.war.list.nowars");
			}

			if(!guild.getNoWarInvitations().isEmpty()) {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.war.list.nowarinvheader");

				String nowarinvs = StringUtils.join(guild.getNoWarInvitations(), guildnameformat, separator);

				plugin.getMessageManager().sendPrefixMessage(sender,nowarinvs);
			}
		}
		return true;
	}
}
