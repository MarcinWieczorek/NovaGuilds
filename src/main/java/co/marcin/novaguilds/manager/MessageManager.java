package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.Title;
import org.bukkit.Bukkit;
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
	public String prefix;
	public ChatColor prefixColor = ChatColor.WHITE;
	public static MessageManager instance;

	public MessageManager(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		instance = this;
	}

	public boolean load() {
		setupDirectories();
		String lang = Config.LANG.getString();
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
	public static String getMessagesString(String path) {
		String msg = StringUtils.fixColors(instance.getMessages().getString(path));

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
	public static void sendPrefixMessage(Player p, String msg) {
		if(!msg.equals("none")) {
			p.sendMessage(StringUtils.fixColors(instance.prefix + msg));
		}
	}

	public static void sendPrefixMessage(CommandSender sender, String msg) {
		if(!msg.equals("none")) {
			sender.sendMessage(StringUtils.fixColors(instance.prefix + msg));
		}
	}

	public static void sendMessage(CommandSender sender, String msg) {
		if(!msg.equals("none")) {
			sender.sendMessage(StringUtils.fixColors(msg));
		}
	}

	public static void sendMessagesList(CommandSender sender, String path, HashMap<String,String> vars, boolean prefix) {
		List<String> list = instance.messages.getStringList(path);

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

	public static void sendMessagesMsg(CommandSender sender, String path) {
		sendMessagesMsg(sender, path, false);
	}

	public static void sendMessagesMsg(CommandSender sender, Message message, HashMap<String,String> vars) {
		sendMessagesMsg(sender, message.getPath(), vars, message.getTitle());
	}

	public static void sendMessagesMsg(CommandSender sender, String path, boolean title) {
		String msg = getMessagesString(path);
		if(NovaGuilds.getInstance().getConfigManager().useTitles() && title && sender instanceof Player) {
			sendTitle((Player) sender, msg);
		}
		else {
			sendPrefixMessage(sender, msg);
		}
	}

	public static void sendMessagesMsg(CommandSender sender, String path, HashMap<String, String> vars, boolean title) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		//sendPrefixMessage(sender,msg);
		if(NovaGuilds.getInstance().getConfigManager().useTitles() && title && sender instanceof Player) {
			sendTitle((Player) sender, msg);
		}
		else {
			sendPrefixMessage(sender, msg);
		}
	}

	public static void sendTitle(Player player, String msg) {
		Title title = new Title("");
		title.setSubtitleColor(instance.prefixColor);
		title.setSubtitle(StringUtils.fixColors(msg));
		title.send(player);
	}

	//broadcast string to all players
	public static void broadcast(String msg) {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	//broadcast message from file to all players with permission
	public static void broadcastMessageForPermitted(String path, String permission) {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			if(p.hasPermission(permission)) {
				sendMessagesMsg(p,path);
			}
		}
	}

	public static void broadcastMessage(Message message, HashMap<String,String> vars) {
		broadcastMessage(message.getPath(), vars);
	}

	public static void broadcastMessage(String path, HashMap<String, String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);

		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	public static void broadcastGuild(NovaGuild guild, Message message, HashMap<String,String> vars, boolean prefix) {
		String msg = getMessagesString(message.getPath());
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
}
