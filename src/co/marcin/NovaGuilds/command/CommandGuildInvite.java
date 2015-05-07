package co.marcin.NovaGuilds.command;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;

public class CommandGuildInvite implements CommandExecutor {
	private final NovaGuilds plugin;
	
	public CommandGuildInvite(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.invite")) {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(args.length == 1) {
			String playername = args[0];
			NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);

			if(nPlayer.isLeader()) { //only leaders can invite
				if(nPlayer.hasGuild()) { //if sender has guild
					if(plugin.getPlayerManager().exists(playername)) { //player exists
						NovaPlayer inPlayer = plugin.getPlayerManager().getPlayerByName(playername);

						if(!inPlayer.hasGuild()) { //if player being invited has no guild
							NovaGuild guild = nPlayer.getGuild();
							if(!inPlayer.isInvitedTo(guild)) { //if he's not invited
								plugin.getPlayerManager().addInvitation(inPlayer, guild);
								plugin.getPlayerManager().updatePlayer(inPlayer);
								plugin.sendMessagesMsg(sender, "chat.player.invited");

								if(inPlayer.getPlayer().isOnline()) {
									HashMap<String, String> vars = new HashMap<>();
									vars.put("GUILDNAME", guild.getName());
									plugin.sendMessagesMsg(inPlayer.getPlayer(), "chat.player.uvebeeninvited", vars);
								}
							} else {
								//TODO: Uninvite
								plugin.sendMessagesMsg(sender, "chat.player.alreadyinvited");
							}
						} else {
							plugin.sendMessagesMsg(sender, "chat.player.hasguild");
						}
					} else {
						plugin.sendMessagesMsg(sender, "chat.player.notexists");
					}
				} else {
					plugin.sendMessagesMsg(sender, "chat.guild.notinguild");
				}
				return true;
			}
		}
		else {
			plugin.sendUsageMessage(sender, "guild.invite");
		}
		return true;
	}
}