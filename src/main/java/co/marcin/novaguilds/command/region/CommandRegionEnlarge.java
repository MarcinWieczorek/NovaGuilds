/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.command.region;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.basic.NovaGroupImpl;
import co.marcin.novaguilds.impl.util.RegionSelectionImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class CommandRegionEnlarge extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(nPlayer.getActiveSelection() != null) {
			nPlayer.getActiveSelection().reset();
		}

		NovaRegion region;
		if(args.length == 0) {
			region = nPlayer.getAtRegion();
		}
		else {
			String indexString = args[0];

			if(!NumberUtils.isNumeric(indexString)) {
				Message.CHAT_ENTERINTEGER.send(sender);
				return;
			}

			int index = Integer.parseInt(indexString);

			region = nPlayer.getGuild().getRegion(index);
		}

		RegionSelection selection = new RegionSelectionImpl(nPlayer, RegionSelection.Type.ENLARGE, region);
		Location corner0 = selection.getCorner(0);
		Location corner1 = selection.getCorner(1);
		int diff = GroupManager.getGroup(sender).get(NovaGroupImpl.Key.REGION_ENLARGE_BLOCKS);

		if(corner0.getBlockX() < corner1.getBlockX()
				&& corner0.getBlockZ() < corner1.getBlockZ()) {
			corner0.add(-diff, 0, -diff);
			corner1.subtract(-diff, 0, -diff);
		}

		if(corner0.getBlockX() < corner1.getBlockX()
				&& corner0.getBlockZ() > corner1.getBlockZ()) {
			corner0.add(-diff, 0, diff);
			corner1.subtract(-diff, 0, diff);
		}

		if(corner0.getBlockX() > corner1.getBlockX()
				&& corner0.getBlockZ() > corner1.getBlockZ()) {
			corner0.add(diff, 0, diff);
			corner1.subtract(diff, 0, diff);
		}

		if(corner0.getBlockX() > corner1.getBlockX()
				&& corner0.getBlockZ() < corner1.getBlockZ()) {
			corner0.add(diff, 0, -diff);
			corner1.subtract(diff, 0, -diff);
		}

		Command.REGION_BUY.execute(sender, new String[0]);
	}
}
