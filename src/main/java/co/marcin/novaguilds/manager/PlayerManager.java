package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("deprecation")
public class PlayerManager {
	private final NovaGuilds plugin;
	private final HashMap<String,NovaPlayer> players = new HashMap<>();
	
	public PlayerManager(NovaGuilds pl) {
		plugin = pl;
	}
	
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
	
	private void savePlayer(NovaPlayer nPlayer) {
		if(nPlayer.isChanged()) { //only if there were changes
			if(plugin.getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
				plugin.getFlatDataManager().savePlayer(nPlayer);
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
	
	public void saveAll() {
		for(NovaPlayer nPlayer : getPlayers()) {
			savePlayer(nPlayer);
		}
	}
	
	//load
	public void loadPlayers() {
		players.clear();
		if(plugin.getConfigManager().getDataStorageType()== DataStorageType.FLAT) {
			for(String playerName : plugin.getFlatDataManager().getPlayerList()) {
				FileConfiguration playerData = plugin.getFlatDataManager().getPlayerData(playerName);
				NovaPlayer nPlayer = playerFromFlat(playerData);

				if(nPlayer != null) {
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
				players.clear();
				ResultSet res = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.PLAYERS_SELECT).executeQuery();
				while(res.next()) {
					players.put(res.getString("name").toLowerCase(), playerFromResult(res));
				}
			}
			catch(SQLException e) {
				LoggerUtils.exception(e);
			}
		}

		LoggerUtils.info("Loaded "+players.size()+" players.");
    }
	
	//add a player
	private void addPlayer(Player player) {
		NovaPlayer nPlayer = NovaPlayer.fromPlayer(player);

		if(plugin.getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
			plugin.getFlatDataManager().addPlayer(nPlayer);
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
					statement.setInt(3, 0); //TODO points from config
					statement.executeUpdate();

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

	private void addIfNotExists(String playername) {
		Player player = plugin.getServer().getPlayerExact(playername);
		addIfNotExists(player);
	}

	public void addIfNotExists(Player player) {
		if(player != null) {
			if(!players.containsKey(player.getName().toLowerCase())) {
				addPlayer(player);
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
		NovaPlayer nPlayer = null;
		try {
			nPlayer = new NovaPlayer();
			Player player = plugin.getServer().getPlayerExact(res.getString("name"));

			if(player != null) {
				if(player.isOnline()) {
					nPlayer.setPlayer(player);
				}
			}

			String guildname = res.getString("guild").toLowerCase();

			String invitedTo = res.getString("invitedto");
			List<String> invitedToListNames = StringUtils.semicolonToList(invitedTo);
			List<NovaGuild> invitedToList = plugin.getGuildManager().nameListToGuildsList(invitedToListNames);

			UUID uuid = UUID.fromString(res.getString("uuid"));

			nPlayer.setUUID(uuid);
			nPlayer.setName(res.getString("name"));
			nPlayer.setInvitedTo(invitedToList);

			nPlayer.setPoints(res.getInt("points"));
			nPlayer.setKills(res.getInt("kills"));
			nPlayer.setDeaths(res.getInt("deaths"));

			if(!guildname.isEmpty()) {
				NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);

				if(guild != null) {
					guild.addPlayer(nPlayer);
					nPlayer.setGuild(guild);
				}
			}

			nPlayer.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return nPlayer;
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
					nPlayer.setGuild(guild);
				}
			}

			nPlayer.setUnchanged();
		}
		return nPlayer;
	}

	public void sendPlayerInfo(CommandSender sender, NovaPlayer nCPlayer) {
		HashMap<String, String> vars = new HashMap<>();
		vars.put("PLAYERNAME", nCPlayer.getName());
		vars.put("POINTS", String.valueOf(nCPlayer.getPoints()));
		vars.put("KILLS", String.valueOf(nCPlayer.getKills()));
		vars.put("DEATHS", String.valueOf(nCPlayer.getDeaths()));
		vars.put("KDR", String.valueOf(nCPlayer.getKills() / (nCPlayer.getDeaths() == 0 ? 1 : nCPlayer.getDeaths())));

		if(nCPlayer.hasGuild()) {
			vars.put("GUILDNAME", nCPlayer.getGuild().getName());
			vars.put("TAG", nCPlayer.getGuild().getTag());
		}

		Message.CHAT_PLAYER_INFO_HEADER.send(sender);
		Message.CHAT_PLAYER_INFO_ITEMS.list().vars(vars).send(sender);
	}
}
