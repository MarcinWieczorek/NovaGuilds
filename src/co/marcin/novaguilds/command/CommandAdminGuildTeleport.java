package co.marcin.novaguilds.command;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;

public class CommandAdminGuildTeleport implements CommandExecutor {
	private final NovaGuilds plugin;
	private final NovaGuild guild;
	
	public CommandAdminGuildTeleport(NovaGuilds pl, NovaGuild guild) {
		plugin = pl;
		this.guild = guild;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//args:
		// 0 - other player
		
		if(!(sender instanceof Player)) {
			plugin.info("You cannot tp to a guild from the console!");
			return true;
		}
		
		if(sender.hasPermission("novaguilds.admin.guild.tp")) {
			if(guild != null) {
				Location home = guild.getSpawnPoint();

				if(home != null) {
					Player player;

					HashMap<String,String> vars = new HashMap<>();
					vars.put("GUILDNAME",guild.getName());

					if(args.length==1) {
						String playername = args[0];

						if(plugin.getPlayerManager().exists(playername)) {
							player = plugin.getServer().getPlayer(playername);
							if(player == null) {
								plugin.sendMessagesMsg(sender,"chat.player.notonline");
								return true;
							}
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.player.notexists");
							return true;
						}
					}
					else {
						player = plugin.senderToPlayer(sender);
					}

					if(player != null) {
						player.teleport(home);
						vars.put("PLAYERNAME",player.getName());
						plugin.sendMessagesMsg(sender, "chat.admin.guild.teleportedother", vars);
					}
					else {
						plugin.sendMessagesMsg(sender, "chat.admin.guild.teleported", vars);
					}
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
			}
		}
		else {
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
		}
		return true;
	}
}
