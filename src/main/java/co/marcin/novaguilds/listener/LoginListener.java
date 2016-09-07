/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.bossbar.BossBarUtils;
import co.marcin.novaguilds.impl.versionimpl.v1_8.TabListNorthTabImpl;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import co.marcin.novaguilds.util.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginListener extends AbstractListener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		//adding player
		plugin.getPlayerManager().addIfNotExists(player);

		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		nPlayer.setPlayer(player);

		//Send version message if there's an update
		if(VersionUtils.isUpdateAvailable() && Permission.NOVAGUILDS_ADMIN_UPDATEAVAILABLE.has(player)) {
			Message.CHAT_UPDATE.send(player);
		}

		NovaRegion region = RegionManager.get(player);
		if(region != null) {
			plugin.getRegionManager().playerEnteredRegion(player, region);
		}

		if(nPlayer.hasGuild()) {
			for(Player onlinePlayer : NovaGuilds.getOnlinePlayers()) {
				NovaPlayer onlineNPlayer = PlayerManager.getPlayer(onlinePlayer);

				if(onlineNPlayer.equals(nPlayer) || !onlineNPlayer.isAtRegion() || !onlineNPlayer.getAtRegion().getGuild().equals(nPlayer.getGuild())) {
					continue;
				}

				plugin.getRegionManager().checkRaidInit(onlineNPlayer);
			}

			//Show bank hologram
			nPlayer.getGuild().showVaultHologram(player);
		}

		//TabAPI
		if(Config.TAGAPI_ENABLED.getBoolean()) {
			if(player.getScoreboard() == null) {
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}

			TagUtils.refresh();
		}

		//PacketExtension
		if(Config.PACKETS_ENABLED.getBoolean()) {
			plugin.getPacketExtension().registerPlayer(player);
		}

		//Tab
		if(Config.TABLIST_ENABLED.getBoolean()) {
			TabList tabList = null;

			switch(ConfigManager.getServerVersion()) {
				case MINECRAFT_1_8_R2:
					tabList = new TabListNorthTabImpl(nPlayer);
					break;
			}

			nPlayer.setTabList(tabList);

			TabUtils.refresh();
		}

		BossBarUtils.removeBar(player);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		NovaPlayer nPlayer = PlayerManager.getPlayer(event.getPlayer());
		nPlayer.setPlayer(null);

		//remove player from raid
		if(nPlayer.isPartRaid()) {
			for(NovaRaid raid : plugin.getGuildManager().getRaidsTakingPart(nPlayer.getGuild())) {
				raid.removePlayerOccupying(nPlayer);
			}
		}
	}
}
