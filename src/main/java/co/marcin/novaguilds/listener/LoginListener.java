package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginListener implements Listener {
	private final NovaGuilds plugin;
	
	public LoginListener(NovaGuilds plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		//adding player
		plugin.getPlayerManager().addIfNotExists(player);

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		//scoreboard
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

		nPlayer.setPlayer(player);
		plugin.getPlayerManager().updateUUID(nPlayer);

		if(plugin.updateAvailable && player.hasPermission("novaguilds.admin.updateavailable")) {
			plugin.getMessageManager().sendMessagesMsg(player,"chat.update");
		}

		if(plugin.getConfigManager().useChatDisplayNameTags()) {
			player.setDisplayName(plugin.tagUtils.getTag(player)+player.getDisplayName());
		}

		//adding to raid TODO: not tested
		//TODO should be done in playerEnteredRegion
//		if(nPlayer.hasGuild()) {
//			//Update his guild's inactive time
//			nPlayer.getGuild().updateInactiveTime();
//
//			NovaRegion rgAtLocation = plugin.getRegionManager().getRegion(player.getLocation());
//
//			if(rgAtLocation != null) {
//				NovaGuild guildAtRegion = rgAtLocation.getGuild();
//
//				List<NovaRaid> raidsTakingPart = plugin.getGuildManager().getRaidsTakingPart(nPlayer.getGuild());
//
//				for(NovaRaid raid : raidsTakingPart) {
//					if(raid.getGuildDefender().equals(guildAtRegion)) {
//						guildAtRegion.getRaid().addPlayerOccupying(nPlayer);
//					}
//				}
//			}
//		}

		if(plugin.getRegionManager().getRegion(player.getLocation()) != null) {
			plugin.getRegionManager().playerEnteredRegion(player,player.getLocation());
		}
		
		//TabAPI
		plugin.tagUtils.updatePrefix(player);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(event.getPlayer());
		nPlayer.setPlayer(null);

		//remove player from raid
		if(nPlayer.isPartRaid()) {
			for(NovaRaid raid : plugin.getGuildManager().getRaidsTakingPart(nPlayer.getGuild())) {
				raid.removePlayerOccupying(nPlayer);
			}
		}
	}
}
