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

import co.marcin.novaguilds.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class NovaRaid {
	private NovaGuild guildAttacker;
	private NovaGuild guildDefender;
	private final long startTime = NumberUtils.systemSeconds();
	private long inactiveTime = NumberUtils.systemSeconds();
	private int killsAttacker;
	private int killsDefender;
	private int progress;
	private boolean finished;
	private final List<NovaPlayer> playersOccupying = new ArrayList<>();

	public NovaRaid(NovaGuild guildAttacker, NovaGuild guildDefender) {
		this.guildAttacker = guildAttacker;
		this.guildDefender = guildDefender;
	}

	public NovaGuild getGuildAttacker() {
		return guildAttacker;
	}

	public NovaGuild getGuildDefender() {
		return guildDefender;
	}

	public long getStartTime() {
		return startTime;
	}

	public int getKillsAttacker() {
		return killsAttacker;
	}

	public int getKillsDefender() {
		return killsDefender;
	}

	public int getProgress() {
		return progress;
	}

	public List<NovaPlayer> getPlayersOccupying() {
		return playersOccupying;
	}

	public int getPlayersOccupyingCount() {
		return playersOccupying.size();
	}

	public boolean getFinished() {
		return finished;
	}

	public long getInactiveTime() {
		return inactiveTime;
	}

	//setters
	public void setGuildAttacker(NovaGuild guild) {
		guildAttacker = guild;
	}

	public void setGuildDefender(NovaGuild guild) {
		guildDefender = guild;
	}

	public void addKillAttacker() {
		killsAttacker++;
	}

	public void addKillDefender() {
		killsDefender++;
	}

	public void resetProgress() {
		progress = 0;
	}

	public boolean isProgressFinished() {
		return progress == 100;
	}

	public void stepProgress() {
		if(progress < 100) {
			//TODO: tests
			//progress += 10;
			progress++;
		}
	}

	public void setFinished(boolean f) {
		finished = f;
	}

	public void finish() {
		finished = true;
	}

	public void updateInactiveTime() {
		inactiveTime = NumberUtils.systemSeconds();
	}

	//add/remove
	public void addPlayerOccupying(NovaPlayer nPlayer) {
		if(!playersOccupying.contains(nPlayer)) {
			playersOccupying.add(nPlayer);
		}
	}

	public void removePlayerOccupying(NovaPlayer nPlayer) {
		playersOccupying.remove(nPlayer);
	}
}
