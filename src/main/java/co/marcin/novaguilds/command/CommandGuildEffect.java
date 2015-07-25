package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
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
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return true;
		}

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return true;
		}

		double price = plugin.getGroupManager().getGroup(sender).getGuildEffectPrice();

		if(nPlayer.getGuild().getMoney() < price) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
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
		nPlayer.getGuild().takeMoney(price);

		//message
		HashMap<String,String> vars = new HashMap<>();
		vars.put("EFFECTTYPE",effectType.getName());

		Message.CHAT_GUILD_EFFECT_SUCCESS.vars(vars).send(sender);
		return true;
	}
}
