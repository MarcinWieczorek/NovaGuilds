/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandGuildInfo extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		String guildName;
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(args.length > 0) {
			guildName = args[0];
		}
		else {
			if(!(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return;
			}

			if(nPlayer.hasGuild()) {
				guildName = nPlayer.getGuild().getName();
			}
			else {
				Message.CHAT_GUILD_NOTINGUILD.send(sender);
				return;
			}
		}

		//searching by name
		NovaGuild guild = GuildManager.getGuildFind(guildName);

		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return;
		}

		Map<VarKey, String> vars = new HashMap<>();

		List<String> guildInfoMessages;
		String separator = Message.CHAT_GUILDINFO_PLAYERSEPARATOR.get();

		if((sender instanceof Player && nPlayer.hasGuild() && guild.isMember(nPlayer)) || Permission.NOVAGUILDS_ADMIN_GUILD_FULLINFO.has(sender)) {
			guildInfoMessages = Message.CHAT_GUILDINFO_FULLINFO.getList();
		}
		else {
			guildInfoMessages = Message.CHAT_GUILDINFO_INFO.getList();
		}

		MessageManager.sendPrefixMessage(sender, guildInfoMessages.get(0));

		int i;
		List<NovaPlayer> playerList = guild.getPlayers();
		String leader = guild.getLeader().getName();
		String players = "";
		String playerColor;
		String leaderPrefixString; //String to insert to playername (leader prefix)
		String leaderPrefixFormat = Message.CHAT_GUILDINFO_LEADERPREFIX.get();

		//players list
		if(!playerList.isEmpty()) {
			for(NovaPlayer nPlayerList : guild.getPlayers()) {
				if(nPlayerList.isOnline()) {
					playerColor = Message.CHAT_GUILDINFO_PLAYERCOLOR_ONLINE.get();
				}
				else {
					playerColor = Message.CHAT_GUILDINFO_PLAYERCOLOR_OFFLINE.get();
				}

				leaderPrefixString = "";
				if(nPlayerList.getName().equalsIgnoreCase(leader)) {
					leaderPrefixString = leaderPrefixFormat;
				}

				players += playerColor + leaderPrefixString + nPlayerList.getName();

				if(!nPlayerList.equals(playerList.get(playerList.size() - 1))) {
					players += separator;
				}
			}
		}

		//allies
		String allies = "";
		if(!guild.getAllies().isEmpty()) {
			String allyFormat = Message.CHAT_GUILDINFO_ALLY.get();
			for(NovaGuild allyGuild : guild.getAllies()) {
				String allyName = org.apache.commons.lang.StringUtils.replace(allyFormat, "{GUILDNAME}", allyGuild.getName());
				allies = allies + allyName + separator;
			}

			allies = allies.substring(0, allies.length() - separator.length());
		}

		//wars
		String wars = "";
		if(!guild.getWars().isEmpty()) {
			String warFormat = Message.CHAT_GUILDINFO_WAR.get();
			for(NovaGuild war : guild.getWars()) {
				String warName = org.apache.commons.lang.StringUtils.replace(warFormat, "{GUILDNAME}", war.getName());
				wars = wars + warName + separator;
			}

			wars = wars.substring(0, wars.length() - separator.length());
		}

		vars.put(VarKey.RANK, "");
		vars.put(VarKey.GUILDNAME, guild.getName());
		vars.put(VarKey.LEADER, guild.getLeader().getName());
		vars.put(VarKey.TAG, guild.getTag());
		vars.put(VarKey.MONEY, String.valueOf(guild.getMoney()));
		vars.put(VarKey.PLAYERS, players);
		vars.put(VarKey.PLAYERSCOUNT, String.valueOf(guild.getPlayers().size()));
		vars.put(VarKey.SLOTS, String.valueOf(guild.getSlots()));
		vars.put(VarKey.POINTS, String.valueOf(guild.getPoints()));
		vars.put(VarKey.LIVES, String.valueOf(guild.getLives()));
		vars.put(VarKey.OPENINV, Message.getOnOff(guild.isOpenInvitation()));

		//live regeneration time
		long liveRegenerationTime = Config.LIVEREGENERATION_REGENTIME.getSeconds() - (NumberUtils.systemSeconds() - guild.getLostLiveTime());
		String liveRegenerationString = StringUtils.secondsToString(liveRegenerationTime);

		long timeWait = (guild.getTimeRest() + Config.RAID_TIMEREST.getSeconds()) - NumberUtils.systemSeconds();

		vars.put(VarKey.LIVEREGENERATIONTIME, liveRegenerationString);
		vars.put(VarKey.TIMEREST, StringUtils.secondsToString(timeWait));

		//time created and protection
		long createdAgo = NumberUtils.systemSeconds() - guild.getTimeCreated();
		long protectionLeft = Config.GUILD_CREATEPROTECTION.getSeconds() - createdAgo;

		vars.put(VarKey.CREATEDAGO, StringUtils.secondsToString(createdAgo, TimeUnit.HOURS));
		vars.put(VarKey.PROTLEFT, StringUtils.secondsToString(protectionLeft, TimeUnit.HOURS));

		//home location coordinates
		Location sp = guild.getHome();
		if(sp != null) {
			vars.put(VarKey.SP_X, String.valueOf(sp.getBlockX()));
			vars.put(VarKey.SP_Y, String.valueOf(sp.getBlockY()));
			vars.put(VarKey.SP_Z, String.valueOf(sp.getBlockZ()));
		}

		//put wars and allies into vars
		vars.put(VarKey.ALLIES, allies);
		vars.put(VarKey.WARS, wars);

		for(i = 1; i < guildInfoMessages.size(); i++) {
			boolean skip = false;
			String guildInfoMessage = guildInfoMessages.get(i);

			//lost live
			if(liveRegenerationTime <= 0 && guildInfoMessage.contains("{LIVEREGENERATIONTIME}")) {
				skip = true;
			}

			//Time rest
			if(timeWait <= 0 && guildInfoMessage.contains("{TIMEREST}")) {
				skip = true;
			}

			//home location
			if((guildInfoMessage.contains("{SP_X}") || guildInfoMessage.contains("{SP_Y}") || guildInfoMessage.contains("{SP_Z}")) && guild.getHome() == null) {
				skip = true;
			}

			//allies
			if(guildInfoMessage.contains("{ALLIES}") && allies.isEmpty()) {
				skip = true;
			}

			//displaying wars
			if(guildInfoMessage.contains("{WARS}") && wars.isEmpty()) {
				skip = true;
			}

			if(guildInfoMessage.contains("{PROTLEFT}") && protectionLeft <= 0) {
				skip = true;
			}

			if(guildInfoMessage.contains("{CREATEDAGO}") && protectionLeft > 0) {
				skip = true;
			}

			if(!skip) {
				guildInfoMessage = MessageManager.replaceVarKeyMap(guildInfoMessage, vars);
				MessageManager.sendMessage(sender, guildInfoMessage);
			}
		}
	}
}
