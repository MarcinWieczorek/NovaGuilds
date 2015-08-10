package co.marcin.novaguilds.interfaces;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.command.CommandSender;

public interface Executor {
	NovaGuilds plugin = NovaGuilds.getInstance();

	void execute(CommandSender sender, String[] args);
}
