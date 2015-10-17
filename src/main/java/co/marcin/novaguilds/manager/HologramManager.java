package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaHologram;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HologramManager {
	private final File file;
	private YamlConfiguration configuration;
	private List<NovaHologram> holograms = new ArrayList<>();

	public HologramManager(File file) {
		this.file = file;
	}

	public void load() {
		try {
			if(!file.exists()) {
				file.createNewFile();
			}

			configuration = YamlConfiguration.loadConfiguration(file);
			int count = 0;

			for(String name : configuration.getKeys(false)) {
				NovaHologram nHologram = new NovaHologram();
				Location location = RegionUtils.sectionToLocation(configuration.getConfigurationSection(name + ".location"));

				nHologram.setName(name);
				nHologram.setLocation(location);
				nHologram.setTop(configuration.getBoolean(name + ".top"));

				List<String> lines = new ArrayList<>();
				if(nHologram.isTop()) {
					lines.add(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
					lines.addAll(NovaGuilds.getInstance().getGuildManager().getTopGuilds());
				}
				else {
					lines.addAll(configuration.getStringList(name + ".lines"));
				}

				nHologram.addLine(lines);

				nHologram.create();

				holograms.add(nHologram);
				LoggerUtils.info("Loaded hologram " + nHologram.getName());
				count++;
			}

			LoggerUtils.info("Finished loading holograms. ("+count+" loaded)");
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
	}

	public void save() {
		if(configuration != null) {
			for(NovaHologram hologram : holograms) {
				if(hologram.isDeleted()) {
					configuration.set(hologram.getName(), null);
					LoggerUtils.info("Deleted hologram " + hologram.getName());
				}
				else {
					if(!hologram.isTop()) {
						configuration.set(hologram.getName() + ".lines", hologram.getLines());
					}

					configuration.set(hologram.getName() + ".top", hologram.isTop());
					configuration.set(hologram.getName() + ".location.world", hologram.getLocation().getWorld().getName());
					configuration.set(hologram.getName() + ".location.x", hologram.getLocation().getX());
					configuration.set(hologram.getName() + ".location.y", hologram.getLocation().getY());
					configuration.set(hologram.getName() + ".location.z", hologram.getLocation().getZ());
					LoggerUtils.info("Saved hologram " + hologram.getName());
				}
			}

			try {
				configuration.save(file);
			}
			catch (IOException e) {
				LoggerUtils.exception(e);
			}
		}
		else {
			LoggerUtils.error("Failed saving holograms, they weren't even loaded!");
		}

		LoggerUtils.info("Finished saving holograms.");
	}

	public NovaHologram addTopHologram(Location location) {
		NovaHologram nHologram = new NovaHologram();
		nHologram.setLocation(location);
		nHologram.addLine(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		nHologram.addLine(NovaGuilds.getInstance().getGuildManager().getTopGuilds());
		nHologram.setName("topX" + NumberUtils.randInt(1,999));
		nHologram.create();
		nHologram.setTop(true);
		holograms.add(nHologram);

		return nHologram;
	}

	public void refreshTopHolograms() {
		for(NovaHologram hologram : holograms) {
			if(hologram.isTop()) {
				hologram.refresh();
			}
		}
	}

	public List<NovaHologram> getHolograms() {
		return holograms;
	}

	public NovaHologram getHologram(String name) {
		for(NovaHologram hologram : holograms) {
			if(hologram.getName().equalsIgnoreCase(name)) {
				return hologram;
			}
		}

		return null;
	}
}
