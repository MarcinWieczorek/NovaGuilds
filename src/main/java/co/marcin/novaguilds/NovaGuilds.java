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
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.impl.listener.packet.PacketListener1_7Impl;
import co.marcin.novaguilds.impl.listener.packet.PacketListener1_8Impl;
import co.marcin.novaguilds.impl.storage.MySQLStorageImpl;
import co.marcin.novaguilds.impl.storage.SQLiteStorageImpl;
import co.marcin.novaguilds.impl.storage.YamlStorageImpl;
import co.marcin.novaguilds.impl.util.PacketExtension1_7Impl;
import co.marcin.novaguilds.impl.util.PacketExtension1_8Impl;
import co.marcin.novaguilds.listener.ChatListener;
import co.marcin.novaguilds.listener.ChestGUIListener;
import co.marcin.novaguilds.listener.DeathListener;
import co.marcin.novaguilds.listener.InventoryListener;
import co.marcin.novaguilds.listener.LoginListener;
import co.marcin.novaguilds.listener.MoveListener;
import co.marcin.novaguilds.listener.PlayerInfoListener;
import co.marcin.novaguilds.listener.PvpListener;
import co.marcin.novaguilds.listener.RegionInteractListener;
import co.marcin.novaguilds.listener.ToolListener;
import co.marcin.novaguilds.listener.VanishListener;
import co.marcin.novaguilds.listener.VaultListener;
import co.marcin.novaguilds.manager.CommandManager;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.GroupManager;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.HologramManager;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.manager.TaskManager;
import co.marcin.novaguilds.util.IOUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import co.marcin.novaguilds.util.VersionUtils;
import com.earth2me.essentials.Essentials;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.confuser.barapi.BarAPI;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NovaGuilds extends JavaPlugin implements NovaGuildsAPI {
	/*
	* Dioricie nasz, ktorys jest w javie, swiec sie bugi Twoje, przyjdz ficzery Twoje,
	* badz kod Twoj jako w gicie tak i w mavenie, stacktrace naszego powszedniego
	* daj nam dzisiaj, i daj nam buildy Twoje, jako i my commity dajemy,
	* i nie wodz nas na wycieki pamieci, ale daj nam Bugi.
	* Escape. ~Bukkit.PL
	* */

	private static NovaGuilds inst;
	private final int build = Integer.parseInt(getDescription().getVersion());

	//Vault
	public Economy econ = null;
	private Essentials essentials;
	private boolean protocolSupportEnabled;

	private GuildManager guildManager;
	private RegionManager regionManager;
	private PlayerManager playerManager;
	private MessageManager messageManager;
	private CommandManager commandManager;
	private ConfigManager configManager;
	private GroupManager groupManager;
	private static final String logPrefix = "[NovaGuilds]";
	private final String commit = getResource("commit.yml")==null ? "invalid" : IOUtils.inputStreamToString(getResource("commit.yml"));

	public final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	public final List<NovaGuild> guildRaids = new ArrayList<>();
	private static boolean raidRunnableRunning = false;
	private co.marcin.novaguilds.api.util.packet.PacketExtension packetExtension;

	private VanishPlugin vanishNoPacket;
	private final HologramManager hologramManager = new HologramManager(new File(getDataFolder(), "holograms.yml"));
	private RankManager rankManager;
	private TaskManager taskManager;
	private Storage storage;

	public void onEnable() {
		inst = this;

		//managers
		taskManager = new TaskManager();
		configManager = new ConfigManager();
		messageManager = new MessageManager();

		if(!getMessageManager().load()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		LoggerUtils.info("Messages loaded: " + Config.LANG_NAME.getString());

		commandManager = new CommandManager();
		guildManager = new GuildManager();
		playerManager = new PlayerManager();
		regionManager = new RegionManager();
		groupManager = new GroupManager();
		rankManager = new RankManager();

		if(!checkDependencies()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//Version check
		VersionUtils.checkVersion();

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
			hologramManager.load();
		}

		//Listeners
		new LoginListener();
		new ToolListener();
		new RegionInteractListener();
		new MoveListener();
		new ChatListener();
		new PvpListener();
		new DeathListener();
		new InventoryListener();
		new PlayerInfoListener();
		new ChestGUIListener();

		if(Config.PACKETS_ENABLED.getBoolean()) {
			if(ConfigManager.isBukkit18()) {
				new PacketListener1_8Impl();
				packetExtension = new PacketExtension1_8Impl();
			}
			else {
				new PacketListener1_7Impl();
				packetExtension = new PacketExtension1_7Impl();
			}

			//Register players (for reload)
			for(Player p : Bukkit.getOnlinePlayers()) {
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
					Bukkit.getPluginManager().callEvent(interactEntityEvent);
					event.setCancelled(interactEntityEvent.isCancelled());
				}
			}, this);
		}

		if(Config.VAULT_ENABLED.getBoolean()) {
			new VaultListener();
		}

		if(getConfigManager().useVanishNoPacket()) {
			new VanishListener();
		}

		//Tablist/tag update
		TagUtils.refresh();
		TabUtils.refresh();

		//metrics
		setupMetrics();

		LoggerUtils.info("#" + VersionUtils.buildCurrent + " (" + getCommit() + ") Enabled");
	}

	public void setUpStorage() {
		switch(getConfigManager().getDataStorageType()) {
			case MYSQL:
				storage = new MySQLStorageImpl(
						Config.MYSQL_HOST.getString(),
						Config.MYSQL_PORT.getString(),
						Config.MYSQL_DATABASE.getString(),
						Config.MYSQL_USERNAME.getString(),
						Config.MYSQL_PASSWORD.getString()
				);
				break;
			case SQLITE:
				storage = new SQLiteStorageImpl(new File(getDataFolder(), "sqlite.db"));
				break;
			case FLAT:
				storage = new YamlStorageImpl(new File(getDataFolder(), "data/"));
				break;
		}
	}
	
	public void onDisable() {
		getGuildManager().save();
		getRegionManager().save();
		getPlayerManager().save();
		getRankManager().save();
		LoggerUtils.info("Saved all data");

		//Save Holograms
		getHologramManager().save();

		if(Config.PACKETS_ENABLED.getBoolean()) {
			getPacketExtension().unregisterChannel();
		}

		//Stop schedulers
		worker.shutdown();

		//reset barapi
		if(Config.BARAPI_ENABLED.getBoolean()) {
			for(Player player : getServer().getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
		}

		//removing holograms
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			for(Hologram h : HologramsAPI.getHolograms(this)) {
				h.delete();
			}
		}
		
		for(Player p : getServer().getOnlinePlayers()) {
			PlayerManager.getPlayer(p).cancelToolProgress();
		}

		for(NovaPlayer nPlayer : getPlayerManager().getPlayers()) {
			if(nPlayer.getActiveSelection() != null) {
				nPlayer.getActiveSelection().reset();
			}
		}

		//getConfigManager().disable();
		LoggerUtils.info("#" + VersionUtils.buildCurrent + " Disabled");
	}

	public static NovaGuilds getInstance() {
		return inst;
	}
	
	//Managers
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
	public Storage getStorage() {
		return storage;
	}

	@Override
	public int getBuild() {
		return build;
	}

	@Override
	public RankManager getRankManager() {
		return rankManager;
	}

	@Override
	public PacketExtension getPacketExtension() {
		return packetExtension;
	}

	//Vault economy
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

		if(rsp == null) {
			return false;
		}

		econ = rsp.getProvider();
		return econ != null;
	}

	//VanishNoPacket
	private boolean checkVanishNoPacket() {
		Plugin p = getServer().getPluginManager().getPlugin("VanishNoPacket");

		if(p instanceof VanishPlugin) {
			vanishNoPacket = (VanishPlugin) p;
		}

		return vanishNoPacket != null;
	}

	private void setupMetrics() {
		try {
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
		catch(IOException e) {
			LoggerUtils.info("Failed to update stats!");
			LoggerUtils.info(e.getMessage());
		}
	}

	public void showRaidBar(NovaRaid raid) {
		if(raid.getFinished()) {
			raid.getGuildAttacker().removeRaidBar();
			raid.getGuildDefender().removeRaidBar();
		}
		else {
			List<Player> players = raid.getGuildAttacker().getOnlinePlayers();
			players.addAll(raid.getGuildDefender().getOnlinePlayers());

			for(Player player : players) {
				if(Config.BARAPI_ENABLED.getBoolean()) {
					BarAPI.setMessage(player, Message.BARAPI_WARPROGRESS.setVar(VarKey.DEFENDER, raid.getGuildDefender().getName()).get(), raid.getProgress());
				}
				else {
					//TODO
					if(raid.getProgress() == 0 || raid.getProgress() % 10 == 0 || raid.getProgress() >= 90) {
						String lines;
						if(raid.getProgress() == 0) {
							lines = "&f";
						}
						else {
							lines = "&4";
						}

						for(int i = 1; i <= 100; i++) {
							lines += "|";
							if(i == raid.getProgress()) {
								lines += "&f";
							}
						}

						MessageManager.sendPrefixMessage(player, lines);
					}
				}
			}
		}
	}

	private boolean checkDependencies() {
		//Vault Economy
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
			LoggerUtils.error("Disabled due to no Vault dependency found!");
			Config.HOLOGRAPHICDISPLAYS_ENABLED.set(false);
			Config.BARAPI_ENABLED.set(false);
			return false;
		}
		LoggerUtils.info("Vault hooked");

		if(!setupEconomy()) {
			LoggerUtils.error("Could not setup Vault's economy, disabling");
			Config.HOLOGRAPHICDISPLAYS_ENABLED.set(false);
			Config.BARAPI_ENABLED.set(false);
			return false;
		}
		LoggerUtils.info("Vault's Economy hooked");

		//HolographicDisplays
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			//Try to find the API
			boolean apiFound;
			try {
				Class.forName("com.gmail.filoghost.holographicdisplays.api.HologramsAPI");
				apiFound = true;
			}
			catch(ClassNotFoundException e) {
				apiFound = false;
			}

			if(getServer().getPluginManager().getPlugin("HolographicDisplays") == null || !apiFound) {
				LoggerUtils.error("Couldn't find HolographicDisplays plugin, disabling this feature.");
				Config.HOLOGRAPHICDISPLAYS_ENABLED.set(false);
			}
			else {
				LoggerUtils.info("HolographicDisplays hooked");
			}
		}

		//BarAPI
		if(Config.BARAPI_ENABLED.getBoolean()) {
			if(getServer().getPluginManager().getPlugin("BarAPI") == null) {
				LoggerUtils.error("Couldn't find BarAPI plugin, disabling this feature.");
				Config.BARAPI_ENABLED.set(false);
			}
			else {
				LoggerUtils.info("BarAPI hooked");
			}
		}

		//VanishNoPacket
		if(checkVanishNoPacket()) {
			LoggerUtils.info("VanishNoPacket hooked");
		}
		else {
			LoggerUtils.info("VanishNoPacket not found, support disabled");
			getConfigManager().disableVanishNoPacket();
		}

		//NorthTab
		if(Config.TABLIST_ENABLED.getBoolean()) {
			if(ConfigManager.isBukkit18()) {
				if(getServer().getPluginManager().getPlugin("NorthTab") == null) {
					LoggerUtils.error("Couldn't find NorthTab plugin, disabling 1.8 tablist.");
					Config.TABLIST_ENABLED.set(false);
				}
				else {
					LoggerUtils.info("NorthTab hooked");
				}
			}
		}

		//ProtocolSupport
		protocolSupportEnabled = getServer().getPluginManager().getPlugin("ProtocolSupport") != null;
		if(isProtocolSupportEnabled()) {
			LoggerUtils.info("Found ProtocolSupport plugin!");
		}

		//Essentials
		essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

		return true;
	}

	public static String getLogPrefix() {
		return logPrefix;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void setCommandManager(CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	public ScheduledExecutorService getWorker() {
		return worker;
	}

	public VanishPlugin getVanishNoPacket() {
		return vanishNoPacket;
	}

	public static boolean isRaidRunnableRunning() {
		return raidRunnableRunning;
	}

	public static void setRaidRunnableRunning(boolean raidRunnableRunning) {
		NovaGuilds.raidRunnableRunning = raidRunnableRunning;
	}

	public static void runTaskLater(Runnable runnable, long delay, TimeUnit timeUnit) {
		Bukkit.getScheduler().runTaskLater(inst, runnable, timeUnit.toSeconds(delay) * 20);
	}

	public String getCommit() {
		return StringUtils.substring(commit, 0, 7);
	}

	public Essentials getEssentials() {
		return essentials;
	}

	public boolean isEssentialsEnabled() {
		return essentials != null;
	}

	public boolean isProtocolSupportEnabled() {
		return protocolSupportEnabled;
	}
}
