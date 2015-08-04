package co.marcin.novaguilds.command;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Commands;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.util.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CommandGuildBoss implements Executor {
	private final Commands command;

	public CommandGuildBoss(Commands command) {
		this.command = command;
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!command.allowedSender(sender)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(sender);

		if(!nPlayer.hasGuild()) {
			Message.CHAT_PLAYER_HASNOGUILD.send(sender);
			return;
		}

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		Player player = (Player) sender;

		Inventory bossGUI = Bukkit.createInventory(null, 36, "Boss GUI");
		for(EntityType type : EntityType.values()) {
			if(type.isAlive() && type.isSpawnable()) {
				if(player.hasPermission("novaguilds.guild.boss.type." + type.name().toLowerCase())) {
					ItemStack item = new ItemStack(Material.MONSTER_EGG, 1, type.getTypeId());
					ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());
					meta.setDisplayName(WordUtils.capitalize(type.name().toLowerCase().replace('_',' ')));
					ArrayList<String> lore = new ArrayList<>();
					int price = 0;
					lore.add(StringUtils.fixColors("&6Price: &c"+price));
					meta.setLore(lore);
					item.setItemMeta(meta);

					bossGUI.addItem(item);
				}
			}
		}

		player.openInventory(bossGUI);
	}
}
