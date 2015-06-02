package co.marcin.novaguilds;

import co.marcin.novaguilds.basic.NovaGroup;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.command.*;
import co.marcin.novaguilds.listener.*;
import co.marcin.novaguilds.manager.CustomCommandManager;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.manager.RegionManager;
import co.marcin.novaguilds.runnable.RunnableAutoSave;
import co.marcin.novaguilds.runnable.RunnableLiveRegeneration;
import co.marcin.novaguilds.runnable.RunnableTeleportRequest;
import co.marcin.novaguilds.utils.StringUtils;
import co.marcin.novaguilds.utils.TagUtils;

import code.husky.mysql.MySQL;
import code.husky.sqlite.SQLite;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.confuser.barapi.BarAPI;

import net.milkbowl.vault.Metrics;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class NovaGuilds extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	private static final String logprefix = "[NovaGuilds] ";
	public final PluginDescriptionFile pdf = this.getDescription();
	private final PluginManager pm = getServer().getPluginManager();
	private String prefix;
	public String sqlp;
	public final boolean DEBUG = getConfig().getBoolean("debug");
	
	private long MySQLReconnectStamp = System.currentTimeMillis();

	//TODO kickowanie z admina dubluje userów gildii

	//Vault
	public Economy econ = null;

	public boolean useHolographicDisplays;
	private boolean useBarAPI;
	
	private boolean useMySQL;
	public String lang;
	
	public final HashMap<String,NovaPlayer> players = new HashMap<>();
	public final HashMap<String,NovaGuild> guilds = new HashMap<>();
	public final HashMap<String,NovaRegion> regions = new HashMap<>();
	public final HashMap<String,NovaGroup> groups = new HashMap<>();
	
	private final GuildManager guildManager = new GuildManager(this);
	private final RegionManager regionManager = new RegionManager(this);
	private final PlayerManager playerManager = new PlayerManager(this);
	private final CustomCommandManager commandManager = new CustomCommandManager(this);

	public long timeRest;
	public int savePeriod; //minutes
	public long timeInactive;
	public long liveRegenerationTime; //minutes

	public TagUtils tagUtils;

	//TODO: test scheduler
	public final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	public final List<NovaGuild> guildRaids = new ArrayList<>();

	//Database
	private MySQL MySQL;
	public Connection c = null;
	
	private FileConfiguration messages = null;
	private File messagesFile;
	public double distanceFromSpawn;

	public void onEnable() {
		saveDefaultConfig();
		sqlp = getConfig().getString("mysql.prefix");
		savePeriod = getConfig().getInt("saveperiod");
		lang = getConfig().getString("lang");

		timeRest = getConfig().getLong("raid.timerest");
		distanceFromSpawn = getConfig().getLong("guild.fromspawn");
		timeInactive = getConfig().getLong("raid.timeinactive");

		String liveRegenerationString = getConfig().getString("liveregenerationtime");
		liveRegenerationTime = StringUtils.StringToSeconds(liveRegenerationString);
		//TODO

		useHolographicDisplays = getConfig().getBoolean("holographicdisplays.enabled");
		useBarAPI = getConfig().getBoolean("barapi.enabled");

		useMySQL = getConfig().getBoolean("usemysql");

		//load groups
		loadGroups();

		tagUtils = new TagUtils(this);

		//HolographicDisplays
		if(useHolographicDisplays) {
			if(!checkHolographicDisplays()) {
				log.severe(logprefix + "Couldn't find HolographicDisplays plugin, disabling this feature.");
				useHolographicDisplays = false;
			}
			else {
				info("HolographicDisplays hooked");
			}
		}
		
		
		//Vault Economy
		if(!checkVault()) {
			log.severe(logprefix+"Disabled due to no Vault dependency found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if(!setupEconomy()) {
			log.severe(logprefix+"Could not setup Vault's economy, disabling");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		info("Vault hooked");
		info("Vault's Economy hooked");

		//BarAPI
		if(useBarAPI) {
			if(!checkBarAPI()) {
				log.severe(logprefix + "Couldn't find BarAPI plugin, disabling this feature.");
				useBarAPI = false;
			} else {
				info("BarAPI hooked");
			}
		}
		
		//messages.yml
		File langsDir = new File(getDataFolder(),"lang/");
		if(!langsDir.exists()) {
			if(langsDir.mkdir()) {
				info("Language dir created");
			}
		}
		
		if(!loadMessages()) {
            return;
		}
		
		prefix = messages.getString("chat.prefix");
		info("Messages loaded");
        
		//Version check
        String latest = StringUtils.getContent("http://novaguilds.marcin.co/latest.info");
        info("You're using build: #"+pdf.getVersion());
        info("Latest build of the plugin is: #"+latest);

		int latestint = 0;
		if(StringUtils.isNumeric(latest)) {
			latestint = Integer.parseInt(latest);
		}

		int version = 0;
		if(StringUtils.isNumeric(pdf.getVersion())) {
			version = Integer.parseInt(pdf.getVersion());
		}

        if(version == latestint) {
        	info("Your plugin build is the latest one");
        }
        else if(version > latestint) {
	        info("You are using unreleased build #"+version);
        }
        else {
        	info("You should update your plugin to #"+latest+"!");
        }
		
		//command executors
		getCommand("novaguilds").setExecutor(new CommandNovaGuilds(this));
		getCommand("ng").setExecutor(new CommandNovaGuilds(this));
		getCommand("nga").setExecutor(new CommandAdmin(this));
		
		getCommand("abandon").setExecutor(new CommandGuildAbandon(this));
		getCommand("guild").setExecutor(new CommandGuild(this));
		getCommand("gi").setExecutor(new CommandGuildInfo(this));
		getCommand("create").setExecutor(new CommandGuildCreate(this));
		getCommand("nghome").setExecutor(new CommandGuildHome(this));
		getCommand("join").setExecutor(new CommandGuildJoin(this));
		getCommand("leave").setExecutor(new CommandGuildLeave(this));
		
		getCommand("invite").setExecutor(new CommandGuildInvite(this));
		
		try {
			if(useMySQL) {
				MySQL = new MySQL(this, getConfig().getString("mysql.host") , getConfig().getString("mysql.port"), getConfig().getString("mysql.database"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"));
				c = MySQL.openConnection();
				info("Connected to MySQL database");
			}
			else {
				SQLite sqlite = new SQLite(this, "sqlite.db");
				c = sqlite.openConnection();
				info("Connected to SQLite database");
			}
			
			//Tables setup
			DatabaseMetaData md = c.getMetaData();
			ResultSet rs = md.getTables(null, null, sqlp+"%", null);
			if(!rs.next()) {
				info("Couldn't find tables in the base. Creating...");
				String[] SQLCreateCode = getSQLCreateCode(useMySQL);
				if(SQLCreateCode.length != 0) {
					try {
						for(String tableCode : SQLCreateCode) {
							createTable(tableCode);
							info("Tables added to the database!");
						}
					}
					catch (SQLException e) {
						info("Could not create tables. Disabling");
						info("SQLException: "+e.getMessage());
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
				info("No MySQL config required.");
			}
			
			//Data loading
			getRegionManager().loadRegions();
			getGuildManager().loadGuilds();
			getPlayerManager().loadPlayers();
			
			//Listeners
			pm.registerEvents(new LoginListener(this), this);
			pm.registerEvents(new ToolListener(this),this);
			pm.registerEvents(new RegionInteractListener(this),this);
			pm.registerEvents(new MoveListener(this),this);
			pm.registerEvents(new ChatListener(this),this);

			pm.registerEvents(new PvpListener(this),this);
			pm.registerEvents(new DeathListener(this),this);

			//Tablist update
			tagUtils.refreshAll();
			
			//save scheduler
			runSaveScheduler();
			info("Save scheduler is running");

			//live regeneration task
			runLiveRegenerationTask();
			info("Live regeneration task is running");

			//metrics
			setupMetrics();

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
		if(useBarAPI) {
			for(Player player : getServer().getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
		}

		//removing holographic displays
		if(useHolographicDisplays) {
			for(Hologram h : HologramsAPI.getHolograms(this)) {
				h.delete();
			}
		}
		
		for(Player p : getServer().getOnlinePlayers()) {
			NovaPlayer nPlayer = getPlayerManager().getPlayerByName(p.getName());
			Location l1 = nPlayer.getSelectedLocation(0);
			Location l2 = nPlayer.getSelectedLocation(1);
			
			if(l1 != null && l2 != null) {
				getRegionManager().sendSquare(p,l1,l2,null,(byte)0);
				getRegionManager().resetCorner(p,l1);
				getRegionManager().resetCorner(p,l2);

				if(nPlayer.getSelectedRegion() != null) {
					getRegionManager().resetHighlightRegion(p,nPlayer.getSelectedRegion());
				}
			}
		}
		
		info("#"+pdf.getVersion()+" Disabled");
	}
	
	public void mysqlReload() {
		if(!useMySQL) return;
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
		log.info(logprefix+msg);
	}

	public void debug(String msg) {
		if(DEBUG) {
			log.info(logprefix + "[DEBUG] " + msg);
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

	//MESSAGES
	
	public boolean loadMessages() {
		messagesFile = new File(getDataFolder()+"/lang", lang+".yml");
        if(!messagesFile.exists()) {
        	if(getResource("lang/"+lang+".yml") != null) {
				saveResource("lang/"+lang+".yml", false);
				info("New messages file created: "+lang+".yml");
        	}
        	else {
        		info("Couldn't find language file: "+lang+".yml");
        		getServer().getPluginManager().disablePlugin(this);
	            return false;
        	}
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        return true;
	}
	
	//set string from file
	public String getMessagesString(String path) {
		String msg = getMessages().getString(path);
		
		if(msg == null || !(msg instanceof String)) {
			return path;
		}
				
		return msg;
	}
	
	//get messages
	public FileConfiguration getMessages() {
		return messages;
	}
	
	public void loadMessagesFile(File msgFile) {
		messagesFile = msgFile;
		
		if(!messagesFile.exists()) {
    		saveResource("lang/"+lang+".yml", false);
    		info("New messages file created");
	    }

		messages = YamlConfiguration.loadConfiguration(messagesFile);
	}
	
	//send string with prefix to a player
	public void sendPrefixMessage(Player p, String msg) {
		p.sendMessage(StringUtils.fixColors(prefix + msg));
	}

	public void sendPrefixMessage(CommandSender sender, String msg) {
		sender.sendMessage(StringUtils.fixColors(prefix + msg));
	}
	
	//send message from file with prefix to a player
	public void sendMessagesMsg(Player p, String path) {
		p.sendMessage(StringUtils.fixColors(prefix + getMessagesString(path)));
	}
	
	//send message from file with prefix and vars to a player
	public void sendMessagesMsg(Player p, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		p.sendMessage(StringUtils.fixColors(prefix + msg));
	}
	
	public void sendMessagesMsg(CommandSender sender, String path) {
		sender.sendMessage(StringUtils.fixColors(prefix + getMessagesString(path)));
	}
	
	public void sendMessagesMsg(CommandSender sender, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		sender.sendMessage(StringUtils.fixColors(prefix + msg));
	}
	
	//broadcast string to all players
	public void broadcast(String msg) {
		for(Player p : getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}
	
	//broadcast message from file to all players
	public void broadcastMessage(String path, String permission) {
		for(Player p : getServer().getOnlinePlayers()) {
			if(p.hasPermission("novaguilds."+permission)) {
				sendMessagesMsg(p,path);
			}
		}
	}

	public void broadcastMessage(String path,HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		
		for(Player p : getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}
	
	public void broadcastGuild(NovaGuild guild, String path) {
		broadcastGuild(guild,path,new HashMap<String,String>());
	}

	public void broadcastGuild(NovaGuild guild, String path,HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = StringUtils.replaceMap(msg, vars);
		
		for(Player p : guild.getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}
	
	//convert sender to player
	public Player senderToPlayer(CommandSender sender) {
		return getServer().getPlayer(sender.getName());
	}
	
	//true=mysql, false=sqlite
	private String[] getSQLCreateCode(boolean mysql) {
		String url = "http://novaguilds.marcin.co/sqltables.txt";
		String sql = StringUtils.getContent(url);
		
		int index;
		if(mysql)
			index=0;
		else
			index=1;
		
		String[] types = sql.split("--TYPE--");
		return types[index].split("--");
	}
	
	private void createTable(String sql) throws SQLException {
		mysqlReload();
		Statement statement;
		sql = StringUtils.replace(sql, "{SQLPREFIX}", sqlp);
		statement = c.createStatement();
		statement.executeUpdate(sql);
	}
	
	public void runSaveScheduler() {
		Runnable task = new RunnableAutoSave(this);
		worker.schedule(task, savePeriod, TimeUnit.MINUTES);
	}

	public void runLiveRegenerationTask() {
		Runnable task = new RunnableLiveRegeneration(this);
		worker.schedule(task,30,TimeUnit.MINUTES);
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

	public void sendUsageMessage(CommandSender sender, String path) {
		sender.sendMessage(StringUtils.fixColors(getMessagesString("chat.usage."+path)));
	}

	public void setWarBar(NovaGuild guild, float percent, NovaGuild defender) {
		if(useBarAPI) {
			String msg = getMessagesString("barapi.warprogress");
			msg = StringUtils.replace(msg, "{DEFENDER}", defender.getName());

			for(NovaPlayer nPlayer : guild.getPlayers()) {
				if(nPlayer.isOnline()) {
					BarAPI.setMessage(nPlayer.getPlayer(), StringUtils.fixColors(msg), percent);
				}
			}
		}
	}

	public void resetWarBar(NovaGuild guild) {
		if(useBarAPI) {
			for(NovaPlayer nPlayer : guild.getPlayers()) {
				if(nPlayer.isOnline()) {
					BarAPI.removeBar(nPlayer.getPlayer());
				}
			}
		}
	}

	public void loadGroups() {
		groups.clear();
		Set<String> groupsNames = getConfig().getConfigurationSection("guild.create.groups").getKeys(false);
		groupsNames.add("admin");

		for(String groupName : groupsNames) {
			groups.put(groupName, new NovaGroup(this, groupName));
		}
	}

	public NovaGroup getGroup(Player player) {
		String groupName = "default";

		if(player == null) {
			return null;
		}

		for(String name : groups.keySet()) {
			if(player.hasPermission("novaguilds.group."+name) && !name.equalsIgnoreCase("default")) {
				groupName = name;
				break;
			}
		}

		return getGroup(groupName);
	}

	public NovaGroup getGroup(CommandSender sender) {
		return getGroup(senderToPlayer(sender));
	}

	public NovaGroup getGroup(String groupName) {
		if(groups.containsKey(groupName)) {
			return groups.get(groupName);
		}

		return null;
	}

	public void delayedTeleport(Player player, Location location, String path) {
		Runnable task = new RunnableTeleportRequest(this,player,location,path);
		worker.schedule(task,getGroup(player).getTeleportDelay(),TimeUnit.SECONDS);

		if(getGroup(player).getTeleportDelay()>0) {
			sendDelayedTeleportMessage(player);
		}
	}

	public void sendDelayedTeleportMessage(Player player) {
		HashMap<String,String> vars = new HashMap<>();
		vars.put("DELAY",getGroup(player).getTeleportDelay()+"");
		sendMessagesMsg(player, "chat.delayedteleport", vars);
	}

	//Utils
	public static long systemSeconds() {
		return System.currentTimeMillis() / 1000;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
