package co.marcin.novaguilds;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.listener.*;
import co.marcin.novaguilds.manager.*;
import co.marcin.novaguilds.runnable.RunnableAutoSave;
import co.marcin.novaguilds.runnable.RunnableLiveRegeneration;
import co.marcin.novaguilds.runnable.RunnableTeleportRequest;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.RegionUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TagUtils;
import code.husky.mysql.MySQL;
import code.husky.sqlite.SQLite;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.confuser.barapi.BarAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;
import java.sql.*;
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
	public final PluginDescriptionFile pdf = this.getDescription();
	private final PluginManager pm = getServer().getPluginManager();
	
	private long MySQLReconnectStamp = System.currentTimeMillis();

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

	public TagUtils tagUtils;
	public boolean updateAvailable = false;

	//TODO: test scheduler
	public final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	public final List<NovaGuild> guildRaids = new ArrayList<>();

	//Database
	private MySQL MySQL;
	public Connection c = null;

	public void onEnable() {
		inst = this;

		//managers
		commandManager = new CustomCommandManager(this);
		configManager = new ConfigManager(this);
		groupManager = new GroupManager(this);

		tagUtils = new TagUtils(this);

		//HolographicDisplays
		if(getConfigManager().useHolographicDisplays()) {
			if(!checkHolographicDisplays()) {
				ConfigManager.getLogger().severe(getConfigManager().getLogPrefix() + "Couldn't find HolographicDisplays plugin, disabling this feature.");
				getConfigManager().disableHolographicDisplays();
			}
			else {
				info("HolographicDisplays hooked");
			}
		}
		
		//Vault Economy
		if(!checkVault()) {
			ConfigManager.getLogger().severe(getConfigManager().getLogPrefix()+"Disabled due to no Vault dependency found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		info("Vault hooked");

		if(!setupEconomy()) {
			ConfigManager.getLogger().severe(getConfigManager().getLogPrefix()+"Could not setup Vault's economy, disabling");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		info("Vault's Economy hooked");

		//BarAPI
		if(getConfigManager().useBarAPI()) {
			if(!checkBarAPI()) {
				ConfigManager.getLogger().severe(getConfigManager().getLogPrefix() + "Couldn't find BarAPI plugin, disabling this feature.");
				getConfigManager().disableBarAPI();
			} else {
				info("BarAPI hooked");
			}
		}
		
		if(!getMessageManager().loadMessages()) {
			getServer().getPluginManager().disablePlugin(this);
            return;
		}

		info("Messages loaded");
        
		//Version check
        String latest = StringUtils.getContent("http://novaguilds.marcin.co/latest.info");
        info("You're using build: #"+pdf.getVersion());
        info("Latest build of the plugin is: #"+latest);

		int latestint = 0;
		if(NumberUtils.isNumeric(latest)) {
			latestint = Integer.parseInt(latest);
		}

		int version = 0;
		if(NumberUtils.isNumeric(pdf.getVersion())) {
			version = Integer.parseInt(pdf.getVersion());
		}

        if(version == latestint) {
        	info("Your plugin build is the latest one");
        }
        else if(version > latestint) {
	        String dev = StringUtils.getContent("http://novaguilds.marcin.co/dev.info");
	        int devVersion = 0;
	        if(NumberUtils.isNumeric(dev)) {
		        devVersion = Integer.parseInt(dev);
	        }

	        if(version > devVersion) {
		        info("You are using unreleased build #" + version);
	        }
	        else if(version == devVersion) {
		        info("You're using latest development build");
	        }
	        else {
		        info("Why the hell are you using outdated dev build?");
	        }
        }
        else {
        	info("You should update your plugin to #"+latest+"!");
			updateAvailable = true;
        }

		try {
			if(getConfigManager().getDataStorageType() == DataStorageType.FLAT) {
				flatDataManager = new FlatDataManager(this);
			}

			if(getConfigManager().getDataStorageType() == DataStorageType.MYSQL) {
				MySQL = new MySQL(this, getConfig().getString("mysql.host") , getConfig().getString("mysql.port"), getConfig().getString("mysql.database"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"));
				c = MySQL.openConnection();
				info("Connected to MySQL database");
			}
			else if(getConfigManager().getDataStorageType() == DataStorageType.SQLITE) {
				SQLite sqlite = new SQLite(this, "sqlite.db");
				c = sqlite.openConnection();
				info("Connected to SQLite database");
			}

			//Tables setup
			if(getConfigManager().getDataStorageType() != DataStorageType.FLAT) {
				DatabaseMetaData md = c.getMetaData();
				ResultSet rs = md.getTables(null, null, getConfigManager().getDatabasePrefix() + "%", null);
				if(!rs.next()) {
					info("Couldn't find tables in the base. Creating...");
					String[] SQLCreateCode = getSQLCreateCode();
					if(SQLCreateCode.length != 0) {
						try {
							for(String tableCode : SQLCreateCode) {
								createTable(tableCode);
								info("Tables added to the database!");
							}
						}
						catch(SQLException e) {
							info("Could not create tables. Disabling");
							info("SQLException: " + e.getMessage());
							getServer().getPluginManager().disablePlugin(this);
							return;
						}
					}
					else {
						info("Couldn't find SQL create code for tables!");
						getServer().getPluginManager().disablePlugin(this);
						return;
					}
				}
				else {
					info("No database config required.");
				}
			}
			
			//Data loading
			getRegionManager().loadRegions();
			info("Regions data loaded");
			getGuildManager().loadGuilds();
			info("Guilds data loaded");
			getPlayerManager().loadPlayers();
			info("Players data loaded");

			info("Post checks running");
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
			info("Save scheduler is running");

			//live regeneration task
			runLiveRegenerationTask();
			info("Live regeneration task is running");

			//metrics
			setupMetrics();

			//Notify admins if there's an update (only for reload)
			if(updateAvailable) {
				getMessageManager().broadcastMessageForPermitted("chat.update","novaguilds.admin.updateavailable");
			}

			info("#"+pdf.getVersion()+" Enabled");
		}
		catch (SQLException e) {
			info("MySQL connection failed.");
			info(e.getMessage());
			pm.disablePlugin(this);
		}
		catch (ClassNotFoundException e) {
			info("MySQL class not found.");
		}
	}
	
	public void onDisable() {
		getGuildManager().saveAll();
		getRegionManager().saveAll();
		getPlayerManager().saveAll();
		info("Saved all data");

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
			Location l1 = nPlayer.getSelectedLocation(0);
			Location l2 = nPlayer.getSelectedLocation(1);
			
			if(l1 != null && l2 != null) {
				RegionUtils.sendSquare(p, l1, l2, null, (byte)-1);
				RegionUtils.resetCorner(p, l1);
				RegionUtils.resetCorner(p, l2);

				if(nPlayer.getSelectedRegion() != null) {
					RegionUtils.resetHighlightRegion(p,nPlayer.getSelectedRegion());
				}
			}
		}

		//getConfigManager().disable();
		info("#"+pdf.getVersion()+" Disabled");
	}

	public static NovaGuilds getInst() {
		return inst;
	}
	
	public void mysqlReload() {
		if(getConfigManager().getDataStorageType() != DataStorageType.MYSQL) return;
		long stamp = System.currentTimeMillis();
		
		if(stamp-MySQLReconnectStamp > 3000) {
	    	try {
				MySQL.closeConnection();
				try {
					c = MySQL.openConnection();
					info("MySQL reconnected");
					MySQLReconnectStamp = System.currentTimeMillis();
				}
				catch (ClassNotFoundException e) {
					info(e.getMessage());
				}
			}
	    	catch (SQLException e1) {
	    		info(e1.getMessage());
			}
		}
    }
	
	public void info(String msg) {
		ConfigManager.getLogger().info(getConfigManager().getLogPrefix()+msg);
	}

	public void debug(String msg) {
		if(getConfigManager().isDebugEnabled()) {
			ConfigManager.getLogger().info(getConfigManager().getLogPrefix() + "[DEBUG] " + msg);
		}
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
	
	//true=mysql, false=sqlite
	private String[] getSQLCreateCode() {
		int index = getConfigManager().getDataStorageType()==DataStorageType.MYSQL ? 0 : 1;

		String url = "http://novaguilds.marcin.co/sqltables.txt";
		String sql = StringUtils.getContent(url);
		
		String[] types = sql.split("--TYPE--");
		return types[index].split("--");
	}
	
	private void createTable(String sql) throws SQLException {
		mysqlReload();
		Statement statement;
		sql = StringUtils.replace(sql, "{SQLPREFIX}", getConfigManager().getDatabasePrefix());
		statement = c.createStatement();
		statement.executeUpdate(sql);
	}
	
	public void runSaveScheduler() {
		Runnable task = new RunnableAutoSave(this);
		worker.schedule(task, getConfigManager().getSaveInterval(), TimeUnit.MINUTES);
	}

	public void runLiveRegenerationTask() {
		Runnable task = new RunnableLiveRegeneration(this);
		worker.schedule(task, getConfigManager().getGuildLiveRegenerationTaskInterval(), TimeUnit.MINUTES);
	}

	private void setupMetrics() {
		if(checkVault()) {
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
				info("Failed to update stats!");
				info(e.getMessage());
			}
		}
		else {
			info("Vault is not enabled, failed to setup Metrics");
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

	//Utils
	public static long systemSeconds() {
		return System.currentTimeMillis() / 1000;
	}
}
