package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
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
		nPlayer.getGuild().setFriendlyPvp(!nPlayer.getGuild().getFriendlyPvp());
		vars.put("FPVP",nPlayer.getGuild().getFriendlyPvp() ? plugin.getMessageManager().getMessagesString("chat.basic.on") : plugin.getMessageManager().getMessagesString("chat.basic.off"));
		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.fpvptoggled",vars);

		return true;
	}
}
