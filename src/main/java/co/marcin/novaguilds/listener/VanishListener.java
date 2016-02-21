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

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.AbstractListener;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import org.bukkit.event.EventHandler;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

public class VanishListener extends AbstractListener {
	@EventHandler
	public void onVanishStatusChange(VanishStatusChangeEvent event) {
		if(event.isVanishing()) {
			NovaPlayer nPlayer = PlayerManager.getPlayer(event.getPlayer());

			if(nPlayer.getAtRegion() != null) {
				plugin.getRegionManager().playerExitedRegion(event.getPlayer());
			}
		}
		else if(RegionManager.get(event.getPlayer()) != null) {
			plugin.getRegionManager().playerEnteredRegion(event.getPlayer(), event.getPlayer().getLocation());
		}
	}
}
