package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
	
	public void addInvitation(NovaPlayer nPlayer, NovaGuild guild) {
		nPlayer.addInvitation(guild);
	}
	
	private void savePlayer(NovaPlayer nPlayer) {
		if(nPlayer.isChanged()) { //only if there were changes
			if(plugin.getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
				plugin.getFlatDataManager().savePlayer(nPlayer);
			}
			else {
				if(!plugin.getDatabaseManager().isConnected()) {
					LoggerUtils.info("[PlayerManager] Connection is not estabilished, stopping current action");
					return;
				}

				plugin.getDatabaseManager().mysqlReload();

				try {
					PreparedStatement preparedStatement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.PLAYERS_UPDATE);

					String guildname = "";
					if(nPlayer.hasGuild()) {
						guildname = nPlayer.getGuild().getName();
					}

					List<String> invitedto = nPlayer.getInvitedTo();
					String joined = StringUtils.join(invitedto, ";");

					preparedStatement.setString(1, joined);
					preparedStatement.setString(2, nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
					preparedStatement.setString(3, nPlayer.getUUID().toString());
					preparedStatement.executeUpdate();

					nPlayer.setUnchanged();
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
				LoggerUtils.info("[PlayerManager] Connection is not estabilished, stopping current action");
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

		LoggerUtils.info("[PlayerManager] Loaded "+players.size()+" players.");
    }
	
	//add a player
	private void addPlayer(Player player) {
		NovaPlayer nPlayer = NovaPlayer.fromPlayer(player);

		if(plugin.getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
			plugin.getFlatDataManager().addPlayer(nPlayer);
		}
		else {
			if(!plugin.getDatabaseManager().isConnected()) {
				LoggerUtils.info("[PlayerManager] Connection is not estabilished, stopping current action");
				return;
			}
			plugin.getDatabaseManager().mysqlReload();

			try {
				PreparedStatement statement = plugin.getDatabaseManager().getPreparedStatement(PreparedStatements.PLAYERS_INSERT);
				UUID uuid = player.getUniqueId();
				String playername = player.getName();

				statement.setString(1, uuid.toString());
				statement.setString(2, playername);
				statement.executeUpdate();
			}
			catch(SQLException e) {
				LoggerUtils.exception(e);
			}
		}

		LoggerUtils.info("New player " + player.getName() + " added to the database");
		players.put(player.getName().toLowerCase(), nPlayer);
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
				LoggerUtils.info("[PlayerManager] UUID updated for player " + nPlayer.getName());
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
			List<String> invitedToList = StringUtils.semicolonToList(invitedTo);

			UUID uuid = UUID.fromString(res.getString("uuid"));

			nPlayer.setUUID(uuid);
			nPlayer.setName(res.getString("name"));
			nPlayer.setInvitedTo(invitedToList);

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

			List<String> invitedToList = playerData.getStringList("invitedto");

			UUID uuid = UUID.fromString(playerData.getString("uuid"));

			nPlayer.setUUID(uuid);
			nPlayer.setName(playerData.getString("name"));
			nPlayer.setInvitedTo(invitedToList);

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
}
