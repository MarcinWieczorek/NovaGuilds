package co.marcin.NovaGuilds.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class ChatListener implements Listener {
	private final NovaGuilds plugin;
	
	public ChatListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		//if(p.hasPermission("novaguilds.notag")) {
		
		String msg = event.getMessage();
		
		Player player = event.getPlayer();
		NovaPlayer nplayer = plugin.getPlayerManager().getPlayerByName(player.getName());
		
		String tag = "";
		String rank = "";
		
		if(nplayer.hasGuild()) {
			NovaGuild guild = nplayer.getGuild(); 
			tag = plugin.config.getString("guild.tag");
			
			if(guild.getLeaderName().equalsIgnoreCase(player.getName())) {
				rank = Utils.fixColors(plugin.getMessages().getString("chat.guildinfo.leaderprefix"));
			}
			
			tag = Utils.fixColors(Utils.replace(tag,"{TAG}",nplayer.getGuild().getTag()));
			
			String prefix_guildchat = plugin.getConfig().getString("chat.guild.prefix");
			String prefix_allychat = plugin.getConfig().getString("chat.ally.prefix");
			
			if(msg.startsWith(prefix_allychat)) { //ally chat
				if(plugin.getConfig().getBoolean("chat.ally.enabled")) {
					if(!plugin.getConfig().getBoolean("chat.ally.colortags")) {
						tag = Utils.removeColors(tag);
					}
					
					if(plugin.getConfig().getBoolean("chat.ally.leaderprefix")) {
						tag = Utils.replace(tag,"{RANK}",rank);
					}
					else {
						tag = Utils.replace(tag,"{RANK}","");
					}
					
					String format = plugin.getConfig().getString("chat.ally.format");
					format = Utils.replace(format, "{TAG}",tag);
					format = Utils.replace(format, "{PLAYERNAME}",nplayer.getName());
					format = Utils.fixColors(format);
					
					msg = msg.substring(prefix_allychat.length(),msg.length());
					
					for(NovaPlayer nP : guild.getPlayers()) {
						if(nP.isOnline()) {
							nP.getPlayer().sendMessage(format+msg);
						}
					}
					
					for(String allyname : guild.getAllies()) {
						NovaGuild allyguild = plugin.getGuildManager().getGuildByName(allyname);
						for(NovaPlayer nP : allyguild.getPlayers()) {
							if(nP.isOnline()) {
								nP.getPlayer().sendMessage(format+msg);
							}
						}
					}
					
					event.setCancelled(true);
				}
			}
			else if(msg.startsWith(prefix_guildchat)) { //guild chat
				if(plugin.getConfig().getBoolean("chat.guild.enabled")) {
					String format = plugin.getConfig().getString("chat.guild.format");
					format = Utils.replace(format, "{PLAYERNAME}",nplayer.getName());
					format = Utils.fixColors(format);
					
					msg = msg.substring(prefix_guildchat.length(),msg.length());
					
					for(NovaPlayer nP : guild.getPlayers()) {
						if(nP.isOnline()) {
							nP.getPlayer().sendMessage(format+msg);
						}
					}
					
					event.setCancelled(true);
				}
			}
			

			tag = Utils.replace(tag,"{RANK}",rank);
			
		}
		
		String format = event.getFormat();
		format = Utils.replace(format,"{TAG}",tag);
		event.setFormat(format);
	}
}
