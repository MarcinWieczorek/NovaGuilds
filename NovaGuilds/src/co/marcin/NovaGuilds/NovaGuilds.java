package co.marcin.NovaGuilds;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

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
import org.bukkit.scheduler.BukkitScheduler;
import org.kitteh.tag.TagAPI;
import org.mcsg.double0negative.tabapi.TabAPI;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import code.husky.mysql.MySQL;
import code.husky.sqlite.SQLite;
import co.marcin.NovaGuilds.Commands.CommandAdmin;
import co.marcin.NovaGuilds.Commands.CommandGuild;
import co.marcin.NovaGuilds.Commands.CommandGuildAbandon;
import co.marcin.NovaGuilds.Commands.CommandGuildCreate;
import co.marcin.NovaGuilds.Commands.CommandGuildHome;
import co.marcin.NovaGuilds.Commands.CommandGuildInfo;
import co.marcin.NovaGuilds.Commands.CommandGuildInvite;
import co.marcin.NovaGuilds.Commands.CommandGuildJoin;
import co.marcin.NovaGuilds.Commands.CommandGuildLeave;
import co.marcin.NovaGuilds.Commands.CommandNovaGuilds;
import co.marcin.NovaGuilds.Listeners.ChatListener;
import co.marcin.NovaGuilds.Listeners.DeathListener;
import co.marcin.NovaGuilds.Listeners.LoginListener;
import co.marcin.NovaGuilds.Listeners.MoveListener;
import co.marcin.NovaGuilds.Listeners.PvpListener;
import co.marcin.NovaGuilds.Listeners.RegionInteractListener;
import co.marcin.NovaGuilds.Listeners.ToolListener;
import co.marcin.NovaGuilds.Manager.GuildManager;
import co.marcin.NovaGuilds.Manager.PlayerManager;
import co.marcin.NovaGuilds.Manager.RegionManager;

