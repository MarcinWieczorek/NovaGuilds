package co.marcin.NovaGuilds.basic;

import org.bukkit.Location;
import org.bukkit.World;

public class NovaRegion {
	private final Location[] corners = new Location[2];
	
	private String guildname;
	private int id;
	private World world;
	
	public World getWorld() {
		return world;
	}

	public int getId() {
		return id;
	}
	
	public String getGuildName() {
		return guildname;
	}

	public Location getCorner(int index) {
		return corners[index];
	}
	
	//setters
	
	public void setWorld(World w) {
		world = w;
	}

	public void setId(int i) {
		id = i;
	}

	public void setGuildName(String name) {
		guildname = name;
	}
	
	public void setCorner(int index,Location l) {
		corners[index] = l;
	}
}
