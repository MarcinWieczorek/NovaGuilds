package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.PreparedStatements;
import co.marcin.novaguilds.impl.basic.NovaGuildImpl;
import co.marcin.novaguilds.util.BannerUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerGuildImpl extends AbstractDatabaseResourceManager<NovaGuild> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerGuildImpl(Storage storage) {
		super(storage, NovaGuild.class);
	}

	@Override
	public List<NovaGuild> load() {
		getStorage().connect();
		List<NovaGuild> list = new ArrayList<>();

		try {
			PreparedStatement statement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_SELECT);
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
				guild.setAdded();
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
				guild.setBannerMeta(BannerUtils.deserialize(res.getString("banner")));

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
	public boolean save(NovaGuild guild) {
		if(!guild.isChanged()) {
			return false;
		}

		if(!guild.isAdded()) {
			add(guild);
			return true;
		}

		getStorage().connect();

		try {
			String homeCoordinates = StringUtils.parseDBLocation(guild.getHome());
			String vaultLocationString = StringUtils.parseDBLocation(guild.getVaultLocation());

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_UPDATE);

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
			preparedStatement.setString(18, BannerUtils.serialize(guild.getBannerMeta()));

			preparedStatement.setInt(19, guild.getId());

			preparedStatement.executeUpdate();
			guild.setUnchanged();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while saving a guild.");
			LoggerUtils.exception(e);
		}

		return true;
	}

	@Override
	public void add(NovaGuild guild) {
		getStorage().connect();

		try {
			String homeLocationString = StringUtils.parseDBLocation(guild.getHome());
			String vaultLocationString = StringUtils.parseDBLocation(guild.getVaultLocation());

			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_INSERT);
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
			preparedStatement.setString(19, BannerUtils.serialize(guild.getBannerMeta())); //banner

			preparedStatement.execute();

			guild.setId(getStorage().returnGeneratedKey(preparedStatement));
			guild.setUnchanged();
			guild.setAdded();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while adding a guild!");
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void remove(NovaGuild guild) {
		if(!guild.isAdded()) {
			return;
		}

		getStorage().connect();

		try {
			PreparedStatement preparedStatement = getStorage().getPreparedStatement(PreparedStatements.GUILDS_DELETE);
			preparedStatement.setInt(1, guild.getId());
			preparedStatement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.info("SQLException while deleting a guild.");
			LoggerUtils.exception(e);
		}
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
