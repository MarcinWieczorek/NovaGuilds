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

		if(args.length == 1) {
			String playername = args[0];
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

			if(nPlayer.isLeader()) { //only leaders can invite
				if(nPlayer.hasGuild()) { //if sender has guild
					if(plugin.getPlayerManager().exists(playername)) { //player exists
						NovaPlayer inPlayer = plugin.getPlayerManager().getPlayer(playername);

						if(!inPlayer.hasGuild()) { //if player being invited has no guild
							NovaGuild guild = nPlayer.getGuild();
							HashMap<String, String> vars = new HashMap<>();
							vars.put("GUILDNAME", guild.getName());
							vars.put("PLAYERNAME", inPlayer.getName());

							if(!inPlayer.isInvitedTo(guild)) { //if he's not invited
								plugin.getPlayerManager().addInvitation(inPlayer, guild);
								plugin.getMessageManager().sendMessagesMsg(sender, "chat.player.invited");

								if(inPlayer.isOnline()) {
									plugin.getMessageManager().sendMessagesMsg(inPlayer.getPlayer(), "chat.player.uvebeeninvited", vars);
								}
							}
							else {
								inPlayer.deleteInvitation(guild);
								plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.invitecanceled", vars);

								if(inPlayer.isOnline()) {
									plugin.getMessageManager().sendMessagesMsg(inPlayer.getPlayer(), "chat.guild.invitecancelednotify", vars);
								}
							}
						}
						else {
							plugin.getMessageManager().sendMessagesMsg(sender, "chat.player.hasguild");
						}
					}
					else {
						plugin.getMessageManager().sendMessagesMsg(sender, "chat.player.notexists");
					}
				}
				else {
					plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notinguild");
				}
				return true;
			}
		}
		else {
			plugin.getMessageManager().sendUsageMessage(sender, "guild.invite");
		}
		return true;
	}
}