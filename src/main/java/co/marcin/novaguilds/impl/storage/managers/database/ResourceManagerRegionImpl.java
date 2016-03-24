package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.impl.storage.AbstractDatabaseStorage;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagerRegionImpl extends AbstractDatabaseResourceManager<NovaRegion> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerRegionImpl(Storage storage) {
		super(storage, NovaRegion.class);

		if(!(storage instanceof AbstractDatabaseStorage)) {
			throw new IllegalArgumentException("Invalid storage type");
		}
	}

	@Override
	public List<NovaRegion> load() {
		getStorage().connect();
		List<co.marcin.novaguilds.api.basic.NovaRegion> list = new ArrayList<>();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_SELECT);

			ResultSet res = statement.executeQuery();
			while(res.next()) {
				World world = Bukkit.getWorld(res.getString("world"));
				String guildName = res.getString("guild");
				NovaGuild guild = GuildManager.getGuildFind(guildName);

				if(guild == null) {
					LoggerUtils.error("There's no guild matching region " + guildName);
					continue;
				}

				if(world != null) {
					co.marcin.novaguilds.api.basic.NovaRegion region = new NovaRegionImpl();

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
	public boolean save(NovaRegion region) {
		if(!region.isChanged()) {
			return false;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_UPDATE);

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

		return true;
	}

	@Override
	public void add(NovaRegion region) {
		getStorage().connect();

		try {
			String loc1 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(0));
			String loc2 = StringUtils.parseDBLocationCoordinates2D(region.getCorner(1));

			if(region.getWorld() == null) {
				region.setWorld(Bukkit.getWorlds().get(0));
			}

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_INSERT);
			preparedStatement.setString(1, loc1);
			preparedStatement.setString(2, loc2);
			preparedStatement.setString(3, region.getGuild().getName());
			preparedStatement.setString(4, region.getWorld().getName());
			preparedStatement.executeUpdate();

			region.setId(getStorage().returnGeneratedKey(preparedStatement));
			region.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void remove(NovaRegion region) {
		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.REGIONS_DELETE);
			preparedStatement.setInt(1, region.getId());
			preparedStatement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.info("An error occured while deleting a guild's region (" + region.getGuild().getName() + ")");
			LoggerUtils.exception(e);
		}
	}
}
