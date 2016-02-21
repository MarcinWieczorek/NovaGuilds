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
import co.marcin.novaguilds.enums.GuildPermission;

import java.util.List;

public interface NovaRank extends Changeable {
	int getId();

	String getName();

	List<NovaPlayer> getMembers();

	List<GuildPermission> getPermissions();

	NovaGuild getGuild();

	boolean isClone();

	boolean isDefault();

	void setId(int id);

	void setName(String name);

	void setPermissions(List<GuildPermission> permissions);

	void setGuild(NovaGuild guild);

	void setClone(boolean clone);

	void setDefault(boolean def);

	void addPermission(GuildPermission permission);

	void addMember(NovaPlayer nPlayer);

	void removePermission(GuildPermission permission);

	void removeMember(NovaPlayer nPlayer);

	boolean hasPermission(GuildPermission permission);

	boolean isNew();

	boolean isGeneric();

	void delete();
}
