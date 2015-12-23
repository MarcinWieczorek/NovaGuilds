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

import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.util.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

public class NovaRank implements Cloneable {
	private int id;
	private String name;
	private NovaGuild guild;
	private boolean changed;
	private final List<GuildPermission> permissions = new ArrayList<>();
	private final List<NovaPlayer> members = new ArrayList<>();

	public NovaRank(int id) {
		this.id = id;
	}

	//Not added rank (id=-1)
	public NovaRank(String name) {
		id = -1;
		this.name = name;
	}

	//getters
	public int getId() {
		if(id <= 0) {
			throw new UnsupportedOperationException("This rank might have been loaded from FLAT and has 0 (or negative) ID");
		}

		return id;
	}

	public String getName() {
		return name;
	}

	public List<NovaPlayer> getMembers() {
		return members;
	}

	public List<GuildPermission> getPermissions() {
		return permissions;
	}

	public NovaGuild getGuild() {
		return guild;
	}

	//setters
	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
		changed();
	}

	public void setPermissions(List<GuildPermission> permissions) {
		this.permissions.clear();
		this.permissions.addAll(permissions);
		changed();
	}
	public void setGuild(NovaGuild guild) {
		this.guild = guild;
		guild.addRank(this);
		changed();
	}

	public void setUnchanged() {
		changed = false;
	}

	public void changed() {
		changed = true;
	}

	//adders
	public void addPermission(GuildPermission permission) {
		permissions.add(permission);
		changed();
	}

	public void addMember(NovaPlayer nPlayer) {
		members.add(nPlayer);
		nPlayer.setGuildRank(this);
		changed();
	}

	//removers
	public void removePermission(GuildPermission permission) {
		permissions.remove(permission);
		changed();
	}

	public void removeMember(NovaPlayer nPlayer) {
		members.remove(nPlayer);
		nPlayer.setGuildRank(null);
		changed();
	}

	//checkers
	public boolean hasPermission(GuildPermission permission) {
		return permissions.contains(permission);
	}

	public boolean isChanged() {
		return changed;
	}

	public boolean isNew() {
		return id == -1;
	}

	@Override
	public NovaRank clone() {
		NovaRank rank = new NovaRank(getId());
		rank.setName(getName());
		rank.setPermissions(getPermissions());
		rank.getMembers().addAll(getMembers());
		return rank;
	}
}
