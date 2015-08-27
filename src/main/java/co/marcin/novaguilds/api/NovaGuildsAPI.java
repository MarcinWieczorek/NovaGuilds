package co.marcin.novaguilds.api;

import co.marcin.novaguilds.manager.*;

public interface NovaGuildsAPI {
	RegionManager getRegionManager();

	GuildManager getGuildManager();

	PlayerManager getPlayerManager();

	MessageManager getMessageManager();

	CustomCommandManager getCommandManager();

	ConfigManager getConfigManager();

	GroupManager getGroupManager();

	FlatDataManager getFlatDataManager();

	HologramManager getHologramManager();

	int getBuild();
}
