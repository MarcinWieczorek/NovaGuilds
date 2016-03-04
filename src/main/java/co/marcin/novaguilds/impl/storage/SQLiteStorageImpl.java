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

import co.marcin.novaguilds.api.storage.Database;
import co.marcin.novaguilds.util.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteStorageImpl extends AbstractDatabaseStorage implements Database {
	private final File databaseFile;

	/**
	 * Creates a new SQLite instance
	 *
	 * @param databaseFile The database file (Must end in .db)
	 */
	public SQLiteStorageImpl(File databaseFile) {
		this.databaseFile = databaseFile;
	}

	@Override
	public void openConnection() throws SQLException, ClassNotFoundException {
		if(checkConnection()) {
			return;
		}

		if(!databaseFile.exists()) {
			try {
				if(!plugin.getDataFolder().exists()) {
					if(!plugin.getDataFolder().mkdirs()) {
						throw new IOException("Failed when creating directories.");
					}
				}

				if(!databaseFile.createNewFile()) {
					throw new IOException("Failed when creating a new file");
				}
			}
			catch(IOException e) {
				LoggerUtils.exception(e);
			}
		}

		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toPath().toString() + "/" + databaseFile.getName());
	}

	@Override
	public boolean connect() {
		if(firstConnect) {
			try {
				openConnection();

				if(!checkTables()) {
					setupTables();
				}

				analyze();

				firstConnect = false;
			}
			catch(SQLException | ClassNotFoundException e) {
				LoggerUtils.exception(e);
			}
		}
		return true;
	}

	@Override
	public Integer returnGeneratedKey(Statement statement) {
		try {
			Statement keyStatement = connection.createStatement();
			ResultSet generatedKeys = keyStatement.executeQuery("SELECT last_insert_rowid()");
			generatedKeys.next();
			int id = generatedKeys.getInt(1);

			if(id==0) {
				throw new RuntimeException("Could not get generated keys");
			}

			return id;
		}
		catch(SQLException e) {
			throw new RuntimeException("Could not get generated keys", e);
		}
	}

	@Override
	public boolean isStatementReturnGeneratedKeysSupported() {
		return false;
	}
}
