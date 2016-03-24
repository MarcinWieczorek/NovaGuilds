package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerPlayerImpl extends AbstractDatabaseResourceManager<NovaPlayer> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerPlayerImpl(Storage storage) {
		super(storage, NovaPlayer.class);
	}

	@Override
	public List<NovaPlayer> load() {
		getStorage().connect();
		List<NovaPlayer> list = new ArrayList<>();

		try {
			ResultSet res = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_SELECT).executeQuery();
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
	public boolean save(NovaPlayer nPlayer) {
		if(!nPlayer.isChanged()) {
			return false;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_UPDATE);

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

		return true;
	}

	@Override
	public void add(NovaPlayer nPlayer) {
		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_INSERT);

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

			nPlayer.setId(getStorage().returnGeneratedKey(preparedStatement));
			nPlayer.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void remove(NovaPlayer nPlayer) {
		getStorage().connect();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.PLAYERS_DELETE);
			statement.setInt(1, nPlayer.getId());
			statement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}
}
