package co.marcin.novaguilds.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Commands {
	GUILD_ACCESS("novaguilds.guild.access",false),
	GUILD_ABANDON("novaguilds.guild.abandon", false),
	GUILD_ALLY("novaguilds.guild.ally", false),
	GUILD_BANK_PAY("novaguilds.guild.bank.pay", false),
	GUILD_BANK_WITHDRAW("novaguilds.guild.bank.withdraw", false),
	GUILD_BUYLIFE("novaguilds.guild.buylife",false),
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
