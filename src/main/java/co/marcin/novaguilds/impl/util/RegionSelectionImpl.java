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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionSelectionImpl implements RegionSelection {
	private final List<Block> blockList = new ArrayList<>();
	private final List<Location> corners = new ArrayList<>(2);
	private final List<NovaPlayer> playerList = new ArrayList<>();
	private final Type type;
	private RegionValidity regionValidity;

	private Material cornerMaterial;
	private Byte cornerData;
	private Material borderMaterial;
	private Byte borderData;

	/**
	 * Constructor for filling with corner locations
	 *
	 * @param nPlayer the player
	 * @param type selection type
	 */
	public RegionSelectionImpl(NovaPlayer nPlayer, Type type) {
		addSpectator(nPlayer);
		this.type = type;

		nPlayer.setActiveSelection(this);
	}

	@Override
	public void send() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!Permission.NOVAGUILDS_ADMIN_REGION_SPECTATE.has(player)) {
				continue;
			}

			NovaPlayer onlineNovaPlayer = PlayerManager.getPlayer(player);

			if(onlineNovaPlayer.getRegionSpectate()) {
				addSpectator(onlineNovaPlayer);
			}
		}

		loadMaterials();

		for(NovaPlayer nPlayer : getPlayerList()) {
			for(Block block : getBlocks()) {
				RegionUtils.resetBlock(nPlayer.getPlayer(), block);
			}
		}

		clearBlockList();

		for(NovaPlayer nPlayer : getPlayerList()) {
			Player player = nPlayer.getPlayer();

			sendRectangle(player);
			highlightCorner(player, getCorner(0), cornerMaterial, cornerData);
			highlightCorner(player, getCorner(1), cornerMaterial, cornerData);
		}
	}

	@Override
	public void reset() {
		for(NovaPlayer nPlayer : getPlayerList()) {
			reset(nPlayer);
		}

		clearBlockList();
	}

	@Override
	public void reset(NovaPlayer nPlayer) {
		for(Block block : new ArrayList<>(getBlocks())) {
			RegionUtils.resetBlock(nPlayer.getPlayer(), block);
		}

		if(getPlayerList().size() == 1 && getPlayerList().contains(nPlayer)) {
			clearBlockList();
		}
	}

	@Override
	public void setCorner(Integer index, Location location) {
		corners.add(index, location);
	}

	@Override
	public void setValidity(RegionValidity regionValidity) {
		this.regionValidity = regionValidity;
	}

	@Override
	public void addSpectator(NovaPlayer nPlayer) {
		if(!playerList.contains(nPlayer)) {
			playerList.add(nPlayer);
		}
	}

	@Override
	public void removeSpectator(NovaPlayer nPlayer) {
		playerList.remove(nPlayer);
	}

	@Override
	public Material getBorderMaterial() {
		return borderMaterial;
	}

	@Override
	public byte getBorderData() {
		return borderData;
	}

	@Override
	public Material getCornerMaterial() {
		return cornerMaterial;
	}

	@Override
	public byte getCornerData() {
		return cornerData;
	}

	@Override
	public RegionValidity getValidity() {
		return regionValidity;
	}

	@Override
	public boolean isValid() {
		return regionValidity == RegionValidity.VALID;
	}

	@Override
	public Location getCorner(Integer index) {
		return corners.size() > index ? corners.get(index) : null;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public List<Block> getBlocks() {
		return blockList;
	}

	@Override
	public NovaPlayer getPlayer() {
		return getPlayerList().get(0);
	}

	@Override
	public List<NovaPlayer> getPlayerList() {
		return playerList;
	}

	protected void loadMaterials() {
		switch(type) {
			case HIGHLIGHT:
				cornerMaterial = Config.REGION_MATERIALS_HIGHLIGHT_REGION_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_HIGHLIGHT_REGION_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_HIGHLIGHT_REGION_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_HIGHLIGHT_REGION_CORNER.getMaterialData();
				break;
			case HIGHLIGHT_RESIZE:
				cornerMaterial = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_HIGHLIGHT_RESIZE_BORDER.getMaterialData();
				break;
			case CREATE:
				cornerMaterial = Config.REGION_MATERIALS_SELECTION_VALID_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_SELECTION_VALID_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_SELECTION_VALID_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_SELECTION_VALID_BORDER.getMaterialData();
				break;
			case RESIZE:
				cornerMaterial = Config.REGION_MATERIALS_RESIZE_CORNER.getMaterial();
				cornerData = Config.REGION_MATERIALS_RESIZE_CORNER.getMaterialData();

				borderMaterial = Config.REGION_MATERIALS_RESIZE_BORDER.getMaterial();
				borderData = Config.REGION_MATERIALS_RESIZE_BORDER.getMaterialData();
				break;
		}

		if(getValidity() != RegionValidity.VALID) {
			cornerMaterial = Config.REGION_MATERIALS_SELECTION_INVALID_CORNER.getMaterial();
			cornerData = Config.REGION_MATERIALS_SELECTION_INVALID_CORNER.getMaterialData();

			borderMaterial = Config.REGION_MATERIALS_SELECTION_INVALID_BORDER.getMaterial();
			borderData = Config.REGION_MATERIALS_SELECTION_INVALID_BORDER.getMaterialData();
		}
	}

	protected void clearBlockList() {
		blockList.clear();
	}

	@SuppressWarnings("deprecation")
	protected void sendRectangle(Player player) {
		if(player == null || getCorner(0) == null || getCorner(1) == null) {
			return;
		}

		for(Block block : RegionUtils.getBorderBlocks(getCorner(0), getCorner(1))) {
			if(borderMaterial == null) {
				RegionUtils.resetBlock(player, block);
				continue;
			}

			player.sendBlockChange(block.getLocation(), borderMaterial, borderData);
			blockList.add(block);
		}
	}

	@SuppressWarnings("deprecation")
	protected void highlightCorner(Player player, Location location, Material material, Byte data) {
		if(player == null || location == null) {
			return;
		}

		location = location.clone();
		Block highest1 = player.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ());
		location.setY(highest1.getY() - (highest1.getType() == Material.SNOW ? 0 : 1));

		if(material == null) {
			material = location.getBlock().getType();
			data = location.getBlock().getData();
		}

		player.sendBlockChange(location, material, data);
		getBlocks().add(location.getBlock());
	}
}
