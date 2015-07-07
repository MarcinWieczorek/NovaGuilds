package co.marcin.novaguilds.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;

public class PvpListener implements Listener {
	private final NovaGuilds plugin;
	
	public PvpListener(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			Player attacker = null;
			Player player = (Player)event.getEntity();
			
			if(event.getDamager() instanceof Player) {
				attacker = (Player)event.getDamager();
			}
			else if(event.getDamager().getType().equals(EntityType.ARROW)) {
				Arrow arrow = (Arrow)event.getDamager();
				
				if(arrow.getShooter() instanceof Player) {
					attacker = (Player)arrow.getShooter();
				}
			}
			
			if(attacker != null) {
				NovaPlayer novaPlayer = plugin.getPlayerManager().getPlayer(player);
				NovaPlayer novaPlayerAttacker = plugin.getPlayerManager().getPlayer(attacker);
				//teampvp
				if(!novaPlayerAttacker.getName().equals(novaPlayer.getName())) {
					if(novaPlayerAttacker.hasGuild() && novaPlayer.hasGuild()) {
						if(plugin.getPlayerManager().isGuildMate(player,attacker)) { //same guild
							if(!novaPlayer.getGuild().getFriendlyPvp()) {
								plugin.getMessageManager().sendMessagesMsg(attacker, "chat.teampvp");
								event.setCancelled(true);

								//remove the arrow
								if(event.getDamager().getType().equals(EntityType.ARROW)) {
									event.getDamager().remove();
								}
							}
						}
						else if(plugin.getPlayerManager().isAlly(player,attacker)) { //ally
							if(!(novaPlayer.getGuild().getFriendlyPvp() && novaPlayerAttacker.getGuild().getFriendlyPvp())) {
								plugin.getMessageManager().sendMessagesMsg(attacker, "chat.allypvp");
								event.setCancelled(true);

								//remove the arrow
								if(event.getDamager().getType().equals(EntityType.ARROW)) {
									event.getDamager().remove();
								}
							}
						}
					}
				}
			}
		}
	}
}
