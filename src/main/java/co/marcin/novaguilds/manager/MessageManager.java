/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.util.Title;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.enums.Lang;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.exception.FatalNovaGuildsException;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import com.earth2me.essentials.Essentials;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private FileConfiguration messages = null;
	public String prefix;
	public ChatColor prefixColor = ChatColor.WHITE;
	private static MessageManager instance;
	private File messagesFile;

	/**
	 * The constructor
	 */
	public MessageManager() {
		instance = this;
	}

	/**
	 * Detects the language basing on Essentials and config
	 */
	public void detectLanguage() throws FileNotFoundException {
		detectEssentialsLocale();
		String lang = Config.LANG_NAME.getString();
		messagesFile = new File(plugin.getDataFolder() + "/lang", lang + ".yml");

		if(!messagesFile.exists()) {
			if(plugin.getResource("lang/" + lang + ".yml") != null) {
				plugin.saveResource("lang/" + lang + ".yml", false);
				LoggerUtils.info("New messages file created: " + lang + ".yml");
			}
			else {
				throw new FileNotFoundException("Couldn't find language file: " + lang + ".yml");
			}
		}
	}

	/**
	 * Checks if the messages file exists
	 *
	 * @return true if the file exists
	 */
	public boolean existsFile() {
		return messagesFile.exists();
	}

	/**
	 * Loads messages
	 */
	public void load() throws FatalNovaGuildsException {
		setupDirectories();

		try {
			detectLanguage();
			messages = Lang.loadConfiguration(messagesFile);

			//Fork, edit and compile NovaGuilds on your own if you want not to use the original prefix
			restorePrefix();

			prefix = Message.CHAT_PREFIX.get();
			prefixColor = ChatColor.getByChar(ChatColor.getLastColors(prefix).charAt(1));

			LoggerUtils.info("Messages loaded: " + Config.LANG_NAME.getString());
		}
		catch(ScannerException | IOException e) {
			throw new FatalNovaGuildsException("Failed to load messages", e);
		}
	}

	public void restorePrefix() {
		String prefix = Message.CHAT_PREFIX.get();
		prefix = StringUtils.removeColors(StringUtils.fixColors(prefix));

		if(!prefix.contains("NovaGuilds")) {
			Message.CHAT_PREFIX.set("&4&l[&7NovaGuilds&4&l] &6");
			LoggerUtils.info("Prefix restored.");
		}
	}

	/**
	 * Setups directories
	 */
	private void setupDirectories() {
		File langDir = new File(plugin.getDataFolder(), "lang/");

		if(!langDir.exists() && langDir.mkdir()) {
			LoggerUtils.info("Language dir created");
		}
	}

	/**
	 * Detects Essentials' Locale
	 */
	public static void detectEssentialsLocale() {
		if(plugin.getDependencyManager().isEnabled(Dependency.ESSENTIALS) && !Config.LANG_OVERRIDEESSENTIALS.getBoolean()) {
			Essentials essentials = plugin.getDependencyManager().get(Dependency.ESSENTIALS, Essentials.class);
			if(essentials.getSettings() == null) {
				return;
			}

			String locale = essentials.getSettings().getLocale();
			if(locale.isEmpty()) {
				locale = "en";
			}

			if(ConfigManager.essentialsLocale.containsKey(locale)) {
				Config.LANG_NAME.set(ConfigManager.essentialsLocale.get(locale));
			}

			LoggerUtils.info("Changed lang to Essentials' locale: " + Config.LANG_NAME.getString());
		}
	}

	/**
	 * Gets message string from configuration
	 *
	 * @param message Message enum
	 * @return message string
	 */
	public static String getMessagesString(Message message) {
		String msg = StringUtils.fixColors(getMessages().getString(message.getPath()));

		return msg == null ? message.getPath() : msg;
	}

	/**
	 * Gets messages FileConfiguration
	 *
	 * @return Messages' FileConfiguration
	 */
	public static FileConfiguration getMessages() {
		return instance.messages;
	}

	/**
	 * Sends prefixed message to a player
	 *
	 * @param sender receiver
	 * @param msg    message string
	 */
	public static void sendPrefixMessage(CommandSender sender, String msg) {
		if(!msg.equals("none")) {
			sender.sendMessage(StringUtils.fixColors(instance.prefix + msg));
		}
	}

	/**
	 * Sends a message without prefix to a player
	 *
	 * @param sender receiver
	 * @param msg    message string
	 */
	public static void sendMessage(CommandSender sender, String msg) {
		if(!msg.equals("none")) {
			sender.sendMessage(StringUtils.fixColors(msg));
		}
	}

	/**
	 * Sends a list of messages to a player
	 *
	 * @param sender  receiver
	 * @param message Message enum
	 */
	public static void sendMessagesList(CommandSender sender, Message message) {
		List<String> list = getMessages().getStringList(message.getPath());
		Map<VarKey, String> vars = message.getVars();
		boolean prefix = message.isPrefix();

		if(list != null) {
			for(String msg : list) {
				if(vars != null) {
					msg = replaceVarKeyMap(msg, vars);
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

	/**
	 * Sends a message to a player
	 *
	 * @param sender  receiver
	 * @param message Message enum
	 */
	public static void sendMessagesMsg(CommandSender sender, Message message) {
		String msg = getMessagesString(message);
		msg = replaceVarKeyMap(msg, message.getVars());
		boolean title = message.getTitle();

		if(Config.USETITLES.getBoolean() && title && sender instanceof Player) {
			sendTitle((Player) sender, msg);
		}
		else {
			if(message.isPrefix()) {
				sendPrefixMessage(sender, msg);
			}
			else {
				sendMessage(sender, msg);
			}
		}
	}

	/**
	 * Send a Title to the player
	 *
	 * @param player Player instance
	 * @param msg    message string
	 */
	public static void sendTitle(Player player, String msg) {
		Title title = null;

		switch(ConfigManager.getServerVersion()) {
			case MINECRAFT_1_7_R4:
				throw new IllegalArgumentException("Cannot send a title on 1.7 server");
			case MINECRAFT_1_8_R2:
				title = new co.marcin.novaguilds.impl.versionimpl.v1_8.TitleImpl();
				break;
			case MINECRAFT_1_9_R1:
			case MINECRAFT_1_9_R2:
			case MINECRAFT_1_10_R1:
				title = new co.marcin.novaguilds.impl.versionimpl.v1_9_R1.TitleImpl();
		}

		title.setSubtitleColor(instance.prefixColor);
		title.setSubtitle(StringUtils.fixColors(msg));
		title.send(player);
	}

	/**
	 * Broadcasts Message to players
	 *
	 * @param playerList List of Players
	 * @param message    Message enum
	 * @param permission Permission enum (null for none)
	 */
	public static void broadcast(List<Player> playerList, Message message, Permission permission) {
		for(Player player : playerList) {
			if(permission == null || permission.has(player)) {
				message.send(player);
			}
		}
	}

	/**
	 * Broadcasts message from file to all players with permission
	 *
	 * @param message    Message enum
	 * @param permission Permission enum
	 */
	public static void broadcast(Message message, Permission permission) {
		broadcast(new ArrayList<>(NovaGuilds.getOnlinePlayers()), message, permission);
	}

	/**
	 * Broadcasts message to all players
	 *
	 * @param message Message enum
	 */
	public static void broadcast(Message message) {
		broadcast(message, null);
	}

	/**
	 * Broadcasts message to guild members
	 *
	 * @param guild   Guild instance
	 * @param message Message enum
	 */
	public static void broadcast(NovaGuild guild, Message message) {
		broadcast(guild.getOnlinePlayers(), message, null);
	}

	/**
	 * Replaces a map of vars preserving the prefix color
	 *
	 * @param msg  message string
	 * @param vars Map<String, String> of variables
	 * @return String
	 */
	public static String replaceVarKeyMap(String msg, Map<VarKey, String> vars) {
		return replaceVarKeyMap(msg, vars, true);
	}

	public static String replaceVarKeyMap(String msg, Map<VarKey, String> vars, boolean usePrefixColor) {
		for(Map.Entry<VarKey, String> entry : vars.entrySet()) {
			vars.put(entry.getKey(), entry.getValue() + (usePrefixColor ? instance.prefixColor : ""));
		}

		return StringUtils.replaceVarKeyMap(msg, vars);
	}

	public static List<String> replaceVarKeyMap(List<String> list, Map<VarKey, String> vars, boolean usePrefixColor) {
		final List<String> newList = new ArrayList<>();

		for(String string : list) {
			string = replaceVarKeyMap(string, vars, usePrefixColor);
			newList.add(string);
		}

		return newList;
	}

	public void setMessages(YamlConfiguration messages) {
		this.messages = messages;
	}

	public static void set(Message message, String string) {
		getMessages().set(message.getPath(), string);
	}

	public static void set(Message message, List<String> list) {
		getMessages().set(message.getPath(), list);
	}
}
