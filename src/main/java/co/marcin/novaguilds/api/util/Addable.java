package co.marcin.novaguilds.api.util;

public interface Addable {
	/**
	 * Checks if added
	 *
	 * @return true if yes
	 */
	boolean isAdded();

	/**
	 * Mark as added
	 */
	void setAdded();

	/**
	 * Mark as not added
	 */
	void setNotAdded();
}
