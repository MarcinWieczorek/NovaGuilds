package co.marcin.novaguilds.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Commands {
	ADMIN_ACCESS(Permission.NOVAGUILDS_ADMIN_ACCESS, true),
	ADMIN_RELOAD(Permission.NOVAGUILDS_ADMIN_RELOAD, true),
	ADMIN_SAVE(Permission.NOVAGUILDS_ADMIN_SAVE, true),

	ADMIN_GUILD_ACCESS(Permission.NOVAGUILDS_GUILD_ACCESS, true),
	ADMIN_GUILD_ABANDON(Permission.NOVAGUILDS_GUILD_ABANDON, true),
	ADMIN_GUILD_PURGE(Permission.NOVAGUILDS_ADMIN_GUILD_PURGE, true),
	ADMIN_GUILD_BANK_PAY(Permission.NOVAGUILDS_ADMIN_GUILD_BANK_PAY, true),
	ADMIN_GUILD_BANK_WITHDRAW(Permission.NOVAGUILDS_ADMIN_GUILD_BANK_WITHDRAW, true),
	ADMIN_GUILD_INACTIVE(Permission.NOVAGUILDS_ADMIN_GUILD_INACTIVE_LIST, true),
	ADMIN_GUILD_INVITE(Permission.NOVAGUILDS_ADMIN_GUILD_INVITE, true),
	ADMIN_GUILD_KICK(Permission.NOVAGUILDS_ADMIN_GUILD_KICK, true),
	ADMIN_GUILD_LIST(Permission.NOVAGUILDS_ADMIN_GUILD_LIST, true),
	ADMIN_GUILD_SET_LEADER(Permission.NOVAGUILDS_ADMIN_GUILD_SET_LEADER, true),
	ADMIN_GUILD_SET_LIVEREGENERATIONTIME(Permission.NOVAGUILDS_ADMIN_GUILD_SET_LIVEREGENERATIONTIME, true),
	ADMIN_GUILD_SET_LIVES(Permission.NOVAGUILDS_ADMIN_GUILD_SET_LIVES, true),
	ADMIN_GUILD_SET_NAME(Permission.NOVAGUILDS_ADMIN_GUILD_SET_NAME, true),
	ADMIN_GUILD_SET_POINTS(Permission.NOVAGUILDS_ADMIN_GUILD_SET_POINTS, true),
	ADMIN_GUILD_SET_TAG(Permission.NOVAGUILDS_ADMIN_GUILD_SET_TAG, true),
	ADMIN_GUILD_SET_TIMEREST(Permission.NOVAGUILDS_ADMIN_GUILD_SET_TIMEREST, true),
	ADMIN_GUILD_SET_SLOTS(Permission.NOVAGUILDS_ADMIN_GUILD_SET_SLOTS, true),
	ADMIN_GUILD_TELEPORT(Permission.NOVAGUILDS_ADMIN_GUILD_TELEPORT, true),

	ADMIN_REGION_ACCESS(Permission.NOVAGUILDS_ADMIN_REGION_ACCESS, true),
	ADMIN_REGION_BYPASS(Permission.NOVAGUILDS_ADMIN_REGION_BYPASS_SELF, true),
	ADMIN_REGION_DELETE(Permission.NOVAGUILDS_ADMIN_REGION_DELETE, true),
	ADMIN_REGION_LIST(Permission.NOVAGUILDS_ADMIN_REGION_LIST, true),
	ADMIN_REGION_TELEPORT(Permission.NOVAGUILDS_ADMIN_REGION_TELEPORT, true),

	ADMIN_HOLOGRAM_ACCESS(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ACCESS, true),
	ADMIN_HOLOGRAM_LIST(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_LIST, true),
	ADMIN_HOLOGRAM_TELEPORT(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT, false),
	ADMIN_HOLOGRAM_DELETE(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_DELETE, true),
	ADMIN_HOLOGRAM_ADD(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADD, false),
	ADMIN_HOLOGRAM_ADDTOP(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_ADDTOP, false),
	ADMIN_HOLOGRAM_TELEPORT_HERE(Permission.NOVAGUILDS_ADMIN_HOLOGRAM_TELEPORT_HERE, false),

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
	GUILD_OPENINVITATION(Permission.NOVAGUILDS_GUILD_OPENINVITATION, false),

	TOOL_GET("novaguilds.tool.get",false);

	private Message usageMessage;
	private boolean allowConsole = true;
	private String permissionPath = "";
	private Permission permission;

	@Deprecated
	Commands(String permission, boolean allowConsole) {
		this.permissionPath = permission;
		this.permission = Permission.fromPath(permissionPath);
		this.allowConsole = allowConsole;
	}

	Commands(Permission permission, boolean allowConsole) {
		this.permission = permission;
		this.permissionPath = permission.getPath();
		this.allowConsole = allowConsole;
	}

	Commands(Permission permission, boolean allowConsole, Message usageMessage) {
		this.permission = permission;
		this.permissionPath = permission.getPath();
		this.allowConsole = allowConsole;
		this.usageMessage = usageMessage;
	}

	public boolean allowConsole() {
		return allowConsole;
	}

	public String getPermission() {
		return permissionPath;
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
}
