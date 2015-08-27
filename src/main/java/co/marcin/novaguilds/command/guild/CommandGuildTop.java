package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashMap;

public class CommandGuildTop implements Executor {
	private final Commands command;

	public CommandGuildTop(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		Collection<NovaGuild> guilds = plugin.getGuildManager().getGuilds();

		if(guilds.isEmpty()) {
			Message.CHAT_GUILD_NOGUILDS.send(sender);
			return;
		}

		int limit = Integer.parseInt(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_TOPROWS.get()); //TODO move to config
		int i=1;

		Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.send(sender);

		HashMap<String, String> vars = new HashMap<>();
		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			vars.clear();
			vars.put("GUILDNAME", guild.getName());
			vars.put("N", String.valueOf(i));
			vars.put("POINTS", String.valueOf(guild.getPoints()));
			Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.title(false).vars(vars).send(sender);
			i++;
		}
	}
}
