package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.util.NumberUtils;
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
			plugin.getMessageManager().sendNoPermissionsMessage(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notinguild");
			return true;
		}

		if(!nPlayer.isLeader()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notleader");
			return true;
		}

		if(nPlayer.getGuild().getMoney() < plugin.getGroupManager().getGroup(sender).getGuildEffectPrice()) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.notenoughmoney");
			return true;
		}

		//TODO: configurable duration
		int duration = plugin.getConfigManager().getGuildEffectDuration();

		List<PotionEffectType> potionEffects = plugin.getConfigManager().getGuildEffects();

		int rand = NumberUtils.randInt(0, potionEffects.size() - 1);
		PotionEffectType effectType = potionEffects.get(rand);

		PotionEffect effect = effectType.createEffect(duration, 1);
		Player player = (Player)sender;

		//add effect
		if(player.hasPotionEffect(effectType)) {
			player.removePotionEffect(effectType);
		}

		for(Player gPlayer : nPlayer.getGuild().getOnlinePlayers()) {
			gPlayer.addPotionEffect(effect);
		}

		//remove money
		nPlayer.getGuild().takeMoney(plugin.getGroupManager().getGroup(sender).getGuildEffectPrice());

		//message
		HashMap<String,String> vars = new HashMap<>();
		vars.put("EFFECTTYPE",effectType.getName());

		plugin.getMessageManager().sendMessagesMsg(sender,"chat.guild.effect.success",vars);

		return true;
	}
}
