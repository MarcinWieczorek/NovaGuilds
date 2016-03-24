package co.marcin.novaguilds.impl.storage.managers.database;

import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.storage.AbstractDatabaseStorage;
import co.marcin.novaguilds.impl.storage.managers.AbstractResourceManager;

public abstract class AbstractDatabaseResourceManager<T> extends AbstractResourceManager<T> {
	/**
	 * The constructor
	 *
	 * @param storage the storage
	 * @param clazz   type class
	 */
	protected AbstractDatabaseResourceManager(Storage storage, Class clazz) {
		super(storage, clazz);
	}

	@Override
	protected final AbstractDatabaseStorage getStorage() {
		return (AbstractDatabaseStorage) super.getStorage();
	}
}
