package co.marcin.NovaGuilds.command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

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
				plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
				return true;
			}
		}

		//searching by name
		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

		//searching by tag
		if(guild == null) {
			guild = plugin.getGuildManager().getGuildByTag(guildname);
		}

		//searching by player
		if(guild == null) {
			guild = plugin.getGuildManager().getGuildByPlayer(args[0]);
		}
		
		if(guild != null) {
			List<String> guildinfomsg = plugin.getMessages().getStringList("chat.guildinfo.info");
			String separator = plugin.getMessages().getString("chat.guildinfo.playerseparator");
			
			if((sender instanceof Player && nPlayer.hasGuild() && guild.getName().equalsIgnoreCase(nPlayer.getGuild().getName())) || sender.hasPermission("NovaGuilds.admin.guild.fullinfo")) {
				guildinfomsg = plugin.getMessages().getStringList("chat.guildinfo.fullinfo");
			}
			
			sender.sendMessage(Utils.fixColors(plugin.prefix+guildinfomsg.get(0)));
			
			int i;
			List<NovaPlayer> gplayers = guild.getPlayers();
			String leader = guild.getLeaderName();
			String players = "";
			String pcolor = "";
			String leaderp; //String to insert to playername (leader prefix)
			String leaderprefix = plugin.getMessages().getString("chat.guildinfo.leaderprefix"); //leader prefix
			
			if(gplayers.size()>0) {
				for(i=0;i<gplayers.size();i++) {
					NovaPlayer nplayer = gplayers.get(i);
					Player p = plugin.getServer().getPlayer(nplayer.getName());
					
					if(p != null && p.isOnline()) {
						pcolor = plugin.getMessages().getString("chat.guildinfo.playercolor.online");
					}
					else {
						pcolor = plugin.getMessages().getString("chat.guildinfo.playercolor.offline");
					}
					
					leaderp = "";
					if(nplayer.getName().equalsIgnoreCase(leader)) {
						leaderp = leaderprefix;
					}
					
					players += pcolor+leaderp+nplayer.getName();
					
					if(i<gplayers.size()-1) players += separator;
				}
			}

			//allies
			String allies = "";
			if(!guild.getAllies().isEmpty()) {
				String allyformat = plugin.getMessagesString("chat.guildinfo.ally");
				for(String ally : guild.getAllies()) {
					ally = Utils.replace(allyformat,"{GUILDNAME}",ally);
					allies = allies + ally + separator;
				}

				allies = allies.substring(0,allies.length()-separator.length());
			}

			//wars
			String wars = "";
			if(!guild.getWars().isEmpty()) {
				String warformat = plugin.getMessagesString("chat.guildinfo.war");
				for(String war : guild.getWars()) {
					war = Utils.replace(warformat,"{GUILDNAME}",war);
					wars = wars + war + separator;
				}

				wars = wars.substring(0,wars.length()-separator.length());
			}
			
			for(i=1;i < guildinfomsg.size();i++) {
				boolean skipmsg = false;
				String tagmsg = plugin.config.getString("guild.tag");
				String gmsg = guildinfomsg.get(i);
				
				tagmsg = Utils.replace(tagmsg,"{TAG}",guild.getTag());
				tagmsg = Utils.replace(tagmsg,"{RANK}","");
				
				gmsg = Utils.replace(gmsg,"{GUILDNAME}",guild.getName());
				gmsg = Utils.replace(gmsg,"{LEADER}",guild.getLeaderName());
				gmsg = Utils.replace(gmsg,"{TAG}",tagmsg);
				gmsg = Utils.replace(gmsg,"{MONEY}",guild.getMoney()+"");
				gmsg = Utils.replace(gmsg,"{PLAYERS}",players);
				gmsg = Utils.replace(gmsg,"{POINTS}",guild.getPoints()+"");
				
				if(gmsg.contains("{SP_X}") || gmsg.contains("{SP_Y}") || gmsg.contains("{SP_Z}")) {
					Location sp = guild.getSpawnPoint();
					if(sp != null) {
						gmsg = Utils.replace(gmsg,"{SP_X}",sp.getBlockX()+"");
						gmsg = Utils.replace(gmsg,"{SP_Y}",sp.getBlockY()+"");
						gmsg = Utils.replace(gmsg,"{SP_Z}",sp.getBlockZ()+"");
					}
					else {
						skipmsg = true;
					}
				}

				if(gmsg.contains("{ALLIES}")) {
					if(allies.isEmpty()) {
						skipmsg = true;
					}
					else {
						gmsg = Utils.replace(gmsg,"{ALLIES}",allies);
					}
				}

				//displaying wars
				if(gmsg.contains("{WARS}")) {
					if(wars.isEmpty()) {
						skipmsg = true;
					}
					else {
						gmsg = Utils.replace(gmsg,"{WARS}",wars);
					}
				}
				
				if(!skipmsg) {
					sender.sendMessage(Utils.fixColors(gmsg));
				}
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
		}
		return true;
	}
}