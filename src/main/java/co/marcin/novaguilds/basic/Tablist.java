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

import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import co.marcin.novaguilds.util.reflect.packet.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tablist {
	private final List<String> lines = new ArrayList<>();
	private final NovaPlayer nPlayer;

	public Tablist(NovaPlayer nPlayer) {
		this.nPlayer = nPlayer;
	}

	private void send(Player player) {
		PacketSender.sendPacket(player, packets(lines.toArray(new String[lines.size()]), false));

		update();

		PacketSender.sendPacket(player, packets(lines.toArray(new String[lines.size()]), true));
	}

	public void send() {
		if(nPlayer.isOnline()) {
			send(nPlayer.getPlayer());
		}
	}

	private void update() {
		lines.clear();

		Player[] op = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);

		Map<String, String> vars = new HashMap<>();
		vars.put("ONLINE", String.valueOf(op.length));
		vars.put("MAX", String.valueOf(Bukkit.getMaxPlayers()));
		vars.put("BALANCE", String.valueOf(nPlayer.getMoney()));
		vars.put("GUILD", nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
		vars.put("TAG", nPlayer.hasGuild() ? nPlayer.getGuild().getTag() : "");
		vars.put("PLAYER", nPlayer.getName());

		char[] colors = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'f' };

		List<String> scheme = Config.TABLIST_SCHEME.getStringList();
		int i=0;
		int t = 0;
		for(String line : scheme) {
			i++;

			if(i == 15) {
				i=0;
				t++;
			}

			line = "&"+colors[i]+StringUtils.replaceMap(line, vars);

			if(t >= 0) {
				line = "&"+colors[t] + line;
			}

			while(lines.contains(StringUtils.fixColors(line))) {
				line = "&"+(NumberUtils.randInt(0, 9)) + line;

				if(line.length() >= 16) {
					break;
				}
			}

			if(line.length() > 16) {
				line = line.substring(0, 16);
			}

			if(!lines.contains(line)) {
				lines.add(StringUtils.fixColors(line));
				LoggerUtils.debug("added line: " + line);
			}
		}
	}

	private static Object[] packets(String[] ss, boolean b) {
		Object[] packets = new Object[ss.length];
		for(int i = 0; i < ss.length; i++) {
			int ping = 0;
			packets[i] = PacketPlayOutPlayerInfo.getPacket(ss[i], b, ping);
		}

		return packets;
	}

	public static void patch() {
		for(Player tPlayer : Bukkit.getOnlinePlayers()) {
//			NovaPlayer tnPlayer = NovaPlayer.get(tPlayer);
			List<String> l = new ArrayList<>();

			for(Player player : Bukkit.getOnlinePlayers()) {
				l.add(player.getName());
//			    lines.add(NovaGuilds.getInstance().tagUtils.getTag(player)+player.getName());
//				String prefix = tnPlayer.getPlayer().getScoreboard().getPlayerTeam(player).getPrefix();
//				tnPlayer.getPlayer().sendMessage(prefix + player.getName());
//				tnPlayer.getTablist().lines.add(prefix+player.getName());
			}
			PacketSender.sendPacket(tPlayer, packets(l.toArray(new String[l.size()]), false));
		}
	}
}
