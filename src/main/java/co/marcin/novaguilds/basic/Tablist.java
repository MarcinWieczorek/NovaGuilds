package co.marcin.novaguilds.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.StringUtils;
import co.marcin.novaguilds.util.reflect.PacketSender;
import co.marcin.novaguilds.util.reflect.packet.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tablist {
	private final List<String> lines = new ArrayList<>();
	private final NovaPlayer nPlayer;
	private static int ping = 0;

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

//		lines.addAll(Config.TABLIST_SCHEME.getStringList());
		Player[] op = Bukkit.getOnlinePlayers();

		HashMap<String, String> vars = new HashMap<>();
		vars.put("ONLINE", String.valueOf(op.length));
		vars.put("MAX", String.valueOf(Bukkit.getMaxPlayers()));
		vars.put("BALANCE", String.valueOf(nPlayer.getMoney()));
		vars.put("GUILD", nPlayer.hasGuild() ? nPlayer.getGuild().getName() : "");
		vars.put("TAG", nPlayer.hasGuild() ? nPlayer.getGuild().getTag() : "");
		vars.put("PLAYER", nPlayer.getName());
//		vars.put("",);

		for(String line : Config.TABLIST_SCHEME.getStringList()) {
			line = StringUtils.replaceMap(line, vars);

			if(line.length() <= 16) {
				lines.add(line);
			}
		}
	}

	private static Object[] packets(String[] ss, boolean b) {
		Object[] packets = new Object[ss.length];
		for(int i = 0; i < ss.length; i++) {
			packets[i] = PacketPlayOutPlayerInfo.getPacket(ss[i], b, ping);
		}

		return packets;
	}

	public static void patch() {
		for(Player tPlayer : Bukkit.getOnlinePlayers()) {
			NovaPlayer tnPlayer = NovaPlayer.get(tPlayer);
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
