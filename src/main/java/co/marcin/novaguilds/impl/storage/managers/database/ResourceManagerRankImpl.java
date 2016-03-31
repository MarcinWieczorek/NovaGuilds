package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.json.JSONArray;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagerRankImpl extends AbstractDatabaseResourceManager<NovaRank> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerRankImpl(Storage storage) {
		super(storage, NovaRank.class);
	}

	@Override
	public List<NovaRank> load() {
		getStorage().connect();
		List<NovaRank> list = new ArrayList<>();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.RANKS_SELECT);

			ResultSet res = statement.executeQuery();
			while(res.next()) {
				boolean fixPlayerList = false;
				NovaRank rank = new NovaRankImpl(res.getInt("id"));
				rank.setAdded();

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

	@Override
	public boolean save(NovaRank rank) {
		if(!rank.isChanged()) {
			return false;
		}

		if(!rank.isAdded()) {
			add(rank);
			return true;
		}

		getStorage().connect();

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
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.RANKS_UPDATE);
			preparedStatement.setString(1, rank.getName());
			preparedStatement.setString(2, rank.getGuild().getName());
			preparedStatement.setString(3, new JSONArray(permissionNamesList).toString());
			preparedStatement.setString(4, new JSONArray(memberNamesList).toString());
			preparedStatement.setBoolean(5, rank.isDefault());
			preparedStatement.setBoolean(6, rank.isClone());

			preparedStatement.setInt(7, rank.getId());
			preparedStatement.execute();

			rank.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}

		return true;
	}

	@Override
	public void add(NovaRank rank) {
		getStorage().connect();

		try {
			List<String> memberNamesList = new ArrayList<>();
			for(NovaPlayer nPlayer : rank.getMembers()) {
				memberNamesList.add(nPlayer.getName());
			}

			List<String> permissionNamesList = new ArrayList<>();
			for(GuildPermission permission : rank.getPermissions()) {
				permissionNamesList.add(permission.name());
			}

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.RANKS_INSERT);
			preparedStatement.setString(1, rank.getName());
			preparedStatement.setString(2, rank.getGuild().getName());
			preparedStatement.setString(3, new JSONArray(permissionNamesList).toString());
			preparedStatement.setString(4, new JSONArray(memberNamesList).toString());
			preparedStatement.setBoolean(5, rank.isDefault());
			preparedStatement.setBoolean(6, rank.isClone());
			preparedStatement.execute();

			rank.setId(getStorage().returnGeneratedKey(preparedStatement));
			rank.setUnchanged();
			rank.setAdded();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void remove(NovaRank rank) {
		if(!rank.isAdded()) {
			return;
		}

		getStorage().connect();

		try {
			if(rank.isAdded()) {
				PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.RANKS_DELETE);
				preparedStatement.setInt(1, rank.getId());
				preparedStatement.execute();
			}
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}
}
