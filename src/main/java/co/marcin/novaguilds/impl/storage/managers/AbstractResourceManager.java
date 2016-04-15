package co.marcin.novaguilds.impl.storage.managers;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.api.storage.Storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractResourceManager<T extends Resource> implements ResourceManager<T> {
	protected final NovaGuilds plugin = NovaGuilds.getInstance();
	private final Storage storage;
	private final Collection<T> removalQueue = new HashSet<>();

	/**
	 * The constructor
	 *
	 * @param storage the storage
	 * @param clazz   type class
	 */
	protected AbstractResourceManager(Storage storage, Class clazz) {
		this.storage = storage;
		register(clazz);
	}

	@Override
	public Integer save(Collection<T> list) {
		int count = 0;

		for(T t : list) {
			if(save(t)) {
				count++;
			}
		}

		return count;
	}

	@Override
	public void remove(List<T> list) {
		for(T t : list) {
			remove(t);
		}
	}

	/**
	 * Gets the storage
	 *
	 * @return the storage
	 */
	protected Storage getStorage() {
		return storage;
	}

	/**
	 * Registers the manager
	 *
	 * @param clazz type class
	 */
	private void register(Class clazz) {
		getStorage().registerResourceManager(clazz, this);
	}

	@Override
	public int executeRemoval() {
		int count = removalQueue.size();

		for(T resource : removalQueue) {
			remove(resource);
		}

		removalQueue.clear();
		return count;
	}

	@Override
	public void addToRemovalQueue(T t) {
		removalQueue.add(t);
	}
}
