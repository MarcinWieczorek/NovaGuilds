package co.marcin.novaguilds.api.util;

import org.bukkit.entity.Player;

public interface IBossBarUtils {
	/**
	 * Sends a message to all players
	 *
	 * @param message the message
	 */
	void setMessage(String message);

	/**
	 * Sends a message
	 *
	 * @param player  the player
	 * @param message the message
	 */
	void setMessage(Player player, String message);

	/**
	 * Sends a message with the bar willed in percent % to all players
	 *
	 * @param message the message
	 * @param percent percent
	 */
	void setMessage(String message, float percent);

	/**
	 * Sends a message with the bar willed in percent %
	 *
	 * @param player  the player
	 * @param message the message
	 * @param percent percent
	 */
	void setMessage(Player player, String message, float percent);

	/**
	 * Sends a message for a fixed amount of time to all players
	 *
	 * @param message the message
	 * @param seconds time in seconds
	 */
	void setMessage(String message, int seconds);

	/**
	 * Sends a message for a fixed amount of time
	 *
	 * @param player  the player
	 * @param message the message
	 * @param seconds time in seconds
	 */
	void setMessage(Player player, String message, int seconds);

	/**
	 * Returns if a player has a bar
	 *
	 * @param player the player
	 * @return boolean
	 */
	boolean hasBar(Player player);

	/**
	 * Removes the bar
	 *
	 * @param player the player
	 */
	void removeBar(Player player);

	/**
	 * Sets the health
	 *
	 * @param player  the player
	 * @param percent percent
	 */
	void setHealth(Player player, float percent);

	/**
	 * Gets the health
	 *
	 * @param player the player
	 * @return the health
	 */
	float getHealth(Player player);

	/**
	 * Gets the string displayed to the player
	 *
	 * @param player the player
	 * @return text
	 */
	String getMessage(Player player);
}
