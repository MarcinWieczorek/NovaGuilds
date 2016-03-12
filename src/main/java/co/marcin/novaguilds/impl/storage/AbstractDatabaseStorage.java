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

package co.marcin.novaguilds.impl.storage;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.Database;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.IOUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.tableanalyzer.TableAnalyzer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDatabaseStorage extends AbstractStorage implements Database {
	protected Connection connection;
	protected boolean firstConnect = true;
	private final Map<PreparedStatements, PreparedStatement> preparedStatementMap = new HashMap<>();

	@Override
	public boolean checkConnection() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	@Override
	public final Connection getConnection() {
		return connection;
	}

	@Override
	public boolean closeConnection() throws SQLException {
		if(connection == null) {
			return false;
		}

		connection.close();
		return true;
	}

	/**
	 * Reconnects
	 *
	 * @return true if success
	 */
	public abstract boolean connect();

	/**
	 * Returns generated key (id)
	 *
	 * @param statement The statement
	 * @return Generated id
	 */
	public abstract Integer returnGeneratedKey(Statement statement);

	public abstract boolean isStatementReturnGeneratedKeysSupported();

	@Override
	public boolean setUp() {
		return connect();
	}

	//Loading
	@Override
	public List<NovaPlayer> loadPlayers() {
		connect();
		List<NovaPlayer> list = new ArrayList<>();

		try {
			ResultSet res = getPreparedStatement(PreparedStatements.PLAYERS_SELECT).executeQuery();
			while(res.next()) {
				String playerName = res.getString("name");

				UUID uuid = UUID.fromString(res.getString("uuid"));
				NovaPlayer nPlayer = new NovaPlayerImpl(uuid);

				Player player = Bukkit.getPlayer(uuid);
				if(player != null && player.isOnline()) {
					nPlayer.setPlayer(player);
				}

				String invitedTo = res.getString("invitedto");
				List<String> invitedToListNames = StringUtils.semicolonToList(invitedTo);
				List<NovaGuild> invitedToList = plugin.getGuildManager().nameListToGuildsList(invitedToListNames);

				nPlayer.setId(res.getInt("id"));
				nPlayer.setName(playerName);
				nPlayer.setInvitedTo(invitedToList);

				nPlayer.setPoints(res.getInt("points"));
				nPlayer.setKills(res.getInt("kills"));
				nPlayer.setDeaths(res.getInt("deaths"));

				String guildName = res.getString("guild").toLowerCase();
				if(!guildName.isEmpty()) {
					NovaGuild guild = GuildManager.getGuildByName(guildName);

					if(guild != null) {
						guild.addPlayer(nPlayer);
					}
				}

				nPlayer.setUnchanged();

				if(nPlayer.getPoints() == 0 && nPlayer.getKills() == 0 && nPlayer.getDeaths() == 0) {
					nPlayer.setPoints(Config.KILLING_STARTPOINTS.getInt());
				}

				list.add(nPlayer);
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public List<NovaGuild> loadGuilds() {
		connect();
		List<NovaGuild> list = new ArrayList<>();

		try {
			PreparedStatement statement = getPreparedStatement(PreparedStatements.GUILDS_SELECT);
			ResultSet res = statement.executeQuery();

			while(res.next()) {
				String homeCoordinates = res.getString("spawn");

				Location homeLocation = null;
				if(!homeCoordinates.isEmpty()) {
					String[] homeSplit = org.apache.commons.lang.StringUtils.split(homeCoordinates, ';');
					if(homeSplit.length == 5) {
						String worldName = homeSplit[0];
						World world = plugin.getServer().getWorld(worldName);

						if(world != null) {
							int x = Integer.parseInt(homeSplit[1]);
							int y = Integer.parseInt(homeSplit[2]);
							int z = Integer.parseInt(homeSplit[3]);
							float yaw = Float.parseFloat(homeSplit[4]);
							homeLocation = new Location(world, x, y, z);
							homeLocation.setYaw(yaw);
						}
					}
				}

				String vaultLocationString = res.getString("bankloc");
				Location vaultLocation = null;
				if(!vaultLocationString.isEmpty()) {
					String[] vaultLocationSplit = vaultLocationString.split(";");
					if(vaultLocationSplit.length == 5) { //LENGTH
						String worldName = vaultLocationSplit[0];
						World world = plugin.getServer().getWorld(worldName);

						if(world != null) {
							int x = Integer.parseInt(vaultLocationSplit[1]);
							int y = Integer.parseInt(vaultLocationSplit[2]);
							int z = Integer.parseInt(vaultLocationSplit[3]);
							vaultLocation = new Location(world, x, y, z);
						}
					}
				}

				//load guild only if there is a spawnpoint.
				//error protection if a world has been deleted
				if(homeLocation == null) {
					LoggerUtils.info("Failed loading guild " + res.getString("name") + ", world does not exist");
				}

				List<String> allies = new ArrayList<>();
				List<String> allyInvitationList = new ArrayList<>();
				List<String> wars = new ArrayList<>();
				List<String> noWarInvitationList = new ArrayList<>();

				if(!res.getString("allies").isEmpty()) {
					allies = StringUtils.semicolonToList(res.getString("allies"));
				}

				if(!res.getString("alliesinv").isEmpty()) {
					allyInvitationList = StringUtils.semicolonToList(res.getString("alliesinv"));
				}

				if(!res.getString("war").isEmpty()) {
					wars = StringUtils.semicolonToList(res.getString("war"));
				}

				if(!res.getString("nowarinv").isEmpty()) {
					noWarInvitationList = StringUtils.semicolonToList(res.getString("nowarinv"));
				}

				UUID stringUUID = UUID.nameUUIDFromBytes(("Guild: " + res.getString("name")).getBytes(Charset.forName("UTF-8"))); //TODO uuid field
				NovaGuild guild = new NovaGuildImpl(stringUUID);
				guild.setId(res.getInt("id"));
				guild.setMoney(res.getDouble("money"));
				guild.setPoints(res.getInt("points"));
				guild.setName(res.getString("name"));
				guild.setTag(res.getString("tag"));
				guild.setLeaderName(res.getString("leader"));
				guild.setLives(res.getInt("lives"));
				guild.setTimeRest(res.getLong("timerest"));
				guild.setLostLiveTime(res.getLong("lostlive"));
				guild.setHome(homeLocation);
				guild.setVaultLocation(vaultLocation);
				guild.setSlots(res.getInt("slots"));

				guild.setAlliesNames(allies);
				guild.setAllyInvitationNames(allyInvitationList);

				guild.setWarsNames(wars);
				guild.setNoWarInvitations(noWarInvitationList);
				guild.setInactiveTime(res.getLong("activity"));
				guild.setTimeCreated(res.getLong("created"));
				guild.setOpenInvitation(res.getBoolean("openinv"));

				//set unchanged
				guild.setUnchanged();

				if(guild.getRegion() != null) {
					guild.getRegion().setUnchanged();
				}

				//Fix slots amount
				if(guild.getSlots() <= 0) {
					guild.setSlots(Config.GUILD_SLOTS_START.getInt());
				}

				if(guild.getId() == 0) {
					LoggerUtils.info("Failed to load guild " + res.getString("name") + ". Invalid ID");
					continue;
				}

				list.add(guild);
			}
		}
		catch(SQLException e) {
			LoggerUtils.info("An error occured while loading guilds!");
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public List<NovaRegion> loadRegions() {
		connect();
		List<NovaRegion> list = new ArrayList<>();

		try {
			PreparedStatement statement = getPreparedStatement(PreparedStatements.REGIONS_SELECT);

			ResultSet res = statement.executeQuery();
			while(res.next()) {
				World world = plugin.getServer().getWorld(res.getString("world"));
				String guildName = res.getString("guild");
				NovaGuild guild = GuildManager.getGuildFind(guildName);

				if(guild == null) {
					LoggerUtils.error("There's no guild matching region " + guildName);
					continue;
				}

				if(world != null) {
					NovaRegion region = new NovaRegionImpl();

					String loc1 = res.getString("loc_1");
					String[] loc1_split = loc1.split(";");

					String loc2 = res.getString("loc_2");
					String[] loc2_split = loc2.split(";");

					Location c1 = new Location(world, Integer.parseInt(loc1_split[0]), 0, Integer.parseInt(loc1_split[1]));
					Location c2 = new Location(world, Integer.parseInt(loc2_split[0]), 0, Integer.parseInt(loc2_split[1]));

					region.setCorner(0, c1);
					region.setCorner(1, c2);
					region.setWorld(world);
					region.setId(res.getInt("id"));
					guild.setRegion(region);
					region.setUnchanged();

					list.add(region);
				}
				else {
					LoggerUtils.info("Failed loading region for guild " + res.getString("guild") + ", world does not exist.");
				}
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return list;
	}

	@Override
	public List<NovaRank> loadRanks() {
		connect();
		List<NovaRank> list = new ArrayList<>();

		try {
			PreparedStatement statement = getPreparedStatement(PreparedStatements.RANKS_SELECT);

			ResultSet res = statement.executeQuery();
			while(res.next()) {
				boolean fixPlayerList = false;
				NovaRank rank = new NovaRankImpl(res.getInt("id"));

				NovaGuild guild = GuildManager.getGuildByName(res.getString("guild"));

				if(guild == null) {
					LoggerUtils.error("Failed to find guild: " + res.getString("name"));
					continue;
				}

				rank.setName(res.getString("name"));
				rank.setGuild(guild);

				for(String permName : StringUtils.jsonToList(res.getString("permissions"))) {
					rank.addPermission(GuildPermission.valueOf(permName));
				}

				for(String playerName : StringUtils.jsonToList(res.getString("members"))) {
					NovaPlayer nPlayer = PlayerManager.getPlayer(playerName);

					if(nPlayer == null) {
						LoggerUtils.error("Player " + playerName + " doesn't exist, cannot be added to rank '" + rank.getName() + "' of guild " + rank.getGuild().getName());
						fixPlayerList = true;
						continue;
					}

					rank.addMember(nPlayer);
				}

				rank.setDefault(res.getBoolean("def"));
				rank.setClone(res.getBoolean("clone"));

				if(!fixPlayerList) {
					rank.setUnchanged();
				}

				list.add(rank);
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return list;
	}

	//Adding
	@Override
	public void add(NovaPlayer nPlayer) {
		connect();

		try {
			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.PLAYERS_INSERT);

			List<String> invitedToNames = new ArrayList<>();
			for(NovaGuild guild : nPlayer.getInvitedTo()) {
				invitedToNames.add(guild.getName());
			}

			String invitedTo = StringUtils.join(invitedToNames, ";");

			//Prepare and execute
			preparedStatement.setString(1, nPlayer.getUUID().toString());
			preparedStatement.setString(2, nPlayer.getName());
			preparedStatement.setString(3, nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
			preparedStatement.setString(4, invitedTo);
			preparedStatement.setInt(5, nPlayer.getPoints());
			preparedStatement.setInt(6, nPlayer.getKills());
			preparedStatement.setInt(7, nPlayer.getDeaths());
			preparedStatement.executeUpdate();

			nPlayer.setId(returnGeneratedKey(preparedStatement));
			nPlayer.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void add(NovaGuild guild) {
		connect();

		try {
			String homeLocationString = StringUtils.parseDBLocation(guild.getHome());
			String vaultLocationString = StringUtils.parseDBLocation(guild.getVaultLocation());

			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.GUILDS_INSERT);
			preparedStatement.setString(1, guild.getTag()); //tag
			preparedStatement.setString(2, guild.getName()); //name
			preparedStatement.setString(3, guild.getLeader().getName()); //leader
			preparedStatement.setString(4, homeLocationString); //home
			preparedStatement.setString(5, serializeNovaGuildList(guild.getAllies()));
			preparedStatement.setString(6, serializeNovaGuildList(guild.getAllyInvitations()));
			preparedStatement.setString(7, serializeNovaGuildList(guild.getWars()));
			preparedStatement.setString(8, serializeNovaGuildList(guild.getNoWarInvitations()));
			preparedStatement.setDouble(9, guild.getMoney()); //money
			preparedStatement.setInt(10, guild.getPoints()); //points
			preparedStatement.setInt(11, guild.getLives()); //lives
			preparedStatement.setLong(12, guild.getTimeRest()); //timerest
			preparedStatement.setLong(13, guild.getLostLiveTime()); //lostlive
			preparedStatement.setLong(14, guild.getInactiveTime()); //active
			preparedStatement.setLong(15, guild.getTimeCreated()); //created
			preparedStatement.setString(16, vaultLocationString); //vault location
			preparedStatement.setInt(17, guild.getSlots()); //slots
			preparedStatement.setBoolean(18, guild.isOpenInvitation()); //openinv

			preparedStatement.execute();

			guild.setId(returnGeneratedKey(preparedStatement));
			guild.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while adding a guild!");
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void add(NovaRegion region) {
		connect();

		try {
			String loc1 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(0));
			String loc2 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(1));

			if(region.getWorld() == null) {
				region.setWorld(plugin.getServer().getWorlds().get(0));
			}

			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.REGIONS_INSERT);
			preparedStatement.setString(1, loc1);
			preparedStatement.setString(2, loc2);
			preparedStatement.setString(3, region.getGuild().getName());
			preparedStatement.setString(4, region.getWorld().getName());
			preparedStatement.executeUpdate();

			region.setId(returnGeneratedKey(preparedStatement));
			region.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void add(NovaRank rank) {
		connect();

		try {
			List<String> memberNamesList = new ArrayList<>();
			for(NovaPlayer nPlayer : rank.getMembers()) {
				memberNamesList.add(nPlayer.getName());
			}

			List<String> permissionNamesList = new ArrayList<>();
			for(GuildPermission permission : rank.getPermissions()) {
				permissionNamesList.add(permission.name());
			}

			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.RANKS_INSERT);
			preparedStatement.setString(1, rank.getName());
			preparedStatement.setString(2, rank.getGuild().getName());
			preparedStatement.setString(3, JSONArray.toJSONString(permissionNamesList));
			preparedStatement.setString(4, JSONArray.toJSONString(memberNamesList));
			preparedStatement.setBoolean(5, rank.isDefault());
			preparedStatement.setBoolean(6, rank.isClone());
			preparedStatement.execute();

			rank.setId(returnGeneratedKey(preparedStatement));
			rank.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	//Saving
	@Override
	public void save(NovaPlayer nPlayer) {
		if(!nPlayer.isChanged()) {
			return;
		}

		connect();

		try {
			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.PLAYERS_UPDATE);

			//prepare data
			List<String> invitedToNames = new ArrayList<>();
			for(NovaGuild guild : nPlayer.getInvitedTo()) {
				invitedToNames.add(guild.getName());
			}

			String joined = StringUtils.join(invitedToNames, ";");

			//prepare and save
			preparedStatement.setString(1, joined);
			preparedStatement.setString(2, nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
			preparedStatement.setInt(3, nPlayer.getPoints());
			preparedStatement.setInt(4, nPlayer.getKills());
			preparedStatement.setInt(5, nPlayer.getDeaths());
			preparedStatement.setString(6, nPlayer.getUUID().toString());
			preparedStatement.executeUpdate();
			nPlayer.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void save(NovaGuild guild) {
		if(!guild.isChanged()) {
			return;
		}

		connect();

		try {
			String homeCoordinates = StringUtils.parseDBLocation(guild.getHome());
			String vaultLocationString = StringUtils.parseDBLocation(guild.getVaultLocation());

			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.GUILDS_UPDATE);

			preparedStatement.setString(1, guild.getTag());
			preparedStatement.setString(2, guild.getName());
			preparedStatement.setString(3, guild.getLeader().getName());
			preparedStatement.setString(4, homeCoordinates);
			preparedStatement.setString(5, serializeNovaGuildList(guild.getAllies()));
			preparedStatement.setString(6, serializeNovaGuildList(guild.getAllyInvitations()));
			preparedStatement.setString(7, serializeNovaGuildList(guild.getWars()));
			preparedStatement.setString(8, serializeNovaGuildList(guild.getNoWarInvitations()));
			preparedStatement.setDouble(9, guild.getMoney());
			preparedStatement.setInt(10, guild.getPoints());
			preparedStatement.setInt(11, guild.getLives());
			preparedStatement.setLong(12, guild.getTimeRest());
			preparedStatement.setLong(13, guild.getLostLiveTime());
			preparedStatement.setLong(14, guild.getInactiveTime());
			preparedStatement.setString(15, vaultLocationString);
			preparedStatement.setInt(16, guild.getSlots());
			preparedStatement.setBoolean(17, guild.isOpenInvitation());

			preparedStatement.setInt(18, guild.getId());

			preparedStatement.executeUpdate();
			guild.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while saving a guild.");
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void save(NovaRegion region) {
		if(!region.isChanged()) {
			return;
		}

		connect();

		try {
			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.REGIONS_UPDATE);

			String loc1 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(0));
			String loc2 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(1));

			preparedStatement.setString(1, loc1);
			preparedStatement.setString(2, loc2);
			preparedStatement.setString(3, region.getGuild().getName());
			preparedStatement.setString(4, region.getWorld().getName());
			preparedStatement.setInt(5, region.getId());
			preparedStatement.executeUpdate();

			region.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void save(NovaRank rank) {
		if(rank.isNew()) {
			add(rank);
			return;
		}

		if(!rank.isChanged()) {
			return;
		}

		connect();

		//Permission list
		List<String> permissionNamesList = new ArrayList<>();
		for(GuildPermission permission : rank.getPermissions()) {
			permissionNamesList.add(permission.name());
		}

		//Member list
		List<String> memberNamesList = new ArrayList<>();
		if(!rank.isDefault()) {
			for(NovaPlayer nPlayer : rank.getMembers()) {
				memberNamesList.add(nPlayer.getName());
			}
		}

		try {
			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.RANKS_UPDATE);
			preparedStatement.setString(1, rank.getName());
			preparedStatement.setString(2, rank.getGuild().getName());
			preparedStatement.setString(3, JSONArray.toJSONString(permissionNamesList));
			preparedStatement.setString(4, JSONArray.toJSONString(memberNamesList));
			preparedStatement.setBoolean(5, rank.isDefault());
			preparedStatement.setBoolean(6, rank.isClone());

			preparedStatement.setInt(7, rank.getId());
			preparedStatement.execute();

			rank.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	//Removing
	@Override
	public void remove(NovaPlayer nPlayer) {
		connect();

		try {
			PreparedStatement statement = getPreparedStatement(PreparedStatements.PLAYERS_DELETE);
			statement.setInt(1, nPlayer.getId());
			statement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void remove(NovaGuild guild) {
		connect();

		try {
			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.GUILDS_DELETE);
			preparedStatement.setInt(1, guild.getId());
			preparedStatement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while deleting a guild.");
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void remove(NovaRegion region) {
		connect();

		try {
			PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.REGIONS_DELETE);
			preparedStatement.setInt(1, region.getId());
			preparedStatement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.info("An error occured while deleting a guild's region (" + region.getGuild().getName() + ")");
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void remove(NovaRank rank) {
		connect();

		try {
			if(!rank.isNew()) {
				PreparedStatement preparedStatement = getPreparedStatement(PreparedStatements.RANKS_DELETE);
				preparedStatement.setInt(1, rank.getId());
				preparedStatement.execute();
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Prepares the statements
	 */
	protected void prepareStatements() {
		try {
			long nanoTime = System.nanoTime();
			LoggerUtils.info("Preparing statements...");
			preparedStatementMap.clear();
			connect();

			int returnKeys = isStatementReturnGeneratedKeysSupported() ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;

			//Guilds insert (id, tag, name, leader, spawn, allies, alliesinv, war, nowarinv, money, points, lives, timerest, lostlive, activity, created, bankloc, slots, openinv)
			String guildsInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "guilds` VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement guildsInsert = getConnection().prepareStatement(guildsInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.GUILDS_INSERT, guildsInsert);

			//Guilds select
			String guildsSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "guilds`";
			PreparedStatement guildsSelect = getConnection().prepareStatement(guildsSelectSQL);
			preparedStatementMap.put(PreparedStatements.GUILDS_SELECT, guildsSelect);

			//Guilds delete
			String guildsDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "guilds` WHERE `id`=?";
			PreparedStatement guildsDelete = getConnection().prepareStatement(guildsDeleteSQL);
			preparedStatementMap.put(PreparedStatements.GUILDS_DELETE, guildsDelete);

			//Guilds update
			String guildsUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "guilds` SET `tag`=?, `name`=?, `leader`=?, `spawn`=?, `allies`=?, `alliesinv`=?, `war`=?, `nowarinv`=?, `money`=?, `points`=?, `lives`=?, `timerest`=?, `lostlive`=?, `activity`=?, `bankloc`=?, `slots`=?, `openinv`=? WHERE `id`=?";
			PreparedStatement guildsUpdate = getConnection().prepareStatement(guildsUpdateSQL);
			preparedStatementMap.put(PreparedStatements.GUILDS_UPDATE, guildsUpdate);


			//Players insert (id, uuid, name, guild, invitedto, points, kills, deaths)
			String playersInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "players` VALUES(null,?,?,?,?,?,?,?)";
			PreparedStatement playersInsert = getConnection().prepareStatement(playersInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.PLAYERS_INSERT, playersInsert);

			//Players select
			String playerSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "players`";
			PreparedStatement playersSelect = getConnection().prepareStatement(playerSelectSQL);
			preparedStatementMap.put(PreparedStatements.PLAYERS_SELECT, playersSelect);

			//Players update
			String playersUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "players` SET `invitedto`=?, `guild`=?, `points`=?, `kills`=?, `deaths`=? WHERE `uuid`=?";
			PreparedStatement playersUpdate = getConnection().prepareStatement(playersUpdateSQL);
			preparedStatementMap.put(PreparedStatements.PLAYERS_UPDATE, playersUpdate);

			//Players delete
			String playersDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "players` WHERE `id`=?";
			PreparedStatement playersDelete = getConnection().prepareStatement(playersDeleteSQL);
			preparedStatementMap.put(PreparedStatements.PLAYERS_DELETE, playersDelete);


			//Regions insert (id, loc_1, loc_2, guild, world)
			String regionsInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "regions` VALUES(null,?,?,?,?);";
			PreparedStatement regionsInsert = getConnection().prepareStatement(regionsInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.REGIONS_INSERT, regionsInsert);

			//Regions select
			String regionsSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "regions`";
			PreparedStatement regionsSelect = getConnection().prepareStatement(regionsSelectSQL);
			preparedStatementMap.put(PreparedStatements.REGIONS_SELECT, regionsSelect);

			//Regions delete
			String regionsDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "regions` WHERE `id`=?";
			PreparedStatement regionsDelete = getConnection().prepareStatement(regionsDeleteSQL);
			preparedStatementMap.put(PreparedStatements.REGIONS_DELETE, regionsDelete);

			//Regions update
			String regionsUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "regions` SET `loc_1`=?, `loc_2`=?, `guild`=?, `world`=? WHERE `id`=?";
			PreparedStatement regionsUpdate = getConnection().prepareStatement(regionsUpdateSQL);
			preparedStatementMap.put(PreparedStatements.REGIONS_UPDATE, regionsUpdate);


			//Ranks insert (id, name, guild, permissions, players, default, clone)
			String ranksInsertSQL = "INSERT INTO `" + Config.MYSQL_PREFIX.getString() + "ranks` VALUES(null,?,?,?,?,?,?);";
			PreparedStatement ranksInsert = getConnection().prepareStatement(ranksInsertSQL, returnKeys);
			preparedStatementMap.put(PreparedStatements.RANKS_INSERT, ranksInsert);

			//Ranks select
			String ranksSelectSQL = "SELECT * FROM `" + Config.MYSQL_PREFIX.getString() + "ranks`";
			PreparedStatement ranksSelect = getConnection().prepareStatement(ranksSelectSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_SELECT, ranksSelect);

			//Ranks delete
			String ranksDeleteSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "ranks` WHERE `id`=?";
			PreparedStatement ranksDelete = getConnection().prepareStatement(ranksDeleteSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_DELETE, ranksDelete);

			//Ranks delete (guild)
			String ranksDeleteGuildSQL = "DELETE FROM `" + Config.MYSQL_PREFIX.getString() + "ranks` WHERE `guild`=?";
			PreparedStatement ranksDeleteGuild = getConnection().prepareStatement(ranksDeleteGuildSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_DELETE_GUILD, ranksDeleteGuild);

			//Ranks update
			String ranksUpdateSQL = "UPDATE `" + Config.MYSQL_PREFIX.getString() + "ranks` SET `name`=?, `guild`=?, `permissions`=?, `members`=?, `def`=?, `clone`=? WHERE `id`=?";
			PreparedStatement ranksUpdate = getConnection().prepareStatement(ranksUpdateSQL);
			preparedStatementMap.put(PreparedStatements.RANKS_UPDATE, ranksUpdate);

			//Log
			LoggerUtils.info("Statements prepared in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - nanoTime), TimeUnit.NANOSECONDS) / 1000.0 + "s");
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets a prepared statement
	 *
	 * @param statement the enum
	 * @return the statement
	 * @throws SQLException
	 */
	public PreparedStatement getPreparedStatement(PreparedStatements statement) throws SQLException {
		if(preparedStatementMap.isEmpty() || !preparedStatementMap.containsKey(statement)) {
			prepareStatements();
		}

		if(preparedStatementMap.get(statement) != null && !(this instanceof SQLiteStorageImpl) && preparedStatementMap.get(statement).isClosed()) {
			prepareStatements();
		}

		PreparedStatement preparedStatement = preparedStatementMap.get(statement);
		preparedStatement.clearParameters();

		return preparedStatement;
	}

	//Analyze tools
	/**
	 * Checks if tables exist in the database
	 *
	 * @return boolean
	 */
	protected boolean checkTables() {
		try {
			DatabaseMetaData md = getConnection().getMetaData();
			ResultSet rs = md.getTables(null, null, Config.MYSQL_PREFIX.getString() + "%", null);
			return rs.next();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return false;
	}

	/**
	 * Adds tables to the database
	 */
	protected void setupTables() {
		try {
			for(String tableCode : getSqlActions()) {
				Statement statement = getConnection().createStatement();
				statement.executeUpdate(tableCode);
				LoggerUtils.info("Table added to the database!");
			}
		}
		catch(SQLException e) {
			LoggerUtils.info("Could not create tables. Switching to secondary storage.");
			plugin.getConfigManager().setToSecondaryDataStorageType();
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Analyzes the database
	 */
	protected void analyze() {
		try {
			LoggerUtils.info("Analyzing the database...");
			TableAnalyzer analyzer = new TableAnalyzer(getConnection());

			for(String action : getSqlActions()) {
				if(action.contains("CREATE TABLE")) {
					String table = org.apache.commons.lang.StringUtils.split(action, '`')[1];
					analyzer.analyze(table, action);
					analyzer.update();
				}
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets an array of SQL create table codes
	 *
	 * @return the array of strings
	 */
	private String[] getSqlActions() {
		InputStream inputStream = plugin.getResource("sql/" + (plugin.getConfigManager().getDataStorageType() == DataStorageType.MYSQL ? "mysql" : "sqlite") + ".sql");
		String sqlString = IOUtils.inputStreamToString(inputStream);

		if(sqlString == null || sqlString.isEmpty() || !sqlString.contains("--")) {
			LoggerUtils.error("Invalid SQL");
			return new String[0];
		}

		sqlString = org.apache.commons.lang.StringUtils.replace(sqlString, "{SQLPREFIX}", Config.MYSQL_PREFIX.getString());
		return sqlString.split("--");
	}

	/**
	 * Serialize a list of guilds to a string of names separated by semicolons.
	 * name1;name2;name3 etc.
	 *
	 * @param list the list
	 * @return the string
	 */
	protected String serializeNovaGuildList(List<NovaGuild> list) {
		String string = "";

		if(!list.isEmpty()) {
			for(NovaGuild guild : list) {
				if(!string.equals("")) {
					string += ";";
				}

				string += guild.getName();
			}
		}

		return string;
	}
}
