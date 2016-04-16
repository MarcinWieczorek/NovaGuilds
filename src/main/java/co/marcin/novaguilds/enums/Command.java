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

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.command.tabcompleter.TabCompleterAdmin;
import co.marcin.novaguilds.command.tabcompleter.TabCompleterGuild;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Command {
	ADMIN_ACCESS(Permission.NOVAGUILDS_ADMIN_ACCESS, "nga", new TabCompleterAdmin()),
	ADMIN_RELOAD(Permission.NOVAGUILDS_ADMIN_RELOAD),
	ADMIN_SAVE(Permission.NOVAGUILDS_ADMIN_SAVE),
	ADMIN_CHATSPY(Permission.NOVAGUILDS_ADMIN_CHATSPY_SELF, Flag.NOCONSOLE),

	ADMIN_CONFIG_ACCESS(Permission.NOVAGUILDS_ADMIN_CONFIG_ACCESS, Message.CHAT_USAGE_NGA_CONFIG_ACCESS),
	ADMIN_CONFIG_GET(Permission.NOVAGUILDS_ADMIN_CONFIG_GET, Message.CHAT_USAGE_NGA_CONFIG_GET),
	ADMIN_CONFIG_RELOAD(Permission.NOVAGUILDS_ADMIN_CONFIG_RELOAD, Message.CHAT_USAGE_NGA_CONFIG_RELOAD),
	ADMIN_CONFIG_RESET(Permission.NOVAGUILDS_ADMIN_CONFIG_RESET, Message.CHAT_USAGE_NGA_CONFIG_RESET, Flag.CONFIRM),
	ADMIN_CONFIG_SAVE(Permission.NOVAGUILDS_ADMIN_CONFIG_SAVE, Message.CHAT_USAGE_NGA_CONFIG_SAVE, Flag.CONFIRM),
	ADMIN_CONFIG_SET(Permission.NOVAGUILDS_ADMIN_CONFIG_SET, Message.CHAT_USAGE_NGA_CONFIG_SET),

	ADMIN_GUILD_ACCESS(Permission.NOVAGUILDS_ADMIN_GUILD_ACCESS),
	ADMIN_GUILD_ABANDON(Permission.NOVAGUILDS_ADMIN_GUILD_ABANDON, Message.CHAT_USAGE_NGA_GUILD_ABANDON, Flag.CONFIRM),
	ADMIN_GUILD_PURGE(Permission.NOVAGUILDS_ADMIN_GUILD_PURGE, Message.CHAT_USAGE_NGA_GUILD_PURGE, Flag.CONFIRM),
	ADMIN_GUILD_BANK_PAY(Permission.NOVAGUILDS_ADMIN_GUILD_BANK_PAY),
	ADMIN_GUILD_BANK_WITHDRAW(Permission.NOVAGUILDS_ADMIN_GUILD_BANK_WITHDRAW),
	ADMIN_GUILD_INACTIVE(Permission.NOVAGUILDS_ADMIN_GUILD_INACTIVE_LIST),
	ADMIN_GUILD_INVITE(Permission.NOVAGUILDS_ADMIN_GUILD_INVITE),
	ADMIN_GUILD_KICK(Permission.NOVAGUILDS_ADMIN_GUILD_KICK),
	ADMIN_GUILD_LIST(Permission.NOVAGUILDS_ADMIN_GUILD_LIST),
	ADMIN_GUILD_SET_LEADER(Permission.NOVAGUILDS_ADMIN_GUILD_SET_LEADER),
	ADMIN_GUILD_SET_LIVEREGENERATIONTIME(Permission.NOVAGUILDS_ADMIN_GUILD_SET_LIVEREGENERATIONTIME),
	ADMIN_GUILD_SET_LIVES(Permission.NOVAGUILDS_ADMIN_GUILD_SET_LIVES),
	ADMIN_GUILD_SET_NAME(Permission.NOVAGUILDS_ADMIN_GUILD_SET_NAME),
	ADMIN_GUILD_SET_POINTS(Permission.NOVAGUILDS_ADMIN_GUILD_SET_POINTS),
	ADMIN_GUILD_SET_TAG(Permission.NOVAGUILDS_ADMIN_GUILD_SET_TAG),
	ADMIN_GUILD_SET_TIMEREST(Permission.NOVAGUILDS_ADMIN_GUILD_SET_TIMEREST),
	ADMIN_GUILD_SET_SLOTS(Permission.NOVAGUILDS_ADMIN_GUILD_SET_SLOTS),
	ADMIN_GUILD_TELEPORT(Permission.NOVAGUILDS_ADMIN_GUILD_TELEPORT_SELF, Message.CHAT_USAGE_NGA_GUILD_TP),
	ADMIN_GUILD_RESET_POINTS(Permission.NOVAGUILDS_ADMIN_GUILD_RESET_POINTS, Message.CHAT_USAGE_NGA_GUILD_RESET_POINTS, Flag.CONFIRM),

	ADMIN_REGION_ACCESS(Permission.NOVAGUILDS_ADMIN_REGION_ACCESS, Message.CHAT_USAGE_NGA_REGION_ACCESS),
	ADMIN_REGION_BYPASS(Permission.NOVAGUILDS_ADMIN_REGION_BYPASS_SELF, Message.CHAT_USAGE_NGA_REGION_BYPASS),
	ADMIN_REGION_DELETE(Permission.NOVAGUILDS_ADMIN_REGION_DELETE, Message.CHAT_USAGE_NGA_REGION_DELETE, Flag.CONFIRM),
	ADMIN_REGION_LIST(Permission.NOVAGUILDS_ADMIN_REGION_LIST, Message.CHAT_USAGE_NGA_REGION_LIST),
	ADMIN_REGION_SPECTATE(Permission.NOVAGUILDS_ADMIN_REGION_CHANGE_SPECTATE_SELF, Message.CHAT_USAGE_NGA_REGION_SPECTATE),
	ADMIN_REGION_TELEPORT(Permission.NOVAGUILDS_ADMIN_REGION_TELEPORT_SELF, Message.CHAT_USAGE_NGA_REGION_TELEPORT),

	ADMIN_HOLOGRAM_ACCESS(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ACCESS),
	ADMIN_HOLOGRAM_LIST(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_LIST),
	ADMIN_HOLOGRAM_TELEPORT(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT, Flag.NOCONSOLE),
	ADMIN_HOLOGRAM_DELETE(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_DELETE),
	ADMIN_HOLOGRAM_ADD(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADD, Flag.NOCONSOLE),
	ADMIN_HOLOGRAM_ADDTOP(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADDTOP, Flag.NOCONSOLE),
	ADMIN_HOLOGRAM_TELEPORT_HERE(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT_HERE, Flag.NOCONSOLE),

	GUILD_ACCESS(Permission.NOVAGUILDS_GUILD_ACCESS, "guild", new TabCompleterGuild()),
	GUILD_ABANDON(Permission.NOVAGUILDS_GUILD_ABANDON, "abandon", Message.CHAT_USAGE_GUILD_ABANDON, Flag.NOCONSOLE, Flag.CONFIRM),
	GUILD_ALLY(Permission.NOVAGUILDS_GUILD_ALLY, Message.CHAT_USAGE_GUILD_ALLY, Flag.NOCONSOLE),
	GUILD_BANK_PAY(Permission.NOVAGUILDS_GUILD_BANK_PAY, Message.CHAT_USAGE_GUILD_BANK_PAY, Flag.NOCONSOLE),
	GUILD_BANK_WITHDRAW(Permission.NOVAGUILDS_GUILD_BANK_WITHDRAW, Message.CHAT_USAGE_GUILD_BANK_WITHDRAW, Flag.NOCONSOLE),
	GUILD_BOSS(Permission.NOVAGUILDS_GUILD_BOSS, Flag.NOCONSOLE),
	GUILD_BUYLIFE(Permission.NOVAGUILDS_GUILD_BUYLIFE, Message.CHAT_USAGE_GUILD_BUY_LIFE, Flag.NOCONSOLE),
	GUILD_BUYSLOT(Permission.NOVAGUILDS_GUILD_BUYSLOT, Message.CHAT_USAGE_GUILD_BUY_SLOT, Flag.NOCONSOLE),
	GUILD_CHATMODE(Permission.NOVAGUILDS_GUILD_CHATMODE, Message.CHAT_USAGE_GUILD_CHATMODE, Flag.NOCONSOLE),
	GUILD_COMPASS(Permission.NOVAGUILDS_GUILD_COMPASS, Message.CHAT_USAGE_GUILD_COMPASS, Flag.NOCONSOLE),
	GUILD_CREATE(Permission.NOVAGUILDS_GUILD_CREATE, "create", Message.CHAT_USAGE_GUILD_CREATE, Flag.NOCONSOLE),
	GUILD_EFFECT(Permission.NOVAGUILDS_GUILD_EFFECT, Message.CHAT_USAGE_GUILD_EFFECT, Flag.NOCONSOLE),
	GUILD_HOME(Permission.NOVAGUILDS_GUILD_HOME, Message.CHAT_USAGE_GUILD_HOME_TELEPORT, Flag.NOCONSOLE),
	GUILD_INFO(Permission.NOVAGUILDS_GUILD_INFO, "gi", Message.CHAT_USAGE_GUILD_INFO),
	GUILD_INVITE(Permission.NOVAGUILDS_GUILD_INVITE, "invite", Message.CHAT_USAGE_GUILD_INVITE, Flag.NOCONSOLE),
	GUILD_JOIN(Permission.NOVAGUILDS_GUILD_JOIN, "join", Message.CHAT_USAGE_GUILD_JOIN, Flag.NOCONSOLE),
	GUILD_KICK(Permission.NOVAGUILDS_GUILD_KICK, Message.CHAT_USAGE_GUILD_KICK, Flag.NOCONSOLE),
	GUILD_LEADER(Permission.NOVAGUILDS_GUILD_LEADER, Message.CHAT_USAGE_GUILD_LEADER, Flag.NOCONSOLE),
	GUILD_LEAVE(Permission.NOVAGUILDS_GUILD_LEAVE, "leave", Message.CHAT_USAGE_GUILD_LEAVE, Flag.NOCONSOLE),
	GUILD_MENU(Permission.NOVAGUILDS_GUILD_MENU, "guildmenu", Message.CHAT_USAGE_GUILD_MENU, Flag.NOCONSOLE),
	GUILD_PVPTOGGLE(Permission.NOVAGUILDS_GUILD_PVPTOGGLE, Message.CHAT_USAGE_GUILD_PVPTOGGLE, Flag.NOCONSOLE),
	GUILD_REQUIREDITEMS(Permission.NOVAGUILDS_GUILD_REQUIREDITEMS, Message.CHAT_USAGE_GUILD_REQUIREDITEMS, Flag.NOCONSOLE),
	GUILD_TOP(Permission.NOVAGUILDS_GUILD_TOP, Message.CHAT_USAGE_GUILD_TOP),
	GUILD_WAR(Permission.NOVAGUILDS_GUILD_WAR, Message.CHAT_USAGE_GUILD_WAR, Flag.NOCONSOLE),
	GUILD_OPENINVITATION(Permission.NOVAGUILDS_GUILD_OPENINVITATION, Message.CHAT_USAGE_GUILD_OPENINVITATION, Flag.NOCONSOLE),
	GUILD_SET_NAME(Permission.NOVAGUILDS_GUILD_SET_NAME, Message.CHAT_USAGE_GUILD_SET_NAME, Flag.NOCONSOLE),
	GUILD_SET_TAG(Permission.NOVAGUILDS_GUILD_SET_TAG, Message.CHAT_USAGE_GUILD_SET_TAG, Flag.NOCONSOLE),

	REGION_ACCESS(Permission.NOVAGUILDS_REGION_ACCESS, Message.CHAT_USAGE_REGION_ACCESS, Flag.NOCONSOLE),
	REGION_BUY(Permission.NOVAGUILDS_REGION_CREATE, Message.CHAT_USAGE_REGION_BUY, Flag.NOCONSOLE),
	REGION_DELETE(Permission.NOVAGUILDS_REGION_DELETE, Message.CHAT_USAGE_REGION_DELETE, Flag.NOCONSOLE, Flag.CONFIRM),

	TOOL_GET(Permission.NOVAGUILDS_TOOL_GET, Flag.NOCONSOLE),
	PLAYERINFO(Permission.NOVAGUILDS_PLAYERINFO, "playerinfo"),
	NOVAGUILDS(Permission.NOVAGUILDS_NOVAGUILDS, "novaguilds"),
	CONFIRM(Permission.NOVAGUILDS_CONFIRM, "confirm");

	public enum Flag {
		NOCONSOLE,
		CONFIRM
	}

	private final Message usageMessage;
	private final Permission permission;
	private final String genericCommand;
	private final TabCompleter tabCompleter;
	private final List<Flag> flags = new ArrayList<>();
	private Object executorVariable;

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param usageMessage   the usage message
	 * @param tabCompleter   tab completer instance
	 * @param flags          command flags
	 */
	Command(Permission permission, String genericCommand, Message usageMessage, TabCompleter tabCompleter, Flag... flags) {
		this.permission = permission;
		this.usageMessage = usageMessage;
		this.genericCommand = genericCommand;
		this.tabCompleter = tabCompleter;
		setFlags(flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param usageMessage   the usage message
	 * @param flags          command flags
	 */
	Command(Permission permission, String genericCommand, Message usageMessage, Flag... flags) {
		this(permission, genericCommand, usageMessage, null, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param tabCompleter   tab completer instance
	 * @param flags          command flags
	 */
	Command(Permission permission, String genericCommand, TabCompleter tabCompleter, Flag... flags) {
		this(permission, genericCommand, null, tabCompleter, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission   the permission
	 * @param usageMessage the usage message
	 * @param flags        command flags
	 */
	Command(Permission permission, Message usageMessage, Flag... flags) {
		this(permission, null, usageMessage, null, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission     the permission
	 * @param genericCommand the generic command string
	 * @param flags          command flags
	 */
	Command(Permission permission, String genericCommand, Flag... flags) {
		this(permission, genericCommand, null, null, flags);
	}

	/**
	 * The constructor
	 *
	 * @param permission the permission
	 * @param flags      command flags
	 */
	Command(Permission permission, Flag... flags) {
		this(permission, null, null, null, flags);
	}

	/**
	 * Sets flags
	 *
	 * @param flags flags
	 */
	private void setFlags(Flag... flags) {
		Collections.addAll(this.flags, flags);
	}

	/**
	 * Gets the permission
	 *
	 * @return the permission string
	 */
	public Permission getPermission() {
		return permission;
	}

	/**
	 * Checks if a sender has permission to execute the command
	 *
	 * @param sender the sender
	 * @return boolean
	 */
	public boolean hasPermission(CommandSender sender) {
		return permission.has(sender) || sender.isOp();
	}

	/**
	 * Checks if the command has a flag
	 *
	 * @param flag flag
	 * @return boolean
	 */
	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
	}

	/**
	 * Checks if a sender is allowed
	 * (if is console)
	 *
	 * @param sender the sender
	 * @return boolean
	 */
	public boolean allowedSender(CommandSender sender) {
		return sender instanceof Player || !hasFlag(Flag.NOCONSOLE);
	}

	/**
	 * Gets usage message
	 *
	 * @return the message
	 */
	public Message getUsageMessage() {
		return usageMessage;
	}

	/**
	 * Executes the command
	 *
	 * @param sender sender
	 * @param args   arguments
	 */
	public void execute(CommandSender sender, String[] args) {
		NovaGuilds.getInstance().getCommandManager().execute(this, sender, args);
	}

	/**
	 * Checks if the command has a generic command
	 *
	 * @return boolean
	 */
	public boolean hasGenericCommand() {
		return genericCommand != null;
	}

	/**
	 * Checks if the command has a tab completer
	 *
	 * @return boolean
	 */
	public boolean hasTabCompleter() {
		return tabCompleter != null;
	}

	/**
	 * Gets generic command string
	 *
	 * @return the string
	 */
	public String getGenericCommand() {
		return genericCommand;
	}

	/**
	 * Gets tab completer
	 *
	 * @return tab completer instance
	 */
	public TabCompleter getTabCompleter() {
		return tabCompleter;
	}

	/**
	 * Sets executor variable
	 *
	 * @param executorVariable the value
	 * @return the instance
	 */
	public Command executorVariable(Object executorVariable) {
		this.executorVariable = executorVariable;
		return this;
	}

	/**
	 * Gets executor variable
	 *
	 * @return the value
	 */
	public Object getExecutorVariable() {
		return executorVariable;
	}
}
