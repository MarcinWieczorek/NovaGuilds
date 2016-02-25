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

package co.marcin.novaguilds.impl.util.preparedtag;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Permission;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPreparedTag implements PreparedTag {
	private final NovaGuild guild;
	private final Config pattern;
	private boolean leaderPrefix = false;
	private boolean hidden = false;
	private Color color = Color.NEUTRAL;

	//Constructors
	public AbstractPreparedTag(Config pattern, NovaGuild guild) {
		this.pattern = pattern;
		this.guild = guild;
	}

	//Getters
	@Override
	public NovaGuild getGuild() {
		return guild;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public boolean isLeaderPrefix() {
		return leaderPrefix;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	//Setters
	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public void setLeaderPrefix(boolean leaderPrefix) {
		this.leaderPrefix = leaderPrefix;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void setUpFor(NovaPlayer nPlayer) {
		if(!nPlayer.isOnline()) {
			return;
		}

		setHidden(Permission.NOVAGUILDS_CHAT_NOTAG.has(nPlayer.getPlayer()));
		setLeaderPrefix(nPlayer.isLeader() && Config.TABAPI_RANKPREFIX.getBoolean());
	}

	@Override
	public String get() {
		if(isHidden() || guild == null) {
			return "";
		}

		String tag = pattern.getString();

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.RANK, isLeaderPrefix() ? Config.CHAT_LEADERPREFIX.getString() : "");
		vars.put(VarKey.COLOR, color.getConfig().getString());
		vars.put(VarKey.TAG, guild.getTag());
		tag = StringUtils.replaceVarKeyMap(tag, vars);
		tag = StringUtils.fixColors(tag);

		return tag;
	}
}
