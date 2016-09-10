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

import co.marcin.novaguilds.api.basic.NovaGroup;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractListener;
import co.marcin.novaguilds.impl.util.RegionSelectionImpl;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.util.ItemStackUtils;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ToolListener extends AbstractListener {
	/**
	 * Handles all tool actions
	 *
	 * @param event player interact event
	 */
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Map<VarKey, String> vars = new HashMap<>();

		if(!ItemStackUtils.isSimilar(event.getItem(), Config.REGION_TOOL.getItemStack())) {
			return;
		}

		event.setCancelled(true);
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		RegionSelection activeSelection = nPlayer.getActiveSelection();
		Location pointedLocation = player.getTargetBlock((HashSet<Byte>) null, 200).getLocation();
		Action action = event.getAction();
		RegionSelection.Type selectionType = RegionSelection.Type.NONE;
		pointedLocation.setWorld(player.getWorld());
		NovaRegion region = RegionManager.get(pointedLocation);
		Location selectedLocation[] = new Location[2];

		if(activeSelection != null && !(activeSelection.getType() == RegionSelection.Type.HIGHLIGHT || activeSelection.getType() == RegionSelection.Type.HIGHLIGHT_RESIZE)) {
			selectedLocation[0] = activeSelection.getCorner(0);
			selectedLocation[1] = activeSelection.getCorner(1);
		}

		RegionValidity regionValidity = RegionValidity.VALID;

		//Change RegionMode
		if((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
			if(!Permission.NOVAGUILDS_TOOL_CHECK.has(player) || !Permission.NOVAGUILDS_REGION_CREATE.has(player)) {
				return;
			}

			nPlayer.setRegionMode(nPlayer.getRegionMode() == RegionMode.CHECK ? RegionMode.SELECT : RegionMode.CHECK);
			nPlayer.cancelToolProgress();
			selectionType = RegionSelection.Type.NONE;

			//highlight corners for resizing
			if(nPlayer.getRegionMode() == RegionMode.SELECT && nPlayer.hasPermission(GuildPermission.REGION_RESIZE) && nPlayer.getGuild().hasRegion()) {
				selectionType = RegionSelection.Type.HIGHLIGHT_RESIZE;
				selectedLocation[0] = nPlayer.getGuild().getRegion().getCorner(0);
				selectedLocation[1] = nPlayer.getGuild().getRegion().getCorner(1);
			}

			Message mode = nPlayer.getRegionMode() == RegionMode.SELECT ? Message.CHAT_REGION_TOOL_MODES_SELECT : Message.CHAT_REGION_TOOL_MODES_CHECK;

			vars.put(VarKey.MODE, mode.get());
			Message.CHAT_REGION_TOOL_TOGGLEDMODE.vars(vars).send(nPlayer);
		}
		else if(nPlayer.getRegionMode() == RegionMode.CHECK && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) { //CHECK MODE
			if(!Permission.NOVAGUILDS_TOOL_CHECK.has(player)) { //permissions check
				return;
			}

			//Reset region highlighted already
			if(activeSelection != null && (activeSelection.getType() == RegionSelection.Type.HIGHLIGHT || activeSelection.getType() == RegionSelection.Type.HIGHLIGHT_RESIZE)) {
				activeSelection.reset();
			}

			if(region != null) {
				selectionType = RegionSelection.Type.HIGHLIGHT;
				selectedLocation[0] = region.getCorner(0);
				selectedLocation[1] = region.getCorner(1);

				vars.put(VarKey.GUILDNAME, region.getGuild().getName());
				Message.CHAT_REGION_BELONGSTO.vars(vars).send(nPlayer);
			}
			else {
				Message.CHAT_REGION_NOREGIONHERE.send(nPlayer);
				selectionType = RegionSelection.Type.NONE;
			}
		}
		else if(event.getAction() != Action.PHYSICAL && nPlayer.getRegionMode() != RegionMode.CHECK) { //CREATE MODE
			Location pointedCornerLocation = pointedLocation.clone();
			pointedCornerLocation.setY(0);
			double[] cornerDistance = new double[]{
					region == null ? 1 : pointedCornerLocation.distance(region.getCorner(0).getBlock().getLocation()),
					region == null ? 1 : pointedCornerLocation.distance(region.getCorner(1).getBlock().getLocation())
			};

			if(region != null && nPlayer.getRegionMode() != RegionMode.RESIZE && (cornerDistance[0] < 1 || cornerDistance[1] < 1)) { //resizing
				selectionType = RegionSelection.Type.RESIZE;

				if(activeSelection != null && activeSelection.getType() == RegionSelection.Type.HIGHLIGHT_RESIZE) {
					selectedLocation[0] = activeSelection.getCorner(0);
					selectedLocation[1] = activeSelection.getCorner(1);
				}

				if(!Permission.NOVAGUILDS_REGION_RESIZE.has(player)) {
					return;
				}

				if(!region.getGuild().isMember(nPlayer) || !nPlayer.hasPermission(GuildPermission.REGION_RESIZE)) {
					return;
				}

				nPlayer.setRegionMode(RegionMode.RESIZE);
				Message.CHAT_REGION_RESIZE_START.send(nPlayer);
			}
			else {
				selectionType = nPlayer.getRegionMode() == RegionMode.RESIZE ? RegionSelection.Type.RESIZE : RegionSelection.Type.CREATE;

				if(!Permission.NOVAGUILDS_REGION_CREATE.has(player)) {
					return;
				}

				int selectCorner = -1;

				//Corner 0
				if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
					selectCorner = 0;
				}

				//Corner 1
				if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
					selectCorner = 1;
				}

				if(selectCorner != -1) {
					int oppositeCorner = selectCorner == 0 ? 1 : 0;

					if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
						selectedLocation[selectCorner] = pointedLocation;
					}
					else {
						if(selectedLocation[oppositeCorner] != null && !pointedLocation.getWorld().equals(selectedLocation[oppositeCorner].getWorld())) {
							return;
						}

						selectedLocation[selectCorner] = pointedLocation;
					}
				}

				if(selectedLocation[0] != null && selectedLocation[1] != null) {
					regionValidity = plugin.getRegionManager().checkRegionSelect(selectedLocation[0], selectedLocation[1]);

					//When resizing if overlaps player's region
					if(nPlayer.getRegionMode() == RegionMode.RESIZE && regionValidity == RegionValidity.OVERLAPS) {
						List<NovaRegion> regionsOverlapped = plugin.getRegionManager().getRegionsInsideArea(selectedLocation[0], selectedLocation[1]);

						if(regionsOverlapped.size() == 1 && regionsOverlapped.get(0).equals(nPlayer.getGuild().getRegion())) {
							regionValidity = RegionValidity.VALID;
						}
					}

					if(regionValidity == RegionValidity.TOOCLOSE) {
						List<NovaGuild> guildsTooClose = plugin.getRegionManager().getGuildsTooClose(selectedLocation[0], selectedLocation[1]);

						if(guildsTooClose.size() == 1 && guildsTooClose.get(0).equals(nPlayer.getGuild())) {
							regionValidity = RegionValidity.VALID;
						}
					}

					switch(regionValidity) {
						case VALID:  //valid
							if(nPlayer.hasGuild()) {
								int regionSize = RegionUtils.checkRegionSize(selectedLocation[0], selectedLocation[1]);
								NovaGroup group = GroupManager.getGroup(nPlayer.getPlayer());
								double price;
								double ppb = group.getDouble(NovaGroup.Key.REGION_PRICEPERBLOCK);

								if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
									price = ppb * (regionSize - nPlayer.getGuild().getRegion().getSurface());
								}
								else {
									price = ppb * regionSize + group.getDouble(NovaGroup.Key.REGION_CREATE_MONEY);
								}

								vars.put(VarKey.SIZE, String.valueOf(regionSize));
								vars.put(VarKey.PRICE, String.valueOf(price));

								Message.CHAT_REGION_SIZE.vars(vars).send(nPlayer);

								if(price > 0) {
									Message.CHAT_REGION_PRICE.vars(vars).send(nPlayer);

									if(!nPlayer.getGuild().hasMoney(price)) {
										vars.put(VarKey.NEEDMORE, String.valueOf(price - nPlayer.getGuild().getMoney()));
										Message.CHAT_REGION_CNOTAFFORD.vars(vars).send(nPlayer);
										break;
									}
								}

								Message.CHAT_REGION_VALIDATION_VALID.send(nPlayer);
							}
							else {
								Message.CHAT_REGION_MUSTVEGUILD.send(nPlayer);
							}
							break;
						case TOOSMALL:
							vars.put(VarKey.MINSIZE, Config.REGION_MINSIZE.getString());
							Message.CHAT_REGION_VALIDATION_TOOSMALL.vars(vars).send(nPlayer);
							break;
						case TOOBIG:
							vars.put(VarKey.MAXSIZE, Config.REGION_MAXSIZE.getString());
							Message.CHAT_REGION_VALIDATION_TOOBIG.vars(vars).send(nPlayer);
							break;
						case OVERLAPS:
							Message.CHAT_REGION_VALIDATION_OVERLAPS.send(nPlayer);
							break;
						case TOOCLOSE:
							Message.CHAT_REGION_VALIDATION_TOOCLOSE.send(nPlayer);
							break;
					}
				}
			}
		}

		if(selectionType != RegionSelection.Type.NONE) {
			RegionSelection selection;

			if(nPlayer.getActiveSelection() != null && nPlayer.getActiveSelection().getType() == selectionType) {
				selection = nPlayer.getActiveSelection();
			}
			else {
				if(nPlayer.getActiveSelection() != null) {
					nPlayer.getActiveSelection().reset();
				}

				selection = new RegionSelectionImpl(nPlayer, selectionType);
			}

			selection.setCorner(0, selectedLocation[0]);
			selection.setCorner(1, selectedLocation[1]);
			selection.setValidity(regionValidity);
			selection.send();
		}
	}
}
