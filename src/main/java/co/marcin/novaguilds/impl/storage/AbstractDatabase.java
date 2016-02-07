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

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract Database class, serves as a base for any connection method (MySQL,
 * SQLite, etc.)
 *
 * @author -_Husky_-
 * @author tips48
 */
public abstract class AbstractDatabase implements co.marcin.novaguilds.api.storage.Database {

	protected Connection connection;
	
	/**
	 * Plugin instance, use for plugin.getDataFolder()
	 */
	protected final Plugin plugin;

	/**
	 * Creates a new Database
	 *
	 * @param plugin Plugin instance
	 */
	protected AbstractDatabase(Plugin plugin) {
		this.plugin = plugin;
		connection = null;
	}

	/**
	 * Opens a connection with the database
	 *
	 * @return Opened connection
	 * @throws SQLException           if the connection can not be opened
	 * @throws ClassNotFoundException if the driver cannot be found
	 */
	public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

	/**
	 * Checks if a connection is open with the database
	 *
	 * @return true if the connection is open
	 * @throws SQLException if the connection cannot be checked
	 */
	public boolean checkConnection() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	/**
	 * Gets the connection with the database
	 *
	 * @return Connection with the database, null if none
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Closes the connection with the database
	 *
	 * @return true if successful
	 * @throws SQLException if the connection cannot be closed
	 */
	public boolean closeConnection() throws SQLException {
		if(connection == null) {
			return false;
		}
		connection.close();
		return true;
	}
}