public class NovaGuilds extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	private final String logprefix = "[NovaGuilds] ";
	public PluginDescriptionFile pdf = this.getDescription();
	public PluginManager pm = getServer().getPluginManager();
	public String prefix;
	public String sqlp;
	public FileConfiguration config;
	public boolean DEBUG = getConfig().getBoolean("debug");
	
	private long MySQLReconnectStamp = System.currentTimeMillis();
	
	//Vault
	public Economy econ = null;
    //public static Permission perms = null;
    //public static Chat chat = null;

	public boolean useTabAPI;
	public boolean useTagAPI;
	public boolean useVault;
	public boolean useHolographicDisplays;
	
	public boolean useMySQL;
	public String lang;
	
	public HashMap<String,NovaPlayer> players = new HashMap<String,NovaPlayer>();
	public HashMap<String,NovaGuild> guilds = new HashMap<String,NovaGuild>();
	public HashMap<String,NovaRegion> regions = new HashMap<String,NovaRegion>();
	
	private GuildManager guildManager = new GuildManager(this);
	private RegionManager regionManager = new RegionManager(this);
	private PlayerManager playerManager = new PlayerManager(this);
	
	public int progress = 0;
	public long savePeriod = 15; //minutes
	
	//Database
	public MySQL MySQL;
	public SQLite sqlite;
	public Connection c = null;
	public Statement statement;
	
	private FileConfiguration messages = null;
	private File messagesFile;
	
	public void onEnable() {
		saveDefaultConfig();
		config = getConfig();
		sqlp = config.getString("mysql.prefix");
		savePeriod = config.getLong("saveperiod");
		lang = config.getString("lang");
		
		useVault = config.getBoolean("usevault");
		useTabAPI = config.getBoolean("tabapi.enabled");
		useTagAPI = config.getBoolean("tagapi.enabled");
		useHolographicDisplays = config.getBoolean("holographicdisplays.enabled");
		
		useMySQL = getConfig().getBoolean("usemysql");
		
		//HolographicDisplays
		if(useHolographicDisplays) {
			if (!setupEconomy() ) {
	            log.severe(String.format("[%s] - Disabled due to no HolographicDisplays dependency found!", pdf.getName()));
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
			info("HolographicDisplays hooked");
		}
		
		
		//Vault Economy
		if(useVault) {
			if (!setupEconomy() ) {
	            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", pdf.getName()));
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
			info("Vault's Economy hooked");
		}
		
		//TabAPI
		if(useTabAPI) {
			if(!checkTabAPI()) {
				log.severe(String.format("[%s] - Disabled due to no TabAPI dependency found!", pdf.getName()));
	            getServer().getPluginManager().disablePlugin(this);
	            return;
			}
			info("TabAPI hooked");
		}
		
		//TabAPI
		if(useTagAPI) {
			if(!checkTagAPI()) {
				log.severe(String.format("[%s] - Disabled due to no TagAPI dependency found!", pdf.getName()));
	            getServer().getPluginManager().disablePlugin(this);
	            return;
			}
			info("TagAPI hooked");
		}
		
		//messages.yml
		File langsDir = new File(getDataFolder(),"lang/");
		if(!langsDir.exists()) {
			langsDir.mkdir();
			//saveResource("lang", false);
			info("Language dir created");
		}
		
		if(!loadMessages()) {
            return;
		}
		
		prefix = messages.getString("chat.prefix");
		info("Messages loaded");
        
		//Version check
        String latest = Utils.getContent("http://novaguilds.marcin.co/latest.info");
        info("You're using version: v"+pdf.getVersion());
        info("Latest version of the plugin is: v"+latest);
        
        if(pdf.getVersion().equalsIgnoreCase(latest)) {
        	info("Your plugin version is the latest one");
        }
        else {
        	info("You should update your plugin to v"+latest+"!");
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
				sqlite = new SQLite(this,"sqlite.db");
				c = sqlite.openConnection();
				info("Connected to SQLite database");
			}
			
			//Tables setup
			DatabaseMetaData md = c.getMetaData();
			ResultSet rs = md.getTables(null, null, sqlp+"%", null);
			if(!rs.next()) {
				info("Couldn't find tables in the base. Creating...");
				String[] SQLCreateCode = getSQLCreateCode(useMySQL);
				if(!SQLCreateCode.equals("")) {
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
			pm.registerEvents(new LoginListener(this),this);
			pm.registerEvents(new ToolListener(this),this);
			pm.registerEvents(new RegionInteractListener(this),this);
			pm.registerEvents(new MoveListener(this),this);
			pm.registerEvents(new ChatListener(this),this);

			pm.registerEvents(new PvpListener(this),this);
			pm.registerEvents(new DeathListener(this),this);
			info("Listeners activated");
			
			//Tablist update
			updateTabAll();
			updateTagAll();
			
			//save scheduler
			runScheduler();
			info("Save scheduler is running");
			
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
		
		for(Hologram h: HologramsAPI.getHolograms(this)) {
			h.delete();
		}
		
		for(Player p : getServer().getOnlinePlayers()) {
			NovaPlayer nPlayer = getPlayerManager().getPlayerByName(p.getName());
			Location l1 = nPlayer.getSelectedLocation(0);
			Location l2 = nPlayer.getSelectedLocation(1);
			
			if(l1 != null && l2 != null) {
				getRegionManager().sendSquare(p,l1,l2,null,(byte)0);
				getRegionManager().resetCorner(p,l1);
				getRegionManager().resetCorner(p,l2);
			}
		}
		
		info("#"+pdf.getVersion()+" Disabled");
	}
	
	public void MySQLreload() {
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
	
	public void reload() {
		saveDefaultConfig();
		config = getConfig();
		prefix = config.getString("prefix");
		sqlp = config.getString("mysql.prefix");
		
		messagesFile = new File(getDataFolder(), "messages.yml");
        if(!messagesFile.exists()) {
				saveResource("messages.yml", false);
				info("New messages file created");
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        info("Messages loaded");
		
		getGuildManager().loadGuilds();
		getPlayerManager().loadPlayers();
	}
	
	public void info(String msg) {
		log.info(logprefix+msg);
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
	
	//Vault economy
	private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        
        if (rsp == null) {
            return false;
        }
        
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public boolean checkTabAPI() {
		if(getServer().getPluginManager().getPlugin("TabAPI") == null) {
            return false;
        }
		return true;
	}
	
	//tagAPI
	public boolean checkTagAPI() {
		if(getServer().getPluginManager().getPlugin("TagAPI") == null) {
            return false;
        }
		return true;
	}
	
	//HolographicDisplays
	public boolean checkHolographicDisplays() {
		if(!getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
	        return false;
	    }
		return true;
	}
	
	public String getTabName(Player player) {
		String tag = "";
		String guildtag = "";
		String rank = "";
		NovaPlayer nplayer = getPlayerManager().getPlayerByName(player.getName());
		String tabName = player.getName();
		
		if(nplayer.hasGuild()) {
			tag = config.getString("guild.tag");
			guildtag = nplayer.getGuild().getTag();
			
			if(!config.getBoolean("tabapi.colortags")) {
				guildtag = Utils.removeColors(guildtag);
			}
			
			tag = Utils.replace(tag,"{TAG}",guildtag);
			
			if(config.getBoolean("tabapi.rankprefix")) {
				if(nplayer.getGuild().getLeaderName().equalsIgnoreCase(player.getName())) {
					rank = messages.getString("chat.guildinfo.leaderprefix");
				}
			}
			
			tag = Utils.replace(tag,"{RANK}",rank);
			tag = Utils.fixColors(tag);
			tabName = tag + tabName;
		}
		
		return tabName;
	}
	
	public void updateTab(Player player) {
		int x=0;
		int y=0;
		String tabName;
		for(Player p : getServer().getOnlinePlayers()) {
			if(useTabAPI) {
				tabName = getTabName(p);
			}
			else {
				tabName = p.getName();
			}
			
			if(DEBUG) info(y+" - "+getTabName(p));
			TabAPI.setTabString(this,player,x,y,tabName);
			y++;
			
			if(y >= TabAPI.getVertSize()) {
				x++;
				y=0;
				if(DEBUG) info(">>>");
			}
		}
		TabAPI.updateAll();
	}
	
	public void updateTabAll() {
		if(checkTabAPI()) {
			int x=0;
			int y=0;
			String tabName;
			
			for(Player p2 : getServer().getOnlinePlayers()) {
				TabAPI.clearTab(p2);
				x=0;
				y=0;
				for(Player p : getServer().getOnlinePlayers()) {
					if(useTabAPI) {
						tabName = getTabName(p);
					}
					else {
						tabName = p.getName();
					}
					
					if(DEBUG) info(y+" - "+getTabName(p));
					TabAPI.setTabString(this,p2,x,y,tabName);
					y++;
					
					if(y >= TabAPI.getVertSize()) {
						x++;
						y=0;
						if(DEBUG) info(">>>");
					}
				}
			}
			TabAPI.updateAll();
		}
	}
	
	//update exclude
	public void updateTabAll(Player excludeplayer) {
		if(checkTabAPI()) {
			int x=0;
			int y=0;
			String tabName;
			
			for(Player p2 : getServer().getOnlinePlayers()) {
				TabAPI.clearTab(p2);
				TabAPI.resetTabList(p2);
				x=0;
				y=0;
				for(Player p : getServer().getOnlinePlayers()) {
					
					if(!p.equals(excludeplayer)) {
						if(useTabAPI) {
							tabName = getTabName(p);
						}
						else {
							tabName = p.getName();
						}
						
						if(DEBUG) info(y+" - "+getTabName(p));
						TabAPI.setTabString(this,p2,x,y,tabName);
						y++;
						
						if(y >= TabAPI.getVertSize()) {
							x++;
							y=0;
							if(DEBUG) info(">>>");
						}
					}
					else if(DEBUG) info("exclude: "+p.getName());
				}
			}
			TabAPI.updateAll();
		}
	}
	
	public void updateTagPlayerToAll(Player p) {
		Set<Player> set = new HashSet<Player>(Arrays.asList(getServer().getOnlinePlayers()));
		TagAPI.refreshPlayer(p, set);
	}
	
	public void updateTagAll() {
		for(Player p: getServer().getOnlinePlayers()) {
			Set<Player> set = new HashSet<Player>(Arrays.asList(getServer().getOnlinePlayers()));
			TagAPI.refreshPlayer(p, set);
		}
	}
	
	public String getTag(Player player) {
		String tag = "";
		String guildtag = "";
		String rank = "";
		NovaPlayer nplayer = getPlayerManager().getPlayerByName(player.getName());
		String tabName = player.getName();
		
		if(nplayer.hasGuild()) {
			tag = config.getString("guild.tag");
			guildtag = nplayer.getGuild().getTag();
			
			if(!config.getBoolean("tabapi.colortags")) {
				guildtag = Utils.removeColors(guildtag);
			}
			
			tag = Utils.replace(tag,"{TAG}",guildtag);
			
			if(config.getBoolean("tabapi.rankprefix")) {
				if(nplayer.getGuild().getLeaderName().equalsIgnoreCase(player.getName())) {
					rank = messages.getString("chat.guildinfo.leaderprefix");
				}
			}
			
			tag = Utils.replace(tag,"{RANK}",rank);
			
			//TODO ally colors
//			if(getConfig().getBoolean("tagapi.allycolor.enabled")) {
//				tabName = getConfig().getString("tagapi.allycolor.color") + tabName;
//			}
			
			tabName = tag + tabName;
		}
		
		return Utils.fixColors(tabName);
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
	
	//set messages
	public void setMessages(FileConfiguration msgs) {
		messages = msgs;
	}
	
	public void setMessagesFile(File msgFile) {
		messagesFile = msgFile;
	}
	
	public void loadMessagesFile(File msgFile) {
		messagesFile = msgFile;
		
		if(!messagesFile.exists()) {
    		saveResource("lang/"+lang+".yml", false);
    		info("New messages file created");
	    }

		setMessages(YamlConfiguration.loadConfiguration(messagesFile));
	}
	
	//send string with prefix to a player
	public void sendPrefixMessage(Player p, String msg) {
		p.sendMessage(Utils.fixColors(prefix+msg));
	}
	
	//send message from file with prefix to a player
	public void sendMessagesMsg(Player p, String path) {
		p.sendMessage(Utils.fixColors(prefix+getMessagesString(path)));
	}
	
	//send message from file with prefix and vars to a player
	public void sendMessagesMsg(Player p, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = replaceMessage(msg,vars);
		p.sendMessage(Utils.fixColors(prefix+msg));
	}
	
	public void sendMessagesMsg(CommandSender sender, String path) {
		sender.sendMessage(Utils.fixColors(prefix+getMessagesString(path)));
	}
	
	public void sendMessagesMsg(CommandSender sender, String path, HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = replaceMessage(msg,vars);
		sender.sendMessage(Utils.fixColors(prefix+msg));
	}
	
	//broadcast string to all players
	public void broadcast(String msg) {
		for(Player p : getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}
	
	//broadcast message from file to all players
	public void broadcastMessage(String path) {
		broadcastMessage(path,null);
	}
	
	public void broadcastMessage(String path,HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = replaceMessage(msg,vars);
		
		for(Player p : getServer().getOnlinePlayers()) {
			sendPrefixMessage(p, msg);
		}
	}
	
	public void broadcastGuild(NovaGuild guild, String path,HashMap<String,String> vars) {
		String msg = getMessagesString(path);
		msg = replaceMessage(msg,vars);
		
		for(NovaPlayer p : guild.getPlayers()) {
			if(p.isOnline())
				sendPrefixMessage(p.getPlayer(), msg);
		}
	}
	
	public String replaceMessage(String msg, HashMap<String,String> vars) {
		if(vars != null) {
			if(vars.size() > 0) {
				for(Entry<String, String> e : vars.entrySet()) {
					msg = Utils.replace(msg,"{"+e.getKey()+"}",e.getValue());
				}
			}
		}
		
		return msg;
	}
	
	//convert sender to player
	public Player senderToPlayer(CommandSender sender) {
		return getServer().getPlayer(sender.getName());
	}
	
	//true=mysql, false=sqlite
	public String[] getSQLCreateCode(boolean mysql) {
		String url = "http://novaguilds.marcin.co/sqltables.txt";
		String sql = Utils.getContent(url);
		
		int index;
		if(mysql)
			index=0;
		else
			index=1;
		
		String[] types = sql.split("--TYPE--");
		String[] codes = types[index].split("--");
		return codes;
	}
	
	public void createTable(String sql) throws SQLException {
		MySQLreload();
		Statement statement;
		sql = Utils.replace(sql,"{SQLPREFIX}",sqlp);
		statement = c.createStatement();
		statement.executeUpdate(sql);
	}
	
	public void runScheduler() {
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				getRegionManager().saveAll();
				getGuildManager().saveAll();
				getPlayerManager().saveAll();
				info("Saved data.");
			}
		}, 0L, 20L * 60 * savePeriod);
	}
}
