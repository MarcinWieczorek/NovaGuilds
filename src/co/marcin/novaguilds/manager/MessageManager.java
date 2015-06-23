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
				plugin.getServer().getPluginManager().disablePlugin(plugin);
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

	//send message from file with prefix to a player
	public void sendMessagesMsg(Player p, String path) {
		p.sendMessage(StringUtils.fixColors(prefix + getMessagesString(path)));
	}

	//send message from file with prefix and vars to a player
	public void sendMessagesMsg(Player p, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		p.sendMessage(StringUtils.fixColors(prefix + msg));
	}

	public void sendMessagesMsg(CommandSender sender, String path) {
		sender.sendMessage(StringUtils.fixColors(prefix + getMessagesString(path)));
	}

	public void sendMessagesMsg(CommandSender sender, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		sender.sendMessage(StringUtils.fixColors(prefix + msg));
	}

	//broadcast string to all players
	public void broadcast(String msg) {
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	//broadcast message from file to all players
	public void broadcastMessage(String path, String permission) {
		for(Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.hasPermission("novaguilds."+permission)) {
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

	public void broadcastGuild(NovaGuild guild, String path) {
		broadcastGuild(guild,path,new HashMap<String,String>());
	}

	public void broadcastGuild(NovaGuild guild, String path,HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);

		for(Player p : guild.getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	public void sendDelayedTeleportMessage(Player player) {
		HashMap<String,String> vars = new HashMap<>();
		vars.put("DELAY",plugin.getGroup(player).getTeleportDelay()+"");
		sendMessagesMsg(player, "chat.delayedteleport", vars);
	}

	public String getLang() {
		return lang;
	}
}
