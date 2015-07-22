package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class CommandGuildInfo implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandGuildInfo(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String guildname;
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);
		
		if(args.length>0) {
			guildname = args[0];
		}
		else {
			if(!(sender instanceof Player)) {
				Message.CHAT_CMDFROMCONSOLE.send(sender);
				return true;
			}
			
			if(nPlayer.hasGuild()) {
				guildname = nPlayer.getGuild().getName();
			}
			else {
				Message.CHAT_GUILD_NOTINGUILD.send(sender);
				return true;
			}
		}

		//searching by name
		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);
		
		if(guild == null) {
			Message.CHAT_GUILD_NAMENOTEXIST.send(sender);
			return true;
		}

		HashMap<String,String> vars = new HashMap<>();

		List<String> guildInfoMessages;
		String separator = Message.CHAT_GUILDINFO_PLAYERSEPARATOR.get();

		if((sender instanceof Player && nPlayer.hasGuild() && guild.getName().equalsIgnoreCase(nPlayer.getGuild().getName())) || sender.hasPermission("novaguilds.admin.guild.fullinfo")) {
			guildInfoMessages = Message.CHAT_GUILDINFO_FULLINFO.getList();
		}
		else {
			guildInfoMessages = Message.CHAT_GUILDINFO_INFO.getList();
		}

		plugin.getMessageManager().sendPrefixMessage(sender, guildInfoMessages.get(0));

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
				String guildName = StringUtils.replace(allyformat, "{GUILDNAME}", allyGuild.getName());
				allies = allies + guildName + separator;
			}

			allies = allies.substring(0,allies.length()-separator.length());
		}

		//wars
		String wars = "";
		if(!guild.getWars().isEmpty()) {
			String warformat = Message.CHAT_GUILDINFO_WAR.get();
			for(NovaGuild war : guild.getWars()) {
				String warName = StringUtils.replace(warformat, "{GUILDNAME}", war.getName());
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
		vars.put("POINTS", guild.getPoints() + "");
		vars.put("LIVES", guild.getLives() + "");

		//live regeneration time
		long liveRegenerationTime = plugin.getConfigManager().getGuildLiveRegenerationTime() - (NumberUtils.systemSeconds() - guild.getLostLiveTime());
		String liveRegenerationString = StringUtils.secondsToString(liveRegenerationTime);

		long timeWait = (guild.getTimeRest() + plugin.getConfigManager().getRaidTimeRest()) - NumberUtils.systemSeconds();
		LoggerUtils.debug("timewait="+timeWait);
		LoggerUtils.debug(guild.getTimeRest() +"+"+ plugin.getConfigManager().getRaidTimeRest() +"-"+ NumberUtils.systemSeconds());

		vars.put("LIVEREGENERATIONTIME", liveRegenerationString);
		vars.put("TIMEREST",StringUtils.secondsToString(timeWait));

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
			if(guild.getLostLiveTime() <= 0 && gmsg.contains("{LIVEREGENERATIONTIME}")) {
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

			if(!skipmsg) {
				gmsg = StringUtils.replaceMap(gmsg,vars);
				sender.sendMessage(StringUtils.fixColors(gmsg));
			}
		}
		return true;
	}
}