/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandGuildInfo implements CommandExecutor, Executor {
	private final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Commands command = Commands.GUILD_INFO;
	 
	public CommandGuildInfo() {
		plugin.getCommandManager().registerExecutor(command, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		execute(sender, args);
		return true;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		String guildname;
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		
		if(args.length>0) {
			guildname = args[0];
		}
		else {
			if(!(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return;
			}
			
			if(nPlayer.hasGuild()) {
				guildname = nPlayer.getGuild().getName();
			}
			else {
				Message.CHAT_GUILD_NOTINGUILD.send(sender);
				return;
			}
		}

		//searching by name
		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);
		
		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return;
		}

		Map<String, String> vars = new HashMap<>();

		List<String> guildInfoMessages;
		String separator = Message.CHAT_GUILDINFO_PLAYERSEPARATOR.get();

		if((sender instanceof Player && nPlayer.hasGuild() && guild.getName().equalsIgnoreCase(nPlayer.getGuild().getName())) || Permission.NOVAGUILDS_ADMIN_FULLINFO.has(sender)) {
			guildInfoMessages = Message.CHAT_GUILDINFO_FULLINFO.getList();
		}
		else {
			guildInfoMessages = Message.CHAT_GUILDINFO_INFO.getList();
		}

		MessageManager.sendPrefixMessage(sender, guildInfoMessages.get(0));

		int i;
		List<NovaPlayer> gplayers = guild.getPlayers();
		String leader = guild.getLeader().getName();
		String players = "";
		String pcolor;
		String leaderp; //String to insert to playername (leader prefix)
		String leaderprefix = Message.CHAT_GUILDINFO_LEADERPREFIX.get();

		//players list
		if(!gplayers.isEmpty()) {
			for(NovaPlayer nPlayerList : guild.getPlayers()) {
				if(nPlayerList.isOnline()) {
					pcolor = Message.CHAT_GUILDINFO_PLAYERCOLOR_ONLINE.get();
				}
				else {
					pcolor = Message.CHAT_GUILDINFO_PLAYERCOLOR_OFFLINE.get();
				}

				leaderp = "";
				if(nPlayerList.getName().equalsIgnoreCase(leader)) {
					leaderp = leaderprefix;
				}

				players += pcolor+leaderp+nPlayerList.getName();

				if(!nPlayerList.equals(gplayers.get(gplayers.size()-1))) {
					players += separator;
				}
			}
		}

		//allies
		String allies = "";
		if(!guild.getAllies().isEmpty()) {
			String allyformat = Message.CHAT_GUILDINFO_ALLY.get();
			for(NovaGuild allyGuild : guild.getAllies()) {
				String guildName = org.apache.commons.lang.StringUtils.replace(allyformat, "{GUILDNAME}", allyGuild.getName());
				allies = allies + guildName + separator;
			}

			allies = allies.substring(0,allies.length()-separator.length());
		}

		//wars
		String wars = "";
		if(!guild.getWars().isEmpty()) {
			String warformat = Message.CHAT_GUILDINFO_WAR.get();
			for(NovaGuild war : guild.getWars()) {
				String warName = org.apache.commons.lang.StringUtils.replace(warformat, "{GUILDNAME}", war.getName());
				wars = wars + warName + separator;
			}

			wars = wars.substring(0,wars.length()-separator.length());
		}

		vars.put("RANK","");
		vars.put("GUILDNAME", guild.getName());
		vars.put("LEADER", guild.getLeader().getName());
		vars.put("TAG", guild.getTag());
		vars.put("MONEY", guild.getMoney() + "");
		vars.put("PLAYERS", players);
		vars.put("PLAYERSCOUNT", String.valueOf(guild.getPlayers().size()));
		vars.put("SLOTS", String.valueOf(guild.getSlots()));
		vars.put("POINTS", guild.getPoints() + "");
		vars.put("LIVES", guild.getLives() + "");
		vars.put("OPENINV", Message.getOnOff(guild.isOpenInvitation()));

		//live regeneration time
		long liveRegenerationTime = Config.LIVEREGENERATION_REGENTIME.getSeconds() - (NumberUtils.systemSeconds() - guild.getLostLiveTime());
		String liveRegenerationString = StringUtils.secondsToString(liveRegenerationTime);

		long timeWait = (guild.getTimeRest() + Config.RAID_TIMEREST.getSeconds()) - NumberUtils.systemSeconds();

		vars.put("LIVEREGENERATIONTIME", liveRegenerationString);
		vars.put("TIMEREST",StringUtils.secondsToString(timeWait));

		//time created and protection
		long createdAgo = NumberUtils.systemSeconds() - guild.getTimeCreated();
		long protLeft = Config.GUILD_CREATEPROTECTION.getSeconds() - createdAgo;

		vars.put("CREATEDAGO", StringUtils.secondsToString(createdAgo, TimeUnit.HOURS));
		vars.put("PROTLEFT", StringUtils.secondsToString(protLeft, TimeUnit.HOURS));

		//spawnpoint location coords
		Location sp = guild.getSpawnPoint();
		if(sp != null) {
			vars.put("SP_X", sp.getBlockX() + "");
			vars.put("SP_Y", sp.getBlockY() + "");
			vars.put("SP_Z", sp.getBlockZ() + "");
		}

		//put wars and allies into vars
		vars.put("ALLIES", allies);
		vars.put("WARS", wars);

		for(i=1;i < guildInfoMessages.size();i++) {
			boolean skipmsg = false;
			String gmsg = guildInfoMessages.get(i);

			//lost live
			if(liveRegenerationTime <= 0 && gmsg.contains("{LIVEREGENERATIONTIME}")) {
				skipmsg = true;
			}

			//Time rest
			if(timeWait <= 0 && gmsg.contains("{TIMEREST}")) {
				skipmsg = true;
			}

			//spawnpoint
			if((gmsg.contains("{SP_X}") || gmsg.contains("{SP_Y}") || gmsg.contains("{SP_Z}")) && guild.getSpawnPoint() == null) {
				skipmsg = true;
			}

			//allies
			if(gmsg.contains("{ALLIES}") && allies.isEmpty()) {
				skipmsg = true;
			}

			//displaying wars
			if(gmsg.contains("{WARS}") && wars.isEmpty()) {
				skipmsg = true;
			}

			if(gmsg.contains("{PROTLEFT}") && protLeft <= 0) {
				skipmsg = true;
			}

			if(gmsg.contains("{CREATEDAGO}") && protLeft > 0) {
				skipmsg = true;
			}

			if(!skipmsg) {
				gmsg = StringUtils.replaceMap(gmsg,vars);
				sender.sendMessage(StringUtils.fixColors(gmsg));
			}
		}
	}
}