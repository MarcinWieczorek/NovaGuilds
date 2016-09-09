package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.impl.util.converter.EnumToNameConverterImpl;
import co.marcin.novaguilds.impl.util.converter.ResourceToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.StringToUUIDConverterImpl;
import co.marcin.novaguilds.impl.util.converter.ToStringConverterImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResourceManagerRankImpl extends AbstractYAMLResourceManager<NovaRank> {
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
		final List<NovaRank> list = new ArrayList<>();

		for(File rankFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(rankFile);
			UUID rankUUID = UUID.fromString(trimExtension(rankFile));
			NovaRank rank = new NovaRankImpl(rankUUID);
			rank.setAdded();
			rank.setName(configuration.getString("name"));

			final List<String> permissionsStringList = configuration.getStringList("permissions");
			final List<GuildPermission> permissionsList = new ArrayList<>();
			for(String permissionString : permissionsStringList) {
				permissionsList.add(GuildPermission.valueOf(permissionString));
			}
			rank.setPermissions(permissionsList);

			NovaGuild guild = GuildManager.getGuild(UUID.fromString(configuration.getString("guild")));

			if(guild == null) {
				LoggerUtils.error("Orphan rank (" + rankUUID.toString() + ") guild: " + configuration.getString("guild"));
				rank.unload();
				continue;
			}

			guild.addRank(rank);
			rank.setGuild(guild);

			for(String stringUUID : configuration.getStringList("members")) {
				NovaPlayer nPlayer = PlayerManager.getPlayer(new StringToUUIDConverterImpl().convert(stringUUID));

				if(nPlayer == null) {
					LoggerUtils.error("Player " + stringUUID + " doesn't exist, cannot be added to rank '" + rank.getName() + "' of guild " + rank.getGuild().getName());
					addToSaveQueue(rank);
					continue;
				}

				rank.addMember(nPlayer);
			}

			rank.setDefault(configuration.getBoolean("def"));
			rank.setClone(configuration.getBoolean("clone"));

			rank.setUnchanged();

			list.add(rank);
		}

		return list;
	}

	@Override
	public boolean save(NovaRank rank) {
		try {
			if(!rank.isChanged() && !isInSaveQueue(rank) || rank.isGeneric() || rank.isUnloaded()) {
				return false;
			}

			if(!rank.isAdded()) {
				add(rank);
			}

			//Permission list
			FileConfiguration configuration = getData(rank);
			final List<String> permissionNamesList = new EnumToNameConverterImpl<GuildPermission>().convert(rank.getPermissions());

			//Member list
			if(!rank.isDefault() && !rank.getMembers().isEmpty()) {
				configuration.set("members", new ToStringConverterImpl().convert((List) new ResourceToUUIDConverterImpl<NovaPlayer>().convert(rank.getMembers())));
			}
			else {
				configuration.set("members", null);
			}

			configuration.set("guild",       rank.getGuild().getUUID().toString());
			configuration.set("name",        rank.getName());
			configuration.set("permissions", permissionNamesList);
			configuration.set("def",         rank.isDefault());
			configuration.set("clone",       rank.isClone());

			configuration.save(getFile(rank));
			rank.setUnchanged();
			return true;
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
			return false;
		}
	}

	@Override
	public File getFile(NovaRank rank) {
		File file = new File(getDirectory(), rank.getUUID().toString() + ".yml");

		if(!file.exists()) {
			File nameFile = new File(getDirectory(), rank.getGuild().getName() + "." + StringUtils.replace(rank.getName(), " ", "_") + ".yml");
			nameFile.renameTo(file);
		}

		return file;
	}

	@Override
	public boolean remove(NovaRank rank) {
		if(!rank.isAdded()) {
			return false;
		}

		if(getFile(rank).delete()) {
			LoggerUtils.info("Deleted rank " + rank.getName() + "'s file.");
			return true;
		}
		else {
			LoggerUtils.error("Failed to delete rank " + rank.getName() + "'s file.");
			return false;
		}
	}
}
