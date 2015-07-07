package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.util.RegionUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Set;

public class ToolListener implements Listener {
	private final NovaGuilds plugin;
	
	public ToolListener(NovaGuilds pl) {
		plugin = pl;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		Material tool = Material.getMaterial(plugin.getConfig().getString("region.tool.item").toUpperCase());
		String toolname = StringUtils.fixColors(plugin.getMessageManager().getMessagesString("items.tool.name"));

		if(player.getItemInHand().getType().equals(tool)) {
			if(player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(toolname)) {
				NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);

				//Spigot and Cauldron (1.8/1.7.10)
				Location pointedLocation = player.getTargetBlock((Set<Material>)null, 200).getLocation(); //TODO: spigot
				//Location pointedLocation = player.getTargetBlock(null, 200).getLocation(); //TODO: CAULDRON

				pointedLocation.setWorld(player.getWorld());

				//Change RegionMode
				if((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && player.isSneaking()) {
					if(!player.hasPermission("novaguilds.tool.check") || !player.hasPermission("novaguilds.region.create")) {
						return;
					}

					event.setCancelled(true);
					nPlayer.setRegionMode(!nPlayer.regionMode());

					String mode;
					if(nPlayer.regionMode()) {
						mode = plugin.getMessageManager().getMessagesString("chat.region.tool.modes.select");
					}
					else {
						mode = plugin.getMessageManager().getMessagesString("chat.region.tool.modes.check");
					}

					HashMap<String, String> vars = new HashMap<>();
					vars.put("MODE", mode);
					plugin.getMessageManager().sendMessagesMsg(player, "chat.region.tool.toggledmode", vars);
					plugin.debug("toggle=" + plugin.getPlayerManager().getPlayer(player).regionMode());

					if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
						RegionUtils.sendSquare(player, nPlayer.getSelectedLocation(0), nPlayer.getSelectedLocation(1), null, (byte) 0);
						RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(0));
						RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(1));
					}

					//unselect corners
					nPlayer.setSelectedLocation(0, null);
					nPlayer.setSelectedLocation(1, null);

					//remove region highlight
					if(nPlayer.getSelectedRegion() != null) {
						RegionUtils.resetHighlightRegion(event.getPlayer(), nPlayer.getSelectedRegion());
					}

					//disable resizing mode
					nPlayer.setResizing(false);

					return;
				}


				NovaRegion rgatloc = plugin.getRegionManager().getRegionAtLocation(pointedLocation);

