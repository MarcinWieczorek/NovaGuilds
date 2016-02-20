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

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.AbstractListener;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ToolListener extends AbstractListener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Map<VarKey, String> vars = new HashMap<>();

		if(!player.getItemInHand().equals(Config.REGION_TOOL.getItemStack())) {
			return;
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		Location pointedLocation = player.getTargetBlock((HashSet<Byte>) null, 200).getLocation();
		Action action = event.getAction();

		pointedLocation.setWorld(player.getWorld());
		NovaRegion region = plugin.getRegionManager().getRegion(pointedLocation);

		//Change RegionMode
		if((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
			if(!Permission.NOVAGUILDS_TOOL_CHECK.has(player) || !Permission.NOVAGUILDS_REGION_CREATE.has(player)) {
				return;
			}

			event.setCancelled(true);
			nPlayer.setRegionMode(nPlayer.getRegionMode() == RegionMode.CHECK ? RegionMode.SELECT : RegionMode.CHECK);
			nPlayer.cancelToolProgress();

			//highlight corners for resizing
			if(nPlayer.getRegionMode() == RegionMode.SELECT && nPlayer.hasPermission(GuildPermission.REGION_RESIZE) && nPlayer.getGuild().hasRegion()) {
				RegionUtils.highlightRegion(player, region, Config.REGION_MATERIALS_RESIZE_CORNER.getMaterial());
				nPlayer.setSelectedRegion(nPlayer.getGuild().getRegion());
			}

			Message mode = nPlayer.getRegionMode() == RegionMode.SELECT ? Message.CHAT_REGION_TOOL_MODES_SELECT : Message.CHAT_REGION_TOOL_MODES_CHECK;

			vars.put(VarKey.MODE, mode.get());
			Message.CHAT_REGION_TOOL_TOGGLEDMODE.vars(vars).send(player);
			return;
		}

		if(nPlayer.getRegionMode() == RegionMode.CHECK && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) { //CHECK MODE
			if(!Permission.NOVAGUILDS_TOOL_CHECK.has(player)) { //permissions check
				return;
			}

			//Reset region highlighted already
			if(nPlayer.getSelectedRegion() != null) {
				RegionUtils.highlightRegion(player, nPlayer.getSelectedRegion(), null);
				nPlayer.setSelectedRegion(null);
			}

			if(region != null) {
				RegionUtils.highlightRegion(player, region, Config.REGION_MATERIALS_CHECK_HIGHLIGHT.getMaterial());
				vars.put(VarKey.GUILDNAME, region.getGuild().getName());
				Message.CHAT_REGION_BELONGSTO.vars(vars).send(player);
				nPlayer.setSelectedRegion(region);
			}
			else {
				Message.CHAT_REGION_NOREGIONHERE.send(player);
			}
		}
		else if(event.getAction() != Action.PHYSICAL && nPlayer.getRegionMode() != RegionMode.CHECK) { //CREATE MODE
			Location pointedCornerLocation = pointedLocation.clone();
			pointedCornerLocation.setY(0);
			double[] cornerDistance = new double[]{region == null ? 1 : pointedCornerLocation.distance(region.getCorner(0).getBlock().getLocation()), region == null ? 1 : pointedCornerLocation.distance(region.getCorner(1).getBlock().getLocation())};

			if(region != null && nPlayer.getRegionMode() != RegionMode.RESIZE && (cornerDistance[0] < 1 || cornerDistance[1] < 1)) { //resizing
				if(!Permission.NOVAGUILDS_REGION_RESIZE.has(player)) {
					return;
				}

				if(region.getGuild().isMember(nPlayer) && nPlayer.hasPermission(GuildPermission.REGION_RESIZE)) {
					int corner = 1;

					if(cornerDistance[0] < 1) {
						corner = 0;
					}

					nPlayer.setRegionMode(RegionMode.RESIZE);
					nPlayer.setResizingCorner(corner);
					Message.CHAT_REGION_RESIZE_START.send(player);
					RegionUtils.sendRectangle(player, nPlayer.getSelectedLocation(0), nPlayer.getSelectedLocation(1), null);
					RegionUtils.sendRectangle(player, region.getCorner(0), region.getCorner(1), Config.REGION_MATERIALS_RESIZE_RECTANGLE.getMaterial(), Config.REGION_MATERIALS_RESIZE_RECTANGLE.getMaterialData());
					nPlayer.setSelectedLocation(0, region.getCorner(0));
					nPlayer.setSelectedLocation(1, region.getCorner(1));
					nPlayer.setSelectedLocation(corner == 1 ? 0 : 1, region.getCorner(corner == 1 ? 0 : 1));
				}
			}
			else {
				if(!Permission.NOVAGUILDS_REGION_CREATE.has(player)) {
					return;
				}

				Location sl0 = nPlayer.getSelectedLocation(0);
				Location sl1 = nPlayer.getSelectedLocation(1);

				//Corner 0
				if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
					if(nPlayer.getRegionMode() != RegionMode.RESIZE) {
						if(sl1 != null && !pointedLocation.getWorld().equals(sl1.getWorld())) {
							return;
						}

						if(sl0 != null) {
							RegionUtils.setCorner(player, sl0, null);

							if(sl1 != null) {
								RegionUtils.sendRectangle(player, sl0, sl1, null);
							}
						}

						RegionUtils.setCorner(player, pointedLocation, Config.REGION_MATERIALS_SELECTION_CORNER.getMaterial());
						nPlayer.setSelectedLocation(0, pointedLocation);
						sl0 = pointedLocation;
					}
				}

				//Corner 1
				if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
					if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
						if(nPlayer.getSelectedLocation(nPlayer.getResizingCorner()) != null) {
							RegionUtils.setCorner(player, nPlayer.getSelectedLocation(nPlayer.getResizingCorner()), null);

							if(nPlayer.getSelectedLocation(nPlayer.getResizingCorner() == 0 ? 1 : 0) != null) {
								RegionUtils.sendRectangle(player, sl0, sl1, null);
							}
						}

						if(nPlayer.getResizingCorner() == 0) {
							sl0 = pointedLocation;
						}
						else {
							sl1 = pointedLocation;
						}

						nPlayer.setSelectedLocation(nPlayer.getResizingCorner(), pointedLocation);
					}
					else {
						if(sl0 != null && !pointedLocation.getWorld().equals(sl0.getWorld())) {
							return;
						}

						if(sl1 != null) {
							RegionUtils.setCorner(player, nPlayer.getSelectedLocation(1), null);

							if(sl0 != null) {
								RegionUtils.sendRectangle(player, sl0, sl1, null);
							}
						}

						nPlayer.setSelectedLocation(1, pointedLocation);
						sl1 = pointedLocation;
					}

					RegionUtils.setCorner(player, pointedLocation, Config.REGION_MATERIALS_SELECTION_CORNER.getMaterial());
				}

				if(sl0 != null && sl1 != null) {
					RegionValidity validSelect = plugin.getRegionManager().checkRegionSelect(sl0, sl1);
					byte rectangleData = Config.REGION_MATERIALS_SELECTION_INVALID.getMaterialData();
					Material rectangleMaterial = Config.REGION_MATERIALS_SELECTION_INVALID.getMaterial();

					//When resizing if overlaps player's region
					if(nPlayer.getRegionMode() == RegionMode.RESIZE && validSelect == RegionValidity.OVERLAPS) {
						List<NovaRegion> regionsOverlapped = plugin.getRegionManager().getRegionsInsideArea(sl0, sl1);
						if(regionsOverlapped.size() == 1 && regionsOverlapped.get(0).equals(nPlayer.getGuild().getRegion())) {
							validSelect = RegionValidity.VALID;
						}
					}

					if(validSelect == RegionValidity.TOOCLOSE) {
						List<NovaGuild> guildsTooClose = plugin.getRegionManager().getGuildsTooClose(sl0, sl1);

						if(guildsTooClose.size() == 1 && guildsTooClose.get(0).equals(nPlayer.getGuild())) {
							validSelect = RegionValidity.VALID;
						}
					}

					switch(validSelect) {
						case VALID:  //valid
							if(nPlayer.hasGuild()) {
								int regionSize = RegionUtils.checkRegionSize(sl0, sl1);
								double price;
								double ppb = GroupManager.getGroup(player).getRegionPricePerBlock();

								if(nPlayer.getRegionMode() == RegionMode.RESIZE) {
									rectangleData = Config.REGION_MATERIALS_RESIZE_RECTANGLE.getMaterialData();
									rectangleMaterial = Config.REGION_MATERIALS_RESIZE_RECTANGLE.getMaterial();
									price = ppb * (regionSize - nPlayer.getGuild().getRegion().getSurface());
								}
								else {
									rectangleData = Config.REGION_MATERIALS_SELECTION_RECTANGLE.getMaterialData();
									rectangleMaterial = Config.REGION_MATERIALS_SELECTION_RECTANGLE.getMaterial();
									price = ppb * regionSize + GroupManager.getGroup(player).getRegionCreateMoney();
								}

								vars.put(VarKey.SIZE, String.valueOf(regionSize));
								vars.put(VarKey.PRICE, String.valueOf(price));

								Message.CHAT_REGION_SIZE.vars(vars).send(player);

								if(price > 0) {
									Message.CHAT_REGION_PRICE.vars(vars).send(player);

									if(!nPlayer.getGuild().hasMoney(price)) {
										vars.put(VarKey.NEEDMORE, String.valueOf(price - nPlayer.getGuild().getMoney()));
										Message.CHAT_REGION_CNOTAFFORD.vars(vars).send(player);
										break;
									}
								}

								Message.CHAT_REGION_VALIDATION_VALID.send(player);
							}
							else {
								Message.CHAT_REGION_MUSTVEGUILD.send(player);
							}
							break;
						case TOOSMALL:
							vars.put(VarKey.MINSIZE, Config.REGION_MINSIZE.getString());
							Message.CHAT_REGION_VALIDATION_TOOSMALL.vars(vars).send(player);
							break;
						case TOOBIG:
							vars.put(VarKey.MAXSIZE, Config.REGION_MAXSIZE.getString());
							Message.CHAT_REGION_VALIDATION_TOOBIG.vars(vars).send(player);
							break;
						case OVERLAPS:
							Message.CHAT_REGION_VALIDATION_OVERLAPS.send(player);
							break;
						case TOOCLOSE:
							Message.CHAT_REGION_VALIDATION_TOOCLOSE.send(player);
							break;
					}

					//corners and rectangles
					RegionUtils.sendRectangle(player, sl0, sl1, rectangleMaterial, rectangleData);
					RegionUtils.setCorner(player, sl0, Config.REGION_MATERIALS_SELECTION_CORNER.getMaterial());
					RegionUtils.setCorner(player, sl1, Config.REGION_MATERIALS_SELECTION_CORNER.getMaterial());
					event.setCancelled(true);
				}
			}
		}
	}
}
