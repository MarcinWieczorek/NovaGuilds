package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.manager.MessageManager;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(args.length > 0) { //adding wars and no war invs
			if(!nPlayer.isLeader()) {
				Message.CHAT_GUILD_NOTLEADER.send(sender);
				return true;
			}

			String guildname = args[0];

			NovaGuild cmdGuild = plugin.getGuildManager().getGuildFind(guildname);

			if(cmdGuild == null) {
				Message.CHAT_GUILD_COULDNOTFIND.send(sender);
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
					vars.put("GUILD2", cmdGuild.getName());
					Message.BROADCAST_GUILD_NOWAR.vars(vars).broadcast();
				}
				else { //inviting to no-war
					cmdGuild.addNoWarInvitation(guild);
					vars.put("GUILDNAME", cmdGuild.getName());
					Message.CHAT_GUILD_WAR_NOWARINV_SUCCESS.vars(vars).send(sender);

					//notify the guild
					vars.clear();
					vars.put("GUILDNAME",guild.getName());
					Message.CHAT_GUILD_WAR_NOWARINV_NOTIFY.vars(vars).broadcast(cmdGuild);
				}
			}
			else { //new war
				if(guild.getName().equalsIgnoreCase(cmdGuild.getName())) {
					Message.CHAT_GUILD_WAR_YOURGUILDWAR.send(sender);
					return true;
				}

				if(guild.isAlly(cmdGuild)) {
					Message.CHAT_GUILD_WAR_ALLY.send(sender);
					return true;
				}

				guild.addWar(cmdGuild);
				cmdGuild.addWar(guild);

				//broadcasts
				HashMap<String,String> vars = new HashMap<>();
				vars.put("GUILD1",guild.getName());
				vars.put("GUILD2", cmdGuild.getName());
				Message.BROADCAST_GUILD_WAR.vars(vars).broadcast();

				plugin.tagUtils.refreshAll();
			}
		}
		else { //List wars
			Message.CHAT_GUILD_WAR_LIST_WARSHEADER.send(sender);
			String separator = Message.CHAT_GUILD_WAR_LIST_SEPARATOR.get();
			String guildnameformat = Message.CHAT_GUILD_WAR_LIST_ITEM.get();

			if(!guild.getWars().isEmpty()) {
				String warsstr = StringUtils.join(guild.getWarsNames(), guildnameformat, separator);
				MessageManager.sendPrefixMessage(sender, warsstr);
			}
			else {
				Message.CHAT_GUILD_WAR_LIST_NOWARS.send(sender);
			}

			if(!guild.getNoWarInvitations().isEmpty()) {
				Message.CHAT_GUILD_WAR_LIST_NOWARINVHEADER.send(sender);

				String nowarinvs = StringUtils.join(guild.getNoWarInvitations(), guildnameformat, separator);

				MessageManager.sendPrefixMessage(sender, nowarinvs);
			}
		}
		return true;
	}
}