				if(!nPlayer.regionMode()) { //CHECK MODE
					if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						if(!player.hasPermission("novaguilds.tool.check")) { //permissions check
							return;
						}

						if(nPlayer.getSelectedRegion() != null) {
							RegionUtils.resetHighlightRegion(player, nPlayer.getSelectedRegion());
						}

						if(rgatloc != null) {
							RegionUtils.highlightRegion(player, rgatloc);
							HashMap<String, String> vars = new HashMap<>();
							vars.put("GUILDNAME", rgatloc.getGuildName());
							plugin.getMessageManager().sendMessagesMsg(event.getPlayer(), "chat.region.belongsto", vars);
							nPlayer.setSelectedRegion(rgatloc);
						}
						else {
							plugin.getMessageManager().sendMessagesMsg(player,"chat.region.noregionhere");
							nPlayer.setSelectedRegion(null);
						}
					}
				}
				else { //CREATE MODE
					if(!event.getAction().equals(Action.PHYSICAL)) {
						if(rgatloc == null && !nPlayer.isResizing()) {
							if(!player.hasPermission("novaguilds.region.create")) {
								return;
							}

							Location sl1 = nPlayer.getSelectedLocation(0);
							Location sl2 = nPlayer.getSelectedLocation(1);
							event.setCancelled(true);

							//Corner 1
							if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
								if(nPlayer.getSelectedLocation(0) != null) {
									RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(0));
									if(nPlayer.getSelectedLocation(1) != null) {
										RegionUtils.sendSquare(player, sl1, sl2, null, (byte) 0);
									}
								}

								RegionUtils.setCorner(player, pointedLocation);
								nPlayer.setSelectedLocation(0, pointedLocation);
								sl1 = pointedLocation;
							}

							//Corner 2
							if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
								if(nPlayer.getSelectedLocation(1) != null) {
									RegionUtils.resetCorner(player, nPlayer.getSelectedLocation(1));
									if(nPlayer.getSelectedLocation(0) != null)
										RegionUtils.sendSquare(player, sl1, sl2, null, (byte) 0);
								}

								RegionUtils.setCorner(player, pointedLocation);
								nPlayer.setSelectedLocation(1, pointedLocation);
								sl2 = pointedLocation;
							}

							if(sl1 != null && sl2 != null) {
//								RegionUtils.distanceBetweenRegions2(plugin.getGuildManager().getGuildByName("rgudt").getRegion(),sl1,sl2);
//								plugin.getRegionManager().sendSquare(player, sl1, sl2, Material.WOOL, (byte)7);
//
//								if(plugin.DEBUG) return;

								RegionValidity validSelect = plugin.getRegionManager().checkRegionSelect(sl1, sl2);
								byte data = Byte.parseByte("15");

								switch(validSelect) {
									case VALID:  //valid
										if(nPlayer.hasGuild()) {
											data = (byte) 14;
											int regionsize = plugin.getRegionManager().checkRegionSize(sl1, sl2);
											String sizemsg = plugin.getMessageManager().getMessagesString("chat.region.size");
											sizemsg = StringUtils.replace(sizemsg, "{SIZE}", regionsize + "");

											double price = plugin.getGroupManager().getGroup(player).getRegionPricePerBlock() * regionsize + plugin.getGroupManager().getGroup(player).getRegionCreateMoney();

											String pricemsg = plugin.getMessageManager().getMessagesString("chat.region.price");
											pricemsg = StringUtils.replace(pricemsg, "{PRICE}", price + "");

											plugin.getMessageManager().sendPrefixMessage(player, sizemsg);
											plugin.getMessageManager().sendPrefixMessage(player, pricemsg);

											double guildBalance = nPlayer.getGuild().getMoney();
											if(guildBalance < price) {
												String cnotaffordmsg = plugin.getMessageManager().getMessagesString("chat.region.cnotafford");
												cnotaffordmsg = StringUtils.replace(cnotaffordmsg, "{NEEDMORE}", price - guildBalance + "");
												plugin.getMessageManager().sendPrefixMessage(player, cnotaffordmsg);
											} else {
												plugin.getMessageManager().sendMessagesMsg(player, "chat.region.selectsuccess");
											}
										} else {
											plugin.getMessageManager().sendMessagesMsg(player, "chat.region.mustveguild");
										}
										break;
									case TOOSMALL:
										String msg = plugin.getMessageManager().getMessagesString("chat.region.toosmall");
										msg = StringUtils.replace(msg, "{MINSIZE}", plugin.getConfig().getInt("region.minsize") + "");
										plugin.getMessageManager().sendPrefixMessage(player, msg);
										break;
									case TOOBIG:
										msg = plugin.getMessageManager().getMessagesString("chat.region.toobig");
										msg = StringUtils.replace(msg, "{MAXSIZE}", plugin.getConfig().getInt("region.maxsize") + "");
										plugin.getMessageManager().sendPrefixMessage(player, msg);
										break;
									case OVERLAPS:
										//TODO
										//NovaRegion rgoverlaped = plugin.getRegionManager().regionInsideArea(sl1,sl2);
										//plugin.getRegionManager().highlightRegion(player, rgoverlaped);
										plugin.getMessageManager().sendMessagesMsg(player, "chat.region.overlaps");
										break;
									case TOOCLOSE:
										plugin.getMessageManager().sendMessagesMsg(player, "chat.guild.tooclose");
										break;
								}

								//corners and rectangles
								RegionUtils.sendSquare(player, sl1, sl2, Material.WOOL, data);
								RegionUtils.setCorner(player, sl1);
								RegionUtils.setCorner(player, sl2);
							}
						}
						else { //resizing
							if(rgatloc != null) {
								if(!player.hasPermission("novaguilds.region.resize")) {
									plugin.getMessageManager().sendNoPermissionsMessage(player);
									return;
								}

								if(nPlayer.isResizing()) {
									NovaRegion region = nPlayer.getGuild().getRegion();
									Location c1 = region.getCorner(0);
									Location c2 = region.getCorner(1);

									if(nPlayer.getResizingCorner() == 0) {
										c1 = pointedLocation;
									}
									else {
										c2 = pointedLocation;
									}

									RegionUtils.sendSquare(player, c1, c2, Material.WOOL, (byte) 14);
								}
								else {
									plugin.debug("guild null=" + (rgatloc.getGuild() == null));
									if(rgatloc.getGuild().isMember(nPlayer)) {
										Location pointedCornerLocation = pointedLocation.clone();
										pointedCornerLocation.setY(0);
										pointedCornerLocation = pointedCornerLocation.getBlock().getLocation();
										plugin.debug("0=" + pointedCornerLocation.distance(rgatloc.getCorner(0).getBlock().getLocation()));
										plugin.debug("1=" + pointedCornerLocation.distance(rgatloc.getCorner(1).getBlock().getLocation()));

										if(pointedCornerLocation.distance(rgatloc.getCorner(0).getBlock().getLocation()) < 1 || pointedCornerLocation.distance(rgatloc.getCorner(0).getBlock().getLocation()) < 1) { //clicked a corner
											int corner = 1;

											if(pointedCornerLocation.distance(rgatloc.getCorner(0)) == 0) {
												corner = 0;
											}

											nPlayer.setResizing(true);
											nPlayer.setResizingCorner(corner);
											player.sendMessage("resizing... " + corner);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
