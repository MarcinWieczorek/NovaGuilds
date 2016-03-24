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

package co.marcin.novaguilds.impl.storage;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.api.storage.Storage;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStorage implements Storage {
	protected static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<Class, ResourceManager> resourceManagers = new HashMap<>();

	@Override
	public void save() {
		plugin.getGuildManager().save();
		plugin.getRankManager().save();
		plugin.getRegionManager().save();
		plugin.getPlayerManager().save();
	}

	@Override
	public <T> ResourceManager<T> getResourceManager(Class<T> clazz) {
		return (ResourceManager<T>) resourceManagers.get(clazz);
	}

	@Override
	public <T> void registerResourceManager(Class clazz, ResourceManager<T> resourceManager) {
		resourceManagers.put(clazz, resourceManager);
	}
}
