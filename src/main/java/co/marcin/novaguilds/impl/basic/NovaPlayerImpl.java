/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRaid;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.basic.TabList;
import co.marcin.novaguilds.api.util.RegionSelection;
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.impl.util.AbstractChangeable;
import co.marcin.novaguilds.runnable.CommandExecutorHandler;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NovaPlayerImpl extends AbstractChangeable implements NovaPlayer {
	private int id = 0;
	private final UUID uuid;
	private String name;
	private Player player;
	private NovaGuild guild;
	private int points = 0;
	private int kills = 0;
	private int deaths = 0;

	private final List<Vehicle> vehicles = new ArrayList<>();
	private final List<NovaGuild> invitedTo = new ArrayList<>();
	private final List<GUIInventory> guiInventoryHistory = new ArrayList<>();
	private final HashMap<UUID, Long> killingHistory = new HashMap<>();

	private boolean bypass = false;
	private boolean compassPointingGuild = false;

	private boolean spyMode = false;
	private NovaRaid partRaid;
	private NovaRank guildRank;
	private NovaRegion atRegion;
	private TabList tabList;
	private CommandExecutorHandler commandExecutorHandler;
	private RegionMode regionMode = RegionMode.CHECK;
	private ChatMode chatMode = ChatMode.NORMAL;
	private RegionSelection activeSelection;
	private boolean regionSpectate;

	public NovaPlayerImpl(UUID uuid) {
		this.uuid = uuid;
	}

	public static NovaPlayer fromPlayer(Player player) {
		if(player != null) {
			NovaPlayer nPlayer = new NovaPlayerImpl(player.getUniqueId());
			nPlayer.setName(player.getName());
			nPlayer.setPlayer(player);
			nPlayer.setPoints(Config.KILLING_STARTPOINTS.getInt());
			return nPlayer;
		}
		return null;
	}

	//getters
	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public NovaGuild getGuild() {
		return guild;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<NovaGuild> getInvitedTo() {
		return invitedTo;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public RegionSelection getActiveSelection() {
		return activeSelection;
	}

	@Override
	public boolean getBypass() {
		return bypass;
	}

	@Override
	public boolean getRegionSpectate() {
		return regionSpectate;
	}

	@Override
	public NovaRegion getAtRegion() {
		return atRegion;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public int getDeaths() {
		return deaths;
	}

	@Override
	public int getKills() {
		return kills;
	}

	@Override
	public double getKillDeathRate() {
		return NumberUtils.roundOffTo2DecPlaces((double) getKills() / (getDeaths() == 0 ? 1 : (double) getDeaths()));
	}

	@Override
	public double getMoney() {
		return NovaGuilds.getInstance().econ.getBalance(name);
	}

	@Override
	public RegionMode getRegionMode() {
		return regionMode;
	}

	@Override
	public TabList getTabList() {
		return tabList;
	}

	@Override
	public CommandExecutorHandler getCommandExecutorHandler() {
		return commandExecutorHandler;
	}

	@Override
	public NovaRaid getPartRaid() {
		return partRaid;
	}

	@Override
	public GUIInventory getGuiInventory() {
		return guiInventoryHistory.isEmpty() ? null : guiInventoryHistory.get(guiInventoryHistory.size() - 1);
	}

	@Override
	public List<GUIInventory> getGuiInventoryHistory() {
		return guiInventoryHistory;
	}

	@Override
	public NovaRank getGuildRank() {
		return guildRank;
	}

	@Override
	public ChatMode getChatMode() {
		return chatMode;
	}

	@Override
	public boolean getSpyMode() {
		return spyMode;
	}

	@Override
	public int getId() {
		return id;
	}

	//setters
	@Override
	public void setGuild(NovaGuild guild) {
		this.guild = guild;
		setChanged();
	}

	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		setChanged();
	}

	@Override
	public void setInvitedTo(List<NovaGuild> list) {
		invitedTo.clear();
		invitedTo.addAll(list);
		setChanged();
	}

	@Override
	public void setRegionMode(RegionMode regionMode) {
		this.regionMode = regionMode;
	}

	@Override
	public void setActiveSelection(RegionSelection selection) {
		activeSelection = selection;
	}

	@Override
	public void setAtRegion(NovaRegion region) {
		atRegion = region;
	}

	@Override
	public void setPoints(int points) {
		this.points = points;
		setChanged();
	}

	@Override
	public void setCompassPointingGuild(boolean compassPointingGuild) {
		this.compassPointingGuild = compassPointingGuild;
	}

	@Override
	public void setDeaths(int deaths) {
		this.deaths = deaths;
		setChanged();
	}

	@Override
	public void setKills(int kills) {
		this.kills = kills;
		setChanged();
	}

	@Override
	public void setTabList(TabList tabList) {
		this.tabList = tabList;
	}

	@Override
	public void toggleBypass() {
		bypass = !bypass;
	}

	@Override
	public void toggleRegionSpectate() {
		regionSpectate = !regionSpectate;
	}

	@Override
	public void setPartRaid(NovaRaid partRaid) {
		this.partRaid = partRaid;
	}

	@Override
	public void setGuiInventory(GUIInventory guiInventory) {
		if(guiInventory == null) {
			removeLastGUIInventoryHistory();
			return;
		}

		if(!guiInventory.equals(getGuiInventory())) {
			guiInventoryHistory.add(guiInventory);
		}
	}

	@Override
	public void setGuildRank(NovaRank guildRank) {
		if(this.guildRank != null) {
			this.guildRank.removeMember(this);
		}

		if(guildRank != null) {
			guildRank.addMember(this);

			if(!hasPermission(GuildPermission.REGION_CREATE) && !hasPermission(GuildPermission.REGION_RESIZE)) {
				cancelToolProgress();
			}
		}

		this.guildRank = guildRank;
	}

	@Override
	public void setChatMode(ChatMode chatMode) {
		this.chatMode = chatMode;
	}

	@Override
	public void setSpyMode(boolean spyMode) {
		this.spyMode = spyMode;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	//check stuff
	@Override
	public boolean isCompassPointingGuild() {
		return compassPointingGuild;
	}

	@Override
	public boolean hasGuild() {
		return getGuild() != null;
	}

	@Override
	public boolean isOnline() {
		return player != null;
	}

	@Override
	public boolean isInvitedTo(NovaGuild guild) {
		return invitedTo.contains(guild);
	}

	@Override
	public boolean isPartRaid() {
		return !(partRaid == null);
	}

	@Override
	public boolean isVehicleListed(Vehicle vehicle) {
		return vehicles.contains(vehicle);
	}

	@Override
	public boolean isLeader() {
		return hasGuild() && getGuild().isLeader(this);
	}

	@Override
	public boolean isAtRegion() {
		return atRegion != null;
	}

	@Override
	public boolean hasMoney(double money) {
		return getMoney() >= money;
	}

	@Override
	public boolean hasPermission(GuildPermission permission) {
		return guildRank != null && guildRank.hasPermission(permission);
	}

	@Override
	public boolean hasTabList() {
		return tabList != null;
	}

	@Override
	public boolean canGetKillPoints(Player player) {
		return !killingHistory.containsKey(player.getUniqueId()) || NumberUtils.systemSeconds() - killingHistory.get(player.getUniqueId()) > Config.KILLING_COOLDOWN.getSeconds();
	}
	
	//add stuff
	@Override
	public void addInvitation(NovaGuild guild) {
		if(!isInvitedTo(guild)) {
			invitedTo.add(guild);
			setChanged();
		}
	}

	@Override
	public void addPoints(int points) {
		this.points += points;
		setChanged();
	}

	@Override
	public void addKill() {
		kills++;
		setChanged();
	}

	@Override
	public void addDeath() {
		deaths++;
		setChanged();
	}

	@Override
	public void addMoney(double money) {
		NovaGuilds.getInstance().econ.depositPlayer(name, money);
	}

	@Override
	public void addKillHistory(Player player) {
		if(killingHistory.containsKey(player.getUniqueId())) {
			killingHistory.remove(player.getUniqueId());
		}

		killingHistory.put(player.getUniqueId(), NumberUtils.systemSeconds());
	}

	@Override
	public void addVehicle(Vehicle vehicle) {
		if(!isVehicleListed(vehicle)) {
			vehicles.add(vehicle);
		}
	}

	@Override
	public void newCommandExecutorHandler(Command command, String[] args) {
		commandExecutorHandler = new CommandExecutorHandler(command, getPlayer(), args);
		Message.CHAT_CONFIRM_NEEDCONFIRM.send(player);
	}
	
	//delete stuff
	@Override
	public void deleteInvitation(NovaGuild guild) {
		invitedTo.remove(guild);
		setChanged();
	}

	@Override
	public void takePoints(int points) {
		this.points -= points;
		setChanged();
	}

	@Override
	public void takeMoney(double money) {
		NovaGuilds.getInstance().econ.withdrawPlayer(name, money);
	}

	@Override
	public void cancelToolProgress() {
		if(isOnline()) {
			if(getActiveSelection() != null) {
				getActiveSelection().reset();
			}

			if(getRegionMode() == RegionMode.RESIZE) {
				setRegionMode(RegionMode.CHECK);
			}
		}
	}

	@Override
	public void removeCommandExecutorHandler() {
		commandExecutorHandler = null;
	}

	@Override
	public void removeLastGUIInventoryHistory() {
		getGuiInventoryHistory().remove(getGuiInventoryHistory().size() - 1);
	}
}
