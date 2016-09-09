package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.impl.storage.AbstractDatabaseStorage;
import co.marcin.novaguilds.impl.storage.managers.AbstractResourceManager;
import co.marcin.novaguilds.util.LoggerUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractDatabaseResourceManager<T extends Resource> extends AbstractResourceManager<T> {
	protected final String columnName;
	private final Collection<T> updateUUIDQueue = new HashSet<>();

	/**
	 * The constructor
	 *
	 * @param storage the storage
	 * @param clazz   type class
	 * @param columnName column name in the database
	 */
	protected AbstractDatabaseResourceManager(Storage storage, Class clazz, String columnName) {
		super(storage, clazz);
		this.columnName = columnName;
	}

	@Override
	protected final AbstractDatabaseStorage getStorage() {
		return (AbstractDatabaseStorage) super.getStorage();
	}

	/**
	 * Gets column name
	 * as it is in the database
	 *
	 * @return column name
	 */
	public final String getColumnName() {
		return columnName;
	}

	/**
	 * Updates resource's UUID in the database
	 *
	 * @param resource resource instance
	 * @param id       resource's ID
	 */
	protected void updateUUID(T resource, int id) {
		try {
			String sql = "UPDATE `" + Config.MYSQL_PREFIX.getString() + getColumnName() + "` SET `uuid`=? WHERE `id`=?";
			PreparedStatement statement = getStorage().getConnection().prepareStatement(sql);
			statement.setString(1, resource.getUUID().toString()); //UUID
			statement.setInt(   2, id);                            //id
			statement.executeUpdate();
		}
		catch(SQLException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Updates resource's UUID in the database
	 * It's supposed to execute updateUUID(T, int)
	 *
	 * @param resource resource instance
	 */
	protected abstract void updateUUID(T resource);

	/**
	 * Adds an object to update UUID queue
	 *
	 * @param t instance
	 */
	public void addToUpdateUUIDQueue(T t) {
		if(!isInUpdateUUIDQueue(t)) {
			updateUUIDQueue.add(t);
		}
	}

	/**
	 * Checks if an object is in save queue
	 *
	 * @param t instance
	 * @return boolean
	 */
	public boolean isInUpdateUUIDQueue(T t) {
		return updateUUIDQueue.contains(t);
	}

	/**
	 * Executes UUID update
	 *
	 * @return changed rows
	 */
	public int executeUpdateUUID() {
		int count = 0;

		for(T t : updateUUIDQueue) {
			updateUUID(t);
			count++;
		}

		updateUUIDQueue.clear();

		return count;
	}
}
