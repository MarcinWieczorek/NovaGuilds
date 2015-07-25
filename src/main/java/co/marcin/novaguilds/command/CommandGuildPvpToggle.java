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
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}

		HashMap<String,String> vars = new HashMap<>();
		nPlayer.getGuild().setFriendlyPvp(!nPlayer.getGuild().getFriendlyPvp());
		vars.put("FPVP", Message.getOnOff(nPlayer.getGuild().getFriendlyPvp()));
		Message.CHAT_GUILD_FPVPTOGGLED.vars(vars).send(sender);

		return true;
	}
}
