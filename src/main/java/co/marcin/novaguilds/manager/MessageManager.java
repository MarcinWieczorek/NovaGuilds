package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MessageManager {
	private final NovaGuilds plugin;
	private FileConfiguration messages = null;
	private String prefix;
	private String lang;

	public MessageManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean loadMessages() {
		lang = plugin.getConfig().getString("lang");
		File messagesFile = new File(plugin.getDataFolder() + "/lang", lang + ".yml");
		if(!messagesFile.exists()) {
			if(plugin.getResource("lang/" + lang + ".yml") != null) {
				plugin.saveResource("lang/" + lang + ".yml", false);
				plugin.info("New messages file created: " + lang + ".yml");
			}
			else {
				plugin.info("Couldn't find language file: " + lang + ".yml");
				return false;
			}
		}

		messages = YamlConfiguration.loadConfiguration(messagesFile);
		prefix = messages.getString("chat.prefix");
		return true;
	}

	//set string from file
	public String getMessagesString(String path) {
		String msg = getMessages().getString(path);

		if(msg == null) {
			return path;
		}

		return msg;
	}

	//get messages
	public FileConfiguration getMessages() {
		return messages;
	}

	//send string with prefix to a player
	public void sendPrefixMessage(Player p, String msg) {
		p.sendMessage(StringUtils.fixColors(prefix + msg));
	}

	public void sendPrefixMessage(CommandSender sender, String msg) {
		sender.sendMessage(StringUtils.fixColors(prefix + msg));
	}

	public void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(StringUtils.fixColors(msg));
	}

	public void sendMessage(Player player, String msg) {
		player.sendMessage(StringUtils.fixColors(msg));
	}

	//send message from file with prefix to a player
	public void sendMessagesMsg(Player p, String path) {
		sendPrefixMessage(p, getMessagesString(path));
	}

	//send message from file with prefix and vars to a player
	public void sendMessagesMsg(Player p, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		p.sendMessage(StringUtils.fixColors(prefix + msg));
	}

	public void sendMessagesList(Player player, String path, HashMap<String,String> vars, boolean prefix) {
		List<String> list = messages.getStringList(path);

		if(list != null) {
			for(String msg : list) {
				if(vars != null) {
					msg = StringUtils.replaceMap(msg, vars);
				}

				if(prefix) {
					sendPrefixMessage(player, msg);
				}
				else {
					sendMessage(player, msg);
				}
			}
		}
	}

	public void sendMessagesList(CommandSender sender, String path, HashMap<String,String> vars, boolean prefix) {
		List<String> list = messages.getStringList(path);

		if(list != null) {
			for(String msg : list) {
				if(vars != null) {
					msg = StringUtils.replaceMap(msg, vars);
				}

				if(prefix) {
					sendPrefixMessage(sender, msg);
				}
				else {
					sendMessage(sender, msg);
				}
			}
		}
	}

	//TODO finish
	public void sendMessagesList(Player player, String path, HashMap<String,String> vars) {
		sendMessagesList(player,path,vars,true);
	}

	//TODO finish
	public void sendMessagesList(Player player, String path) {
		sendMessagesList(player,path,null,true);
	}

	public void sendMessagesMsg(CommandSender sender, String path) {
		sendPrefixMessage(sender,getMessagesString(path));
	}

	public void sendMessagesMsg(CommandSender sender, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		sendPrefixMessage(sender,msg);
	}

	//broadcast string to all players
	public void broadcast(String msg) {
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	//broadcast message from file to all players
	public void broadcastMessageForPermitted(String path, String permission) {
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.hasPermission(permission)) {
				sendMessagesMsg(p,path);
			}
		}
	}

	public void broadcastMessage(String path,HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);

		for(Player p : plugin.getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	public void broadcastGuild(NovaGuild guild, String path, boolean prefix) {
		broadcastGuild(guild,path,new HashMap<String,String>(),prefix);
	}

	public void broadcastGuild(NovaGuild guild, String path,HashMap<String,String> vars, boolean prefix) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);

		for(Player p : guild.getOnlinePlayers()) {
			if(prefix) {
				sendPrefixMessage(p, msg);
			}
			else {
				sendMessage(p, msg);
			}
		}
	}

	//TODO finish
	public void broadcastAllies(NovaGuild guild, String path, HashMap<String,String> vars, boolean prefix) {
		for(NovaGuild ally : guild.getAllies()) {
			broadcastGuild(ally,path,vars,prefix);
		}
	}

	public void sendDelayedTeleportMessage(Player player) {
		HashMap<String,String> vars = new HashMap<>();
		vars.put("DELAY",plugin.getGroup(player).getTeleportDelay()+"");
		sendMessagesMsg(player, "chat.delayedteleport", vars);
	}

	public void sendNoPermissionsMessage(CommandSender sender) {
		sendMessagesMsg(sender, "chat.nopermissions");
	}

	public void sendNoPermissionsMessage(Player player) {
		sendMessagesMsg(player,"chat.nopermissions");
	}

	public void sendUsageMessage(CommandSender sender, String path) {
		sender.sendMessage(StringUtils.fixColors(getMessagesString("chat.usage." + path)));
	}

	public String getLang() {
		return lang;
	}
}
