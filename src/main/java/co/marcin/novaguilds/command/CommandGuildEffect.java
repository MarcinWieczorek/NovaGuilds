package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

public class CommandGuildEffect implements Executor {
	private final Commands command;

	public CommandGuildEffect(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.hasPermission(sender)) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return;
		}

		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		double price = plugin.getGroupManager().getGroup(sender).getGuildEffectPrice();

		if(nPlayer.getGuild().getMoney() < price) {
			Message.CHAT_GUILD_NOTENOUGHMONEY.send(sender);
			return;
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
	}
}
