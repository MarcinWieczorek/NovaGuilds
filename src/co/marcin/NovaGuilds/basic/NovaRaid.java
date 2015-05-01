package co.marcin.NovaGuilds.basic;

import java.util.ArrayList;
import java.util.List;

public class NovaRaid {
	private NovaGuild guildAttacker;
	private NovaGuild guildDefender;
	private long startTime = systemSeconds();
	private long inactiveTime = systemSeconds();
	private int killsAttacker;
	private int killsDefender;
	private int progress;
	private boolean finished;
	private List<NovaPlayer> playersOccupying = new ArrayList<>();

	public NovaRaid(NovaGuild guildAttacker, NovaGuild guildDefender) {
		this.guildAttacker = guildAttacker;
		this.guildDefender = guildDefender;
	}

	public NovaRaid() {

	}

	public NovaGuild getGuildAttacker() {
		return guildAttacker;
	}

	public NovaGuild getGuildDefender() {
		return guildDefender;
	}

	public long getStartTime() {
		return startTime;
	}

	public int getKillsAttacker() {
		return killsAttacker;
	}

	public int getKillsDefender() {
		return killsDefender;
	}

	public int getProgress() {
		return progress;
	}

	public List<NovaPlayer> getPlayersOccupying() {
		return playersOccupying;
	}

	public int getPlayersOccupyingCount() {
		return playersOccupying.size();
	}

	public boolean getFinished() {
		return finished;
	}

	public long getInactiveTime() {
		return inactiveTime;
	}

	//setters
	public void setGuildAttacker(NovaGuild guild) {
		guildAttacker = guild;
	}

	public void setGuildDefender(NovaGuild guild) {
		guildDefender = guild;
	}

	public void addKillAttacker() {
		killsAttacker++;
	}

	public void addKillDefender() {
		killsDefender++;
	}

	public void resetProgress() {
		progress = 0;
	}

	public boolean isProgressFinished() {
		return progress == 100;
	}

	public void stepProgress() {
		if(progress < 100) {
			//TODO: tests
			progress += 10;
			//progress++;
		}
	}

	public void setFinished(boolean f) {
		finished = f;
	}

	public void finish() {
		finished = true;
	}

	public void updateInactiveTime() {
		inactiveTime = systemSeconds();
	}

	//add/remove
	public void addPlayerOccupying(NovaPlayer nPlayer) {
		if(!playersOccupying.contains(nPlayer)) {
			playersOccupying.add(nPlayer);
		}
	}

	public void removePlayerOccupying(NovaPlayer nPlayer) {
		playersOccupying.remove(nPlayer);
	}

	//Utils
	public long systemSeconds() {
		return System.currentTimeMillis() / 1000;
	}
}
