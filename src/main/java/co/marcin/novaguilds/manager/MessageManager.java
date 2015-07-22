package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.Title;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MessageManager {
	private final NovaGuilds plugin;
	private FileConfiguration messages = null;
	private String prefix;
	private ChatColor prefixColor = ChatColor.WHITE;
	private String lang;

	public MessageManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean loadMessages() {
		setupDirectories();
		lang = plugin.getConfig().getString("lang");
		File messagesFile = new File(plugin.getDataFolder() + "/lang", lang + ".yml");
		if(!messagesFile.exists()) {
			if(plugin.getResource("lang/" + lang + ".yml") != null) {
				plugin.saveResource("lang/" + lang + ".yml", false);
				LoggerUtils.info("New messages file created: " + lang + ".yml");
			}
			else {
				LoggerUtils.info("Couldn't find language file: " + lang + ".yml");
				return false;
			}
		}

		try {
			messages = YamlConfiguration.loadConfiguration(messagesFile);
		}
		catch(ScannerException e) {
			LoggerUtils.exception(e);
		}

		prefix = Message.CHAT_PREFIX.get();
		String prefixwospace = StringUtils.replace(prefix," ","");
		prefixwospace = prefixwospace.substring(prefixwospace.length() - 2);
		if(prefixwospace.startsWith("&")) {
			prefixColor = ChatColor.getByChar(prefixwospace.charAt(1));
		}

		return true;
	}

	private void setupDirectories() {
		File langsDir = new File(plugin.getDataFolder(),"lang/");
		if(!langsDir.exists()) {
			if(langsDir.mkdir()) {
				LoggerUtils.info("Language dir created");
			}
		}
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
		sendMessagesList(player, path, vars, true);
	}

	//TODO finish
	public void sendMessagesList(Player player, String path) {
		sendMessagesList(player, path, null, true);
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

	public void sendMessagesMsg(CommandSender sender, String path) {
		sendMessagesMsg(sender, path, false);
	}

	public void sendMessagesMsg(CommandSender sender, Message message, HashMap<String,String> vars) {
		sendMessagesMsg(sender, message.getPath(), vars, message.getTitle());
	}

	public void sendMessagesMsg(CommandSender sender, String path, boolean title) {
		String msg = getMessagesString(path);
		if(plugin.getConfigManager().useTitles() && title && sender instanceof Player) {
			sendTitle((Player) sender, msg);
		}
		else {
			sendPrefixMessage(sender, msg);
		}
	}

	public void sendMessagesMsg(CommandSender sender, String path, HashMap<String,String> vars) {
		sendMessagesMsg(sender,path,vars,true);
	}

	public void sendMessagesMsg(CommandSender sender, String path, HashMap<String,String> vars, boolean title) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		//sendPrefixMessage(sender,msg);
		if(plugin.getConfigManager().useTitles() && title && sender instanceof Player) {
			sendTitle((Player) sender, msg);
		}
		else {
			sendPrefixMessage(sender, msg);
		}
	}

	public void sendTitle(Player player, String msg) {
		Title title = new Title("");
		title.setSubtitleColor(prefixColor);
		title.setSubtitle(StringUtils.fixColors(msg));
		title.send(player);
	}

	//broadcast string to all players
	public void broadcast(String msg) {
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	//broadcast message from file to all players with permission
	public void broadcastMessageForPermitted(String path, String permission) {
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.hasPermission(permission)) {
				sendMessagesMsg(p,path);
			}
		}
	}

	public void broadcastMessage(Message message, HashMap<String,String> vars) {
		broadcastMessage(message.getPath(), vars);
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

	public void broadcastGuild(NovaGuild guild, Message message,HashMap<String,String> vars, boolean prefix) {
		broadcastGuild(guild, message.getPath(), vars, prefix);
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
		vars.put("DELAY",plugin.getGroupManager().getGroup(player).getGuildTeleportDelay()+"");
		Message.CHAT_DELAYEDTELEPORT.vars(vars).send(player);
	}

	public void sendNoPermissionsMessage(CommandSender sender) {
		Message.CHAT_NOPERMISSIONS.send(sender);
	}

//	public void sendNoPermissionsMessage(Player player) {
//		sendMessagesMsg(player, Message.CHAT_NOPERMISSIONS);
//	}

	public void sendUsageMessage(CommandSender sender, String path) {
		sender.sendMessage(StringUtils.fixColors(getMessagesString("chat.usage." + path)));
	}

	public String getLang() {
		return lang;
	}
}
