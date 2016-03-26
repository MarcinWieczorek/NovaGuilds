package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagerRankImpl extends AbstractYAMLResourceManager<NovaRank> {
	private final AbstractYAMLResourceManager<NovaGuild> guildResourceManager = (AbstractYAMLResourceManager<NovaGuild>) getStorage().getResourceManager(NovaGuild.class);

	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerRankImpl(Storage storage) {
		super(storage, NovaRank.class, "rank/");
	}

	@Override
	public List<NovaRank> load() {
		List<NovaRank> list = new ArrayList<>();

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			boolean fixPlayerList = false;

			FileConfiguration guildData = guildResourceManager.getData(guild);
			ConfigurationSection ranksConfigurationSection = guildData.getConfigurationSection("ranks");
			List<String> rankNamesList = new ArrayList<>();

			if(!guildData.isConfigurationSection("ranks")) {
				continue;
			}

			rankNamesList.addAll(ranksConfigurationSection.getKeys(false));

			for(String rankName : rankNamesList) {
				ConfigurationSection rankConfiguration = ranksConfigurationSection.getConfigurationSection(rankName);

				NovaRank rank = new NovaRankImpl(0);
				rank.setAdded();
				rank.setName(rankName);

				List<String> permissionsStringList = rankConfiguration.getStringList("permissions");
				List<GuildPermission> permissionsList = new ArrayList<>();
				for(String permissionString : permissionsStringList) {
					permissionsList.add(GuildPermission.valueOf(permissionString));
				}
				rank.setPermissions(permissionsList);

				guild.addRank(rank);
				rank.setGuild(guild);

				for(String playerName : rankConfiguration.getStringList("members")) {
					NovaPlayer nPlayer = PlayerManager.getPlayer(playerName);

					if(nPlayer == null) {
						LoggerUtils.error("Player " + playerName + " doesn't exist, cannot be added to rank '" + rank.getName() + "' of guild " + rank.getGuild().getName());
						fixPlayerList = true;
						continue;
					}

					rank.addMember(nPlayer);
				}

				rank.setDefault(rankConfiguration.getBoolean("def"));
				rank.setClone(rankConfiguration.getBoolean("clone"));

				if(!fixPlayerList) {
					rank.setUnchanged();
				}

				list.add(rank);
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaRank rank) {
		if(rank.isChanged() || rank.isGeneric()) {
			return false;
		}

		rank.getGuild().setChanged();
		return guildResourceManager.save(rank.getGuild());
	}

	@Override
	public void add(NovaRank rank) {
		throw new UnsupportedOperationException("Not supported yet");
	}

	@Override
	public File getFile(NovaRank rank) {
		return new File(getDirectory(), rank.getGuild().getName() + "." + StringUtils.replace(rank.getName(), " ", "_") + ".yml");
	}

	@Override
	public void remove(NovaRank rank) {
		guildResourceManager.save(rank.getGuild());
	}

	@Override
	public FileConfiguration getData(NovaRank rank) {
		File file = guildResourceManager.getFile(rank.getGuild());
		FileConfiguration configuration = loadConfiguration(file);

		if(configuration == null) {
			return null;
		}

		return (FileConfiguration) configuration.getConfigurationSection("ranks");
	}
}
