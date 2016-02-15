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

package co.marcin.novaguilds.impl.basic.tablist;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import tk.northpl.tab.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabList1_8NorthTabImpl implements TabList {
	private final NovaPlayer nPlayer;
	private final List<String> lines = new ArrayList<>();
	private final Map<VarKey, String> vars = new HashMap<>();

	public TabList1_8NorthTabImpl(NovaPlayer nPlayer) {
		this.nPlayer = nPlayer;
		clear();
	}

	@Override
	public void send() {
		if(!nPlayer.isOnline()) {
			return;
		}

		TabUtils.fillVars(this);

		int x = 0;
		int y = 0;
		for(String line : lines) {
			line = StringUtils.replaceVarKeyMap(line, vars);
			line = StringUtils.fixColors(line);

			API.setTabSlot(nPlayer.getPlayer(), x, y, line);
			y++;
		}
	}

	@Override
	public NovaPlayer getPlayer() {
		return nPlayer;
	}

	@Override
	public Map<VarKey, String> getVars() {
		return vars;
	}

	@Override
	public void clear() {
		lines.clear();
		lines.addAll(Config.TABLIST_SCHEME.getStringList());
		vars.clear();
	}
}
