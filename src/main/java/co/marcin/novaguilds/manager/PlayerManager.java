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

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class PlayerManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Map<String, NovaPlayer> players = new HashMap<>();
	
	public boolean exists(String playername) {
		return players.containsKey(playername.toLowerCase());
	}

	//new getters
	public NovaPlayer getPlayer(String playername) {
		addIfNotExists(playername);

		return players.get(playername.toLowerCase());
	}

	public NovaPlayer getPlayer(CommandSender sender) {
		addIfNotExists(sender.getName());

		return getPlayer(sender.getName());
	}

	public NovaPlayer getPlayer(Player player) {
		addIfNotExists(player);

		return getPlayer(player.getName());
	}

	public Collection<NovaPlayer> getPlayers() {
		return players.values();
	}

	public Collection<NovaPlayer> getOnlinePlayers() {
		Collection<NovaPlayer> collection = new HashSet<>();

		for(Player player : Bukkit.getOnlinePlayers()) {
			collection.add(NovaPlayer.get(player));
		}

		return collection;
	}
	
	private void save(NovaPlayer nPlayer) {
		if(nPlayer.isChanged()) { //only if there were changes
			if(plugin.getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
				plugin.getFlatDataManager().save(nPlayer);
			}
			else {
				if(!plugin.getDatabaseManager().isConnected()) {
					LoggerUtils.info("Connection is not estabilished, stopping current action");
					return;
				}

				plugin.getDatabaseManager().mysqlReload();

				try {
					PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.PLAYERS_UPDATE);

					//prepare data
					String joined = StringUtils.join(nPlayer.getInvitedToNames(), ";");

					//prepare and save
					preparedStatement.setString(1, joined);
					preparedStatement.setString(2, nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
					preparedStatement.setInt(3, nPlayer.getPoints());
					preparedStatement.setInt(4, nPlayer.getKills());
					preparedStatement.setInt(5, nPlayer.getDeaths());
					preparedStatement.setString(6, nPlayer.getUUID().toString());
					preparedStatement.executeUpdate();
				}
				catch(SQLException e) {
					LoggerUtils.exception(e);
				}
			}
		}
	}
	
	public void save() {
		long startTime = System.nanoTime();
		int count = 0;

		for(NovaPlayer nPlayer : getPlayers()) {
			if(nPlayer.isChanged()) {
				count++;
			}

			save(nPlayer);
		}

		LoggerUtils.info("Players data saved in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " players)");
	}
	
	//load
	public void load() {
		players.clear();
		if(plugin.getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
			for(String playerName : plugin.getFlatDataManager().getPlayerList()) {
				FileConfiguration playerData = plugin.getFlatDataManager().getPlayerData(playerName);
				NovaPlayer nPlayer = playerFromFlat(playerData);

				if(nPlayer != null) {
					if(nPlayer.getPoints() == 0 && nPlayer.getKills() == 0 && nPlayer.getDeaths() == 0) {
						nPlayer.setPoints(Config.KILLING_STARTPOINTS.getInt());
					}

					players.put(playerName.toLowerCase(), nPlayer);
				}
				else {
					LoggerUtils.info("Loaded player is null. name: " + playerName);
				}
			}
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				ResultSet res = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.PLAYERS_SELECT).executeQuery();
				while(res.next()) {
					NovaPlayer nPlayer = playerFromResult(res);

					if(nPlayer == null) {
						continue;
					}

					if(nPlayer.getPoints() == 0 && nPlayer.getKills() == 0 && nPlayer.getDeaths() == 0) {
						nPlayer.setPoints(Config.KILLING_STARTPOINTS.getInt());
					}

					players.put(nPlayer.getName().toLowerCase(), nPlayer);
				}
			}
			catch(SQLException e) {
				LoggerUtils.exception(e);
			}
		}

		LoggerUtils.info("Loaded " + players.size() + " players.");
	}
	
	//add a player
	private void add(Player player) {
		NovaPlayer nPlayer = NovaPlayer.fromPlayer(player);

		if(plugin.getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
			plugin.getFlatDataManager().add(nPlayer);
			players.put(player.getName().toLowerCase(), nPlayer);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				PreparedStatement statement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.PLAYERS_INSERT);
				UUID uuid = player.getUniqueId();
				String playername = player.getName();

				if(!statement.isClosed()) {
					statement.setString(1, uuid.toString());
					statement.setString(2, playername);
					statement.setInt(3, Config.KILLING_STARTPOINTS.getInt());
					statement.executeUpdate();

					ResultSet keys = statement.getGeneratedKeys();
					int id = 0;
					if(keys.next()) {
						id = keys.getInt(1);
					}

					nPlayer.setId(id);
					nPlayer.setUnchanged();

					LoggerUtils.info("New player " + player.getName() + " added to the database");
					players.put(player.getName().toLowerCase(), nPlayer);
				}
				else {
					LoggerUtils.error("Statement is closed.");
				}
			}
			catch(SQLException e) {
				LoggerUtils.exception(e);
			}
		}
	}

	public void delete(int id) {
		if(plugin.getConfigManager().getDataStorageType() != DataStorageType.FLAT) {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("Connection is not estabilished, stopping current action");
				return;
			}

			plugin.getDatabaseManager().mysqlReload();

			try {
				PreparedStatement statement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.PLAYERS_DELETE);
				statement.setInt(1, id);
				statement.executeUpdate();

			}
			catch(SQLException e) {
				LoggerUtils.exception(e);
			}
		}
	}

	private void addIfNotExists(String playername) {
		Player player = plugin.getServer().getPlayerExact(playername);
		addIfNotExists(player);
	}

	public void addIfNotExists(Player player) {
		if(player != null) {
			if(!players.containsKey(player.getName().toLowerCase())) {
				add(player);
			}
		}
	}

	public void updateUUID(NovaPlayer nPlayer) {
		if(nPlayer.isOnline()) {
			if(!nPlayer.getUUID().toString().equals(nPlayer.getPlayer().getUniqueId().toString())) {
				nPlayer.setUUID(nPlayer.getPlayer().getUniqueId());
				LoggerUtils.info("UUID updated for player " + nPlayer.getName());
			}
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

	private NovaPlayer playerFromResult(ResultSet res) {
		try {
			String playerName = res.getString("name");

			//Doubled players
			if(players.containsKey(playerName)) {
				if(Config.DELETEINVALID.getBoolean()) {
					delete(res.getInt("id"));
					LoggerUtils.info("Removed doubled player: " + playerName);
				}
				else {
					LoggerUtils.error("Doubled player: " + playerName);
				}

				return null;
			}

			NovaPlayer nPlayer = new NovaPlayer();
			Player player = plugin.getServer().getPlayerExact(playerName);

			if(player != null && player.isOnline()) {
				nPlayer.setPlayer(player);
			}

			String guildName = res.getString("guild").toLowerCase();

			String invitedTo = res.getString("invitedto");
			List<String> invitedToListNames = StringUtils.semicolonToList(invitedTo);
			List<NovaGuild> invitedToList = plugin.getGuildManager().nameListToGuildsList(invitedToListNames);

			UUID uuid = UUID.fromString(res.getString("uuid"));

			nPlayer.setUUID(uuid);
			nPlayer.setId(res.getInt("id"));
			nPlayer.setName(playerName);
			nPlayer.setInvitedTo(invitedToList);

			nPlayer.setPoints(res.getInt("points"));
			nPlayer.setKills(res.getInt("kills"));
			nPlayer.setDeaths(res.getInt("deaths"));

			if(!guildName.isEmpty()) {
				NovaGuild guild = plugin.getGuildManager().getGuildByName(guildName);

				if(guild != null) {
					guild.addPlayer(nPlayer);
				}
			}

			nPlayer.setUnchanged();

			return nPlayer;
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return null;
	}

	private NovaPlayer playerFromFlat(FileConfiguration playerData) {
		NovaPlayer nPlayer = null;
		if(playerData != null) {
			nPlayer = new NovaPlayer();
			Player player = plugin.getServer().getPlayerExact(playerData.getString("name"));

			if(player != null) {
				if(player.isOnline()) {
					nPlayer.setPlayer(player);
				}
			}

			String guildname = playerData.getString("guild").toLowerCase();

			UUID uuid = UUID.fromString(playerData.getString("uuid"));

			nPlayer.setUUID(uuid);
			nPlayer.setName(playerData.getString("name"));
			List<NovaGuild> invitedToList = plugin.getGuildManager().nameListToGuildsList(playerData.getStringList("invitedto"));
			nPlayer.setInvitedTo(invitedToList);

			nPlayer.setPoints(playerData.getInt("points"));
			nPlayer.setKills(playerData.getInt("kills"));
			nPlayer.setDeaths(playerData.getInt("deaths"));

			if(!guildname.isEmpty()) {
				NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

				if(guild != null) {
					guild.addPlayer(nPlayer);
				}
			}

			nPlayer.setUnchanged();
		}
		return nPlayer;
	}

	public void sendPlayerInfo(CommandSender sender, NovaPlayer nCPlayer) {
		Map<String, String> vars = new HashMap<>();
		vars.put("PLAYERNAME", nCPlayer.getName());
		vars.put("POINTS", String.valueOf(nCPlayer.getPoints()));
		vars.put("KILLS", String.valueOf(nCPlayer.getKills()));
		vars.put("DEATHS", String.valueOf(nCPlayer.getDeaths()));
		vars.put("KDR", String.valueOf(nCPlayer.getKillDeathRate()));

		String guildRow = "";
		if(nCPlayer.hasGuild()) {
			vars.put("GUILDNAME", nCPlayer.getGuild().getName());
			vars.put("TAG", nCPlayer.getGuild().getTag());
			guildRow = Message.CHAT_PLAYER_INFO_GUILDROW.vars(vars).get();
		}

		vars.put("GUILDROW", guildRow);

		Message.CHAT_PLAYER_INFO_HEADER.send(sender);

		for(String row : Message.CHAT_PLAYER_INFO_ITEMS.getList()) {
			if(!row.contains("{GUILDROW}") || nCPlayer.hasGuild()) {
				row = MessageManager.replaceMap(row, vars);
				MessageManager.sendMessage(sender, row);
			}
		}
	}
}
