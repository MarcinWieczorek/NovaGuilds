package co.marcin.novaguilds.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NovaPlayer {
	private Player player;
	private NovaGuild novaGuild;
	private boolean leader = false;
	private boolean hasGuild = false;
	private String name;
	private UUID uuid;
	private List<String> invitedTo = new ArrayList<>();
	private boolean regionMode = false;
	private boolean bypass = false;
	private boolean isonline = false;
	private NovaRegion selectedRegion;
	private NovaRegion atRegion;
	private NovaRaid partRaid;
	private boolean changed = false;

	public NovaPlayer(Player player) {
		if(player != null) {
			setUUID(player.getUniqueId());
			setName(player.getName());
			setPlayer(player);
		}
	}

	public NovaPlayer() {

	}

	//Region selecting
	private final Location[] regionSelectedLocations = new Location[2];
	
	//getters
	public Player getPlayer() {
		return player;
	}
	
	public NovaGuild getGuild() {
		return novaGuild;
	}
	
	public boolean isLeader() {
		return leader;
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
		novaGuild = guild;
		
		if(guild == null) {
			hasGuild = false;
			changed = true;
			return;
		}

		if(guild.isLeader(this)) {
			leader = true;
		}
		
		hasGuild = true;
		changed = true;
	}

	public void setPlayer(Player p) {
		player = p;

		setOnline(!(player == null));
	}

	public void setName(String n) {
		name = n;
		changed = true;
	}
	
	public void setHasGuild(boolean v) {
		hasGuild = v;
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
	
	public void setOnline(boolean b) {
		isonline = b;
	}

	public void setLeader(boolean b) {
		leader = b;
	}

	public void setAtRegion(NovaRegion region) {
		atRegion = region;
	}
	
	//check stuff
	public boolean hasGuild() {
		return hasGuild;
	}
	
	public boolean isOnline() {
		return isonline;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setUnchanged() {
		changed = false;
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
