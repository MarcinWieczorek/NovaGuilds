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
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandRegionBuy extends AbstractCommandExecutor {
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

		RegionSelection activeSelection = nPlayer.getActiveSelection();

		if(activeSelection == null || !activeSelection.hasBothSelections()) {
			Message.CHAT_REGION_VALIDATION_NOTSELECTED.send(sender);
			return;
		}

		Location selectedLocation0 = activeSelection.getCorner(0);
		Location selectedLocation1 = activeSelection.getCorner(1);

		RegionValidity selectionValidity = plugin.getRegionManager().checkRegionSelect(activeSelection);

		if(selectionValidity != RegionValidity.VALID) {
			Message.CHAT_REGION_VALIDATION_NOTVALID.send(sender);
			return;
		}

		int regionSize = RegionUtils.checkRegionSize(selectedLocation0, selectedLocation1);

		if(guild.getRegions().size() >= Config.REGION_MAXAMOUNT.getInt() && nPlayer.getRegionMode() != RegionMode.RESIZE) {
			Message.CHAT_REGION_MAXAMOUNT.clone().setVar(VarKey.AMOUNT, Config.REGION_MAXAMOUNT.getInt()).send(nPlayer);
			return;
		}

		//region's price
		double price;
		NovaGroup group = GroupManager.getGroup(sender);
		double ppb = group.getDouble(NovaGroup.Key.REGION_PRICEPERBLOCK);

		if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
			price = ppb * (regionSize - activeSelection.getSelectedRegion().getSurface());
		}
		else {
			price = ppb * regionSize + group.getDouble(NovaGroup.Key.REGION_CREATE_MONEY);
		}

		if(price > 0 && guild.getMoney() < price) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
		}

		if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
			NovaRegion region = activeSelection.getSelectedRegion();

			region.setCorner(0, activeSelection.getCorner(0));
			region.setCorner(1, activeSelection.getCorner(1));

			Message.CHAT_REGION_RESIZE_SUCCESS.send(sender);
		}
		else {
			NovaRegion region = new NovaRegionImpl(UUID.randomUUID(), nPlayer.getActiveSelection());

			nPlayer.getGuild().addRegion(region);
			Message.CHAT_REGION_CREATED.send(sender);
		}

		if(price > 0) {
			guild.takeMoney(price);
		}

		nPlayer.cancelToolProgress();
		plugin.getRegionManager().checkAtRegionChange();
	}
}
