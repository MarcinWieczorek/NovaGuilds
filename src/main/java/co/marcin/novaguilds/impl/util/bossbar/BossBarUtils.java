package co.marcin.novaguilds.impl.util.bossbar;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.IBossBarUtils;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.entity.Player;

public class BossBarUtils {
	private static IBossBarUtils bossBarUtils;

	static {
		if(bossBarUtils == null) {
			switch(ConfigManager.getServerVersion()) {
				case MINECRAFT_1_7:
				case MINECRAFT_1_8:
					boolean bossBarAPI = NovaGuilds.getInstance().getDependencyManager().isEnabled(Dependency.BOSSBARAPI);
					bossBarUtils = bossBarAPI ? new BossBarUtilsBossBarImpl() : new BossBarUtilsBarAPIImpl();
					break;
				case MINECRAFT_1_9:
					bossBarUtils = new BossBarUtilsBukkitImpl();
					break;
			}
		}
	}

	public static void setMessage(String message) {
		bossBarUtils.setMessage(message);
	}

	public static void setMessage(Player player, String message) {
		bossBarUtils.setMessage(player, message);
	}

	public static void setMessage(String message, float percent) {
		bossBarUtils.setMessage(message, percent);
	}

	public static void setMessage(Player player, String message, float percent) {
		bossBarUtils.setMessage(player, message, percent);
	}

	public static void setMessage(String message, int seconds) {
		bossBarUtils.setMessage(message, seconds);
	}

	public static void setMessage(Player player, String message, int seconds) {
		bossBarUtils.setMessage(player, message, seconds);
	}

	public static boolean hasBar(Player player) {
		return bossBarUtils.hasBar(player);
	}

	public static void removeBar(Player player) {
		bossBarUtils.removeBar(player);
	}

	public static void setHealth(Player player, float percent) {
		bossBarUtils.setHealth(player, percent);
	}

	public static float getHealth(Player player) {
		return bossBarUtils.getHealth(player);
	}

	public static String getMessage(Player player) {
		return bossBarUtils.getMessage(player);
	}
}
