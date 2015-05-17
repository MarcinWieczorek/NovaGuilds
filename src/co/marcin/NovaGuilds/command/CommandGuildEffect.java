package co.marcin.NovaGuilds.command;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

public class CommandGuildEffect implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildEffect(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.guild.effect")) {

			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);

		if(!nPlayer.hasGuild()) {

			return true;
		}

		if(!nPlayer.isLeader()) {

			return true;
		}

		int duration = 2000;

		List<String> potionEffects = plugin.getConfig().getStringList("guild.effects");

		int rand = StringUtils.randInt(0, potionEffects.size() - 1);
		PotionEffectType effectType = PotionEffectType.getByName(potionEffects.get(rand));

		if(effectType == null) { //invalid effect

			return true;
		}


		PotionEffect effect = effectType.createEffect(duration, 1);

		Player player = plugin.senderToPlayer(sender);

		if(player.hasPotionEffect(effectType)) {
			player.removePotionEffect(effectType);
		}

		for(Player gPlayer : nPlayer.getGuild().getOnlinePlayers()) {
			gPlayer.addPotionEffect(effect);
		}

		//message
		HashMap<String,String> vars = new HashMap<>();
		vars.put("EFFECTTYPE",effectType.getName());

		plugin.sendMessagesMsg(sender,"chat.guild.effect.success",vars);

		return true;
	}
}
