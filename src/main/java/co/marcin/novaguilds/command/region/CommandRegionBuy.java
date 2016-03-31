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

package co.marcin.novaguilds.command.region;


import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandRegionBuy extends AbstractCommandExecutor implements CommandExecutor {
	private static final Command command = Command.REGION_BUY;

	public CommandRegionBuy() {
		super(command);
	}
	
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		command.execute(sender, args);
		return true;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.hasPermission(nPlayer.getRegionMode() == RegionMode.RESIZE ? GuildPermission.REGION_RESIZE : GuildPermission.REGION_CREATE)) {
			Message.CHAT_GUILD_NOGUILDPERM.send(sender);
			return;
		}

		if(guild.hasRegion() && nPlayer.getRegionMode() != RegionMode.RESIZE) {
			Message.CHAT_GUILD_HASREGIONALREADY.send(sender);
			return;
		}

		RegionSelection activeSelection = nPlayer.getActiveSelection();

		if(activeSelection == null) {
			Message.CHAT_REGION_VALIDATION_NOTSELECTED.send(sender);
			return;
		}

		Location selectedLocation0 = activeSelection.getCorner(0);
		Location selectedLocation1 = activeSelection.getCorner(1);

		RegionValidity selectionValidity = plugin.getRegionManager().checkRegionSelect(selectedLocation0, selectedLocation1);

		if(nPlayer.getRegionMode() == RegionMode.RESIZE && selectionValidity == RegionValidity.OVERLAPS) {
			List<NovaRegion> regionsOverlapped = plugin.getRegionManager().getRegionsInsideArea(selectedLocation0, selectedLocation1);
			if(regionsOverlapped.size() == 1 && regionsOverlapped.get(0).equals(nPlayer.getGuild().getRegion())) {
				selectionValidity = RegionValidity.VALID;
			}
		}

		if(selectionValidity == RegionValidity.TOOCLOSE) {
			List<NovaGuild> guildsTooClose = plugin.getRegionManager().getGuildsTooClose(selectedLocation0, selectedLocation1);

			if(guildsTooClose.size() == 1 && guildsTooClose.get(0).equals(nPlayer.getGuild())) {
				selectionValidity = RegionValidity.VALID;
			}
		}

		if(selectionValidity != RegionValidity.VALID) {
			Message.CHAT_REGION_VALIDATION_NOTVALID.send(sender);
			return;
		}

		int regionSize = RegionUtils.checkRegionSize(selectedLocation0, selectedLocation1);

		//region's price
		double price;
		NovaGroup group = GroupManager.getGroup(sender);
		double ppb = group.getRegionPricePerBlock();

		if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
			price = ppb * (regionSize - guild.getRegion().getSurface());
		}
		else {
			price = ppb * regionSize + group.getRegionCreateMoney();
		}

		if(price > 0 && guild.getMoney() < price) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
			NovaRegion region = guild.getRegion();

			region.setCorner(0, activeSelection.getCorner(0));
			region.setCorner(1, activeSelection.getCorner(1));

			region.getCorner(0).setY(0);
			region.getCorner(1).setY(0);
			Message.CHAT_REGION_RESIZE_SUCCESS.send(sender);
		}
		else {
			NovaRegion region = new NovaRegionImpl(nPlayer.getActiveSelection());

			nPlayer.getGuild().setRegion(region);
			Message.CHAT_REGION_CREATED.send(sender);
		}

		if(price > 0) {
			guild.takeMoney(price);
		}

		nPlayer.cancelToolProgress();
	}
}
