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

package co.marcin.novaguilds.api.basic;

import org.bukkit.Location;

import java.util.List;

public interface NovaHologram {
	String getName();

	Location getLocation();

	List<String> getLines();

	void setName(String name);

	void setLocation(Location location);

	void addLine(String line);

	void clearLines();

	void addLine(List<String> lines);

	void refresh();

	void teleport(Location location);

	void create();

	void delete();

	boolean isTop();

	boolean isDeleted();

	void setTop(boolean top);
}
