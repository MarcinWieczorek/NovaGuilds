package co.marcin.novaguilds.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Commands {
	GUILD_ACCESS("novaguilds.guild.access",false),
	GUILD_LEAVE("novaguilds.guild.leave",false),
	TOOL_GET("novaguilds.tool.get",false);

	private boolean allowConsole = true;
	private String permission = "";

	Commands() {}

	Commands(String permission) {
		this.permission = permission;
	}

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
