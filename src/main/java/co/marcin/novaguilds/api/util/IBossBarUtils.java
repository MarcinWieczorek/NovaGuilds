package co.marcin.novaguilds.api.util;

import org.bukkit.entity.Player;

public interface IBossBarUtils {
	void setMessage(String message);

	void setMessage(Player player, String message);

	void setMessage(String message, float percent);

	void setMessage(Player player, String message, float percent);

	void setMessage(String message, int seconds);

	void setMessage(Player player, String message, int seconds);

	boolean hasBar(Player player);

	void removeBar(Player player);

	void setHealth(Player player, float percent);

	float getHealth(Player player);

	String getMessage(Player player);
}
