package co.marcin.NovaGuilds;

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
	private List<String> invitedTo;
	private boolean regionMode;
	private boolean bypass;
	private boolean isonline;
	private NovaRegion selectedRegion;
	
	//Region selecting
	private Location[] regionSelectedLocations = {null,null};
	
	//getters
	public Player getPlayer() {
		return player;
	}
	
	public NovaGuild getGuild() {
		return novaGuild;
	}
	
	public boolean getLeader() {
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
	
	//setters
	public void setGuild(NovaGuild guild) {
		novaGuild = guild;
		
		if(guild == null) {
			hasGuild = false;
			return;
		}
		
		hasGuild = true;
	}

	public void setPlayer(Player p) {
		player = p;
	}

	public void setName(String n) {
		name = n;
	}
	
	public void setHasGuild(boolean v) {
		hasGuild = v;
	}
	
	public void setUUID(UUID id) {
		uuid = id;
	}
	
	public void setInvitedTo(List<String> invto) {
		invitedTo = invto;
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
	
	//check stuff
	public boolean hasGuild() {
		return hasGuild;
	}
	
	public boolean isOnline() {
		return isonline;
	}
	
	public boolean isInvitedTo(NovaGuild guild) {
		return invitedTo.contains(guild.getName());
	}
	
	public boolean regionMode() {
		return regionMode;
	}
	
	//Case sensitive!
	public boolean isInvitedTo(String guildname) {
		return invitedTo.contains(guildname);
	}
	
	//add stuff
	public void addInvitation(NovaGuild guild) {
		invitedTo.add(guild.getName());
	}
	
	//delete stuff
	public void deleteInvitation(NovaGuild guild) {
		invitedTo.remove(guild.getName());
	}

	public void toggleBypass() {
		bypass = !bypass;
	}
}
