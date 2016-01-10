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
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.GUIInventory;
import co.marcin.novaguilds.runnable.CommandExecutorHandler;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
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
	private final HashMap<UUID, Long> killingHistory = new HashMap<>();
	private final Tablist tablist;
	private CommandExecutorHandler commandExecutorHandler;
	private final List<Vehicle> vehicles = new ArrayList<>();
	private final List<GUIInventory> guiInventoryHistory = new ArrayList<>();
	private NovaRank guildRank;
	private ChatMode chatMode = ChatMode.NORMAL;

	public NovaPlayer() {
		tablist = new Tablist(this);
	}

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

	public static NovaPlayer get(CommandSender sender) {
		return NovaGuilds.getInstance().getPlayerManager().getPlayer(sender);
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

	public Tablist getTablist() {
		return tablist;
	}

	public CommandExecutorHandler getCommandExecutorHandler() {
		return commandExecutorHandler;
	}

	public NovaRaid getPartRaid() {
		return partRaid;
	}

	public GUIInventory getGuiInventory() {
		return guiInventoryHistory.isEmpty() ? null : guiInventoryHistory.get(guiInventoryHistory.size()-1);
	}

	public List<GUIInventory> getGuiInventoryHistory() {
		return guiInventoryHistory;
	}

	public NovaRank getGuildRank() {
		return guildRank;
	}

	public ChatMode getChatMode() {
		return chatMode;
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
		changed = true;
	}

	public void setCompassPointingGuild(boolean compassPointingGuild) {
		this.compassPointingGuild = compassPointingGuild;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		changed = true;
	}

	public void setKills(int kills) {
		this.kills = kills;
		changed = true;
	}

	public void setScoreBoard(Scoreboard sb) {
		if(isOnline()) {
			player.setScoreboard(sb);
		}
	}

	public void toggleBypass() {
		bypass = !bypass;
	}

	public void setPartRaid(NovaRaid partRaid) {
		this.partRaid = partRaid;
	}

	public void setGuiInventory(GUIInventory guiInventory) {
		if(guiInventory == null) {
			removeLastGUIInventoryHistory();
			return;
		}

		if(!guiInventory.equals(getGuiInventory())) {
			guiInventoryHistory.add(guiInventory);
		}
	}

	public void setGuildRank(NovaRank guildRank) {
		if(this.guildRank != null) {
			this.guildRank.removeMember(this);
		}

		if(guildRank != null) {
			guildRank.addMember(this);
		}

		this.guildRank = guildRank;
	}

	public void setChatMode(ChatMode chatMode) {
		this.chatMode = chatMode;
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

	public boolean isVehicleListed(Vehicle vehicle) {
		return vehicles.contains(vehicle);
	}

	public boolean isLeader() {
		return hasGuild() && getGuild().isLeader(this);
	}

	public boolean hasMoney(double money) {
		return getMoney() >= money;
	}

	public boolean hasPermission(GuildPermission permission) {
		return guildRank.hasPermission(permission);
	}

	public boolean canGetKillPoints(Player player) {
		return !killingHistory.containsKey(player.getUniqueId()) || NumberUtils.systemSeconds() - killingHistory.get(player.getUniqueId()) > Config.KILLING_COOLDOWN.getSeconds();
	}
	
	//add stuff
	public void addInvitation(NovaGuild guild) {
		if(!isInvitedTo(guild)) {
			invitedTo.add(guild);
			changed = true;
		}
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

	public void addVehicle(Vehicle vehicle) {
		if(!isVehicleListed(vehicle)) {
			vehicles.add(vehicle);
		}
	}

	public void newCommandExecutorHandler(Commands command, String[] args) {
		commandExecutorHandler = new CommandExecutorHandler(command, getPlayer(), args);
		Message.CHAT_CONFIRM_NEEDCONFIRM.send(player);
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

	public void cancelToolProgress() {
		RegionUtils.sendRectangle(getPlayer(), getSelectedLocation(0), getSelectedLocation(1), null, (byte)0);
		RegionUtils.setCorner(getPlayer(), getSelectedLocation(0), null, (byte)0);
		RegionUtils.setCorner(getPlayer(), getSelectedLocation(1), null, (byte)0);
		RegionUtils.highlightRegion(getPlayer(), getSelectedRegion(), null);

		setResizingCorner(0);
		setResizing(false);
		setSelectedRegion(null);
		setSelectedLocation(0, null);
		setSelectedLocation(1, null);
	}

	public void removeCommandExecutorHandler() {
		commandExecutorHandler = null;
	}

	public void removeLastGUIInventoryHistory() {
		getGuiInventoryHistory().remove(getGuiInventoryHistory().size() - 1);
	}
}
