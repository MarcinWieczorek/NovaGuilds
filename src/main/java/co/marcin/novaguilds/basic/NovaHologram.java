package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.ItemStackUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NovaHologram {
	private String name;
	private Location location;
	private List<String> lines = new ArrayList<>();
	private Hologram hologram;
	private boolean isTop = false;

	//getters
	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public List<String> getLines() {
		return lines;
	}

	//setters
	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	//add
	public void addLine(String line) {
		lines.add(line);
	}

	public void clearLines() {
		lines.clear();
	}

	public void addLine(List<String> lines) {
		this.lines.addAll(lines);
	}

	public void refresh() {
		hologram.clearLines();

		if(isTop()) {
			clearLines();
			addLine(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
			addLine(NovaGuilds.getInstance().getGuildManager().getTopGuilds());
		}

		for(String line : lines) {
			if(line.startsWith("[ITEM]")) {
				String ISline = line.substring(6);
				ItemStack is = ItemStackUtils.stringToItemStack(ISline);

				if(is != null) {
					hologram.appendItemLine(is);
				}
			} else {
				hologram.appendTextLine(line);
			}
		}
	}

	public void create() {
		hologram = HologramsAPI.createHologram(NovaGuilds.getInstance(), location);
		refresh();
	}

	//check
	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean top) {
		this.isTop = top;
	}
}
