package co.marcin.novaguilds.basic;

import org.bukkit.Location;
import org.bukkit.World;

public class NovaRegion {
	private final Location[] corners = new Location[2];
	
	private String guildname;
	private int id;
	private World world;
	private NovaGuild guild;
	private boolean changed = false;
	
	public World getWorld() {
		return world;
	}

	public int getId() {
		return id;
	}
	
	public String getGuildName() {
		return guildname;
	}

	public NovaGuild getGuild() {
		return guild;
	}

	public Location getCorner(int index) {
		return corners[index];
	}
	
	//setters
	public void setUnChanged() {
		changed = false;
	}

	private void changed() {
		changed = true;
	}

	public void setWorld(World w) {
		world = w;
		changed();
	}

	public void setId(int i) {
		id = i;
		changed();
	}

	public void setGuildName(String name) {
		guildname = name;
		changed();
	}

	public void setGuild(NovaGuild guild) {
		this.guild = guild;
		guildname = guild.getName();
		changed();
	}
	
	public void setCorner(int index,Location l) {
		corners[index] = l;
		changed();
	}

	//checkers
	public boolean isChanged() {
		return changed;
	}
}
