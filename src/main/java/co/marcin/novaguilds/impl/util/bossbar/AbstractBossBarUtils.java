package co.marcin.novaguilds.impl.util.bossbar;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.IBossBarUtils;
import org.bukkit.entity.Player;

public abstract class AbstractBossBarUtils implements IBossBarUtils {
	@Override
	public void setMessage(String message) {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			setMessage(player, message);
		}
	}

	@Override
	public void setMessage(String message, float percent) {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			setMessage(player, message, percent);
		}
	}

	@Override
	public void setMessage(String message, int seconds) {
		for(Player player : NovaGuilds.getOnlinePlayers()) {
			setMessage(player, message, seconds);
		}
	}
}
