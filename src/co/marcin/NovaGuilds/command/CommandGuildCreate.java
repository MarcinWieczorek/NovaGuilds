package co.marcin.NovaGuilds.command;

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

import co.marcin.NovaGuilds.basic.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.basic.NovaPlayer;
import co.marcin.NovaGuilds.utils.StringUtils;

public class CommandGuildCreate implements CommandExecutor {
	public final NovaGuilds plugin;
	
	public CommandGuildCreate(NovaGuilds pl) {
		plugin = pl;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 2) return false;
		
		if(!(sender instanceof Player)) {
			plugin.info("You cannot create a guild from the console!");
			return true;
		}
		Player player = plugin.getServer().getPlayer(sender.getName());

		String tag = args[0];
		String guildname = args[1];
		
		//remove colors
		guildname = StringUtils.removeColors(guildname);
		if(!plugin.getConfig().getBoolean("guild.settings.tag.color")) {
			tag = StringUtils.removeColors(tag);
		}
			
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerBySender(sender);
		
		if(!nPlayer.hasGuild()) {
			if(plugin.getGuildManager().getGuildByName(guildname) == null) {
				if(plugin.getGuildManager().getGuildByTag(tag) == null) {
					if(plugin.getRegionManager().getRegionAtLocation(player.getLocation())==null) {
						//tag length
						if(tag.length() > plugin.getConfig().getInt("guild.settings.tag.max")) { //too long
							plugin.sendMessagesMsg(sender,"chat.createguild.tag.toolong");
							return true;
						}
						
						if(StringUtils.removeColors(tag).length() < plugin.getConfig().getInt("guild.settings.tag.min")) { //too short
							plugin.sendMessagesMsg(sender,"chat.createguild.tag.tooshort");
							return true;
						}
						
						//name length
						if(guildname.length() > plugin.getConfig().getInt("guild.settings.name.max")) { //too long
							plugin.sendMessagesMsg(sender,"chat.createguild.name.toolong");
							return true;
						}
						
						if(guildname.length() < plugin.getConfig().getInt("guild.settings.name.min")) { //too short
							plugin.sendMessagesMsg(sender,"chat.createguild.name.tooshort");
							return true;
						}
						
						String group = "default";
						
						for(String s : plugin.getConfig().getConfigurationSection("guild.create.groups").getKeys(false)) {
							if(sender.hasPermission("NovaGuilds.group."+s)) {
								group = s;
								break;
							}
						}
						
						//items required
						List<ItemStack> items = new ArrayList<>();
						List<String> itemstr = plugin.getConfig().getStringList("guild.create.groups."+group+".items");
						PlayerInventory inventory = player.getInventory();
						boolean hasitems = true;
						boolean hasMoney = true;
						int i=0;
						
						double requiredmoney = plugin.getConfig().getInt("guild.create."+group+".money"); 
						
						if(requiredmoney>0 || sender.hasPermission("NovaGuilds.group.admin")) {
							if(plugin.econ.getBalance(player.getName()) < requiredmoney) {
								hasMoney = false;
							}
						}
						
						if(itemstr.size()==0 || sender.hasPermission("NovaGuilds.group.admin")) {
							hasitems=true;
							if(plugin.DEBUG) plugin.info("no items required");
						}
						else {
							ItemStack stack;
							for(i=0;i<itemstr.size();i++) {
								String[] exp = itemstr.get(i).split(" ");
								String idname;
								String[] dataexp = null;
								byte data = (byte)0;
								int amount = Integer.parseInt(exp[1]);
								
								if(exp[0].contains(":")) {
									dataexp = exp[0].split(":");
									idname = dataexp[0];
									data = Byte.parseByte(dataexp[1]);
								}
								else {
									idname = exp[0];
								}
								
								stack = new ItemStack(Material.getMaterial(idname.toUpperCase()),amount);
								
								if(dataexp != null) {
									stack.getData().setData(data);
								}
								
								items.add(stack);
							}
							
							for(i=0;i<items.size();i++) {
								if(!inventory.containsAtLeast(items.get(i),items.get(i).getAmount())) {
									hasitems = false;
								}
							}
						}
							
						if(hasitems) { //ALL PASSED
							if(hasMoney) {
								//Guild object
								NovaGuild newguild = new NovaGuild();
								newguild.setName(guildname);
								newguild.setTag(tag);
								newguild.setLeaderName(sender.getName());
								newguild.setSpawnPoint(player.getLocation());
								newguild.addPlayer(nPlayer);
								plugin.getGuildManager().addGuild(newguild);
								
								//taking money away
								plugin.econ.withdrawPlayer(sender.getName(),requiredmoney);
								
								//taking items away
								for(ItemStack item : items) {
									player.getInventory().removeItem(item);
								}
								
								//update tag and tabs
								plugin.updateTabAll();
								plugin.tagUtils.updateTagPlayerToAll(plugin.senderToPlayer(sender));
								
								//messages
								sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.createguild.success")));
								
								HashMap<String,String> vars = new HashMap<>();
								vars.put("GUILDNAME",newguild.getName());
								vars.put("PLAYER",sender.getName());
								plugin.broadcastMessage("broadcast.guild.created", vars);
							}
							else {
								String rmmsg = plugin.getMessages().getString("chat.createguild.notenoughtmoney");
								rmmsg = StringUtils.replace(rmmsg, "{REQUIREDMONEY}", requiredmoney + "");
								sender.sendMessage(StringUtils.fixColors(plugin.prefix + rmmsg));
							}
						}
						else {
							String itemlist = "";
							for(i=0;i<items.size();i++) {
								String itemrow = plugin.getMessages().getString("chat.createguild.itemlist");
								itemrow = StringUtils.replace(itemrow, "{ITEMNAME}", items.get(i).getType().name());
								itemrow = StringUtils.replace(itemrow, "{AMOUNT}", items.get(i).getAmount() + "");
								
								itemlist += itemrow;
								
								if(i<items.size()-1) itemlist+= plugin.getMessages().getString("chat.createguild.itemlistsep");
							}
							
							sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.createguild.noitems")));
							sender.sendMessage(StringUtils.fixColors(itemlist));
						}
					}
					else { //region at loc
						sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.createguild.regionhere")));
					}
				}
				else { //tag exists
					sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.createguild.tagexists")));
				}
			}
			else { //name exists
				sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.createguild.nameexists")));
			}
		}
		else { //has guild already
			sender.sendMessage(StringUtils.fixColors(plugin.prefix + plugin.getMessages().getString("chat.createguild.hasguild")));
		}
		return true;
	}
}
