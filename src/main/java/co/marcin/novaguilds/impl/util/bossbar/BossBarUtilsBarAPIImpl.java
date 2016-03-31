package co.marcin.novaguilds.impl.util.bossbar;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.IBossBarUtils;
import me.confuser.barapi.BarAPI;
import org.bukkit.entity.Player;

public class BossBarUtilsBarAPIImpl implements IBossBarUtils {
	@Override
	public void setMessage(String message) {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			BarAPI.setMessage(player, message);
		}
	}

	@Override
	public void setMessage(Player player, String message) {
		BarAPI.setMessage(player, message);
	}

	@Override
	public void setMessage(String message, float percent) {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			BarAPI.setMessage(player, message, percent);
		}
	}

	@Override
	public void setMessage(Player player, String message, float percent) {
		BarAPI.setMessage(player, message, percent);
	}

	@Override
	public void setMessage(String message, int seconds) {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			BarAPI.setMessage(player, message, seconds);
		}
	}

	@Override
	public void setMessage(Player player, String message, int seconds) {
		BarAPI.setMessage(player, message, seconds);
	}

	@Override
	public boolean hasBar(Player player) {
		return BarAPI.hasBar(player);
	}

	@Override
	public void removeBar(Player player) {
		BarAPI.removeBar(player);
	}

	@Override
	public void setHealth(Player player, float percent) {
		BarAPI.setHealth(player, percent);
	}

	@Override
	public float getHealth(Player player) {
		return BarAPI.getHealth(player);
	}

	@Override
	public String getMessage(Player player) {
		return BarAPI.getMessage(player);
	}
}
