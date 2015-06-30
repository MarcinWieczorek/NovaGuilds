package co.marcin.novaguilds.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {
	private final NovaGuilds plugin;
	
	public ChatListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		String msg = event.getMessage();
		
		Player player = event.getPlayer();
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(player.getName());
		
		String tag = "";
		String rank = "";

		if(nPlayer.hasGuild()) {
			tag = plugin.getConfig().getString("guild.tag");
			NovaGuild guild = nPlayer.getGuild();
			
			if(guild.getLeader().getName().equalsIgnoreCase(player.getName())) {
				rank = StringUtils.fixColors(plugin.getMessageManager().getMessagesString("chat.guildinfo.leaderprefix"));
			}
			
			tag = StringUtils.fixColors(StringUtils.replace(tag, "{TAG}", nPlayer.getGuild().getTag()));

			tag = StringUtils.replace(tag, "{RANK}", rank);

			String prefixChatGuild = plugin.getConfig().getString("chat.guild.prefix");
			String prefixChatAlly = plugin.getConfig().getString("chat.ally.prefix");

			if(msg.startsWith(prefixChatAlly)) { //ally chat
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

					msg = msg.substring(prefixChatAlly.length(),msg.length());

					for(NovaPlayer nP : guild.getPlayers()) {
						if(nP.isOnline()) {
							nP.getPlayer().sendMessage(format+msg);
						}
					}

					if(guild.getAllies().size() > 0) {
						for(String allyName : guild.getAllies()) {
							NovaGuild allyGuild = plugin.getGuildManager().getGuildByName(allyName);
							for(NovaPlayer nP : allyGuild.getPlayers()) {
								if(nP.isOnline()) {
									nP.getPlayer().sendMessage(format + msg);
								}
							}
						}
					}

					event.setCancelled(true);
					plugin.getServer().getConsoleSender().sendMessage(format+msg);
				}
			}
			else if(msg.startsWith(prefixChatGuild)) { //guild chat
				if(plugin.getConfig().getBoolean("chat.guild.enabled")) {
					String format = plugin.getConfig().getString("chat.guild.format");
					format = StringUtils.replace(format, "{PLAYERNAME}", nPlayer.getName());
					format = StringUtils.fixColors(format);

					msg = msg.substring(prefixChatGuild.length(),msg.length());

					for(NovaPlayer nP : guild.getPlayers()) {
						if(nP.isOnline()) {
							nP.getPlayer().sendMessage(format+msg);
						}
					}

					event.setCancelled(true);
					plugin.getServer().getConsoleSender().sendMessage(format+msg);
				}
			}
			else if(player.hasPermission("novaguilds.chat.notag")) {
				tag = "";
			}
		}

		String format = event.getFormat();
		format = StringUtils.replace(format, "{TAG}", tag);
		event.setFormat(format);
	}

	@EventHandler
	public void onCommandExecute(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage().substring(1, event.getMessage().length());

		if(cmd.contains(" ")) {
			String[] split = cmd.split(" ");
			cmd = split[0];
		}

		plugin.debug(event.getMessage());

		if(plugin.getCommandManager().existsAlias(cmd)) {
			event.setMessage(event.getMessage().replaceFirst(cmd, plugin.getCommandManager().getMainCommand(cmd)));
		}

		//TODO: subCommands
	}
}
