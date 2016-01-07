/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Lang;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.Title;
import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
		detectEssentialsLocale();
		String lang = Config.LANG_NAME.getString();
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
			messages = Lang.loadConfiguration(messagesFile);
		}
		catch(ScannerException | IOException e) {
			LoggerUtils.exception(e);
		}

		prefix = Message.CHAT_PREFIX.get();
		prefixColor = ChatColor.getByChar(ChatColor.getLastColors(prefix).charAt(1));

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

	public static void detectEssentialsLocale() {
		Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

		if(essentials != null && !Config.LANG_OVERRIDEESSENTIALS.getBoolean()) {
			String locale = essentials.getSettings().getLocale();
			if(locale.isEmpty()) {
				locale = "en";
			}

			if(ConfigManager.essentialsLocale.containsKey(locale)) {
				Config.LANG_NAME.set(ConfigManager.essentialsLocale.get(locale));
			}

			LoggerUtils.info("Changed lang to Essentials' locale: "+Config.LANG_NAME.getString());
		}
	}

	//set string from file
	public static String getMessagesString(String path) {
		String msg = StringUtils.fixColors(instance.getMessages().getString(path));

		return msg==null ? path : msg;
	}

	//get messages
	public FileConfiguration getMessages() {
		return messages;
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

	public static void sendMessagesList(CommandSender sender, String path, Map<String,String> vars, boolean prefix) {
		List<String> list = instance.messages.getStringList(path);

		if(list != null) {
			for(String msg : list) {
				if(vars != null) {
					msg = replaceMap(msg, vars);
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

	public static void sendMessagesMsg(CommandSender sender, Message message, Map<String,String> vars) {
		sendMessagesMsg(sender, message.getPath(), vars, message.getTitle());
	}

	public static void sendMessagesMsg(CommandSender sender, String path, boolean title) {
		String msg = getMessagesString(path);

		if(Config.USETITLES.getBoolean() && title && sender instanceof Player) {
			sendTitle((Player) sender, msg);
		}
		else {
			sendPrefixMessage(sender, msg);
		}
	}

	public static void sendMessagesMsg(CommandSender sender, String path, Map<String, String> vars, boolean title) {
		String msg = getMessagesString(path);
		msg = replaceMap(msg, vars);

		if(Config.USETITLES.getBoolean() && title && sender instanceof Player) {
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

	//broadcast message from file to all players with permission
	public static void broadcastMessageForPermitted(Message message, Permission permission) {
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			if(permission.has(p)) {
				sendMessagesMsg(p, message.getPath());
			}
		}
	}

	public static void broadcastMessage(Message message, Map<String,String> vars) {
		String msg = getMessagesString(message.getPath());
		msg = replaceMap(msg, vars);

		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}

	public static void broadcastGuild(NovaGuild guild, Message message, Map<String,String> vars, boolean prefix) {
		String msg = getMessagesString(message.getPath());
		msg = replaceMap(msg, vars);

		for(Player p : guild.getOnlinePlayers()) {
			if(prefix) {
				sendPrefixMessage(p, msg);
			}
			else {
				sendMessage(p, msg);
			}
		}
	}

	public static String replaceMap(String msg, Map<String, String> vars) {
		for(Map.Entry<String, String> entry : vars.entrySet()) {
			vars.put(entry.getKey(), entry.getValue() + NovaGuilds.getInstance().getMessageManager().prefixColor);
		}

		return StringUtils.replaceMap(msg, vars);
	}
}
