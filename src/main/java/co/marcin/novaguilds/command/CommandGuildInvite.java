package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandGuildInvite implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildInvite(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.invite")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(args.length != 1) {
			plugin.getMessageManager().sendUsageMessage(sender, "guild.invite");
			return true;
		}

		String playername = args[0];
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notinguild");
			return true;
		}

		if(!nPlayer.isLeader()) { //only leaders can invite
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notleader");
			return true;
		}

		NovaPlayer invitePlayer = plugin.getPlayerManager().getPlayer(playername);

		if(invitePlayer == null) { //player exists
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.player.notexists");
			return true;
		}

		if(invitePlayer.hasGuild()) { //if player being invited has no guild
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.player.hasguild");
			return true;
		}

		NovaGuild guild = nPlayer.getGuild();
		HashMap<String, String> vars = new HashMap<>();
		vars.put("GUILDNAME", guild.getName());
		vars.put("PLAYERNAME", invitePlayer.getName());

		if(!invitePlayer.isInvitedTo(guild)) { //invite
			plugin.getPlayerManager().addInvitation(invitePlayer, guild);
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.player.invited");

			if(invitePlayer.isOnline()) {
				plugin.getMessageManager().sendMessagesMsg(invitePlayer.getPlayer(), "chat.player.uvebeeninvited", vars);
			}
		}
		else { //cancel invitation
			invitePlayer.deleteInvitation(guild);
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.invitecanceled", vars);

			if(invitePlayer.isOnline()) {
				plugin.getMessageManager().sendMessagesMsg(invitePlayer.getPlayer(), "chat.guild.invitecancelednotify", vars);
			}
		}
		return true;
	}
}