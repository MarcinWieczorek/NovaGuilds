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

package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.manager.DependencyManager;

public enum Dependency {
	VAULT("Vault", true),
	VANISHNOPACKET("VanishNoPacket", false),
	ESSENTIALS("Essentials", false),
	BOSSBARAPI("BossBarAPI", false),
	BARAPI("BarAPI", false),
	SCOREBOARDSTATS("ScoreboardStats", false),
	HOLOGRAPHICDISPLAYS("HolographicDisplays", false, new DependencyManager.HolographicDisplaysAPIChecker());

	private final String name;
	private final boolean hardDependency;
	private DependencyManager.RunnableWithException[] additionalTasks = new DependencyManager.RunnableWithException[0];

	/**
	 * The constructor
	 *
	 * @param name           the name
	 * @param hardDependency true if required
	 */
	Dependency(String name, boolean hardDependency) {
		this.name = name;
		this.hardDependency = hardDependency;
	}

	/**
	 * The constructor
	 *
	 * @param name            the name
	 * @param hardDependency  true if required
	 * @param additionalTasks additional tasks
	 */
	Dependency(String name, boolean hardDependency, DependencyManager.RunnableWithException... additionalTasks) {
		this(name, hardDependency);
		this.additionalTasks = additionalTasks;
	}

	/**
	 * Gets the name
	 *
	 * @return dependency name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if the dependency is required (hard)
	 *
	 * @return boolean
	 */
	public boolean isHardDependency() {
		return hardDependency;
	}

	/**
	 * Checks if there are additional tasks
	 *
	 * @return boolean
	 */
	public boolean hasAdditionalTasks() {
		return additionalTasks.length > 0;
	}

	/**
	 * Gets additional tasks
	 *
	 * @return array of runables
	 */
	public DependencyManager.RunnableWithException[] getAdditionalTasks() {
		return additionalTasks;
	}
}
