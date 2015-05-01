package co.marcin.NovaGuilds.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.utils.StringUtils;

public class ChatListener implements Listener {
	private final NovaGuilds plugin;
	
	public ChatListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String msg = event.getMessage();
		
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(player.getName());
		
		String tag = "";
		String rank = "";

		if(nPlayer.hasGuild()) {
			NovaGuild guild = nPlayer.getGuild();
			tag = plugin.config.getString("guild.tag");
			
			if(guild.getLeaderName().equalsIgnoreCase(player.getName())) {
				rank = StringUtils.fixColors(plugin.getMessages().getString("chat.guildinfo.leaderprefix"));
			}
			
			tag = StringUtils.fixColors(StringUtils.replace(tag, "{TAG}", nPlayer.getGuild().getTag()));
			
			String prefix_guildchat = plugin.getConfig().getString("chat.guild.prefix");
			String prefix_allychat = plugin.getConfig().getString("chat.ally.prefix");
			
			if(msg.startsWith(prefix_allychat)) { //ally chat
				if(plugin.getConfig().getBoolean("chat.ally.enabled")) {
					if(!plugin.getConfig().getBoolean("chat.ally.colortags")) {
						tag = StringUtils.removeColors(tag);
					}
					
					if(plugin.getConfig().getBoolean("chat.ally.leaderprefix")) {
						tag = StringUtils.replace(tag, "{RANK}", rank);
					}
					else {
						tag = StringUtils.replace(tag, "{RANK}", "");
					}
					
					String format = plugin.getConfig().getString("chat.ally.format");
					format = StringUtils.replace(format, "{TAG}", tag);
					format = StringUtils.replace(format, "{PLAYERNAME}", nPlayer.getName());
					format = StringUtils.fixColors(format);
					
					msg = msg.substring(prefix_allychat.length(),msg.length());
					
					for(NovaPlayer nP : guild.getPlayers()) {
						if(nP.isOnline()) {
							nP.getPlayer().sendMessage(format+msg);
						}
					}

					if(guild.getAllies().size() > 0) {
						for(String allyname : guild.getAllies()) {
							NovaGuild allyguild = plugin.getGuildManager().getGuildByName(allyname);
							for(NovaPlayer nP : allyguild.getPlayers()) {
								if(nP.isOnline()) {
									nP.getPlayer().sendMessage(format + msg);
								}
							}
						}
					}

					event.setCancelled(true);
				}
			}
			else if(msg.startsWith(prefix_guildchat)) { //guild chat
				if(plugin.getConfig().getBoolean("chat.guild.enabled")) {
					String format = plugin.getConfig().getString("chat.guild.format");
					format = StringUtils.replace(format, "{PLAYERNAME}", nPlayer.getName());
					format = StringUtils.fixColors(format);
					
					msg = msg.substring(prefix_guildchat.length(),msg.length());
					
					for(NovaPlayer nP : guild.getPlayers()) {
						if(nP.isOnline()) {
							nP.getPlayer().sendMessage(format+msg);
						}
					}
					
					event.setCancelled(true);
				}
			}
			

			tag = StringUtils.replace(tag, "{RANK}", rank);
			
		}
		
		if(player.hasPermission("NovaGuilds.chat.notag")) {
			tag = "";
		}
		
		String format = event.getFormat();
		format = StringUtils.replace(format, "{TAG}", tag);
		event.setFormat(format);
	}
}
