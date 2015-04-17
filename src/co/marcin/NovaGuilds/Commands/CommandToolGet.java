package co.marcin.NovaGuilds.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandToolGet implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandToolGet(NovaGuilds pl) {
		plugin = pl;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("novaguilds.tool.get")) {
			Material tool = Material.getMaterial(plugin.getConfig().getString("region.tool.item").toUpperCase());
			
			if(tool != null) {
				ItemStack stick = new ItemStack(tool, 1);
				ItemMeta meta = stick.getItemMeta();
		        meta.setDisplayName(Utils.fixColors(plugin.getMessages().getString("items.tool.name")));
		       
		        List<String> lorecodes = plugin.getMessages().getStringList("items.tool.lore");
		        List<String> lore = new ArrayList<>();
		        
		        for(String l : lorecodes) {
		        	lore.add(Utils.fixColors(l));
		        }
		        
		        meta.setLore(lore);
				
		        stick.setItemMeta(meta);
		        Player player = plugin.getServer().getPlayer(sender.getName());
				player.getInventory().addItem(stick);
			}
		}
		else {
			plugin.sendMessagesMsg(sender, "chat.nopermissions");
		}
		return true;
	}
}
