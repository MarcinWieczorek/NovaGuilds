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

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.command.tabcompleter.TabCompleterAdmin;
import co.marcin.novaguilds.command.tabcompleter.TabCompleterGuild;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public enum Command {
	ADMIN_ACCESS(Permission.NOVAGUILDS_ADMIN_ACCESS, true, "nga", new TabCompleterAdmin()),
	ADMIN_RELOAD(Permission.NOVAGUILDS_ADMIN_RELOAD),
	ADMIN_SAVE(Permission.NOVAGUILDS_ADMIN_SAVE),
	ADMIN_CHATSPY(Permission.NOVAGUILDS_ADMIN_CHATSPY_SELF, CommandFlag.NOCONSOLE),

	ADMIN_CONFIG_ACCESS(Permission.NOVAGUILDS_ADMIN_CONFIG_ACCESS, null, Message.CHAT_USAGE_NGA_CONFIG_ACCESS),
	ADMIN_CONFIG_GET(Permission.NOVAGUILDS_ADMIN_CONFIG_GET, true, Message.CHAT_USAGE_NGA_CONFIG_GET),
	ADMIN_CONFIG_RELOAD(Permission.NOVAGUILDS_ADMIN_CONFIG_RELOAD, true, Message.CHAT_USAGE_NGA_CONFIG_RELOAD),
	ADMIN_CONFIG_RESET(Permission.NOVAGUILDS_ADMIN_CONFIG_RESET, true, Message.CHAT_USAGE_NGA_CONFIG_RESET, true),
	ADMIN_CONFIG_SAVE(Permission.NOVAGUILDS_ADMIN_CONFIG_SAVE, true, Message.CHAT_USAGE_NGA_CONFIG_SAVE, true),
	ADMIN_CONFIG_SET(Permission.NOVAGUILDS_ADMIN_CONFIG_SET, true, Message.CHAT_USAGE_NGA_CONFIG_SET),

	ADMIN_GUILD_ACCESS(Permission.NOVAGUILDS_ADMIN_GUILD_ACCESS),
	ADMIN_GUILD_ABANDON(Permission.NOVAGUILDS_ADMIN_GUILD_ABANDON, true, Message.CHAT_USAGE_NGA_GUILD_ABANDON, true),
	ADMIN_GUILD_PURGE(Permission.NOVAGUILDS_ADMIN_GUILD_PURGE, true, Message.CHAT_USAGE_NGA_GUILD_PURGE, true),
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
	ADMIN_GUILD_TELEPORT(Permission.NOVAGUILDS_ADMIN_GUILD_TELEPORT_SELF, true, Message.CHAT_USAGE_NGA_GUILD_TP),
	ADMIN_GUILD_RESET_POINTS(Permission.NOVAGUILDS_ADMIN_GUILD_RESET_POINTS, null, Message.CHAT_USAGE_NGA_GUILD_RESET_POINTS, CommandFlag.CONFIRM),

	ADMIN_REGION_ACCESS(Permission.NOVAGUILDS_ADMIN_REGION_ACCESS, true, Message.CHAT_USAGE_NGA_REGION_ACCESS),
	ADMIN_REGION_BYPASS(Permission.NOVAGUILDS_ADMIN_REGION_BYPASS_SELF, true, Message.CHAT_USAGE_NGA_REGION_BYPASS),
	ADMIN_REGION_DELETE(Permission.NOVAGUILDS_ADMIN_REGION_DELETE, null, Message.CHAT_USAGE_NGA_REGION_DELETE, CommandFlag.CONFIRM),
	ADMIN_REGION_LIST(Permission.NOVAGUILDS_ADMIN_REGION_LIST, true, Message.CHAT_USAGE_NGA_REGION_LIST),
	ADMIN_REGION_TELEPORT(Permission.NOVAGUILDS_ADMIN_REGION_TELEPORT_SELF, true, Message.CHAT_USAGE_NGA_REGION_TELEPORT),

	ADMIN_HOLOGRAM_ACCESS(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ACCESS),
	ADMIN_HOLOGRAM_LIST(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_LIST),
	ADMIN_HOLOGRAM_TELEPORT(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT, CommandFlag.NOCONSOLE),
	ADMIN_HOLOGRAM_DELETE(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_DELETE),
	ADMIN_HOLOGRAM_ADD(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADD, CommandFlag.NOCONSOLE),
	ADMIN_HOLOGRAM_ADDTOP(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADDTOP, CommandFlag.NOCONSOLE),
	ADMIN_HOLOGRAM_TELEPORT_HERE(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT_HERE, CommandFlag.NOCONSOLE),

	GUILD_ACCESS(Permission.NOVAGUILDS_GUILD_ACCESS, false, "guild", new TabCompleterGuild()),
	GUILD_ABANDON(Permission.NOVAGUILDS_GUILD_ABANDON, "abandon", Message.CHAT_USAGE_GUILD_ABANDON, CommandFlag.NOCONSOLE, CommandFlag.CONFIRM),
	GUILD_ALLY(Permission.NOVAGUILDS_GUILD_ALLY, false, Message.CHAT_USAGE_GUILD_ALLY),
	GUILD_BANK_PAY(Permission.NOVAGUILDS_GUILD_BANK_PAY, false, Message.CHAT_USAGE_GUILD_BANK_PAY),
	GUILD_BANK_WITHDRAW(Permission.NOVAGUILDS_GUILD_BANK_WITHDRAW, false, Message.CHAT_USAGE_GUILD_BANK_WITHDRAW),
	GUILD_BOSS(Permission.NOVAGUILDS_GUILD_BOSS, CommandFlag.NOCONSOLE),
	GUILD_BUYLIFE(Permission.NOVAGUILDS_GUILD_BUYLIFE, false, Message.CHAT_USAGE_GUILD_BUY_LIFE),
	GUILD_BUYSLOT(Permission.NOVAGUILDS_GUILD_BUYSLOT, false, Message.CHAT_USAGE_GUILD_BUY_SLOT),
	GUILD_CHATMODE(Permission.NOVAGUILDS_GUILD_CHATMODE, false, Message.CHAT_USAGE_GUILD_CHATMODE),
	GUILD_COMPASS(Permission.NOVAGUILDS_GUILD_COMPASS, false, Message.CHAT_USAGE_GUILD_COMPASS),
	GUILD_CREATE(Permission.NOVAGUILDS_GUILD_CREATE, "create", Message.CHAT_USAGE_GUILD_CREATE, CommandFlag.NOCONSOLE),
	GUILD_EFFECT(Permission.NOVAGUILDS_GUILD_EFFECT, false, Message.CHAT_USAGE_GUILD_EFFECT),
	GUILD_HOME(Permission.NOVAGUILDS_GUILD_HOME, false, Message.CHAT_USAGE_GUILD_HOME_TELEPORT),
	GUILD_INFO(Permission.NOVAGUILDS_GUILD_INFO, "gi", Message.CHAT_USAGE_GUILD_INFO),
	GUILD_INVITE(Permission.NOVAGUILDS_GUILD_INVITE, "invite", Message.CHAT_USAGE_GUILD_INVITE, CommandFlag.NOCONSOLE),
	GUILD_JOIN(Permission.NOVAGUILDS_GUILD_JOIN, "join", Message.CHAT_USAGE_GUILD_JOIN, CommandFlag.NOCONSOLE),
	GUILD_KICK(Permission.NOVAGUILDS_GUILD_KICK, false, Message.CHAT_USAGE_GUILD_KICK),
	GUILD_LEADER(Permission.NOVAGUILDS_GUILD_LEADER, false, Message.CHAT_USAGE_GUILD_LEADER),
	GUILD_LEAVE(Permission.NOVAGUILDS_GUILD_LEAVE, "leave", Message.CHAT_USAGE_GUILD_LEAVE, CommandFlag.NOCONSOLE),
	GUILD_MENU(Permission.NOVAGUILDS_GUILD_MENU, "guildmenu", Message.CHAT_USAGE_GUILD_MENU, CommandFlag.NOCONSOLE),
	GUILD_PVPTOGGLE(Permission.NOVAGUILDS_GUILD_PVPTOGGLE, false, Message.CHAT_USAGE_GUILD_PVPTOGGLE),
	GUILD_REQUIREDITEMS(Permission.NOVAGUILDS_GUILD_REQUIREDITEMS, false, Message.CHAT_USAGE_GUILD_REQUIREDITEMS),
	GUILD_TOP(Permission.NOVAGUILDS_GUILD_TOP, true, Message.CHAT_USAGE_GUILD_TOP),
	GUILD_WAR(Permission.NOVAGUILDS_GUILD_WAR, false, Message.CHAT_USAGE_GUILD_WAR),
	GUILD_OPENINVITATION(Permission.NOVAGUILDS_GUILD_OPENINVITATION, false, Message.CHAT_USAGE_GUILD_OPENINVITATION),
	GUILD_SET_NAME(Permission.NOVAGUILDS_GUILD_SET_NAME, false, Message.CHAT_USAGE_GUILD_SET_NAME),
	GUILD_SET_TAG(Permission.NOVAGUILDS_GUILD_SET_TAG, false, Message.CHAT_USAGE_GUILD_SET_TAG),

	REGION_ACCESS(Permission.NOVAGUILDS_REGION_ACCESS, null, Message.CHAT_USAGE_REGION_ACCESS, CommandFlag.NOCONSOLE),
	REGION_BUY(Permission.NOVAGUILDS_REGION_CREATE, null, Message.CHAT_USAGE_REGION_BUY, CommandFlag.NOCONSOLE),
	REGION_DELETE(Permission.NOVAGUILDS_REGION_DELETE, null, Message.CHAT_USAGE_REGION_DELETE, CommandFlag.NOCONSOLE, CommandFlag.CONFIRM),

	TOOL_GET(Permission.NOVAGUILDS_TOOL_GET, CommandFlag.NOCONSOLE),
	PLAYERINFO(Permission.NOVAGUILDS_PLAYERINFO, true, "playerinfo"),
	NOVAGUILDS(Permission.NOVAGUILDS_NOVAGUILDS, true, "novaguilds"),
	CONFIRM(Permission.NOVAGUILDS_CONFIRM, false, "confirm");

	private enum CommandFlag {
		NOCONSOLE,
		CONFIRM
	}

	private Message usageMessage;
	private boolean allowConsole = true;
	private Permission permission;
	private String genericCommand;
	private TabCompleter tabCompleter;
	private boolean needConfirm = false;
	private Object executorVariable;

	Command(Permission permission, CommandFlag... flags) {
		this.permission = permission;
		setFlags(flags);
	}

	Command(Permission permission, boolean allowConsole, String genericCommand) {
		this.permission = permission;
		this.allowConsole = allowConsole;
		this.genericCommand = genericCommand;
	}

	Command(Permission permission, boolean allowConsole, String genericCommand, TabCompleter tabCompleter) {
		this.permission = permission;
		this.allowConsole = allowConsole;
		this.genericCommand = genericCommand;
		this.tabCompleter = tabCompleter;
	}

	Command(Permission permission, boolean allowConsole, Message usageMessage) {
		this.permission = permission;
		this.allowConsole = allowConsole;
		this.usageMessage = usageMessage;
	}

	Command(Permission permission, boolean allowConsole, Message usageMessage, boolean needConfirm) {
		this.permission = permission;
		this.allowConsole = allowConsole;
		this.usageMessage = usageMessage;
		this.needConfirm = needConfirm;
	}

	Command(Permission permission, String genericCommand, Message usageMessage, CommandFlag... flags) {
		this.permission = permission;
		this.genericCommand = genericCommand;
		this.usageMessage = usageMessage;
		
		setFlags(flags);
	}

	private void setFlags(CommandFlag[] flags) {
		for(CommandFlag flag : flags) {
			if(flag == CommandFlag.NOCONSOLE) {
				allowConsole = false;
			}
			else if(flag == CommandFlag.CONFIRM) {
				needConfirm = true;
			}
		}
	}

	public boolean allowConsole() {
		return allowConsole;
	}

	public String getPermission() {
		return permission.getPath();
	}

	public boolean hasPermission(CommandSender sender) {
		return permission.has(sender) || sender.isOp();
	}

	public boolean allowedSender(CommandSender sender) {
		return sender instanceof Player || allowConsole;
	}

	public Message getUsageMessage() {
		return usageMessage;
	}

	private Executor getExecutor() {
		return NovaGuilds.getInstance().getCommandManager().getExecutor(this);
	}

	public void execute(CommandSender sender, String[] args) {
		NovaGuilds.getInstance().getCommandManager().execute(this, sender, args);
	}

	public boolean hasGenericCommand() {
		return genericCommand != null;
	}

	public boolean hasTabCompleter() {
		return tabCompleter != null;
	}

	public String getGenericCommand() {
		return genericCommand;
	}

	public TabCompleter getTabCompleter() {
		return tabCompleter;
	}

	public boolean isNeedConfirm() {
		return needConfirm;
	}

	public Command executorVariable(Object executorVariable) {
		this.executorVariable = executorVariable;
		return this;
	}

	public Object getExecutorVariable() {
		return executorVariable;
	}
}
