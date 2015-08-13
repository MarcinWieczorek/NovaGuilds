package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class NovaPlayer {
	private Player player;
	private NovaGuild guild;
	private String name;
	private UUID uuid;
	private int points;
	private int kills;
	private int deaths;

	private List<NovaGuild> invitedTo = new ArrayList<>();
	private boolean regionMode = false;
	private boolean bypass = false;
	private NovaRegion selectedRegion;
	private NovaRegion atRegion;
	private NovaRaid partRaid;
	private boolean changed = false;
	private boolean resizing = false;
	private int resizingCorner = 0;
	private boolean compassPointingGuild = false;
	private HashMap<UUID, Long> killingHistory = new HashMap<>();

	public static NovaPlayer fromPlayer(Player player) {
		if(player != null) {
			NovaPlayer nPlayer = new NovaPlayer();
			nPlayer.setUUID(player.getUniqueId());
			nPlayer.setName(player.getName());
			nPlayer.setPlayer(player);
			return nPlayer;
		}
		return null;
	}

	//Region selecting
	private final Location[] regionSelectedLocations = new Location[2];
	
	//getters
	public Player getPlayer() {
		return player;
	}
	
	public NovaGuild getGuild() {
		return guild;
	}
	
	public boolean isLeader() {
		return hasGuild() && getGuild().isLeader(this);
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getInvitedToNames() {
		List<String> invitedToNames = new ArrayList<>();

		for(NovaGuild guild : invitedTo) {
			invitedToNames.add(guild.getName());
		}

		return invitedToNames;
	}

	public List<NovaGuild> getInvitedTo() {
		return invitedTo;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Location getSelectedLocation(int index) {
		return regionSelectedLocations[index];
	}
	
	public NovaRegion getSelectedRegion() {
		return selectedRegion;
	}
	
	public boolean getBypass() {
		return bypass;
	}

	public NovaRegion getAtRegion() {
		return atRegion;
	}

	public int getResizingCorner() {
		return resizingCorner;
	}

	public void setScoreBoard(Scoreboard sb) {
		if(isOnline()) {
			player.setScoreboard(sb);
		}
	}

	public int getPoints() {
		return points;
	}

	public int getDeaths() {
		return deaths;
	}

	public int getKills() {
		return kills;
	}

	public double getMoney() {
		return NovaGuilds.getInstance().econ.getBalance(name);
	}

	public boolean getRegionMode() {
		return regionMode;
	}

	public Scoreboard getScoreBoard() {
		return player.isOnline() ? player.getScoreboard() : null;
	}

	public NovaRaid getPartRaid() {
		return partRaid;
	}

	//setters
	public void setGuild(NovaGuild guild) {
		this.guild = guild;
		changed = true;
	}

	public void setPlayer(Player p) {
		player = p;
	}

	public void setName(String n) {
		name = n;
		changed = true;
	}
	
	public void setUUID(UUID id) {
		uuid = id;
		changed = true;
	}

	public void setInvitedTo(List<NovaGuild> invto) {
		invitedTo = invto;
		changed = true;
	}
	
	public void setRegionMode(boolean rm) {
		regionMode = rm;
	}
	
	public void setSelectedLocation(int index,Location l) {
		regionSelectedLocations[index] = l;
	}
	
	public void setSelectedRegion(NovaRegion region) {
		selectedRegion = region;
	}

	public void setAtRegion(NovaRegion region) {
		atRegion = region;
	}

	public void setUnchanged() {
		changed = false;
	}

	public void setResizing(boolean b) {
		resizing = b;
	}

	public void setResizingCorner(int index) {
		resizingCorner = index;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setCompassPointingGuild(boolean compassPointingGuild) {
		this.compassPointingGuild = compassPointingGuild;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public void toggleBypass() {
		bypass = !bypass;
	}

	public void setPartRaid(NovaRaid partRaid) {
		this.partRaid = partRaid;
	}
	
	//check stuff
	public boolean isCompassPointingGuild() {
		return compassPointingGuild;
	}

	public boolean hasGuild() {
		return getGuild() != null;
	}
	
	public boolean isOnline() {
		return player != null;
	}

	public boolean isResizing() {
		return resizing;
	}

	public boolean isChanged() {
		return changed;
	}
	
	public boolean isInvitedTo(NovaGuild guild) {
		return invitedTo.contains(guild);
	}

	public boolean isPartRaid() {
		return !(partRaid == null);
	}

	public boolean hasMoney(double money) {
		return getMoney() >= money;
	}

	public boolean canGetKillPoints(Player player) {
		return !killingHistory.containsKey(player.getUniqueId()) || NumberUtils.systemSeconds() - killingHistory.get(player.getUniqueId()) > Config.KILLING_COOLDOWN.getSeconds();
	}
	
	//add stuff
	public void addInvitation(NovaGuild guild) {
		invitedTo.add(guild);
		changed = true;
	}

	public void addPoints(int points) {
		this.points += points;
		changed = true;
	}

	public void addKill() {
		kills++;
		changed = true;
	}

	public void addDeath() {
		deaths++;
		changed = true;
	}

	public void addMoney(double money) {
		NovaGuilds.getInstance().econ.depositPlayer(name, money);
	}

	public void addKillHistory(Player player) {
		if(killingHistory.containsKey(player.getUniqueId())) {
			killingHistory.remove(player.getUniqueId());
		}

		killingHistory.put(player.getUniqueId(), NumberUtils.systemSeconds());
	}
	
	//delete stuff
	public void deleteInvitation(NovaGuild guild) {
		invitedTo.remove(guild);
		changed = true;
	}

	public void takePoints(int points) {
		this.points -= points;
		changed = true;
	}

	public void takeMoney(double money) {
		NovaGuilds.getInstance().econ.withdrawPlayer(name, money);
	}
}
