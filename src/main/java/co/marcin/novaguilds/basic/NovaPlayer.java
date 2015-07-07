package co.marcin.novaguilds.basic;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NovaPlayer implements Cloneable {
	private Player player;
	private NovaGuild guild;
	private String name;
	private UUID uuid;
	private List<String> invitedTo = new ArrayList<>();
	private boolean regionMode = false;
	private boolean bypass = false;
	private NovaRegion selectedRegion;
	private NovaRegion atRegion;
	private NovaRaid partRaid;
	private boolean changed = false;
	private boolean resizing = false;
	private int resizingCorner = 0;
	private int points;

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
	
	public List<String> getInvitedTo() {
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

	public int getPoints() {
		return points;
	}

	/*
	* Get raid the player is taking part in
	* */
	public NovaRaid getPartRaid() {
		return partRaid;
	}

	public boolean isPartRaid() {
		return !(partRaid == null);
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
	
	public void setInvitedTo(List<String> invto) {
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

	public void addPoints(int points) {
		this.points += points;
	}

	public void takePoints(int points) {
		this.points -= points;
	}
	
	//check stuff
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
		return invitedTo.contains(guild.getName());
	}
	
	public boolean regionMode() {
		return regionMode;
	}
	
	//add stuff
	public void addInvitation(NovaGuild guild) {
		invitedTo.add(guild.getName());
		changed = true;
	}
	
	//delete stuff
	public void deleteInvitation(NovaGuild guild) {
		invitedTo.remove(guild.getName());
		changed = true;
	}

	public void toggleBypass() {
		bypass = !bypass;
	}
}
