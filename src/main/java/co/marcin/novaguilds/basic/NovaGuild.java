/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TagUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.confuser.barapi.BarAPI;
import org.bukkit.Effect;
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
	private long timeCreated;
	private int lives;
	private int slots;
	private boolean openInvitation = false;
	private boolean changed = false;
	private boolean friendlyPvp = false;
	private Location vaultLocation;
	private Hologram vaultHologram;

	private final List<NovaPlayer> players = new ArrayList<>();

	private final List<NovaGuild> allies = new ArrayList<>();
	private final List<String> alliesNames = new ArrayList<>();
	private final List<String> alliesInvited = new ArrayList<>();

	private final List<NovaGuild> war = new ArrayList<>();
	private final List<String> warNames = new ArrayList<>();

	private final List<String> nowarInvited = new ArrayList<>();

	private final List<String> invitedPlayersNames = new ArrayList<>();
	private final List<NovaPlayer> invitedPlayers = new ArrayList<>();
	private final List<NovaRank> ranks = new ArrayList<>();

	public static NovaGuild get(String mixedString) {
		return NovaGuilds.getInstance().getGuildManager().getGuildFind(mixedString);
	}

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
		return alliesInvited;
	}

	public List<String> getWarsNames() {
		return warNames;
	}

	public List<NovaGuild> getWars() {
		return war;
	}

	public List<String> getNoWarInvitations() {
		return nowarInvited;
	}

	public String getTag() {
		return tag;
	}

	public NovaRegion getRegion() {
		return region;
	}

	public Hologram getVaultHologram() {
		return vaultHologram;
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

	public Location getVaultLocation() {
		return vaultLocation;
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

	public boolean getFriendlyPvp() {
		return friendlyPvp;
	}

	public List<NovaPlayer> getInvitedPlayers() {
		return invitedPlayers;
	}

	public long getTimeCreated() {
		return timeCreated;
	}

	public int getSlots() {
		return slots;
	}

	public List<NovaRank> getRanks() {
		return ranks;
	}

	public NovaRank getDefaultRank() {
		for(NovaRank rank : getRanks()) {
			if(rank.isDef()) {
				return rank;
			}
		}

		return null;
	}

	//setters
	public void setVaultHologram(Hologram hologram) {
		vaultHologram = hologram;
	}

	public void setUnchanged() {
		changed = false;
	}

	private void changed() {
		changed = true;
	}

	public void setName(String n) {
		name = n;
		changed();

		//Force changed()
		for(NovaPlayer nPlayer : getPlayers()) {
			nPlayer.setGuild(this);
		}
	}

	public void setTag(String t) {
		tag = t;
		changed();
	}

	public void setRegion(NovaRegion r) {
		region = r;
		changed();

		if(r != null) {
			r.setGuild(this);
		}
	}

	public void setLeaderName(String name) {
		leaderName = name;
	}

	public void setVaultLocation(Location location) {
		vaultLocation = location;
		changed();
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

	public void setAllies(List<NovaGuild> guilds) {
		allies.clear();
		allies.addAll(guilds);

		changed();
	}

	public void setAlliesNames(List<String> list) {
		alliesNames.clear();
		alliesNames.addAll(list);

		changed();
	}

	public void setAllyInvitations(List<String> list) {
		alliesInvited.clear();
		alliesInvited.addAll(list);

		changed();
	}

	public void setWars(List<NovaGuild> list) {
		war.clear();
		war.addAll(list);
	}

	public void setWarsNames(List<String> list) {
		warNames.clear();
		warNames.addAll(list);

		changed();
	}

	public void setNoWarInvitations(List<String> list) {
		nowarInvited.clear();
		nowarInvited.addAll(list);

		changed();
	}

	public void setPoints(int p) {
		points = p;
		changed();
	}

	public void setOpenInvitation(boolean openInvitation) {
		this.openInvitation = openInvitation;
		changed();
	}

	public void updateTimeRest() {
		timeRest = NumberUtils.systemSeconds();
		changed();
	}

	public void updateLostLive() {
		lostLiveTime = NumberUtils.systemSeconds();
		changed();
	}

	public void updateInactiveTime() {
		inactiveTime = NumberUtils.systemSeconds();
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

	public void setFriendlyPvp(boolean pvp) {
		friendlyPvp = pvp;
	}

	public void isNotRaid() {
		raid = null;
	}

	public void setTimeCreated(long timeCreated) {
		this.timeCreated = timeCreated;
	}

	public void setSlots(int slots) {
		this.slots = slots;
	}

	public void setRanks(List<NovaRank> ranks) {
		this.ranks.clear();
		this.ranks.addAll(ranks);
	}

	//check
	public boolean isInvitedToAlly(NovaGuild guild) {
		return alliesInvited.contains(guild.getName().toLowerCase());
	}

	public boolean isWarWith(NovaGuild guild) {
		return war.contains(guild);
	}

	public boolean isNoWarInvited(NovaGuild guild) {
		return nowarInvited.contains(guild.getName().toLowerCase());
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
		return !(raid == null) && NovaGuilds.getInstance().guildRaids.contains(this);
	}

	public boolean isChanged() {
		return changed;
	}

	public boolean isFull() {
		return getPlayers().size() >= slots;
	}

	public boolean isOpenInvitation() {
		return openInvitation;
	}

	public boolean hasMoney(double money) {
		return this.money >= money;
	}

	//add/remove
	public void addAlly(NovaGuild guild) {
		if(!isAlly(guild)) {
			alliesNames.add(guild.getName().toLowerCase());
			allies.add(guild);
			changed();
		}
	}

	public void addAllyInvitation(NovaGuild guild) {
		if(!isInvitedToAlly(guild)) {
			alliesInvited.add(guild.getName().toLowerCase());
			changed();
		}
	}

	public void addWar(NovaGuild guild) {
		if(!isWarWith(guild)) {
			warNames.add(guild.getName().toLowerCase());
			war.add(guild);
			changed();
		}
	}

	public void addNoWarInvitation(NovaGuild guild) {
		if(!isNoWarInvited(guild)) {
			nowarInvited.add(guild.getName().toLowerCase());
			changed();
		}
	}

	public void addPlayer(NovaPlayer nPlayer) {
		if(nPlayer == null) {
			LoggerUtils.info("Tried to add null player to a guild! " + name);
			return;
		}

		if(!players.contains(nPlayer)) {
			players.add(nPlayer);
			nPlayer.setGuild(this);

			if(getLeaderName()!=null && getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
				setLeader(nPlayer);
				leaderName = null;
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

	public void addSlot() {
		slots++;
	}

	public void addRank(NovaRank rank) {
		if(!ranks.contains(rank)) {
			ranks.add(rank);
			if(rank.getGuild()==null || !rank.getGuild().equals(this)) {
				rank.setGuild(this);
			}
		}
	}

	public void removePlayer(NovaPlayer nPlayer) {
		if(players.contains(nPlayer)) {
			players.remove(nPlayer);
			nPlayer.setGuild(null);
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
		if(nowarInvited.contains(guild.getName().toLowerCase())) {
			nowarInvited.remove(guild.getName().toLowerCase());
			changed();
		}
	}

	public void removeRank(NovaRank rank) {
		ranks.remove(rank);
		rank.setGuild(null);
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
		if(alliesInvited.contains(guild.getName().toLowerCase())) {
			alliesInvited.remove(guild.getName().toLowerCase());
			changed();
		}
	}

	public void createRaid(NovaGuild attacker) {
		raid = new NovaRaid(attacker,this);
	}

	public boolean isMember(NovaPlayer nPlayer) {
		return players.contains(nPlayer);
	}

	public void destroy() {
		//remove players
		for(NovaPlayer nP : getPlayers()) {
			nP.setGuild(null);

			//update tags
			if(nP.isOnline()) {
				TagUtils.updatePrefix(nP.getPlayer());
			}
		}

		//remove guild invitations
		for(NovaPlayer nPlayer : NovaGuilds.getInstance().getPlayerManager().getPlayers()) {
			if(nPlayer.isInvitedTo(this)) {
				nPlayer.deleteInvitation(this);
			}
		}

		//remove allies and wars
		for(NovaGuild nGuild : NovaGuilds.getInstance().getGuildManager().getGuilds()) {
			//ally
			if(nGuild.isAlly(this)) {
				nGuild.removeAlly(this);
			}

			//ally invitation
			if(nGuild.isInvitedToAlly(this)) {
				nGuild.removeAllyInvitation(this);
			}

			//war
			if(nGuild.isWarWith(this)) {
				nGuild.removeWar(this);
			}

			//no war invitation
			if(nGuild.isNoWarInvited(this)) {
				nGuild.removeNoWarInvitation(this);
			}
		}

		//remove raid
		//TODO

		//bank and hologram
		if(this.getVaultHologram() != null) {
			this.getVaultHologram().delete();
		}


		GuildManager.checkVaultDestroyed(this);
		if(getVaultLocation() != null) {
			getVaultLocation().getBlock().breakNaturally();
			getVaultLocation().getWorld().playEffect(getVaultLocation(), Effect.SMOKE,1000);
		}
	}

	public void showVaultHologram(Player player) {
		if(vaultHologram != null) {
			vaultHologram.getVisibilityManager().showTo(player);
		}
	}

	public void hideVaultHologram(Player player) {
		if(vaultHologram != null) {
			vaultHologram.getVisibilityManager().hideTo(player);
		}
	}

	public void removeRaidBar() {
		if(Config.BARAPI_ENABLED.getBoolean()) {
			for(Player player : getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
		}
	}
}
