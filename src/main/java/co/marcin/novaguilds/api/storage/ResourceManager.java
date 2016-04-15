package co.marcin.novaguilds.api.storage;

import java.util.Collection;
import java.util.List;

public interface ResourceManager<T extends Resource> {
	/**
	 * Loads data
	 *
	 * @return list with data
	 */
	List<T> load();

	/**
	 * Saves data
	 *
	 * @param t instance
	 * @return true if saved
	 */
	boolean save(T t);

	/**
	 * Saves data from a list
	 *
	 * @param list the list
	 * @return amount of saved items
	 */
	Integer save(Collection<T> list);

	/**
	 * Adds data
	 *
	 * @param t instance
	 */
	void add(T t);

	/**
	 * Removes data
	 *
	 * @param t instance
	 */
	void remove(T t);

	/**
	 * Removes data from a list
	 *
	 * @param list the list
	 */
	void remove(List<T> list);

	/**
	 * Adds an object to removal queue
	 */
	void addToRemovalQueue(T t);

	/**
	 * Actually deletes queued data
	 *
	 * @return amount of removed items
	 */
	int executeRemoval();
}
