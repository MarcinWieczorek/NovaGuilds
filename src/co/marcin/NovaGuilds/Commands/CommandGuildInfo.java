package co.marcin.NovaGuilds.Commands;


import java.util.ArrayList;
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
		
		NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
		
		if(guild == null) {
			guild = plugin.getGuildManager().getGuildByTag(guildname);
		}
		
		if(guild != null) {
			List<String> guildinfomsg = new ArrayList<String>();
			
			if((nPlayer.hasGuild() && guild.getName().equalsIgnoreCase(nPlayer.getGuild().getName())) || sender.hasPermission("novaguilds.admin.guild.fullinfo")) {
				guildinfomsg = plugin.getMessages().getStringList("chat.guildinfo.fullinfo");
			}
			else {
				guildinfomsg = plugin.getMessages().getStringList("chat.guildinfo.info");
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
					
					if(p instanceof Player &&p.isOnline()) {
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
					
					if(i<gplayers.size()-1) players += plugin.getMessages().getString("chat.guildinfo.playerseparator");
				}
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
				
				if(skipmsg==false) {
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