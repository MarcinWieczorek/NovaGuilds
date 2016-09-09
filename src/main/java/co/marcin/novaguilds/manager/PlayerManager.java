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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<String, NovaPlayer> players = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	//new getters
	public static NovaPlayer getPlayer(String playerName) {
		plugin.getPlayerManager().addIfNotExists(Bukkit.getPlayerExact(playerName));

		return plugin.getPlayerManager().players.get(playerName);
	}

	public static NovaPlayer getPlayer(CommandSender sender) {
		if(sender instanceof Player) {
			plugin.getPlayerManager().addIfNotExists((Player) sender);
		}

		return getPlayer(sender.getName());
	}

	public static NovaPlayer getPlayer(UUID uuid) {
		for(NovaPlayer nPlayer : plugin.getPlayerManager().getPlayers()) {
			if(nPlayer.getUUID().equals(uuid)) {
				return nPlayer;
			}
		}

		return null;
	}

	public Collection<NovaPlayer> getPlayers() {
		return players.values();
	}

	public Collection<NovaPlayer> getOnlinePlayers() {
		Collection<NovaPlayer> collection = new HashSet<>();

		for(Player player : NovaGuilds.getOnlinePlayers()) {
			collection.add(getPlayer(player));
		}

		return collection;
	}

	public void save() {
		long startTime = System.nanoTime();
		int count = getResourceManager().executeSave() + getResourceManager().save(getPlayers());
		LoggerUtils.info("Players data saved in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " players)");

		startTime = System.nanoTime();
		count = getResourceManager().executeRemoval();
		LoggerUtils.info("Players removed in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " players)");
	}

	public void load() {
		players.clear();
		for(NovaPlayer nPlayer : getResourceManager().load()) {
			players.put(nPlayer.getName(), nPlayer);
		}

		LoggerUtils.info("Loaded " + players.size() + " players.");
	}

	private void add(Player player) {
		Validate.notNull(player);

		NovaPlayer nPlayer = new NovaPlayerImpl(player.getUniqueId());
		nPlayer.setName(player.getName());
		nPlayer.setPlayer(player);
		nPlayer.setPoints(Config.KILLING_STARTPOINTS.getInt());

		players.put(nPlayer.getName(), nPlayer);
	}

	public void addIfNotExists(Player player) {
		if(player != null && !players.containsKey(player.getName())) {
			add(player);
		}
	}

	public boolean isGuildMate(Player player1, Player player2) {
		NovaPlayer nPlayer1 = getPlayer(player1);
		NovaPlayer nPlayer2 = getPlayer(player2);

		return nPlayer1.getGuild().isMember(nPlayer2) || nPlayer1.equals(nPlayer2);
	}

	public boolean isAlly(Player player1, Player player2) {
		NovaPlayer nPlayer1 = getPlayer(player1);
		NovaPlayer nPlayer2 = getPlayer(player2);

		return nPlayer1.getGuild().isAlly(nPlayer2.getGuild()) || nPlayer1.equals(nPlayer2);
	}

	public void sendPlayerInfo(CommandSender sender, NovaPlayer nCPlayer) {
		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PLAYERNAME, nCPlayer.getName());
		vars.put(VarKey.POINTS, String.valueOf(nCPlayer.getPoints()));
		vars.put(VarKey.KILLS, String.valueOf(nCPlayer.getKills()));
		vars.put(VarKey.DEATHS, String.valueOf(nCPlayer.getDeaths()));
		vars.put(VarKey.KDR, String.valueOf(nCPlayer.getKillDeathRate()));

		String guildRow = "";
		if(nCPlayer.hasGuild()) {
			vars.put(VarKey.GUILDNAME, nCPlayer.getGuild().getName());
			vars.put(VarKey.TAG, nCPlayer.getGuild().getTag());
			guildRow = Message.CHAT_PLAYER_INFO_GUILDROW.vars(vars).get();
		}

		vars.put(VarKey.GUILDROW, guildRow);

		Message.CHAT_PLAYER_INFO_HEADER.send(sender);

		for(String row : Message.CHAT_PLAYER_INFO_ITEMS.getList()) {
			if(!row.contains("{GUILDROW}") || nCPlayer.hasGuild()) {
				row = MessageManager.replaceVarKeyMap(row, vars);
				MessageManager.sendMessage(sender, row);
			}
		}
	}

	/**
	 * Gets a limited list of top players by points
	 *
	 * @param count limit
	 * @return list of players
	 */
	public List<NovaPlayer> getTopPlayersByPoints(int count) {
		return limitList(getTopPlayersByPoints(), count);
	}

	/**
	 * Gets top players by points
	 *
	 * @return list of players
	 */
	public List<NovaPlayer> getTopPlayersByPoints() {
		final List<NovaPlayer> playerList = new ArrayList<>(players.values());

		Collections.sort(playerList, new Comparator<NovaPlayer>() {
			public int compare(NovaPlayer o1, NovaPlayer o2) {
				return o2.getPoints() - o1.getPoints();
			}
		});

		return playerList;
	}

	public List<NovaPlayer> getTopPlayersByKDR() {
		final List<NovaPlayer> playerList = new ArrayList<>(players.values());

		Collections.sort(playerList, new Comparator<NovaPlayer>() {
			public int compare(NovaPlayer p1, NovaPlayer p2) {
				if(p1.getKillDeathRate() > p2.getKillDeathRate()) {
					return -1;
				}
				if(p1.getKillDeathRate() < p2.getKillDeathRate()) {
					return 1;
				}
				return 0;
			}
		});

		return playerList;
	}

	public List<NovaPlayer> getTopPlayersByKDR(int count) {
		return limitList(getTopPlayersByKDR(), count);
	}

	public static <T> List<T> limitList(List<T> list, int limit) {
		return list.subList(0, list.size() < limit ? list.size() : limit);
	}

	public ResourceManager<NovaPlayer> getResourceManager() {
		return plugin.getStorage().getResourceManager(NovaPlayer.class);
	}
}
