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

package co.marcin.novaguilds.util.caseinsensitivemap;

import gnu.trove.strategy.HashingStrategy;

/**
 * Case insensitive hashing strategy.
 */
public class CaseInsensitiveHashingStrategy implements HashingStrategy<String> {
	private static final long serialVersionUID = 0;

	/**
	 * Protected constructor, use {@link #INSTANCE} to get instance.
	 */
	protected CaseInsensitiveHashingStrategy() {
	}

	/**
	 * Static instance of this hashing strategy.
	 */
	public static final CaseInsensitiveHashingStrategy INSTANCE = new CaseInsensitiveHashingStrategy();

	@Override
	public int computeHashCode(final String s) {
		return s.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(final String s1, final String s2) {
		return (s1.equals(s2)) || (((s2 != null)) && (s1.toLowerCase().equals(s2.toLowerCase())));
	}
}
