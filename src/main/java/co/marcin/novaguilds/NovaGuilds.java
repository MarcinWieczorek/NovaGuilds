package co.marcin.novaguilds;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.listener.*;
import co.marcin.novaguilds.manager.*;
import co.marcin.novaguilds.runnable.RunnableAutoSave;
import co.marcin.novaguilds.runnable.RunnableLiveRegeneration;
import co.marcin.novaguilds.runnable.RunnableTeleportRequest;
import co.marcin.novaguilds.util.*;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.confuser.barapi.BarAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NovaGuilds extends JavaPlugin {
	/*
	* Dioricie nasz, ktorys jest w javie, swiec sie bugi Twoje, przyjdz ficzery Twoje,
	* badz kod Twoj jako w gicie tak i w mavenie, stacktrace naszego powszedniego
	* daj nam dzisiaj, i daj nam buildy Twoje, jako i my commity dajemy,
	* i nie wodz nas na wycieki pamieci, ale daj nam Bugi.
	* Escape. ~Bukkit.PL
	* */
	private static NovaGuilds inst;
	public final PluginDescriptionFile pdf = getDescription();
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
	public boolean updateAvailable = false;

	//TODO: test scheduler
	public final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	public final List<NovaGuild> guildRaids = new ArrayList<>();

	//Database
	private DatabaseManager databaseManager;

	public void onEnable() {
		inst = this;
		//TODO not working
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

		if(!getMessageManager().loadMessages()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		//managers
		configManager = new ConfigManager(this);
		LoggerUtils.info("[ConfigManager] Enabled");
		commandManager = new CustomCommandManager(this);
		LoggerUtils.info("[CommandManager] Enabled");
		groupManager = new GroupManager(this);
		LoggerUtils.info("[GroupManager] Enabled");

		tagUtils = new TagUtils(this);
		databaseManager = new DatabaseManager(this);

		if(!checkDependencies()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		LoggerUtils.info("Messages loaded");
        
		//Version check
        checkVersion();

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
			}
		}

		//Tables setup
		if(getConfigManager().getDataStorageType() != DataStorageType.FLAT) {
			databaseManager.setupTables();
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

		//Listeners
		new LoginListener(this);
		new ToolListener(this);
		new RegionInteractListener(this);
		new MoveListener(this);
		new ChatListener(this);
		new PvpListener(this);
		new DeathListener(this);
		new InventoryListener(this);

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
		if(updateAvailable) {
			getMessageManager().broadcastMessageForPermitted("chat.update","novaguilds.admin.updateavailable");
		}

		LoggerUtils.info("#"+pdf.getVersion()+" Enabled");
	}
	
	public void onDisable() {
		getGuildManager().saveAll();
		getRegionManager().saveAll();
		getPlayerManager().saveAll();
		LoggerUtils.info("Saved all data");

		//Stop schedulers
		worker.shutdown();

		//reset barapi
		if(getConfigManager().useBarAPI()) {
			for(Player player : getServer().getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
		}

		//removing holographic displays
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
					RegionUtils.resetCorner(p, l1);
					RegionUtils.resetCorner(p, l2);

					if(nPlayer.getSelectedRegion() != null) {
						RegionUtils.highlightRegion(p, nPlayer.getSelectedRegion(), null);
					}
				}
			}
		}

		//getConfigManager().disable();
		LoggerUtils.info("#" + pdf.getVersion() + " Disabled");
	}

	public static NovaGuilds getInst() {
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

	//HolographicDisplays
	public boolean checkHolographicDisplays() {
		return getServer().getPluginManager().getPlugin("HolographicDisplays") != null;
	}
	
	public void runSaveScheduler() {
		Runnable task = new RunnableAutoSave(this);
		worker.schedule(task, getConfigManager().getSaveInterval(), TimeUnit.SECONDS);
	}

	public void runLiveRegenerationTask() {
		Runnable task = new RunnableLiveRegeneration(this);
		worker.schedule(task, getConfigManager().getGuildLiveRegenerationTaskInterval(), TimeUnit.SECONDS);
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

	public void setWarBar(NovaGuild guild, float percent, NovaGuild defender) {
		String msg = getMessageManager().getMessagesString("barapi.warprogress");
		msg = StringUtils.replace(msg, "{DEFENDER}", defender.getName());

		for(Player player : guild.getOnlinePlayers()) {
			if(getConfigManager().useBarAPI()) {
				BarAPI.setMessage(player, StringUtils.fixColors(msg), percent);
			}
			else {
				//TODO
				getMessageManager().sendPrefixMessage(player,msg);
			}
		}
	}

	public void resetWarBar(NovaGuild guild) {
		if(getConfigManager().useBarAPI()) {
			for(NovaPlayer nPlayer : guild.getPlayers()) {
				if(nPlayer.isOnline()) {
					BarAPI.removeBar(nPlayer.getPlayer());
				}
			}
		}
	}

	public void delayedTeleport(Player player, Location location, String path) {
		Runnable task = new RunnableTeleportRequest(this,player,location,path);
		worker.schedule(task,getGroupManager().getGroup(player).getGuildTeleportDelay(),TimeUnit.SECONDS);

		if(getGroupManager().getGroup(player).getGuildTeleportDelay() > 0) {
			getMessageManager().sendDelayedTeleportMessage(player);
		}
	}

	public boolean isBankItemStack(ItemStack itemStack) {
		return itemStack.equals(getConfigManager().getGuildBankItem());
		//return itemStack.hasItemMeta() && itemStack.getItemMeta().getDisplayName().contains("Guild's Bank") && itemStack.getType()==Material.CHEST;
	}

	public void appendBankHologram(NovaGuild guild) {
		if(getConfigManager().useHolographicDisplays()) {
			if(getConfigManager().isGuildBankHologramEnabled()) {
				if(guild.getBankHologram() == null) {
					Location hologramLocation = guild.getBankLocation().clone();

					double x = hologramLocation.getX() > 0 ? -0.5 : 0.5;
					double z = hologramLocation.getZ() > 0 ? 0.5 : -0.5;

					hologramLocation.add(x, 2, z);
					Hologram hologram = HologramsAPI.createHologram(this, hologramLocation);
					for(String hologramLine : getConfigManager().getGuildBankHologramLines()) {
						if(hologramLine.startsWith("[ITEM]")) {
							hologramLine = hologramLine.substring(6);
							LoggerUtils.debug(hologramLine);
							ItemStack itemStack = ItemStackUtils.stringToItemStack(hologramLine);
							if(itemStack != null) {
								hologram.appendItemLine(itemStack);
							}
						}
						else {
							hologram.appendTextLine(StringUtils.fixColors(hologramLine));
						}
					}

					guild.setBankHologram(hologram);
				}
			}
		}
	}

	public boolean isBankBlock(Block block) {
		if(block.getType()== getConfigManager().getGuildBankItem().getType()) {
			for(NovaGuild guild : getGuildManager().getGuilds()) {
				if(guild.getBankLocation() != null) {
					if(guild.getBankLocation().distance(block.getLocation()) < 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void checkVersion() {
		String latest = StringUtils.getContent("http://novaguilds.marcin.co/latest.info");
		LoggerUtils.info("You're using build: #"+pdf.getVersion());
		LoggerUtils.info("Latest stable build of the plugin is: #"+latest);

		int latestint = 0;
		if(NumberUtils.isNumeric(latest)) {
			latestint = Integer.parseInt(latest);
		}

		int version = 0;
		if(NumberUtils.isNumeric(pdf.getVersion())) {
			version = Integer.parseInt(pdf.getVersion());
		}

		if(version == latestint) {
			LoggerUtils.info("Your plugin build is the latest stable one");
		}
		else if(version > latestint) {
			String dev = StringUtils.getContent("http://novaguilds.marcin.co/dev.info");
			int devVersion = 0;
			if(NumberUtils.isNumeric(dev)) {
				devVersion = Integer.parseInt(dev);
			}

			if(version > devVersion) {
				LoggerUtils.info("You are using unreleased build #" + version);
			}
			else if(version == devVersion) {
				LoggerUtils.info("You're using latest development build");
			}
			else {
				LoggerUtils.info("Why the hell are you using outdated dev build?");
			}
		}
		else {
			LoggerUtils.info("You should update your plugin to #"+latest+"!");
			updateAvailable = true;
		}
	}

	public boolean checkDependencies() {
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

		return true;
	}

	public int getBuild() {
		return build;
	}

	public static String getLogPrefix() {
		return logPrefix;
	}

	//Utils
}
