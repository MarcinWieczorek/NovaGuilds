/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.impl.versionimpl.v1_7_R4;

import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.util.Packet;
import co.marcin.novaguilds.api.util.packet.PacketEvent;
import co.marcin.novaguilds.api.util.packet.PacketExtension;
import co.marcin.novaguilds.impl.basic.AbstractTabList;
import co.marcin.novaguilds.impl.util.AbstractPacketHandler;
import co.marcin.novaguilds.impl.versionimpl.v1_7_R4.packet.PacketPlayOutPlayerInfo;
import co.marcin.novaguilds.util.CompatibilityUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TabListImpl extends AbstractTabList {
	private boolean first = true;

	static {
		new AbstractPacketHandler("PacketPlayOutPlayerInfo", PacketExtension.PacketHandler.Direction.OUT) {
			@Override
			public void handle(PacketEvent event) {
				try {
					PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(event.getPacket());

					if(!packet.getUsername().startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
						event.setCancelled(true);
					}
				}
				catch(IllegalAccessException e) {
					LoggerUtils.exception(e);
				}
			}
		};
	}

	/**
	 * The constructor
	 *
	 * @param nPlayer tablist owner
	 */
	public TabListImpl(NovaPlayer nPlayer) {
		super(nPlayer);
	}

	@Override
	public void send() {
		if(!getPlayer().isOnline()) {
			return;
		}

		Scoreboard scoreboard = getPlayer().getPlayer().getScoreboard();
		List<Packet> packets = new ArrayList<>();
		List<String> oldLines = new ArrayList<>(lines);
		TabUtils.fillVars(this);

		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			line = StringUtils.replaceVarKeyMap(line, getVars());
			line = StringUtils.fixColors(line);

			//Add order
			StringBuilder colorPrefix = new StringBuilder();
			for(char ic : String.valueOf(i).toCharArray()) {
				colorPrefix.append(ChatColor.COLOR_CHAR + "").append(ic);
			}

			if(!line.startsWith(colorPrefix.substring(colorPrefix.length() - 2))) {
				colorPrefix.append(ChatColor.COLOR_CHAR + "r");
			}

			line = colorPrefix + line;

			Team team = scoreboard.getTeam("ngtab_" + i);
			if(team != null) {
				team.unregister();
			}

			team = scoreboard.registerNewTeam("ngtab_" + i);
			String prefix = "", suffix = "";

			if(line.length() > 16) {
				prefix = line.substring(0, 15);
				line = line.substring(15);

				if(prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
					prefix = prefix.substring(0, prefix.length() - 1);
					line = ChatColor.COLOR_CHAR + line;
				}

				if(!line.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
					String lastColors = ChatColor.getLastColors(prefix);
					ChatColor color = ChatColor.getByChar(lastColors.charAt(1));

					if(color == null) {
						color = ChatColor.WHITE;
					}

					line = color + line;
				}

				if(line.length() > 16) {
					suffix = line.substring(15, line.length());
					line = line.substring(0, 15);

					if(line.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
						suffix = ChatColor.COLOR_CHAR + suffix;
						line = line.substring(0, line.length() - 1);
					}

					if(line.length() > 16) {
						line = line.substring(0, 15);
					}
				}
			}

			team.setPrefix(prefix);
			team.setSuffix(suffix);
			CompatibilityUtils.addTeamEntry(team, line);
			lines.set(i, line);
		}

		try {
			if(!first) {
				for(String line : oldLines) {
					packets.add(new PacketPlayOutPlayerInfo(line, PacketPlayOutPlayerInfo.Action.REMOVE_PLAYER, 0));
				}
			}

			for(int i = 0; i < 20; i++) {
				for(int x = 0; x < 3; x++) {
					String line = i < lines.size() ? lines.get(i + x*20) : "";
					packets.add(new PacketPlayOutPlayerInfo(line, PacketPlayOutPlayerInfo.Action.ADD_PLAYER, 0));
				}
			}

			if(first) {
				first = false;
			}

			PacketSender.sendPacket(getPlayer().getPlayer(), packets.toArray());
		}
		catch(IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
			LoggerUtils.exception(e);
		}
	}
}
