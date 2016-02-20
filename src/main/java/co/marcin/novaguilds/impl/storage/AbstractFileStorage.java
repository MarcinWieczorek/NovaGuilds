/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.storage;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileStorage extends AbstractStorage {
	protected String extension;
	private final File dataDirectory;
	private final File playersDirectory;
	private final File guildsDirectory;
	private final File regionsDirectory;
	private final File ranksDirectory;

	public AbstractFileStorage(File dataDirectory) {
		this.dataDirectory = dataDirectory;
		playersDirectory = new File(dataDirectory, "player/");
		guildsDirectory = new File(dataDirectory, "guild/");
		regionsDirectory = new File(dataDirectory, "region/");
		ranksDirectory = new File(dataDirectory, "rank/");

		setUp();
	}

	@Override
	public boolean setUp() {
		if(!dataDirectory.exists()) {
			if(dataDirectory.mkdir()) {
				LoggerUtils.info("Data directory created");
			}
		}

		if(dataDirectory.exists()) {
			if(!playersDirectory.exists()) {
				if(playersDirectory.mkdir()) {
					LoggerUtils.info("Players directory created");
				}
			}

			if(!guildsDirectory.exists()) {
				if(guildsDirectory.mkdir()) {
					LoggerUtils.info("Guilds directory created");
				}
			}

			if(!regionsDirectory.exists()) {
				if(regionsDirectory.mkdir()) {
					LoggerUtils.info("Regions directory created");
				}
			}

			if(!ranksDirectory.exists()) {
				if(ranksDirectory.mkdir()) {
					LoggerUtils.info("Ranks directory created");
				}
			}
		}
		else {
			LoggerUtils.error("Could not setup directories!");
			LoggerUtils.error("Switching to secondary data storage type!");
			return false;
		}

		return true;
	}

	protected File getPlayerFile(NovaPlayer nPlayer) {
		return new File(playersDirectory, nPlayer.getUUID().toString() + "." + extension);
	}

	protected File getGuildFile(NovaGuild guild) {
		return new File(guildsDirectory, guild.getUUID().toString() + "." + extension);
	}

	protected File getRegionFile(NovaRegion region) {
		return new File(regionsDirectory, region.getGuild().getName() + "." + extension);
	}

	protected File getRankFile(NovaRank rank) {
		return new File(ranksDirectory, rank.getGuild().getName() + "." + StringUtils.replace(rank.getName(), " ", "_") + "." + extension);
	}

	protected final File getDataDirectory() {
		return dataDirectory;
	}

	protected final File getPlayersDirectory() {
		return playersDirectory;
	}

	protected final File getGuildsDirectory() {
		return guildsDirectory;
	}

	protected final File getRegionsDirectory() {
		return regionsDirectory;
	}

	protected final File getRanksDirectory() {
		return ranksDirectory;
	}

	protected List<File> getPlayerFiles() {
		return getFileList(getPlayersDirectory());
	}

	protected List<File> getGuildFiles() {
		return getFileList(getGuildsDirectory());
	}

	protected List<File> getRegionFiles() {
		return getFileList(getRegionsDirectory());
	}

	protected List<File> getRankFiles() {
		return getFileList(getRanksDirectory());
	}

	protected final List<File> getFileList(File directory) {
		File[] files = directory.listFiles();
		List<File> list = new ArrayList<>();

		if(files != null) {
			list.addAll(Arrays.asList(files));
		}

		return list;
	}

	protected final String trimExtension(File file) {
		return StringUtils.substring(file.getName(), 0, StringUtils.lastIndexOf(file.getName(), '.'));
	}

	protected final void setExtension(String extension) {
		this.extension = extension;
	}
}
