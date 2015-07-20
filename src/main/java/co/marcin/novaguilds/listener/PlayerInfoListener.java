package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.HashMap;

public class PlayerInfoListener implements Listener {
	private final NovaGuilds plugin;

	public PlayerInfoListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickPlayer(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if(event.getRightClicked() instanceof Player) {
			if(event.getPlayer().hasPermission("novaguilds.playerinfo")) { //TODO better permission node
				Player clickedPlayer = (Player) event.getRightClicked();
				NovaPlayer nCPlayer = plugin.getPlayerManager().getPlayer(clickedPlayer);

				HashMap<String, String> vars = new HashMap<>();
				vars.put("PLAYERNAME", nCPlayer.getName());
				vars.put("POINTS", String.valueOf(nCPlayer.getPoints()));
				vars.put("KILLS", String.valueOf(nCPlayer.getKills()));
				vars.put("DEATHS", String.valueOf(nCPlayer.getDeaths()));
				vars.put("KDR", String.valueOf(nCPlayer.getKills() / (nCPlayer.getDeaths() == 0 ? 1 : nCPlayer.getDeaths())));

				if(nCPlayer.hasGuild()) {
					vars.put("GUILDNAME", nCPlayer.getGuild().getName());
					vars.put("TAG", plugin.tagUtils.getTag(nCPlayer.getPlayer()));
				}

				Message.CHAT_PLAYER_INFO_HEADER.send(player);
				Message.CHAT_PLAYER_INFO_ITEMS.list().vars(vars).send(player);
			}
		}
	}
}
