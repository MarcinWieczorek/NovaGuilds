/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2018 Marcin (CTRL) Wieczorek
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

public interface NovaGroup {
    interface Key<T> {
        /**
         * Gets variable type
         *
         * @return the type
         */
        Class<T> getType();
    }

    /**
     * Get group's name
     *
     * @return name
     */
    String getName();

    /**
     * Gets a value
     *
     * @param key the key
     * @param <T> type parameter
     * @return value as object
     */
    <T> T get(Key<T> key);
}
