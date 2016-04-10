package co.marcin.novaguilds.impl.util.bossbar;

import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

public class BossBarUtilsBossBarImpl extends AbstractBossBarUtils {
	@Override
	public void setMessage(Player player, String message) {
		BossBarAPI.setMessage(player, message);
	}

	@Override
	public void setMessage(Player player, String message, float percent) {
		BossBarAPI.setMessage(player, message, percent);
	}

	@Override
	public void setMessage(Player player, String message, int seconds) {
		BossBarAPI.setMessage(player, message, seconds);
	}

	@Override
	public boolean hasBar(Player player) {
		return BossBarAPI.hasBar(player);
	}

	@Override
	public void removeBar(Player player) {
		BossBarAPI.removeBar(player);
	}

	@Override
	public void setHealth(Player player, float percent) {
		BossBarAPI.setHealth(player, percent);
	}

	@Override
	public float getHealth(Player player) {
		return BossBarAPI.getHealth(player);
	}

	@Override
	public String getMessage(Player player) {
		return BossBarAPI.getMessage(player);
	}
}
