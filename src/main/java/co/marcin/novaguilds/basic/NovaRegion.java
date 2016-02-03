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

package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class NovaRegion {
	private final Location[] corners = new Location[2];

	private int id;
	private World world;
	private NovaGuild guild;
	private boolean changed = false;

	private int width = 0;
	private int height = 0;
	private int size = 0;

	public static NovaRegion get(Location location) {
		return NovaGuilds.getInstance().getRegionManager().getRegion(location);
	}

	public static NovaRegion get(Block block) {
		return get(block.getLocation());
	}

	public static NovaRegion get(Entity entity) {
		return get(entity.getLocation());
	}
	
	public World getWorld() {
		return world;
	}

	public int getId() {
		if(id <= 0) {
			throw new UnsupportedOperationException("This rank might have been loaded from FLAT and has 0 (or negative) ID");
		}

		return id;
	}

	public NovaGuild getGuild() {
		return guild;
	}

	public Location getCorner(int index) {
		return corners[index];
	}

	public int getWidth() {
		if(width == 0) {
			width = Math.abs(getCorner(0).getBlockX() - getCorner(1).getBlockX()) + 1;
		}

		return width;
	}

	public int getHeight() {
		if(height == 0) {
			height = Math.abs(getCorner(0).getBlockZ() - getCorner(1).getBlockZ()) + 1;
		}

		return height;
	}

	public int getDiagonal() {
		if(size == 0) {
			int w = getWidth();
			int h = getHeight();

			int sumsq = (int) (Math.pow(w, 2) + Math.pow(h, 2));
			size = Math.round((int) Math.sqrt(sumsq));
		}

		return size;
	}

	public int getSurface() {
		return getHeight() * getWidth();
	}

	public Location getCenter() {
		return RegionUtils.getCenterLocation(getCorner(0), getCorner(1));
	}
	
	//setters
	public void setUnChanged() {
		changed = false;
	}

	private void changed() {
		changed = true;
	}

	public void setWorld(World world) {
		this.world = world;
		changed();
	}

	public void setId(int id) {
		this.id = id;
		changed();
	}

	public void setGuild(NovaGuild guild) {
		this.guild = guild;
	}
	
	public void setCorner(int index, Location location) {
		corners[index] = location;
		changed();
	}

	//checkers
	public boolean isChanged() {
		return changed;
	}
}
