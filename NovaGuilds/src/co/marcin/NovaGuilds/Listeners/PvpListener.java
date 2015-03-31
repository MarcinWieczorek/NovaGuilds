package co.marcin.NovaGuilds.Listeners;

import java.util.HashMap;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.Utils;

public class PvpListener implements Listener {
	private NovaGuilds pl;
	
	public PvpListener(NovaGuilds novaGuilds) {
		pl = novaGuilds;
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			Player attacker = null;
			Player player = (Player)event.getEntity();
			
			if(event.getDamager() instanceof Player) {
				attacker = (Player)event.getDamager();
			}
			else if(event.getDamager().equals(EntityType.ARROW)) {
				Arrow arrow = (Arrow)event.getDamager();
				
				if(arrow.getShooter() instanceof Player) {
					attacker = (Player)arrow.getShooter();
				}
			}
			
			if(attacker != null) {
				NovaPlayer novaPlayer = pl.getPlayerManager().getPlayerByName(player.getName());
				NovaPlayer novaPlayerAttacker = pl.getPlayerManager().getPlayerByName(attacker.getName());
				
				//teampvp
				if(novaPlayerAttacker.hasGuild() && novaPlayer.hasGuild()) {
					if(novaPlayerAttacker.getGuild().equals(novaPlayer.getGuild())) {
						attacker.sendMessage(Utils.fixColors(pl.prefix+pl.getMessages().getString("chat.teampvp")));
						event.setCancelled(true);
						return;
					}
					else if(novaPlayerAttacker.getGuild().isAlly(novaPlayer.getGuild())) {
						attacker.sendMessage(Utils.fixColors(pl.prefix+pl.getMessages().getString("chat.allypvp")));
						event.setCancelled(true);
						return;
					}
				}
				
				//kill
				if(player.getHealth()-event.getDamage() < 1) {
					String tag1 = "";
					String tag2 = "";
					String tagscheme = pl.getConfig().getString("guild.tag");
					tagscheme = Utils.replace(tagscheme, "{RANK}","");
					
					if(novaPlayer.hasGuild()) {
						tag1 = tagscheme = Utils.replace(tagscheme, "{TAG}",novaPlayer.getGuild().getTag());
					}

					if(novaPlayerAttacker.hasGuild()) {
						tag2 = tagscheme = Utils.replace(tagscheme, "{TAG}",novaPlayerAttacker.getGuild().getTag());
					}
					
					HashMap<String,String> vars = new HashMap<String,String>();
					vars.put("PLAYER1",player.getName());
					vars.put("PLAYER2",attacker.getName());
					vars.put("TAG1",tag1);
					vars.put("TAG2",tag2);
					pl.broadcastMessage("broadcast.pvp.killed",vars);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
	}
}
