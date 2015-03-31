package co.marcin.NovaGuilds.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.Utils;

public class CommandCreateGuild implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandCreateGuild(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 2) return false;
		
		if(!(sender instanceof Player)) {
			plugin.info("You cannot create a guild from the console!");
		}
		Player player = plugin.getServer().getPlayer(sender.getName());

		String tag = args[0];
		String guildname = args[1];
			

		if(!plugin.getPlayerManager().getPlayerByName(sender.getName()).hasGuild()) {
			if(plugin.getGuildManager().getGuildByName(guildname) == null) {
				if(plugin.getGuildManager().getGuildByTag(tag) == null) {
					if(plugin.getRegionManager().getRegionAtLocation(player.getLocation())==null) {
						//items required
						List<ItemStack> items = new ArrayList<ItemStack>();
						List<String> itemstr = plugin.config.getStringList("guild.createitems");
						PlayerInventory inventory = player.getInventory();
						boolean hasitems = true;
						boolean hasMoney = true;
						int i=0;
						
						double requiredmoney = plugin.config.getInt("guild.createmoney"); 
						
						if(requiredmoney>0) {
							if(plugin.econ.getBalance(player.getName()) < requiredmoney) {
								hasMoney = false;
							}
						}
						
						if(itemstr.size()==0) {
							hasitems=true;
							if(plugin.DEBUG) plugin.info("no items required");
						}
						else {
							for(i=0;i<itemstr.size();i++) {
								String[] exp = itemstr.get(i).split(" ");
								ItemStack stack = new ItemStack(Material.getMaterial(exp[0].toUpperCase()),Integer.parseInt(exp[1]));
								items.add(stack);
							}
							
							for(i=0;i<items.size();i++) {
								if(!inventory.containsAtLeast(items.get(i),items.get(i).getAmount())) {
									hasitems = false;
								}
							}
						}
							
						if(hasitems==true) { //ALL PASSED
							if(hasMoney==true) {
								//Guild object
								NovaGuild newguild = new NovaGuild();
								newguild.setName(guildname);
								newguild.setTag(tag);
								newguild.setLeaderName(sender.getName());
								newguild.setSpawnPoint(player.getLocation());
								plugin.getGuildManager().addGuild(newguild);
								
								//taking money away
								plugin.econ.withdrawPlayer(sender.getName(),requiredmoney);
								
								//taking items away
								for(i=0;i<itemstr.size();i++) {
									player.getInventory().remove(items.get(i));
								}
								
								plugin.updateTabAll();
								plugin.updateTagPlayerToAll(plugin.senderToPlayer(sender));
								sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.createguild.success")));
								
								HashMap<String,String> vars = new HashMap<String,String>();
								vars.put("GUILDNAME",newguild.getName());
								vars.put("PLAYER",sender.getName());
								plugin.broadcastMessage("broadcast.guild.created", vars);
							}
							else {
								String rmmsg = plugin.getMessages().getString("chat.createguild.notenoughtmoney");
								rmmsg = Utils.replace(rmmsg,"{REQUIREDMONEY}",requiredmoney+"");
								sender.sendMessage(Utils.fixColors(plugin.prefix+rmmsg));
							}
						}
						else {
							String itemlist = "";
							for(i=0;i<items.size();i++) {
								String itemrow = plugin.getMessages().getString("chat.createguild.itemlist");
								itemrow = Utils.replace(itemrow,"{ITEMNAME}",items.get(i).getType().name());
								itemrow = Utils.replace(itemrow,"{AMOUNT}",items.get(i).getAmount()+"");
								
								itemlist += itemrow;
								
								if(i<items.size()-1) itemlist+= plugin.getMessages().getString("chat.createguild.itemlistsep");
							}
							
							sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.createguild.noitems")));
							sender.sendMessage(Utils.fixColors(itemlist));
						}
					}
					else { //region at loc
						sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.createguild.regionhere")));
					}
				}
				else { //tag exists
					sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.createguild.tagexists")));
				}
			}
			else { //name exists
				sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.createguild.nameexists")));
			}
		}
		else { //has guild already
			sender.sendMessage(Utils.fixColors(plugin.prefix+plugin.getMessages().getString("chat.createguild.hasguild")));
		}
		return true;
	}
}
