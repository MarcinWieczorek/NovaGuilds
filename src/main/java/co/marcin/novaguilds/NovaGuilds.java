/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds;

import co.marcin.novaguilds.api.NovaGuildsAPI;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.api.util.SignGUI;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.exception.FatalNovaGuildsException;
import co.marcin.novaguilds.impl.storage.StorageConnector;
import co.marcin.novaguilds.impl.util.ScoreboardStatsHook;
import co.marcin.novaguilds.impl.util.bossbar.BossBarUtils;
import co.marcin.novaguilds.impl.util.logging.WrappedLogger;
import co.marcin.novaguilds.listener.VanishListener;
import co.marcin.novaguilds.listener.VaultListener;
import co.marcin.novaguilds.manager.CommandManager;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.DependencyManager;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.HologramManager;
import co.marcin.novaguilds.manager.ListenerManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.manager.TaskManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import co.marcin.novaguilds.util.VersionUtils;
import co.marcin.novaguilds.util.reflect.Reflections;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class NovaGuilds extends JavaPlugin implements NovaGuildsAPI {
	/*
	 * Dioricie nasz, któryś jest w Javie, święć się bugi Twoje, przyjdź ficzery Twoje,
	 * bądź kod Twój jako w gicie tak i w mavenie, stacktrace naszego powszedniego
	 * daj nam dzisiaj, i daj nam buildy Twoje, jako i my commity dajemy,
	 * i nie wódź nas na wycieki pamięci, ale daj nam Bugi.
	 * Escape. ~Bukkit.PL
	 */

	private static NovaGuilds instance;

	private final DependencyManager dependencyManager;
	private final ListenerManager   listenerManager;
	private final HologramManager   hologramManager;
	private final CommandManager    commandManager;
	private final MessageManager    messageManager;
	private final RegionManager     regionManager;
	private final PlayerManager     playerManager;
	private final ConfigManager     configManager;
	private final GuildManager      guildManager;
	private final GroupManager      groupManager;
	private final RankManager       rankManager;
	private final TaskManager       taskManager;

	private PacketExtension packetExtension;
	private Storage storage;
	private SignGUI signGUI;
	private static Method getOnlinePlayersMethod;

	static {
		try {
			getOnlinePlayersMethod = Server.class.getMethod("getOnlinePlayers");
		}
		catch(NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}

	public NovaGuilds() {
		instance = this;

		dependencyManager = new DependencyManager();
		hologramManager   = new HologramManager();
		listenerManager   = new ListenerManager();
		messageManager    = new MessageManager();
		commandManager    = new CommandManager();
		regionManager     = new RegionManager();
		playerManager     = new PlayerManager();
		configManager     = new ConfigManager();
		guildManager      = new GuildManager();
		groupManager      = new GroupManager();
		rankManager       = new RankManager();
		taskManager       = new TaskManager();
	}

	@Override
	public void onEnable() {
		try {
			//managers
			getDependencyManager().setUp();
			getConfigManager().reload();
			getMessageManager().load();
			getCommandManager().setUp();
			getGroupManager().load();
			getListenerManager().registerListeners();

			//Version check
			VersionUtils.checkVersion();

			//Setups the wrapped logger
			setupWrappedLogger();

			setUpStorage();

			//Data loading
			getGuildManager().load();
			LoggerUtils.info("Guilds data loaded");
			getRegionManager().load();
			LoggerUtils.info("Regions data loaded");
			getRankManager().loadDefaultRanks();
			getPlayerManager().load();
			LoggerUtils.info("Players data loaded");

			LoggerUtils.info("Post checks running");
			getGuildManager().postCheck();

			getRankManager().load();
			LoggerUtils.info("Ranks data loaded");

			//HologramManager
			if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
				getHologramManager().load();
			}

			if(Config.PACKETS_ENABLED.getBoolean()) {
				switch(ConfigManager.getServerVersion()) {
					case MINECRAFT_1_7_R3:
						packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.PacketExtensionImpl();

						if(Config.SIGNGUI_ENABLED.getBoolean()) {
							signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_7_R3.SignGUIImpl();
						}

						if(Config.PACKETS_ADVANCEDENTITYUSE.getBoolean()) {
							new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.PacketListenerImpl();
						}
						break;
					case MINECRAFT_1_7_R4:
						packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.PacketExtensionImpl();

						if(Config.SIGNGUI_ENABLED.getBoolean()) {
							signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.SignGUIImpl();
						}

						if(Config.PACKETS_ADVANCEDENTITYUSE.getBoolean()) {
							new co.marcin.novaguilds.impl.versionimpl.v1_7_R4.PacketListenerImpl();
						}
						break;
					case MINECRAFT_1_8_R1:
						packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_8_R3.PacketExtensionImpl();

						if(Config.SIGNGUI_ENABLED.getBoolean()) {
							signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_8_R1.SignGUIImpl();
						}
						break;
					case MINECRAFT_1_8_R2:
					case MINECRAFT_1_8_R3:
						packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_8_R3.PacketExtensionImpl();

						if(Config.SIGNGUI_ENABLED.getBoolean()) {
							signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_8_R3.SignGUIImpl();
						}
						break;
					case MINECRAFT_1_9_R1:
						packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_8_R3.PacketExtensionImpl();

						if(Config.SIGNGUI_ENABLED.getBoolean()) {
							signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_9_R1.SignGUIImpl();
						}
						break;
					case MINECRAFT_1_9_R2:
					case MINECRAFT_1_10_R1:
					case MINECRAFT_1_10_R2:
						packetExtension = new co.marcin.novaguilds.impl.versionimpl.v1_8_R3.PacketExtensionImpl();

						if(Config.SIGNGUI_ENABLED.getBoolean()) {
							signGUI = new co.marcin.novaguilds.impl.versionimpl.v1_9_R2.SignGUIImpl();
						}
						break;
				}

				//Register players (for reload)
				for(Player p : NovaGuilds.getOnlinePlayers()) {
					getPacketExtension().registerPlayer(p);
				}
			}
			else {
				getServer().getPluginManager().registerEvents(new Listener() {
					@EventHandler
					public void onPlayerInteractEntity(org.bukkit.event.player.PlayerInteractEntityEvent event) {
						PlayerInteractEntityEvent clickEvent = new PlayerInteractEntityEvent(event.getPlayer(), event.getRightClicked(), EntityUseAction.INTERACT);
						getServer().getPluginManager().callEvent(clickEvent);
						event.setCancelled(clickEvent.isCancelled());
					}

					@EventHandler
					public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
						PlayerInteractEntityEvent interactEntityEvent = new PlayerInteractEntityEvent(event.getPlayer(), event.getRightClicked(), EntityUseAction.INTERACT_AT);
						ListenerManager.getLoggedPluginManager().callEvent(interactEntityEvent);
						event.setCancelled(interactEntityEvent.isCancelled());
					}
				}, this);
			}

			if(signGUI == null) {
				Config.SIGNGUI_ENABLED.set(false);
			}

			if(Config.VAULT_ENABLED.getBoolean()) {
				new VaultListener();
			}

			if(getDependencyManager().isEnabled(Dependency.VANISHNOPACKET)) {
				new VanishListener();
			}

			if(getDependencyManager().isEnabled(Dependency.SCOREBOARDSTATS)) {
				new ScoreboardStatsHook();
			}

			//Tablist/tag update
			TagUtils.refresh();
			TabUtils.refresh();

			//metrics
			setupMetrics();

			LoggerUtils.info("#" + VersionUtils.getBuildCurrent() + " (" + VersionUtils.getCommit() + ") Enabled");
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}
	}

	@Override
	public void onDisable() {
		if(FatalNovaGuildsException.fatal) {
			LoggerUtils.info("#" + VersionUtils.getBuildCurrent() + " (FATAL) Disabled");
			return;
		}

		getGuildManager().save();
		getRegionManager().save();
		getPlayerManager().save();
		getRankManager().save();
		LoggerUtils.info("Saved all data");

		if(Config.PACKETS_ENABLED.getBoolean() && getPacketExtension() != null) {
			getPacketExtension().unregisterChannel();
		}

		if(getSignGUI() != null) {
			getSignGUI().destroy();
		}

		//reset barapi
		if(Config.BOSSBAR_ENABLED.getBoolean()) {
			for(Player player : NovaGuilds.getOnlinePlayers()) {
				BossBarUtils.removeBar(player);
			}
		}

		//removing holograms
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			//Save holograms
			getHologramManager().save();

			for(Hologram h : HologramsAPI.getHolograms(this)) {
				h.delete();
			}
		}

		for(Player p : NovaGuilds.getOnlinePlayers()) {
			PlayerManager.getPlayer(p).cancelToolProgress();
		}

		for(NovaPlayer nPlayer : getPlayerManager().getPlayers()) {
			if(nPlayer.getActiveSelection() != null) {
				nPlayer.getActiveSelection().reset();
			}
		}

		LoggerUtils.info("#" + VersionUtils.getBuildCurrent() + " Disabled");
	}

	@Override
	public GuildManager getGuildManager() {
		return guildManager;
	}

	@Override
	public RegionManager getRegionManager() {
		return regionManager;
	}

	@Override
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}

	@Override
	public MessageManager getMessageManager() {
		return messageManager;
	}

	@Override
	public HologramManager getHologramManager() {
		return hologramManager;
	}

	@Override
	public ConfigManager getConfigManager() {
		return configManager;
	}

	@Override
	public GroupManager getGroupManager() {
		return groupManager;
	}

	@Override
	public TaskManager getTaskManager() {
		return taskManager;
	}

	@Override
	public ListenerManager getListenerManager() {
		return listenerManager;
	}

	@Override
	public Storage getStorage() {
		return storage;
	}

	@Override
	public RankManager getRankManager() {
		return rankManager;
	}

	@Override
	public PacketExtension getPacketExtension() {
		return packetExtension;
	}

	@Override
	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	/**
	 * Gets the instance
	 *
	 * @return the instance
	 */
	public static NovaGuilds getInstance() {
		return instance;
	}

	/**
	 * Sets up the storage
	 *
	 * @throws FatalNovaGuildsException if fails
	 */
	public void setUpStorage() throws FatalNovaGuildsException {
		storage = new StorageConnector().getStorage();
	}

	/**
	 * Setups metrics
	 *
	 * @throws IOException if fails
	 */
	private void setupMetrics() throws IOException {
		Metrics metrics = new Metrics(this);
		Metrics.Graph guildsAndUsersGraph = metrics.createGraph("Guilds and users");

		guildsAndUsersGraph.addPlotter(new Metrics.Plotter("Guilds") {
			@Override
			public int getValue() {
				return getGuildManager().getGuilds().size();
			}
		});

		guildsAndUsersGraph.addPlotter(new Metrics.Plotter("Users") {
			@Override
			public int getValue() {
				return getPlayerManager().getPlayers().size();
			}
		});

		metrics.start();
	}

	/**
	 * Runs a runnable
	 *
	 * @param runnable Runnable implementation
	 * @param delay    delay in timeUnit
	 * @param timeUnit time unit
	 */
	public static void runTaskLater(Runnable runnable, long delay, TimeUnit timeUnit) {
		Bukkit.getScheduler().runTaskLater(instance, runnable, timeUnit.toSeconds(delay) * 20);
	}

	/**
	 * Gets sign gui
	 *
	 * @return SignGUI implementation
	 */
	public SignGUI getSignGUI() {
		return signGUI;
	}

	/**
	 * Gets online players
	 *
	 * @return Collection of online players
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Player> getOnlinePlayers() {
		Collection<Player> collection = new HashSet<>();

		try {
			if(getOnlinePlayersMethod.getReturnType().equals(Collection.class)) {
				collection = ((Collection) getOnlinePlayersMethod.invoke(Bukkit.getServer()));
			}
			else {
				Player[] array = ((Player[]) getOnlinePlayersMethod.invoke(Bukkit.getServer()));
				Collections.addAll(collection, array);
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}

		return collection;
	}

	/**
	 * Setups the wrapped logger
	 *
	 * @throws NoSuchFieldException   when something goes wrong
	 * @throws IllegalAccessException when something goes wrong
	 */
	private void setupWrappedLogger() throws NoSuchFieldException, IllegalAccessException {
		Field loggerField = Reflections.getPrivateField(JavaPlugin.class, "logger");
		loggerField.set(this, new WrappedLogger(this));
	}
}
