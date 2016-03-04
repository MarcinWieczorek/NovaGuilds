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

import gnu.trove.map.hash.TCustomHashMap;

import java.util.Map;

public class CaseInsensitiveMap<V> extends TCustomHashMap<String, V> {
	private static final long serialVersionUID = 0;
	
	public CaseInsensitiveMap() {
		super(CaseInsensitiveHashingStrategy.INSTANCE);
	}

	public CaseInsensitiveMap(final int initialCapacity) {
		super(CaseInsensitiveHashingStrategy.INSTANCE, initialCapacity);
	}

	public CaseInsensitiveMap(final int initialCapacity, final float loadFactor) {
		super(CaseInsensitiveHashingStrategy.INSTANCE, initialCapacity, loadFactor);
	}

	public CaseInsensitiveMap(final TCustomHashMap<? extends String, ? extends V> map) {
		super(CaseInsensitiveHashingStrategy.INSTANCE, map);
	}

	public CaseInsensitiveMap(final Map<? extends String, ? extends V> map) {
		super(CaseInsensitiveHashingStrategy.INSTANCE, map);
	}
}
