package co.marcin.novaguilds.command;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.utils.StringUtils;

public class CommandGuildInfo implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandGuildInfo(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String guildname;
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);
		
		if(args.length>0) {
			guildname = args[0];
		}
		else {
			if(!(sender instanceof Player)) {
				plugin.info("You cannot check console's guild!");
				return true;
			}
			
			if(nPlayer.hasGuild()) {
				guildname = nPlayer.getGuild().getName();
			}
			else {
				plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notinguild");
				return true;
			}
		}

		//searching by name
		NovaGuild guild = plugin.getGuildManager().getGuildFind(guildname);
		
		if(guild != null) {
			HashMap<String,String> vars = new HashMap<>();

			List<String> guildInfoMessages = plugin.getMessageManager().getMessages().getStringList("chat.guildinfo.info");
			String separator = plugin.getMessageManager().getMessagesString("chat.guildinfo.playerseparator");
			
			if((sender instanceof Player && nPlayer.hasGuild() && guild.getName().equalsIgnoreCase(nPlayer.getGuild().getName())) || sender.hasPermission("novaguilds.admin.guild.fullinfo")) {
				guildInfoMessages = plugin.getMessageManager().getMessages().getStringList("chat.guildinfo.fullinfo");
			}

			plugin.getMessageManager().sendPrefixMessage(sender, guildInfoMessages.get(0));
			
			int i;
			List<NovaPlayer> gplayers = guild.getPlayers();
			String leader = guild.getLeaderName();
			String players = "";
			String pcolor;
			String leaderp; //String to insert to playername (leader prefix)
			String leaderprefix = plugin.getMessageManager().getMessagesString("chat.guildinfo.leaderprefix"); //leader prefix

			//players list
			if(gplayers.size()>0) {
				for(NovaPlayer nPlayerList : guild.getPlayers()) {
					if(nPlayerList.isOnline()) {
						pcolor = plugin.getMessageManager().getMessagesString("chat.guildinfo.playercolor.online");
					}
					else {
						pcolor = plugin.getMessageManager().getMessagesString("chat.guildinfo.playercolor.offline");
					}
					
					leaderp = "";
					if(nPlayerList.getName().equalsIgnoreCase(leader)) {
						leaderp = leaderprefix;
					}
					
					players += pcolor+leaderp+nPlayerList.getName();
					
					if(!nPlayerList.equals(gplayers.get(gplayers.size()-1))) players += separator;
				}
			}

			//allies
			String allies = "";
			if(!guild.getAllies().isEmpty()) {
				String allyformat = plugin.getMessageManager().getMessagesString("chat.guildinfo.ally");
				for(String ally : guild.getAllies()) {
					ally = StringUtils.replace(allyformat, "{GUILDNAME}", ally);
					allies = allies + ally + separator;
				}

				allies = allies.substring(0,allies.length()-separator.length());
			}

			//wars
			String wars = "";
			if(!guild.getWars().isEmpty()) {
				String warformat = plugin.getMessageManager().getMessagesString("chat.guildinfo.war");
				for(String war : guild.getWars()) {
					war = StringUtils.replace(warformat, "{GUILDNAME}", war);
					wars = wars + war + separator;
				}

				wars = wars.substring(0,wars.length()-separator.length());
			}

			vars.put("RANK","");
			vars.put("GUILDNAME", guild.getName());
			vars.put("LEADER", guild.getLeaderName());
			vars.put("TAG", guild.getTag());
			vars.put("MONEY", guild.getMoney() + "");
			vars.put("PLAYERS", players);
			vars.put("POINTS", guild.getPoints() + "");
			vars.put("LIVES", guild.getLives() + "");

			//live regeneration time
			long liveRegenerationTime = plugin.liveRegenerationTime - (NovaGuilds.systemSeconds() - guild.getLostLiveTime());
			String liveRegenerationString = StringUtils.secondsToString(liveRegenerationTime);

			long timeWait = (guild.getTimeRest() + plugin.timeRest) - NovaGuilds.systemSeconds();
			plugin.debug("timewait="+timeWait);
			plugin.debug(guild.getTimeRest() +"+"+ plugin.timeRest +"-"+ NovaGuilds.systemSeconds());

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
		}
		else {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.namenotexist");
		}
		return true;
	}
}