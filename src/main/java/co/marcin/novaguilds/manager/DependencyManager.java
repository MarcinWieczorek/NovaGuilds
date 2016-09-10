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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.exception.FatalNovaGuildsException;
import co.marcin.novaguilds.exception.MissingDependencyException;
import co.marcin.novaguilds.util.LoggerUtils;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;

public class DependencyManager {
	private final Map<Dependency, Plugin> pluginMap = new HashMap<>();
	private Economy economy;

	/**
	 * Sets up the manager
	 *
	 * @throws FatalNovaGuildsException when something goes wrong
	 */
	public void setUp() throws FatalNovaGuildsException {
		try {
			checkDependencies();
			setupEconomy();
		}
		catch(MissingDependencyException e) {
			throw new FatalNovaGuildsException("Could not satisfy dependencies", e);
		}
	}

	/**
	 * Checks dependencies
	 *
	 * @throws MissingDependencyException when something goes wrong
	 */
	public void checkDependencies() throws MissingDependencyException {
		pluginMap.clear();

		for(Dependency dependency : Dependency.values()) {
			Plugin plugin = getPlugin(dependency.getName());

			if(plugin != null) {
				pluginMap.put(dependency, plugin);
				LoggerUtils.info("Found plugin " + dependency.getName());

				if(dependency.hasAdditionalTasks()) {
					for(RunnableWithException additionalTask : dependency.getAdditionalTasks()) {
						try {
							LoggerUtils.info("Running additional task '" + additionalTask.getClass().getSimpleName() + "' for " + dependency.getName());
							additionalTask.run();
						}
						catch(Exception e) {
							throw new MissingDependencyException("Could not pass additional task '" + additionalTask.getClass().getSimpleName() + "' for " + dependency.getName(), e);
						}
					}
				}
			}
			else {
				if(dependency.isHardDependency()) {
					throw new MissingDependencyException("Missing dependency " + dependency.getName());
				}
				else {
					LoggerUtils.info("Could not find plugin: " + dependency.getName() + ", disabling certain features");
				}
			}
		}
	}

	/**
	 * Checks if a dependency is enabled
	 *
	 * @param dependency dependency enum
	 * @return boolean
	 */
	public boolean isEnabled(Dependency dependency) {
		return pluginMap.containsKey(dependency);
	}

	/**
	 * Setups economy
	 */
	private void setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		economy = rsp.getProvider();
		Validate.notNull(economy);
	}

	/**
	 * Gets a plugin by its name
	 *
	 * @param name plugin's name
	 * @return plugin instance
	 */
	private Plugin getPlugin(String name) {
		return ListenerManager.getLoggedPluginManager().getPlugin(name);
	}

	/**
	 * Gets the object of a plugin
	 *
	 * @param dependency dependency enum
	 * @param cast       class to cast
	 * @param <T>        class to cast
	 * @return plugin instance
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(Dependency dependency, Class<T> cast) {
		return (T) pluginMap.get(dependency);
	}

	public static class HolographicDisplaysAPIChecker implements RunnableWithException {
		@Override
		public void run() throws ClassNotFoundException {
			Class.forName("com.gmail.filoghost.holographicdisplays.api.HologramsAPI");
		}
	}

	public interface RunnableWithException {
		/**
		 * Runs.
		 *
		 * @throws Exception when something goes wrong
		 */
		void run() throws Exception;
	}

	/**
	 * Gets the Economy
	 *
	 * @return economy instance
	 */
	public Economy getEconomy() {
		return economy;
	}
}
