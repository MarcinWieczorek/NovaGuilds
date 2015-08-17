package co.marcin.novaguilds.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Commands {
	ADMIN_GUILD_ABANDON("novaguilds.admin.guild.abandon", true),
	ADMIN_GUILD_PURGE("novaguilds.admin.guild.purge", true),
	ADMIN_GUILD_BANK_PAY("novaguilds.admin.guild.bank.pay", true),
	ADMIN_GUILD_BANK_WITHDRAW("novaguilds.admin.guild.bank.withdraw", true),
	ADMIN_GUILD_INACTIVE("novaguilds.admin.guild.inactive.list", true),
	ADMIN_GUILD_INVITE("novaguilds.admin.guild.invite", true),
	ADMIN_GUILD_KICK("novaguilds.admin.guild.kick", true),
	ADMIN_GUILD_LIST("novaguilds.admin.guild.list", true),
	ADMIN_GUILD_SET_LEADER("novaguilds.admin.guild.set.leader", true),
	ADMIN_GUILD_SET_LIVEREGENERATIONTIME("novaguilds.admin.guild.set.liveregenerationtime", true),
	ADMIN_GUILD_SET_LIVES("novaguilds.admin.guild.set.lives", true),
	ADMIN_GUILD_SET_NAME("novaguilds.admin.guild.set.name", true),
	ADMIN_GUILD_SET_POINTS("novaguilds.admin.guild.set.points", true),
	ADMIN_GUILD_SET_TAG("novaguilds.admin.guild.set.tag", true),
	ADMIN_GUILD_SET_TIMEREST("novaguilds.admin.guild.set.timerest", true),
	ADMIN_GUILD_SET_SLOTS("novaguilds.admin.guild.set.slots", true),
	ADMIN_GUILD_TELEPORT("novaguilds.admin.guild.teleport", true),

	GUILD_ACCESS("novaguilds.guild.access",false),
	GUILD_ABANDON("novaguilds.guild.abandon", false),
	GUILD_ALLY("novaguilds.guild.ally", false),
	GUILD_BANK_PAY("novaguilds.guild.bank.pay", false),
	GUILD_BANK_WITHDRAW("novaguilds.guild.bank.withdraw", false),
	GUILD_BUYLIFE("novaguilds.guild.buylife",false),
	GUILD_BUYSLOT("novaguilds.guild.buyslot",false),
	GUILD_COMPASS("novaguilds.guild.compass",false),
	GUILD_CREATE("novaguilds.guild.create", false),
	GUILD_EFFECT("novaguilds.guild.effect", false),
	GUILD_HOME("novaguilds.guild.home", false),
	GUILD_INFO("novaguilds.guild.info", true),
	GUILD_INVITE("novaguilds.guild.invite", false),
	GUILD_JOIN("novaguilds.guild.join", false),
	GUILD_KICK("novaguilds.guild.kick", false),
	GUILD_LEADER("novaguilds.guild.leader", false),
	GUILD_LEAVE("novaguilds.guild.leave", false),
	GUILD_MENU("novaguilds.guild.menu", false),
	GUILD_PVPTOGGLE("novaguilds.guild.pvptoggle", false),
	GUILD_REQUIREDITEMS("novaguilds.guild.requireditems", false),
	GUILD_TOP("novaguilds.guild.top", true),
	GUILD_WAR("novaguilds.guild.war", false),
	GUILD_BOSS("novaguilds.guild.boss", false),
	TOOL_GET("novaguilds.tool.get",false);

	private boolean allowConsole = true;
	private String permission = "";

	Commands(String permission, boolean allowConsole) {
		this.permission = permission;
		this.allowConsole = allowConsole;
	}

	public boolean allowConsole() {
		return allowConsole;
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasPermission(CommandSender sender) {
		return sender.hasPermission(permission) || sender.isOp();
	}

	public boolean allowedSender(CommandSender sender) {
		return sender instanceof Player || allowConsole;
	}
}
