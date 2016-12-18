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

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.exception.FatalNovaGuildsException;
import co.marcin.novaguilds.exception.StorageConnectionFailedException;
import co.marcin.novaguilds.util.LoggerUtils;

import java.io.File;

public class StorageConnector {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private int storageConnectionAttempt = 1;
	private Storage storage;
	private boolean isSecondary;

	/**
	 * The constructor
	 *
	 * @throws FatalNovaGuildsException when something goes wrong
	 */
	public StorageConnector() throws FatalNovaGuildsException {
		handle();
	}

	/**
	 * Handles storage connecting
	 *
	 * @throws FatalNovaGuildsException when something goes wrong
	 */
	public void handle() throws FatalNovaGuildsException {
		try {
			connect();
		}
		catch(StorageConnectionFailedException | RuntimeException e) {
			if(e instanceof IllegalArgumentException
					&& e.getMessage() != null
					&& e.getMessage().contains("credentials")) {
				LoggerUtils.info(e.getClass().getName());
				LoggerUtils.info(e.getMessage());
			}
			else {
				LoggerUtils.exception(e);
			}

			storageConnectionAttempt++;
			if(storageConnectionAttempt > 3) {
				if(isSecondary) {
					throw new FatalNovaGuildsException("Failed while connecting to the storage");
				}
				else {
					isSecondary = true;
					plugin.getConfigManager().setToSecondaryDataStorageType();
					storageConnectionAttempt = 1;
				}
			}

			handle();
		}
	}

	/**
	 * Creates the storage
	 *
	 * @throws StorageConnectionFailedException when something goes wrong
	 */
	public void connect() throws StorageConnectionFailedException {
		DataStorageType storageType = plugin.getConfigManager().getDataStorageType();
		LoggerUtils.info("Connecting to " + storageType.name() + " storage (attempt: " + storageConnectionAttempt + ")");

		switch(storageType) {
			case MYSQL:
				if(Config.MYSQL_HOST.getString().isEmpty()) {
					plugin.getConfigManager().setToSecondaryDataStorageType();
					isSecondary = true;
					storageConnectionAttempt = 0;
					throw new IllegalArgumentException("MySQL credentials not specified in the config. Switching to secondary storage.");
				}

				storage = new MySQLStorageImpl(
						Config.MYSQL_HOST.getString(),
						Config.MYSQL_PORT.getString(),
						Config.MYSQL_DATABASE.getString(),
						Config.MYSQL_USERNAME.getString(),
						Config.MYSQL_PASSWORD.getString()
				);
				break;
			case SQLITE:
				storage = new SQLiteStorageImpl(new File(plugin.getDataFolder(), "sqlite.db"));
				break;
			case FLAT:
				storage = new YamlStorageImpl(new File(plugin.getDataFolder(), "data/"));
				break;
		}

		storage.registerManagers();
		LoggerUtils.info("Successfully connected to the storage");
	}

	/**
	 * Gets the storage
	 *
	 * @return the storage
	 */
	public Storage getStorage() {
		return storage;
	}
}
