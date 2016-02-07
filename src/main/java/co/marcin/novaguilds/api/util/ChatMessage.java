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

package co.marcin.novaguilds.api.util;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface ChatMessage {
	void send();

	void send(Player player);

	void send(NovaPlayer nPlayer);

	void send(NovaGuild guild);

	void sendToGuilds(List<NovaGuild> guildList);

	void sendToPlayers(List<Player> playerList);

	void sendToNovaPlayers(List<NovaPlayer> playerList);

	Player getPlayer();

	String getMessage();

	String getFormat();

	PreparedTag getTag();

	boolean isReportToConsole();

	void setMessage(String message);

	void setFormat(String format);

	void setTag(PreparedTag tag);

	void setReportToConsole(boolean reportToConsole);

	void report();
}
