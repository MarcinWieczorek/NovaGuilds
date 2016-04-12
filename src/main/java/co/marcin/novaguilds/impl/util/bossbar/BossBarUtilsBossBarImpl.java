package co.marcin.novaguilds.impl.util.bossbar;

import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.util.LoggerUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarUtilsBossBarImpl extends AbstractBossBarUtils {
	protected static final boolean v1_9 = ConfigManager.getServerVersion() == ConfigManager.ServerVersion.MINECRAFT_1_9;
	private final Map<UUID, BossBar> bossBarMap = new HashMap<>();

	private BossBar createIfNotExists(Player player) {
		if(!v1_9) {
			return null;
		}

		if(hasBar(player)) {
			return getBossBar(player);
		}

		BossBar bossBar = BossBarAPI.addBar(player, new TextComponent(""), Config.BOSSBAR_RAIDBAR_COLOR.toEnum(BossBarAPI.Color.class), Config.BOSSBAR_RAIDBAR_STYLE.toEnum(BossBarAPI.Style.class), 0);

		bossBarMap.put(player.getUniqueId(), bossBar);
		return bossBar;
	}

	private BossBar getBossBar(Player player) {
		return bossBarMap.get(player.getUniqueId());
	}

	@Override
	public void setMessage(Player player, String message) {
		setMessage(player, message, 100F);
	}

	@Override
	public void setMessage(Player player, String message, float percent) {
		if(v1_9) {
			BossBar bar = createIfNotExists(player);
			LoggerUtils.debug(new TextComponent(message).toString());
			bar.setMessage(new TextComponent(message).toString());
			bar.setProgress(percent / 100F);
		}
		else {
			BossBarAPI.setMessage(player, message, percent);
		}
	}

	@Override
	public void setMessage(Player player, String message, int seconds) {
		throw new UnsupportedOperationException("Not supported yet");
	}

	@Override
	public boolean hasBar(Player player) {
		return BossBarAPI.hasBar(player) || bossBarMap.containsKey(player.getUniqueId());
	}

	@Override
	public void removeBar(Player player) {
		if(v1_9) {
			BossBar bar = bossBarMap.remove(player.getUniqueId());
			bar.removePlayer(player);
		}
		else {
			BossBarAPI.removeBar(player);
		}
	}

	@Override
	public void setHealth(Player player, float percent) {
		if(v1_9) {
			createIfNotExists(player).setProgress(percent / 100F);
		}
		else {
			BossBarAPI.setHealth(player, percent);
		}
	}

	@Override
	public float getHealth(Player player) {
		return hasBar(player) ? v1_9 ? getBossBar(player).getProgress() : BossBarAPI.getHealth(player) : 0;
	}

	@Override
	public String getMessage(Player player) {
		return hasBar(player) ? v1_9 ? getBossBar(player).getMessage() : BossBarAPI.getMessage(player) : "";
	}
}
