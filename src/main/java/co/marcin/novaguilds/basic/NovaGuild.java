package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NovaGuild {
	private int id;
	private String name;
	private String tag;
	private NovaRegion region;
	private String leaderName;
	private NovaPlayer leader;
	private Location spawnpoint;
	private double money = 0;
	private int points;
	private NovaRaid raid;
	private long timeRest;
	private long lostLiveTime;
	private long inactiveTime;
	private int lives;
	private boolean changed = false;

	private final List<NovaPlayer> players = new ArrayList<>();

	private final List<NovaGuild> allies = new ArrayList<>();
	private final List<String> alliesNames = new ArrayList<>();
	private final List<String> allies_invited = new ArrayList<>();

	private final List<NovaGuild> war = new ArrayList<>();
	private final List<String> warNames = new ArrayList<>();

	private final List<String> nowar_inv = new ArrayList<>();

	//getters
	public String getName() {
		return name;
	}

	public int getPoints() {
		return points;
	}

	public List<NovaGuild> getAllies() {
		return allies;
	}

	public List<String> getAlliesNames() {
		return alliesNames;
	}

	public List<String> getAllyInvitations() {
		return allies_invited;
	}

	public List<String> getWarsNames() {
		return warNames;
	}

	public List<NovaGuild> getWars() {
		return war;
	}

	public List<String> getNoWarInvitations() {
		return nowar_inv;
	}

	public String getTag() {
		return tag;
	}

	public NovaRegion getRegion() {
		return region;
	}

	public List<NovaPlayer> getPlayers() {
		return players;
	}

	public List<NovaPlayer> getOnlineNovaPlayers() {
		List<NovaPlayer> list = new ArrayList<>();

		for(NovaPlayer nPlayer : getPlayers()) {
			if(nPlayer.isOnline() && nPlayer.getPlayer() != null) {
				list.add(nPlayer);
			}
		}

		return list;
	}

	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();

		for(NovaPlayer nPlayer : getOnlineNovaPlayers()) {
			list.add(nPlayer.getPlayer());
		}

		return list;
	}

	public NovaPlayer getLeader() {
		return leader;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public Location getSpawnPoint() {
		return spawnpoint;
	}

	public int getId() {
		return id;
	}

	public double getMoney() {
		return money;
	}

	public NovaRaid getRaid() {
		return raid;
	}

	public long getTimeRest() {
		return timeRest;
	}

	public int getLives() {
		return lives;
	}

	public long getLostLiveTime() {
		return lostLiveTime;
	}

	public long getInactiveTime() {
		return inactiveTime;
	}

	//setters
	public void setUnchanged() {
		changed = false;
	}

	private void changed() {
		changed = true;
	}

	public void setName(String n) {
		name = n;
		changed();
	}

	public void setTag(String t) {
		tag = t;
		changed();
	}

	public void setRegion(NovaRegion r) {
		if(r != null) {
			region = r;
			r.setGuild(this);

			changed();
		}
	}

	public void setLeaderName(String name) {
		leaderName = name;
	}

	public void setLeader(NovaPlayer nPlayer) {
		leader = nPlayer;
		changed();
	}

	public void setSpawnPoint(Location loc) {
		spawnpoint = loc;
		changed();
	}

	public void setId(int i) {
		id = i;
		changed();
	}

	public void setMoney(double m) {
		money = m;
		changed();
	}

	public void setAllies(List<String> a) {
		allies.clear();

		for(String ally : a) {
			alliesNames.add(ally.toLowerCase());
		}

		changed();
	}

	public void setAllyInvitations(List<String> ai) {
		allies_invited.clear();

		for(String allyinv : ai) {
			allies_invited.add(allyinv.toLowerCase());
		}

		changed();
	}

	public void setWarsNames(List<String> w) {
		warNames.clear();

		for(String warr : w) {
			warNames.add(warr.toLowerCase());
		}

		changed();
	}

	public void setNoWarInvitations(List<String> nwi) {
		nowar_inv.clear();

		for(String nowar : nwi) {
			nowar_inv.add(nowar.toLowerCase());
		}

		changed();
	}

	public void setPoints(int p) {
		points = p;
		changed();
	}

	public void updateTimeRest() {
		timeRest = NovaGuilds.systemSeconds();
		changed();
	}

	public void updateLostLive() {
		lostLiveTime = NovaGuilds.systemSeconds();
		changed();
	}

	public void updateInactiveTime() {
		inactiveTime = NovaGuilds.systemSeconds();
		changed();
	}

	public void setLostLiveTime(long t) {
		lostLiveTime = t;
		changed();
	}

	public void setInactiveTime(long time) {
		inactiveTime = time;
		changed();
	}

	public void resetLostLiveTime() {
		lostLiveTime = 0;
		changed();
	}

	public void setLives(int l) {
		lives = l;
		changed();
	}

	public void setTimeRest(long timeRest) {
		this.timeRest = timeRest;
		changed();
	}

	public void isNotRaid() {
		raid = null;
	}

	//check
	public boolean isInvitedToAlly(NovaGuild guild) {
		return allies_invited.contains(guild.getName().toLowerCase());
	}

	public boolean isWarWith(NovaGuild guild) {
		return war.contains(guild);
	}

	public boolean isNoWarInvited(NovaGuild guild) {
		return nowar_inv.contains(guild.getName().toLowerCase());
	}

	public boolean isLeader(NovaPlayer nPlayer) {
		return leader.equals(nPlayer);
	}

	public boolean isLeader(CommandSender sender) {
		return leader.getName().equals(sender.getName());
	}

	public boolean hasRegion() {
		return region != null;
	}

	public boolean isAlly(NovaGuild guild) {
		return guild != null && allies.contains(guild);
	}

	public boolean isRaid() {
		return !(raid == null);
	}

	public boolean isChanged() {
		return changed;
	}

	//add/remove
	public void addAlly(NovaGuild guild) {
		alliesNames.add(guild.getName().toLowerCase());
		allies.add(guild);
		changed();
	}

	public void addAllyInvitation(NovaGuild guild) {
		allies_invited.add(guild.getName().toLowerCase());
		changed();
	}

	public void addWar(NovaGuild guild) {
		warNames.add(guild.getName().toLowerCase());
		war.add(guild);
		changed();
	}

	public void addNoWarInvitation(NovaGuild guild) {
		nowar_inv.add(guild.getName().toLowerCase());
		changed();
	}

	public void addPlayer(NovaPlayer nPlayer) {
		if(nPlayer == null) {
			NovaGuilds.getInst().info("Tried to add null player to a guild! "+name);
			return;
		}

		if(!players.contains(nPlayer)) {
			players.add(nPlayer);

			if(getLeaderName()!=null && getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
				setLeader(nPlayer);
				leaderName = null;
				NovaGuilds.getInst().debug("Changed leader "+name+"="+nPlayer.getName());
			}
		}
	}

	public void addMoney(double m) {
		money += m;
		changed();
	}

	public void addPoints(int p) {
		points += p;
		changed();
	}

	public void removePlayer(NovaPlayer nPlayer) {
		if(players.contains(nPlayer)) {
			players.remove(nPlayer);
			changed();
		}
	}

	public void removeAlly(NovaGuild guild) {
		if(allies.contains(guild)) {
			allies.remove(guild);
			alliesNames.remove(guild.getName().toLowerCase());
			changed();
		}
	}

	public void removeWar(NovaGuild guild) {
		if(war.contains(guild)) {
			war.remove(guild);
			warNames.remove(guild.getName().toLowerCase());
			changed();
		}
	}

	public void removeNoWarInvitation(NovaGuild guild) {
		if(nowar_inv.contains(guild.getName().toLowerCase())) {
			nowar_inv.remove(guild.getName().toLowerCase());
			changed();
		}
	}
	
	public void takeMoney(double m) {
		money -= m;
		changed();
	}

	public void takeLive() {
		lives--;
		changed();
	}

	public void addLive() {
		lives++;
		changed();
	}
	
	public void takePoints(int p) {
		points -= p;
		changed();
	}

	public void removeAllyInvitation(NovaGuild guild) {
		if(allies_invited.contains(guild.getName().toLowerCase())) {
			allies_invited.remove(guild.getName().toLowerCase());
			changed();
		}
	}

	public void createRaid(NovaGuild attacker) {
		raid = new NovaRaid(attacker,this);
	}

	public boolean isMember(NovaPlayer nPlayer) {
		return players.contains(nPlayer);
	}
}
