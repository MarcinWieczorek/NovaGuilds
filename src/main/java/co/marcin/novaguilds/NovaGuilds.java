package co.marcin.novaguilds;

import co.marcin.novaguilds.api.NovaGuildsAPI;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.event.PlayerInteractEntityEvent;
import co.marcin.novaguilds.listener.*;
import co.marcin.novaguilds.manager.*;
import co.marcin.novaguilds.runnable.RunnableAutoSave;
import co.marcin.novaguilds.runnable.RunnableLiveRegeneration;
import co.marcin.novaguilds.util.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.confuser.barapi.BarAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

	//TODO kickowanie z admina dubluje userow gildii
	//TODO @up nie wiem czy aktualne

	//Vault
	public Economy econ = null;

	private final GuildManager guildManager = new GuildManager(this);
	private final RegionManager regionManager = new RegionManager(this);
	private final PlayerManager playerManager = new PlayerManager(this);
	private final MessageManager messageManager = new MessageManager(this);
	private CustomCommandManager commandManager;
	private ConfigManager configManager;
	private GroupManager groupManager;
	private FlatDataManager flatDataManager;
	private static final String logPrefix = "[NovaGuilds]";

	public TagUtils tagUtils;

	//TODO: test scheduler
	public final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	public final List<NovaGuild> guildRaids = new ArrayList<>();

	//Database
	private DatabaseManager databaseManager;
	private VanishPlugin vanishNoPacket;
	private ProtocolManager protocolManager;
	private HologramManager hologramManager;

	public void onEnable() {
		inst = this;

		//managers
		configManager = new ConfigManager(this);

		if(!getMessageManager().loadMessages()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		LoggerUtils.info("Messages loaded");

		commandManager = new CustomCommandManager(this);
		groupManager = new GroupManager(this);

		tagUtils = new TagUtils(this);
		databaseManager = new DatabaseManager(this);

		if(!checkDependencies()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//PlayerInteractEntityEvent
		protocolManager.addPacketListener(
				new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
					@Override
					public void onPacketReceiving(PacketEvent event) {
						if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
							EnumWrappers.EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
							Player player = event.getPlayer();
							Entity entity = event.getPacket().getEntityModifier(event).getValues().get(0);

							if (entity != null) {
								PlayerInteractEntityEvent clickEvent = new PlayerInteractEntityEvent(player, entity, action);
								plugin.getServer().getPluginManager().callEvent(clickEvent);
								event.setCancelled(clickEvent.isCancelled());
							}
						}
					}
				});
        
		//Version check
        VersionUtils.checkVersion();

		int attempts = 0;
		// && (flatDataManager!=null && !flatDataManager.isConnected())
		while(!databaseManager.isConnected()) {
			if(attempts == 2) {
				LoggerUtils.error("Tried to connect twice but failed.");
				break;
			}

			LoggerUtils.info("Connecting to "+ getConfigManager().getDataStorageType().name() +" storage");
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
		getRegionManager().loadRegions();
		LoggerUtils.info("Regions data loaded");
		getGuildManager().loadGuilds();
		LoggerUtils.info("Guilds data loaded");
		getPlayerManager().loadPlayers();
		LoggerUtils.info("Players data loaded");

		LoggerUtils.info("Post checks running");
		getGuildManager().postCheckGuilds();
		getRegionManager().postCheckRegions();

		//HologramManager
		hologramManager = new HologramManager(new File(getDataFolder(), "holograms.yml"));
		hologramManager.load();

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

		if(getConfigManager().isGuildBankEnabled()) {
			new VaultListener(this);
		}

		if(getConfigManager().useVanishNoPacket()) {
			new VanishListener(this);
		}

		//Tablist/tag update
		tagUtils.refreshAll();

		//save scheduler
		runSaveScheduler();
		LoggerUtils.info("Save scheduler is running");

		//live regeneration task
		runLiveRegenerationTask();
		LoggerUtils.info("Live regeneration task is running");

		//metrics
		setupMetrics();

		//Notify admins if there's an update (only for reload)
		if(VersionUtils.updateAvailable) {
			Message.CHAT_UPDATE.broadcast("novaguilds.admin.updateavailable");
		}

		LoggerUtils.info("#" + getBuild() + " Enabled");
	}
	
	public void onDisable() {
		getGuildManager().saveAll();
		getRegionManager().saveAll();
		getPlayerManager().saveAll();
		LoggerUtils.info("Saved all data");

		//Save Holograms
		getHologramManager().save();

		//Stop schedulers
		worker.shutdown();

		//reset barapi
		if(getConfigManager().useBarAPI()) {
			for(Player player : getServer().getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
		}

		//removing holograms
		if(getConfigManager().useHolographicDisplays()) {
			for(Hologram h : HologramsAPI.getHolograms(this)) {
				h.delete();
			}
		}
		
		for(Player p : getServer().getOnlinePlayers()) {
			NovaPlayer nPlayer = getPlayerManager().getPlayer(p);

			if(nPlayer != null) {
				Location l1 = nPlayer.getSelectedLocation(0);
				Location l2 = nPlayer.getSelectedLocation(1);

				if(l1 != null && l2 != null) {
					RegionUtils.sendSquare(p, l1, l2, null, (byte) -1);
					RegionUtils.setCorner(p, l1, null);
					RegionUtils.setCorner(p, l2, null);

					if(nPlayer.getSelectedRegion() != null) {
						RegionUtils.highlightRegion(p, nPlayer.getSelectedRegion(), null);
					}
				}
			}
		}

		//getConfigManager().disable();
		LoggerUtils.info("#" + getBuild() + " Disabled");
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

	public CustomCommandManager getCommandManager() {
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

	//Vault economy
	private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        
        if(rsp == null) {
            return false;
        }
        
        econ = rsp.getProvider();
        return econ != null;
    }

	//Vault
	private boolean checkVault() {
		return getServer().getPluginManager().getPlugin("Vault") != null;
	}

	//BarAPI
	private boolean checkBarAPI() {
		return getServer().getPluginManager().getPlugin("BarAPI") != null;
	}

	//VanishNoPacket
	private boolean checkVanishNoPacket() {
		Plugin p = getServer().getPluginManager().getPlugin("VanishNoPacket");

		if(p instanceof VanishPlugin) {
			vanishNoPacket = (VanishPlugin) p;
		}

		return vanishNoPacket != null;
	}

	//HolographicDisplays
	private boolean checkHolographicDisplays() {
		return getServer().getPluginManager().getPlugin("HolographicDisplays") != null;
	}
	
	private void runSaveScheduler() {
		worker.scheduleAtFixedRate(new RunnableAutoSave(this), Config.SAVEINTERVAL.getSeconds(), Config.SAVEINTERVAL.getSeconds(), TimeUnit.SECONDS);
	}

	private void runLiveRegenerationTask() {
		Runnable task = new RunnableLiveRegeneration(this);
		worker.scheduleAtFixedRate(task, Config.LIVEREGENERATION_TASKINTERVAL.getSeconds(), Config.LIVEREGENERATION_TASKINTERVAL.getSeconds(), TimeUnit.SECONDS);
	}

	private void setupMetrics() {
		try {
			Metrics metrics = new Metrics(this);
			Metrics.Graph weaponsUsedGraph = metrics.createGraph("Guilds and users");

			weaponsUsedGraph.addPlotter(new Metrics.Plotter("Guilds") {
				@Override
				public int getValue() {
					return getGuildManager().getGuilds().size();
				}
			});

			weaponsUsedGraph.addPlotter(new Metrics.Plotter("Users") {
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
			resetWarBar(raid.getGuildAttacker());
			resetWarBar(raid.getGuildDefender());
		}
		else {
			HashMap<String,String> vars = new HashMap<>();
			vars.put("DEFENDER", raid.getGuildDefender().getName());
			List<Player> players = raid.getGuildAttacker().getOnlinePlayers();
			players.addAll(raid.getGuildDefender().getOnlinePlayers());

			for(Player player : players) {
				if(Config.BARAPI_ENABLED.getBoolean()) {
					BarAPI.setMessage(player, Message.BARAPI_WARPROGRESS.vars(vars).get(), raid.getProgress());
				}
				else {
					//TODO
					if(raid.getProgress() == 0 || raid.getProgress()%10 == 0 || raid.getProgress() >= 90) {
						String lines;
						if(raid.getProgress() == 0) {
							lines = "&f";
						}
						else {
							lines = "&4";
						}

						for(int i=1; i<=100; i++) {
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

	public void resetWarBar(NovaGuild guild) {
		if(getConfigManager().useBarAPI()) {
			for(Player player : guild.getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
		}
	}

	private boolean checkDependencies() {
		//HolographicDisplays
		if(getConfigManager().useHolographicDisplays()) {
			if(!checkHolographicDisplays()) {
				LoggerUtils.error("Couldn't find HolographicDisplays plugin, disabling this feature.");
				getConfigManager().disableHolographicDisplays();
			}
			else {
				LoggerUtils.info("HolographicDisplays hooked");
			}
		}

		//Vault Economy
		if(!checkVault()) {
			LoggerUtils.error("Disabled due to no Vault dependency found!");
			return false;
		}
		LoggerUtils.info("Vault hooked");

		if(!setupEconomy()) {
			LoggerUtils.error("Could not setup Vault's economy, disabling");
			return false;
		}
		LoggerUtils.info("Vault's Economy hooked");

		//BarAPI
		if(getConfigManager().useBarAPI()) {
			if(!checkBarAPI()) {
				LoggerUtils.error("Couldn't find BarAPI plugin, disabling this feature.");
				getConfigManager().disableBarAPI();
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

		//ProtocolLib
		if(getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
			protocolManager = ProtocolLibrary.getProtocolManager();
			LoggerUtils.info("ProtocolLib hooked");
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

	public void setCommandManager(CustomCommandManager commandManager) {
		this.commandManager = commandManager;
	}

	public ScheduledExecutorService getWorker() {
		return worker;
	}

	public VanishPlugin getVanishNoPacket() {
		return vanishNoPacket;
	}
}
