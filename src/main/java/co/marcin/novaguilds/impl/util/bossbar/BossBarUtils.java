package co.marcin.novaguilds.impl.util.bossbar;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.util.IBossBarUtils;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.entity.Player;

public class BossBarUtils {
	private static IBossBarUtils bossBarUtils;

	static {
		if(bossBarUtils == null) {
			if(Config.BOSSBAR_ENABLED.getBoolean()) {
				switch(ConfigManager.getServerVersion()) {
					case MINECRAFT_1_7_2:
					case MINECRAFT_1_7_10:
					case MINECRAFT_1_8:
						boolean bossBarAPI = NovaGuilds.getInstance().getDependencyManager().isEnabled(Dependency.BOSSBARAPI);
						bossBarUtils = bossBarAPI ? new BossBarUtilsBossBarImpl() : new BossBarUtilsBarAPIImpl();
						break;
					case MINECRAFT_1_9_R1:
					case MINECRAFT_1_10_R1:
						bossBarUtils = new BossBarUtilsBukkitImpl();
						break;
				}
			}
			else {
				bossBarUtils = new AbstractBossBarUtils() {
					@Override
					public void setMessage(Player player, String message) {

					}

					@Override
					public void setMessage(Player player, String message, float percent) {

					}

					@Override
					public void setMessage(Player player, String message, int seconds) {

					}

					@Override
					public boolean hasBar(Player player) {
						return false;
					}

					@Override
					public void removeBar(Player player) {

					}

					@Override
					public void setHealth(Player player, float percent) {

					}

					@Override
					public float getHealth(Player player) {
						return 0;
					}

					@Override
					public String getMessage(Player player) {
						return null;
					}
				};
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
