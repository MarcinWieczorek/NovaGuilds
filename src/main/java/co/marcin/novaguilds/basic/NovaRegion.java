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

	private int width = 0;
	private int height = 0;
	private int size = 0;
	
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

	public int getWidth() {
		if(width == 0) {
			//width = (int) Math.round(Math.abs(getCorner(0).getX() - getCorner(1).getX()));
			width = Math.abs(getCorner(0).getBlockX() - getCorner(1).getBlockX()) + 1;
		}

		return width;
	}

	public int getHeight() {
		if(height == 0) {
			//height = (int) Math.round(Math.abs(getCorner(0).getZ() - getCorner(1).getZ()));
			height = Math.abs(getCorner(0).getBlockZ() - getCorner(1).getBlockZ()) + 1;
		}

		return height;
	}

	public int getDiagonal() {
		if(size == 0) {
			size = Math.round((int)Math.sqrt(getWidth()^2 + getHeight()^2));
			//size = Math.round(Double.parseDouble(Math.sqrt(getWidth()^2 + getHeight()^2)+""));
		}

		return size;
	}

	public int getSurface() {
		return getHeight() * getWidth();
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
