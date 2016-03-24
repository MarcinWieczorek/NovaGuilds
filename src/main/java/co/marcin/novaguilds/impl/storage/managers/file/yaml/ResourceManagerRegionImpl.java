package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.basic.NovaRegionImpl;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagerRegionImpl extends AbstractYAMLResourceManager<NovaRegion> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 */
	public ResourceManagerRegionImpl(Storage storage) {
		super(storage, NovaRegion.class, "region/");
	}

	@Override
	public List<NovaRegion> load() {
		List<NovaRegion> list = new ArrayList<>();

		for(File regionFile : getFiles()) {
			FileConfiguration configuration = loadConfiguration(regionFile);

			if(configuration != null) {
				World world = plugin.getServer().getWorld(configuration.getString("world"));

				if(world != null) {
					String guildName = trimExtension(regionFile);
					NovaGuild guild = GuildManager.getGuildFind(guildName);

					if(guild == null) {
						LoggerUtils.error("There's no guild matching region " + guildName);
						continue;
					}

					NovaRegion region = new NovaRegionImpl();

					Location c1 = new Location(world, configuration.getInt("corner1.x"), 0, configuration.getInt("corner1.z"));
					Location c2 = new Location(world, configuration.getInt("corner2.x"), 0, configuration.getInt("corner2.z"));

					region.setCorner(0, c1);
					region.setCorner(1, c2);
					region.setWorld(world);
					guild.setRegion(region);
					region.setUnchanged();

					list.add(region);
				}
			}
		}

		return list;
	}

	@Override
	public boolean save(NovaRegion region) {
		if(!region.isChanged()) {
			return false;
		}

		FileConfiguration regionData = getData(region);

		if(regionData != null) {
			try {
				//set values
				regionData.set("world", region.getWorld().getName());

				//corners
				regionData.set("corner1.x", region.getCorner(0).getBlockX());
				regionData.set("corner1.z", region.getCorner(0).getBlockZ());

				regionData.set("corner2.x", region.getCorner(1).getBlockX());
				regionData.set("corner2.z", region.getCorner(1).getBlockZ());

				//save
				regionData.save(getFile(region));
				region.setUnchanged();
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Attempting to save non-existing region. " + region.getGuild().getName());
		}

		return true;
	}

	@Override
	public void remove(NovaRegion region) {
		if(getFile(region).delete()) {
			LoggerUtils.info("Deleted guild " + region.getGuild().getName() + " region's file.");
		}
		else {
			LoggerUtils.error("Failed to delete guild " + region.getGuild().getName() + " region's file.");
		}
	}

	@Override
	public File getFile(NovaRegion region) {
		return new File(getDirectory(), region.getGuild().getName() + ".yml");
	}
}
