package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandGuildPvpToggle implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildPvpToggle(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.pvptoggle")) {
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		if(!(sender instanceof Player)) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.cmdfromconsole");
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notinguild");
			return true;
		}

		if(!nPlayer.isLeader()) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.notleader");
			return true;
		}

		HashMap<String,String> vars = new HashMap<>();
		vars.put("FPVP",nPlayer.getBypass() ? plugin.getMessageManager().getMessagesString("basic.on") : plugin.getMessageManager().getMessagesString("basic.off"));
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.fpvptoggled",vars);

		return true;
	}
}
