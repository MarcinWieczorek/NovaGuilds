package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.basic.NovaPlayerImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerPlayerImpl extends AbstractYAMLResourceManager<NovaPlayer> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerPlayerImpl(Storage storage) {
		super(storage, NovaPlayer.class, "player/");
	}

	@Override
	public List<NovaPlayer> load() {
		List<NovaPlayer> list = new ArrayList<>();

		for(File playerFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(playerFile);

			if(configuration != null) {
				UUID uuid = UUID.fromString(configuration.getString("uuid"));
				NovaPlayer nPlayer = new NovaPlayerImpl(uuid);
				nPlayer.setAdded();

				Player player = plugin.getServer().getPlayer(uuid);

				if(player != null) {
					if(player.isOnline()) {
						nPlayer.setPlayer(player);
					}
				}

				nPlayer.setName(configuration.getString("name"));
				List<NovaGuild> invitedToList = plugin.getGuildManager().nameListToGuildsList(configuration.getStringList("invitedto"));
				nPlayer.setInvitedTo(invitedToList);

				nPlayer.setPoints(configuration.getInt("points"));
				nPlayer.setKills(configuration.getInt("kills"));
				nPlayer.setDeaths(configuration.getInt("deaths"));

				String guildName = configuration.getString("guild").toLowerCase();
				if(!guildName.isEmpty()) {
					NovaGuild guild = GuildManager.getGuildByName(guildName);

					if(guild != null) {
						guild.addPlayer(nPlayer);
					}
				}

				nPlayer.setUnchanged();

				list.add(nPlayer);
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaPlayer nPlayer) {
		if(!nPlayer.isChanged()) {
			return false;
		}

		if(!nPlayer.isAdded()) {
			add(nPlayer);
		}

		FileConfiguration playerData = getData(nPlayer);

		if(playerData != null) {
			try {
				//set values
				playerData.set("uuid", nPlayer.getUUID().toString());
				playerData.set("name", nPlayer.getName());
				playerData.set("guild", nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
				playerData.set("invitedto", nPlayer.getInvitedTo());
				playerData.set("points", nPlayer.getPoints());
				playerData.set("kills", nPlayer.getKills());
				playerData.set("deaths", nPlayer.getDeaths());

				//save
				playerData.save(getFile(nPlayer));
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing player. " + nPlayer.getName());
		}

		return true;
	}

	@Override
	public void remove(NovaPlayer nPlayer) {
		if(!nPlayer.isAdded()) {
			return;
		}

		if(getFile(nPlayer).delete()) {
			LoggerUtils.info("Deleted player " + nPlayer.getName() + "'s file.");
		}
		else {
			LoggerUtils.error("Failed to delete player " + nPlayer.getName() + "'s file.");
		}
	}

	@Override
	public File getFile(NovaPlayer nPlayer) {
		return new File(getDirectory(), nPlayer.getUUID().toString() + ".yml");
	}
}
