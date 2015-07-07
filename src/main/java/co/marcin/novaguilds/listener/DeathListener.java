package co.marcin.novaguilds.listener;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.util.StringUtils;

public class DeathListener implements Listener {
	private final NovaGuilds plugin;
	
	public DeathListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {

		if(event.getEntity().getKiller() != null) {
			Player player = event.getEntity();
			Player attacker = event.getEntity().getKiller();
			
			NovaPlayer novaPlayer = plugin.getPlayerManager().getPlayer(player);
			NovaPlayer novaPlayerAttacker = plugin.getPlayerManager().getPlayer(attacker);
			
			String tag1 = "";
			String tag2 = "";
			String tagscheme = plugin.getConfig().getString("guild.tag");
			tagscheme = StringUtils.replace(tagscheme, "{RANK}", "");
			
			if(novaPlayer.hasGuild()) {
				tag1 = StringUtils.replace(tagscheme, "{TAG}", novaPlayer.getGuild().getTag());
			}

			if(novaPlayerAttacker.hasGuild()) {
				tag2 = StringUtils.replace(tagscheme, "{TAG}", novaPlayerAttacker.getGuild().getTag());
			}
			
			HashMap<String,String> vars = new HashMap<>();
			vars.put("PLAYER1",player.getName());
			vars.put("PLAYER2",attacker.getName());
			vars.put("TAG1",tag1);
			vars.put("TAG2",tag2);
			plugin.getMessageManager().broadcastMessage("broadcast.pvp.killed",vars);
			
			if(novaPlayer.hasGuild()) {
				NovaGuild guildVictim = plugin.getGuildManager().getGuildByPlayer(novaPlayer);
				guildVictim.takePoints(plugin.getConfig().getInt("guild.deathpoints"));
			}
			
			if(novaPlayerAttacker.hasGuild()) {
				NovaGuild guildAttacker = plugin.getGuildManager().getGuildByPlayer(novaPlayerAttacker);
				guildAttacker.addPoints(plugin.getConfig().getInt("guild.killpoints"));
			}
			
			//disable death message
			event.setDeathMessage(null);
		}
	}
}
