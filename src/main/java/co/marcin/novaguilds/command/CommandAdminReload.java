package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandAdminReload implements CommandExecutor {
	private final NovaGuilds plugin;
	 
	public CommandAdminReload(NovaGuilds plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.reload")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}
		
		Message.CHAT_RELOAD_RELOADING.send(sender);

		//Remove holograms
		if(plugin.getConfigManager().useHolographicDisplays()) {
			for(Hologram h : HologramsAPI.getHolograms(plugin)) {
				h.delete();
			}
		}

		//plugin's vars from config
		plugin.getConfigManager().reload();
		Message.CHAT_RELOAD_CONFIG.send(sender);

		//MySQL
		//TODO reload mysql
		Message.CHAT_RELOAD_MYSQL.send(sender);

		//messages
		plugin.getMessageManager().loadMessages();
		Message.CHAT_RELOAD_MESSAGES.send(sender);

		//regions
		plugin.getRegionManager().loadRegions();
		Message.CHAT_RELOAD_REGIONS.send(sender);

		//guilds
		plugin.getGuildManager().loadGuilds();
		Message.CHAT_RELOAD_GUILDS.send(sender);

		//players
		plugin.getPlayerManager().loadPlayers();
		Message.CHAT_RELOAD_PLAYERS.send(sender);

		//groups
		plugin.getGroupManager().loadGroups();
		Message.CHAT_RELOAD_GROUPS.send(sender);

		LoggerUtils.info("Post checks running");
		plugin.getGuildManager().postCheckGuilds();
		plugin.getRegionManager().postCheckRegions();

		//all done
		Message.CHAT_RELOAD_RELOADED.send(sender);

		return true;
	}
}