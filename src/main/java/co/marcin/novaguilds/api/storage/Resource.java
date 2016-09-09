package co.marcin.novaguilds.api.storage;

import co.marcin.novaguilds.api.util.Addable;
import co.marcin.novaguilds.api.util.Changeable;

import java.util.UUID;

public interface Resource extends Addable, Changeable {
	/**
	 * Gets resource UUID
	 *
	 * @return uuid
	 */
	UUID getUUID();

	/**
	 * Marks the resource as unloaded
	 * So it won't be processed
	 * later
	 */
	void unload();

	/**
	 * Checks if the resource
	 * has been unloaded
	 *
	 * @return true if unloaded
	 */
	boolean isUnloaded();
}
