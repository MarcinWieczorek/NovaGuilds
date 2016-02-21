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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.api.util.Changeable;
import org.bukkit.Location;
import org.bukkit.World;

public interface NovaRegion extends Changeable {
	World getWorld();

	int getId();

	NovaGuild getGuild();

	Location getCorner(int index);

	int getWidth();

	int getHeight();

	int getDiagonal();

	int getSurface();

	Location getCenter();

	void setWorld(World world);

	void setId(int id);

	void setGuild(NovaGuild guild);

	void setCorner(int index, Location location);
}
