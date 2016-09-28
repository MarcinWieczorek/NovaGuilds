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

package co.marcin.novaguilds.impl.versionimpl.v1_8_R1;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.impl.basic.AbstractTabList;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import tk.northpl.tab.API;

public class TabListImpl extends AbstractTabList {
	/**
	 * The constructor
	 *
	 * @param nPlayer tablist owner
	 */
	public TabListImpl(NovaPlayer nPlayer) {
		super(nPlayer);
	}

	@Override
	public void send() {
		if(!getPlayer().isOnline()) {
			return;
		}

		TabUtils.fillVars(this);

		int x = 0;
		int y = 0;
		for(String line : lines) {
			line = StringUtils.replaceVarKeyMap(line, getVars());
			line = StringUtils.fixColors(line);

			API.setTabSlot(getPlayer().getPlayer(), x, y, line);
			y++;
		}
	}
}
