package co.marcin.NovaGuilds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NovaGuild {
	private int id;
	private String name;
	private String tag;
	private NovaRegion region;
	private String leadername;
	private Location spawnpoint;
	private double money = 0;
	private int points;
	
	private List<NovaPlayer> players = new ArrayList<>();
	public List<String> players_nick = new ArrayList<>();

	private List<String> allies = new ArrayList<>();
	private List<String> allies_invited = new ArrayList<>();

	private List<String> war = new ArrayList<>();
	private List<String> nowar_inv = new ArrayList<>();
	
	//getters
	public String getName() {
		return name;
	}
	
	public int getPoints() {
		return points;
	}
	
	public List<String> getAllies() {
		return allies;
	}
	
	public List<String> getAllyInvitations() {
		return allies_invited;
	}

	public List<String> getWars() {
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
	
	public String getLeaderName() {
		return leadername;
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
	
	//setters
	public void setName(String n) {
		name = n;
	}
	
	public void setTag(String t) {
		tag = t;
	}
	
	public void setRegion(NovaRegion r) {
		region = r;
	}
	
	public void setLeaderName(String name) {
		leadername = name;
	}
	
	public void setSpawnPoint(Location loc) {
		spawnpoint = loc;
	}
	
	public void setId(int i) {
		id = i;
	}
	
	public void setMoney(double m) {
		money = m;
	}
	
	public void setAllies(List<String> a) {
		allies.clear();

		for(String ally : a) {
			allies.add(ally.toLowerCase());
		}
	}
	
	public void setAllyInvitations(List<String> ai) {
		allies_invited.clear();

		for(String allyinv : ai) {
			allies_invited.add(allyinv.toLowerCase());
		}
	}

	public void setWars(List<String> w) {
		war.clear();

		for(String warr : w) {
			war.add(warr.toLowerCase());
		}
	}

	public void setNoWarInvitations(List<String> nwi) {
		nowar_inv.clear();

		for(String nowar : nwi) {
			nowar_inv.add(nowar.toLowerCase());
		}
	}
	
	public void setPoints(int p) {
		points = p;
	}
	
	//check
	public boolean isInvitedToAlly(String guildname) {
		return allies_invited.contains(guildname.toLowerCase());
	}

	public boolean isWarWith(NovaGuild guild) {
		return war.contains(guild.getName().toLowerCase());
	}

	public boolean isNoWarInvited(NovaGuild guild) {
		return nowar_inv.contains(guild.getName().toLowerCase());
	}

	public boolean isLeader(String playername) {
		return leadername.equals(playername);
	}
	
	public boolean isLeader(Player player) {
		return leadername.equals(player.getName());
	}
	
	public boolean isLeader(CommandSender sender) {
		return leadername.equals(sender.getName());
	}
	
	public boolean hasRegion() {
		return region != null;
	}
	
	public boolean isAlly(NovaGuild guild) {
		return allies.contains(guild.getName().toLowerCase());
	}
	
	//add/remove
	public void addAlly(String guildname) {
		allies.add(guildname.toLowerCase());
	}
	
	public void addAllyInvitation(String guildname) {
		allies_invited.add(guildname.toLowerCase());
	}

	public void addWar(String guildname) {
		war.add(guildname.toLowerCase());
	}

	public void addWar(NovaGuild guild) {
		war.add(guild.getName().toLowerCase());
	}

	public void addNoWarInvitation(NovaGuild guild) {
		nowar_inv.add(guild.getName().toLowerCase());
	}

	public void addPlayer(NovaPlayer p) {
		players.add(p);
		players_nick.add(p.getName());
	}
	
	public void addMoney(double m) {
		money += m;
	}
	
	public void addPoints(int p) {
		points += p;
	}
	
	public void removePlayer(NovaPlayer p) {
		if(players.contains(p))
			players.remove(p);
	}
	
	public void removeAlly(String guildname) {
		if(allies.contains(guildname.toLowerCase()))
			allies.remove(guildname.toLowerCase());
	}

	public void removeWar(NovaGuild guild) {
		if(war.contains(guild.getName().toLowerCase()))
			war.remove(guild.getName().toLowerCase());
	}

	public void removeNoWarInvitation(NovaGuild guild) {
		if(nowar_inv.contains(guild.getName().toLowerCase()))
			nowar_inv.remove(guild.getName().toLowerCase());
	}
	
	public void takeMoney(double m) {
		money -= m;
	}
	
	public void takePoints(int p) {
		points -= p;
	}

	public void removeAllyInvitation(String allyname) {
		if(allies_invited.contains(allyname.toLowerCase()))
			allies_invited.remove(allyname.toLowerCase());
	}

	public boolean isMember(NovaPlayer nPlayer) {
		return players_nick.contains(nPlayer.getName());
	}
}
