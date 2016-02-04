/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.util.TagUtils;
import co.marcin.novaguilds.util.VersionUtils;
import co.marcin.novaguilds.util.reflect.PacketExtension;
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
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		//adding player
		plugin.getPlayerManager().addIfNotExists(player);

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

		nPlayer.setPlayer(player);
		plugin.getPlayerManager().updateUUID(nPlayer);

		//Send version message if there's an update
		if(VersionUtils.updateAvailable && Permission.NOVAGUILDS_ADMIN_UPDATEAVAILABLE.has(player)) {
			Message.CHAT_UPDATE.send(player);
		}

		//Show bank hologram
		if(nPlayer.hasGuild()) {
			nPlayer.getGuild().showVaultHologram(player);
		}

		if(plugin.getRegionManager().getRegion(player.getLocation()) != null) {
			plugin.getRegionManager().playerEnteredRegion(player, player.getLocation());
		}

		//TabAPI
		if(Config.TAGAPI_ENABLED.getBoolean()) {
			if(player.getScoreboard() == null) {
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}

			TagUtils.updatePrefix(player);
		}

		//Tab
		if(Config.TABLIST_ENABLED.getBoolean()) {
		}

		//PacketExtension
		if(Config.PACKETS_ENABLED.getBoolean()) {
			PacketExtension.registerPlayer(player);
		}
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
