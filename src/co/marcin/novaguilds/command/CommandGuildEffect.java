package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.utils.StringUtils;
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
			plugin.sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);

		if(!nPlayer.hasGuild()) {
			plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
			return true;
		}

		if(!nPlayer.isLeader()) {
			plugin.sendMessagesMsg(sender,"chat.guild.notleader");
			return true;
		}

		if(nPlayer.getGuild().getMoney() < plugin.getGroup(sender).getEffectPrice()) {
			plugin.sendMessagesMsg(sender,"chat.guild.notenoughtmoney");
			return true;
		}

		//TODO: configurable duration
		int duration = 2000;

		List<String> potionEffects = plugin.getConfig().getStringList("guild.effects");

		if(potionEffects.size() == 0) {
			plugin.sendMessagesMsg(sender,"chat.erroroccured");
			plugin.info("Invalid effect, check config!");
			return true;
		}

		int rand = StringUtils.randInt(0, potionEffects.size() - 1);
		PotionEffectType effectType = PotionEffectType.getByName(potionEffects.get(rand));

		if(effectType == null) { //invalid effect
			plugin.sendMessagesMsg(sender,"chat.erroroccured");
			plugin.info("Invalid effect, check config!");
			return true;
		}


		PotionEffect effect = effectType.createEffect(duration, 1);

		Player player = plugin.senderToPlayer(sender);

		//add effect
		if(player.hasPotionEffect(effectType)) {
			player.removePotionEffect(effectType);
		}

		for(Player gPlayer : nPlayer.getGuild().getOnlinePlayers()) {
			gPlayer.addPotionEffect(effect);
		}

		//remove money
		nPlayer.getGuild().takeMoney(plugin.getGroup(sender).getEffectPrice());

		//message
		HashMap<String,String> vars = new HashMap<>();
		vars.put("EFFECTTYPE",effectType.getName());

		plugin.sendMessagesMsg(sender,"chat.guild.effect.success",vars);

		return true;
	}
}
