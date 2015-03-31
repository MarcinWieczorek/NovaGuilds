package co.marcin.NovaGuilds.Commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import co.marcin.NovaGuilds.NovaGuild;
import co.marcin.NovaGuilds.NovaGuilds;
import co.marcin.NovaGuilds.NovaPlayer;
import co.marcin.NovaGuilds.NovaRegion;
import co.marcin.NovaGuilds.Utils;

public class CommandNovaGuilds implements CommandExecutor {
	NovaGuilds plugin;
	
	public CommandNovaGuilds(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("book")) {
				if(!plugin.DEBUG) return false;
		        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		        BookMeta bm = (BookMeta)book.getItemMeta();
		        bm.setPages(Arrays.asList(new String[] { 
		        	""
		        }));
		        bm.setAuthor("CTRL");
		        bm.setTitle("Guilds Bank");
		        book.setItemMeta(bm);
		        Player player = plugin.getServer().getPlayer(sender.getName());
		        player.getInventory().setItem(8, book);
			}
			else if(args[0].equalsIgnoreCase("tool")) { //TOOL
				new CommandToolGet(plugin).onCommand(sender, cmd, label, args);
			}
			else if(args[0].equalsIgnoreCase("bc")) { //BROADCAST
				if(!plugin.DEBUG) return false;
				if(args.length > 1) {
					String msg ="";
					for(int i=1;i<args.length;i++) {
						msg += args[i]+" ";
					}
					
					plugin.broadcast(msg);
				}
				else {
					plugin.sendMessagesMsg(sender,"chat.usage.ng.broadcast");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("admin")) { //Admin commands
				if(sender.hasPermission("novaguilds.admin.access")) {
					new CommandAdmin(plugin).onCommand(sender, cmd, label, Utils.parseArgs(args,1));
				}
			}
			else if(args[0].equalsIgnoreCase("hd")) { //HolographicDisplays
				if(!plugin.DEBUG) return false;
				if(args.length>1) { //GUILDINFO
					if(args[1].equalsIgnoreCase("top")) {
						Player player = plugin.senderToPlayer(sender);
						Hologram hologram = HologramsAPI.createHologram(plugin,player.getLocation());
						hologram.appendTextLine(Utils.fixColors(plugin.getMessages().getString("holographicdisplays.topguilds.header")));
						
						int i = 1;
						for(Entry<String, NovaGuild> guild : plugin.getGuildManager().getGuilds()) {
							String rowmsg = plugin.getMessages().getString("holographicdisplays.topguilds.row");
							rowmsg = Utils.replace(rowmsg, "{GUILDNAME}",guild.getValue().getName());
							rowmsg = Utils.replace(rowmsg, "{N}",i+"");
							hologram.appendTextLine(Utils.fixColors(rowmsg));
							i++;
							if(i>plugin.getMessages().getInt("holographicdisplays.topguilds.toprows"))
								break;
						}
						return true;
					}
					
					String guildname = args[1];
					Player player = plugin.senderToPlayer(sender);
					Hologram hologram = HologramsAPI.createHologram(plugin,player.getLocation());
					
					NovaGuild guild = plugin.getGuildManager().getGuildByName(guildname);
					if(guild != null) {
						List<String> guildinfomsg = plugin.getMessages().getStringList("chat.guildinfo.info");
						
						int i;
						List<NovaPlayer> gplayers = guild.getPlayers();
						String leader = guild.getLeaderName();
						String players = "";
						String pcolor = "";
						String leaderp; //String to insert to playername (leader prefix)
						String leaderprefix = plugin.getMessages().getString("chat.guildinfo.leaderprefix"); //leader prefix
						
						if(gplayers.size()>0) {
							for(i=0;i<gplayers.size();i++) {
								NovaPlayer nplayer = gplayers.get(i);
								Player p = plugin.getServer().getPlayer(nplayer.getName());
								
								if(p instanceof Player &&p.isOnline()) {
									pcolor = plugin.getMessages().getString("chat.guildinfo.playercolor.online");
								}
								else {
									pcolor = plugin.getMessages().getString("chat.guildinfo.playercolor.offline");
								}
								
								leaderp = "";
								if(nplayer.getName().equalsIgnoreCase(leader)) {
									leaderp = leaderprefix;
								}
								
								players += pcolor+leaderp+nplayer.getName();
								
								if(i<gplayers.size()-1) players += plugin.getMessages().getString("chat.guildinfo.playerseparator");
							}
						}
						
						for(i=0;i < guildinfomsg.size();i++) {
							boolean skipmsg = false;
							String tagmsg = plugin.config.getString("guild.tag");
							String gmsg = guildinfomsg.get(i);
							
							tagmsg = Utils.replace(tagmsg,"{TAG}",guild.getTag());
							tagmsg = Utils.replace(tagmsg,"{RANK}","");
							
							gmsg = Utils.replace(gmsg,"{GUILDNAME}",guild.getName());
							gmsg = Utils.replace(gmsg,"{LEADER}",guild.getLeaderName());
							gmsg = Utils.replace(gmsg,"{TAG}",tagmsg);
							gmsg = Utils.replace(gmsg,"{MONEY}",guild.getMoney()+"");
							gmsg = Utils.replace(gmsg,"{PLAYERS}",players);
							
							if(gmsg.contains("{SP_X}") || gmsg.contains("{SP_Y}") || gmsg.contains("{SP_Z}")) {
								Location sp = guild.getSpawnPoint();
								if(sp != null) {
									gmsg = Utils.replace(gmsg,"{SP_X}",sp.getBlockX()+"");
									gmsg = Utils.replace(gmsg,"{SP_Y}",sp.getBlockY()+"");
									gmsg = Utils.replace(gmsg,"{SP_Z}",sp.getBlockZ()+"");
								}
								else {
									skipmsg = true;
								}
							}
							
							if(skipmsg==false) {
								hologram.appendTextLine(Utils.fixColors(gmsg));
							}
						}
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.guild.namenotexist");
					}
				}
				else {
					for(Hologram h: HologramsAPI.getHolograms(plugin)) {
						h.delete();
					}
				}
			}
			else if(args[0].equalsIgnoreCase("guild") || args[0].equalsIgnoreCase("g")) { // command /g
					new CommandGuild(plugin).onCommand(sender, cmd, label, Utils.parseArgs(args,1));
			}
			else if(args[0].equalsIgnoreCase("rg")) { //REGION
				if(args[1].equalsIgnoreCase("buy")) { //create
					if(!plugin.DEBUG) return true;
					if(sender.hasPermission("novaguilds.region.create")) {
						NovaPlayer nPlayer = plugin.getPlayerManager().getPlayerByName(sender.getName());
						
						if(nPlayer.hasGuild()) {
							NovaGuild guild = nPlayer.getGuild();
							
							if(guild.getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
								if(!guild.hasRegion()) {
									if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
										Location sl1 = nPlayer.getSelectedLocation(0);
										Location sl2 = nPlayer.getSelectedLocation(1);
	
										if(plugin.getRegionManager().checkRegionSelect(sl1, sl2).equals("valid")) {
											double createprice = plugin.getConfig().getDouble("region.createprice");
											double pricepb = plugin.getConfig().getDouble("region.pricepb");
											int regionsize = plugin.getRegionManager().checkRegionSize(nPlayer.getSelectedLocation(0),nPlayer.getSelectedLocation(1));
											double price = pricepb * regionsize + createprice;
											
											if(guild.getMoney() >= price) {
												NovaRegion region = new NovaRegion();
												
												region.setCorner(0,nPlayer.getSelectedLocation(0));
												region.setCorner(1,nPlayer.getSelectedLocation(1));
												
												plugin.getRegionManager().addRegion(region, guild);
												guild.takeMoney(price);
												plugin.getGuildManager().saveGuild(guild);
												plugin.sendMessagesMsg(sender,"chat.region.created");
											}
											else {
												plugin.sendMessagesMsg(sender,"chat.guild.notenoughtmoney");
											}
										}
										else {
											plugin.sendMessagesMsg(sender,"chat.region.notvalid");
										}
									}
									else {
										plugin.sendMessagesMsg(sender,"chat.region.areanotselected");
									}
								}
								else {
									plugin.sendMessagesMsg(sender,"chat.guild.hasregionalready");
								}
							}
							else {
								plugin.sendMessagesMsg(sender,"chat.guild.notleader");
							}
						}
						else {
							plugin.sendMessagesMsg(sender,"chat.guild.notinguild");
						}
					}
					else {
						plugin.sendMessagesMsg(sender,"chat.nopermissions");
					}
				}
			}
			else {
				plugin.sendMessagesMsg(sender,"chat.unknowncmd");
			}
		}
		else {
			String[] info = {
				"NovaGuilds &6#&c"+plugin.pdf.getVersion(),
				"Author: &6Marcin Wieczorek",
				"March, 2015 &4Pol&fand",
				"&bhttp://novaguilds.marcin.co/",
				"Latest plugin build: &6#&c{LATEST}"
			};
			
			sender.sendMessage(Utils.fixColors(plugin.prefix+"NovaGuilds Information"));
			String latest = Utils.getContent("http://novaguilds.marcin.co/latest.info");
			
			for(int i=0;i<info.length;i++) {
				info[i] = Utils.replace(info[i],"{LATEST}",latest);
				sender.sendMessage(Utils.fixColors("&2"+info[i]));
			}
		}
		
		return true;
	}
}
