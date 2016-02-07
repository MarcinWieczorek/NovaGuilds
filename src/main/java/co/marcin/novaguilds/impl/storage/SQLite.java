/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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

import co.marcin.novaguilds.util.LoggerUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects to and uses a SQLite database
 *
 * @author tips48
 */
public class SQLite extends AbstractDatabase {
	private final String dbLocation;

	/**
	 * Creates a new SQLite instance
	 *
	 * @param plugin     Plugin instance
	 * @param dbLocation Location of the Database (Must end in .db)
	 */
	public SQLite(Plugin plugin, String dbLocation) {
		super(plugin);
		this.dbLocation = dbLocation;
	}

	@Override
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if(checkConnection()) {
			return connection;
		}

		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}

		File file = new File(plugin.getDataFolder(), dbLocation);

		if(!(file.exists())) {
			try {
				file.createNewFile();
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}

		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toPath().toString() + "/" + dbLocation);
		return connection;
	}
}
