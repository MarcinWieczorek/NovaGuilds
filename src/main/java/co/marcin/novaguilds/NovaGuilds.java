/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
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
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.EntityUseAction;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.listener.ChatListener;
import co.marcin.novaguilds.listener.ChestGUIListener;
import co.marcin.novaguilds.listener.DeathListener;
import co.marcin.novaguilds.listener.InventoryListener;
import co.marcin.novaguilds.listener.LoginListener;
import co.marcin.novaguilds.listener.MoveListener;
import co.marcin.novaguilds.listener.PacketListener;
import co.marcin.novaguilds.listener.PlayerInfoListener;
import co.marcin.novaguilds.listener.PvpListener;
import co.marcin.novaguilds.listener.RegionInteractListener;
import co.marcin.novaguilds.listener.ToolListener;
import co.marcin.novaguilds.listener.VanishListener;
import co.marcin.novaguilds.listener.VaultListener;
import co.marcin.novaguilds.manager.CommandManager;
import co.marcin.novaguilds.manager.ConfigManager;
import co.marcin.novaguilds.manager.DatabaseManager;
import co.marcin.novaguilds.manager.FlatDataManager;
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
import co.marcin.novaguilds.util.TagUtils;
import co.marcin.novaguilds.util.VersionUtils;
import co.marcin.novaguilds.util.reflect.PacketExtension;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.confuser.barapi.BarAPI;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private GuildManager guildManager;
	private RegionManager regionManager;
	private PlayerManager playerManager;
	private MessageManager messageManager;
	private CommandManager commandManager;
	private ConfigManager configManager;
	private GroupManager groupManager;
	private FlatDataManager flatDataManager;
	private static final String logPrefix = "[NovaGuilds]";
	private final String commit = getResource("commit.yml")==null ? "invalid" : IOUtils.inputStreamToString(getResource("commit.yml"));

	public final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	public final List<NovaGuild> guildRaids = new ArrayList<>();
	private static boolean raidRunnableRunning = false;

	//Database
	private DatabaseManager databaseManager;
	private VanishPlugin vanishNoPacket;
	private HologramManager hologramManager = new HologramManager(new File(getDataFolder(), "holograms.yml"));
	private RankManager rankManager;
	private TaskManager taskManager;

	public void onEnable() {
		inst = this;

		//managers
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
		databaseManager = new DatabaseManager();
		taskManager = new TaskManager();

		if(!checkDependencies()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//Version check
		VersionUtils.checkVersion();

		int attempts = 0;
		while(!databaseManager.isConnected()) {
			if(attempts == 2) {
				LoggerUtils.error("Tried to connect twice but failed.");
				break;
			}

			LoggerUtils.info("Connecting to " + getConfigManager().getDataStorageType().name() + " storage");
			attempts++;

			if(getConfigManager().getDataStorageType() == DataStorageType.MYSQL) {
				databaseManager.connectToMysql();
			}

			if(getConfigManager().getDataStorageType() == DataStorageType.SQLITE) {
				databaseManager.connectToSQLite();
			}

			if(getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
				flatDataManager = new FlatDataManager(this);
				if(flatDataManager.isConnected()) {
					LoggerUtils.info("Connected to FLAT storage.");
					break;
				}
				else {
					getConfigManager().setToSecondaryDataStorageType();
				}
			}
		}

		//Data loading
		getRegionManager().load();
		LoggerUtils.info("Regions data loaded");
		getGuildManager().load();
		LoggerUtils.info("Guilds data loaded");
		getRankManager().loadDefaultRanks();
		getPlayerManager().load();
		LoggerUtils.info("Players data loaded");

		LoggerUtils.info("Post checks running");
		getGuildManager().postCheck();
		getRegionManager().postCheck();

		getRankManager().load();
		LoggerUtils.info("Ranks data loaded");

		//HologramManager
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			hologramManager.load();
		}

		//Listeners
		new LoginListener(this);
		new ToolListener(this);
		new RegionInteractListener(this);
		new MoveListener(this);
		new ChatListener(this);
		new PvpListener(this);
		new DeathListener(this);
		new InventoryListener(this);
		new PlayerInfoListener(this);
		new ChestGUIListener();

		if(Config.PACKETS_ENABLED.getBoolean()) {
			new PacketListener(this);

			//Register players (for reload)
			for(Player p : Bukkit.getOnlinePlayers()) {
				PacketExtension.registerPlayer(p);
			}
		}
		else {
			if(Config.TABLIST_ENABLED.getBoolean()) {
				Config.TABLIST_ENABLED.set(false);
			}

			getServer().getPluginManager().registerEvents(new Listener() {
				@EventHandler
				public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
					co.marcin.novaguilds.event.PlayerInteractEntityEvent clickEvent = new co.marcin.novaguilds.event.PlayerInteractEntityEvent(event.getPlayer(), event.getRightClicked(), EntityUseAction.INTERACT);
					getServer().getPluginManager().callEvent(clickEvent);
				}
			}, this);
		}

		if(Config.VAULT_ENABLED.getBoolean()) {
			new VaultListener(this);
		}

		if(getConfigManager().useVanishNoPacket()) {
			new VanishListener(this);
		}

		//Tablist/tag update
		TagUtils.refreshAll();

		//save scheduler
		taskManager.startTask(TaskManager.Task.AUTOSAVE);
		LoggerUtils.info("Save scheduler is running");

		//live regeneration task
		taskManager.startTask(TaskManager.Task.LIVEREGENERATION);
		LoggerUtils.info("Live regeneration task is running");

		//Hologram refresh task
		if(Config.HOLOGRAPHICDISPLAYS_ENABLED.getBoolean()) {
			taskManager.startTask(TaskManager.Task.HOLOGRAM_REFRESH);
		}

		//Inactive cleaner task
		if(Config.CLEANUP_ENABLED.getBoolean()) {
			taskManager.startTask(TaskManager.Task.CLEANUP);
			LoggerUtils.info("Cleanup task started.");
		}

		//metrics
		setupMetrics();

		LoggerUtils.info("#" + VersionUtils.buildCurrent + " (" + getCommit() + ") Enabled");
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
			PacketExtension.unregisterNovaGuildsChannel();
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
			getPlayerManager().getPlayer(p).cancelToolProgress();
		}

		//getConfigManager().disable();
		LoggerUtils.info("#" + VersionUtils.buildCurrent + " Disabled");
	}

	public static NovaGuilds getInstance() {
		return inst;
	}
	
	//Managers
	public GuildManager getGuildManager() {
		return guildManager;
	}

	public RegionManager getRegionManager() {
		return regionManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	public HologramManager getHologramManager() {
		return hologramManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public FlatDataManager getFlatDataManager() {
		return flatDataManager;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public TaskManager getTaskManager() {
		return taskManager;
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
			Map<String, String> vars = new HashMap<>();
			vars.put("DEFENDER", raid.getGuildDefender().getName());
			List<Player> players = raid.getGuildAttacker().getOnlinePlayers();
			players.addAll(raid.getGuildDefender().getOnlinePlayers());

			for(Player player : players) {
				if(Config.BARAPI_ENABLED.getBoolean()) {
					BarAPI.setMessage(player, Message.BARAPI_WARPROGRESS.vars(vars).get(), raid.getProgress());
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

		return true;
	}

	public int getBuild() {
		return build;
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

	public RankManager getRankManager() {
		return rankManager;
	}

	public static void runTaskLater(Runnable runnable, long delay, TimeUnit timeUnit) {
		Bukkit.getScheduler().runTaskLater(inst, runnable, timeUnit.toSeconds(delay) * 20);
	}

	public String getCommit() {
		return StringUtils.substring(commit, 0, 7);
	}
}
