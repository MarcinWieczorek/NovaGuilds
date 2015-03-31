package co.marcin.NovaGuilds;

import org.bukkit.Location;
import org.bukkit.World;

public class NovaRegion {
	private int x1 = 0;
	private int z1 = 0;
	private int x2 = 0;
	private int z2 = 0;

	private Location[] corners = {null,null};
	
	private String guildname;
	private int id;
	private World world;
	
	public World getWorld() {
		return world;
	}
	
	public Integer getX1() {
		return x1;
	}
	
	public int getId() {
		return id;
	}
	
	public int getZ1() {
		return z1;
	}
	
	public int getX2() {
		return x2;
	}
	
	public int getZ2() {
		return z2;
	}
	
	public int getWidth() {
		return Math.abs(x1-x2);
	}
	
	public int getLenght() {
		return Math.abs(z1-z2);
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
	
	public void setX1(int v) {
		x1 = v;
	}
	
	public void setZ1(int v) {
		z1 = v;
	}
	
	public void setX2(int v) {
		x2 = v;
	}
	
	public void setZ2(int v) {
		z2 = v;
	}

	public void setGuildName(String name) {
		guildname = name;
	}
	
	public void setCorner(int index,Location l) {
		corners[index] = l;
	}
}
