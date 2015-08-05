package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.util.LoggerUtils;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NovaPlayer {
	private Player player;
	private NovaGuild guild;
	private String name;
	private UUID uuid;
	private int points;
	private int kills;
	private int deaths;
	private GameProfile profile;
	private Tablist tablist;

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

	public static NovaPlayer fromPlayer(Player player) {
		if(player != null) {
			NovaPlayer nPlayer = new NovaPlayer();
			nPlayer.setUUID(player.getUniqueId());
			nPlayer.setName(player.getName());
			nPlayer.setPlayer(player);
			nPlayer.generateProfile();
			return nPlayer;
		}
		return null;
	}

	//generate profile
	public void generateProfile() {
		int type = 0;
		for(Constructor c : GameProfile.class.getConstructors()) {
			if(Arrays.equals(c.getParameterTypes(), new Class<?>[]{ String.class, String.class })) {
				type = 1;
			}
			else if(Arrays.equals(c.getParameterTypes(), new Class<?>[] {UUID.class, String.class})) {
				type = 2;
			}
			else {
				LoggerUtils.error("GameProfile constructor not found!");
			}
		}

		try {
			if(type == 1) {
				this.profile = GameProfile.class.getConstructor(new Class<?>[] {
						String.class,
						String.class
				}).newInstance(uuid.toString(), name);
			}
			else if(type == 2) {
				this.profile = GameProfile.class.getConstructor(new Class<?>[] {
						UUID.class,
						String.class
				}).newInstance(uuid, name);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
		return NovaGuilds.getInst().econ.getBalance(name);
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

	public GameProfile getProfile() {
		return profile;
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
		NovaGuilds.getInst().econ.depositPlayer(name, money);
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
		NovaGuilds.getInst().econ.withdrawPlayer(name, money);
	}
}
