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
	boolean remove(T t);

	/**
	 * Removes data from a list
	 *
	 * @param list the list
	 */
	int remove(Collection<T> list);

	/**
	 * Adds an object to save queue
	 *
	 * @param t instance
	 */
	void addToSaveQueue(T t);

	/**
	 * Removes an object from save queue
	 *
	 * @param t instance
	 */
	void removeFromSaveQueue(T t);

	/**
	 * Checks if an object is in save queue
	 *
	 * @param t instance
	 * @return boolean
	 */
	boolean isInSaveQueue(T t);

	/**
	 * Adds an object to removal queue
	 *
	 * @param t instance
	 */
	void addToRemovalQueue(T t);

	/**
	 * Checks if an object is in removal queue
	 *
	 * @param t instance
	 * @return boolean
	 */
	boolean isInRemovalQueue(T t);

	/**
	 * Actually deletes queued data
	 *
	 * @return amount of removed items
	 */
	int executeRemoval();

	/**
	 * Saves queued data
	 *
	 * @return amount of saved items
	 */
	int executeSave();
}
