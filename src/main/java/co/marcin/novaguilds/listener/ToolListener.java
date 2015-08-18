package co.marcin.novaguilds.listener;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.util.RegionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ToolListener implements Listener {
	private final NovaGuilds plugin;
	
	public ToolListener(NovaGuilds pl) {
		plugin = pl;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		HashMap<String, String> vars = new HashMap<>();

		if(!player.getItemInHand().equals(Config.REGION_TOOL.getItemStack())) {
			return;
		}

		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(player);
		Location pointedLocation = player.getTargetBlock((HashSet<Byte>) null, 200).getLocation();
		Action action = event.getAction();

		pointedLocation.setWorld(player.getWorld());

		//Change RegionMode
		if((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && player.isSneaking()) {
			if(!player.hasPermission("novaguilds.tool.check") || !player.hasPermission("novaguilds.region.create")) {
				return;
			}

			//remove region highlight
			if(nPlayer.getSelectedRegion() != null) {
				RegionUtils.highlightRegion(event.getPlayer(), nPlayer.getSelectedRegion(), null);
			}

			event.setCancelled(true);
			nPlayer.setRegionMode(!nPlayer.getRegionMode());

			String mode;
			if(nPlayer.getRegionMode()) {
				mode = Message.CHAT_REGION_TOOL_MODES_SELECT.get();

				if(nPlayer.hasGuild() && nPlayer.isLeader() && nPlayer.getGuild().hasRegion()) {
					RegionUtils.highlightRegion(player, nPlayer.getGuild().getRegion(), Material.GOLD_BLOCK);
					nPlayer.setSelectedRegion(nPlayer.getGuild().getRegion());
				}
			}
			else {
				mode = Message.CHAT_REGION_TOOL_MODES_CHECK.get();
			}

			vars.put("MODE", mode);
			Message.CHAT_REGION_TOOL_TOGGLEDMODE.vars(vars).send(player);

			if(nPlayer.getSelectedLocation(0) != null && nPlayer.getSelectedLocation(1) != null) {
				RegionUtils.sendSquare(player, nPlayer.getSelectedLocation(0), nPlayer.getSelectedLocation(1), null, (byte) 0);
				RegionUtils.setCorner(player, nPlayer.getSelectedLocation(0), null);
				RegionUtils.setCorner(player, nPlayer.getSelectedLocation(1), null);
			}

			//unselect corners
			nPlayer.setSelectedLocation(0, null);
			nPlayer.setSelectedLocation(1, null);

			//disable resizing mode
			nPlayer.setResizing(false);

			return;
		}


		NovaRegion region = plugin.getRegionManager().getRegion(pointedLocation);

		if(!nPlayer.getRegionMode() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) { //CHECK MODE
			if(!player.hasPermission("novaguilds.tool.check")) { //permissions check
				return;
			}

			//if(nPlayer.getSelectedRegion() != null && !(nPlayer.getGuild().hasRegion() && nPlayer.getGuild().getRegion().equals(nPlayer.getSelectedRegion()))) {
			if(nPlayer.getSelectedRegion() != null) {
				RegionUtils.highlightRegion(event.getPlayer(), nPlayer.getSelectedRegion(), null);
			}

			if(region != null) {
				RegionUtils.highlightRegion(player, region, Material.DIAMOND_BLOCK);
				vars.put("GUILDNAME", region.getGuildName());
				Message.CHAT_REGION_BELONGSTO.vars(vars).send(player);
				nPlayer.setSelectedRegion(region);
			}
			else {
				Message.CHAT_REGION_NOREGIONHERE.send(player);
				nPlayer.setSelectedRegion(null);
			}
		}
		else if(event.getAction() != Action.PHYSICAL) { //CREATE MODE
			if(region != null && !nPlayer.isResizing()) { //resizing
				if(!player.hasPermission("novaguilds.region.resize")) {
					return;
				}

				if(region.getGuild().isMember(nPlayer) && nPlayer.isLeader()) {
					Location pointedCornerLocation = pointedLocation.clone();
					pointedCornerLocation.setY(0);

					if(pointedCornerLocation.distance(region.getCorner(0).getBlock().getLocation()) < 1 || pointedCornerLocation.distance(region.getCorner(1).getBlock().getLocation()) < 1) { //clicked a corner
						int corner = 1;

						if(pointedCornerLocation.distance(region.getCorner(0)) < 1) {
							corner = 0;
						}

						nPlayer.setResizing(true);
						nPlayer.setResizingCorner(corner);
						Message.CHAT_REGION_RESIZE_START.send(player);
						RegionUtils.sendSquare(player, nPlayer.getSelectedLocation(0), nPlayer.getSelectedLocation(1), null, (byte) 0);
						nPlayer.setSelectedLocation(0, null);
						nPlayer.setSelectedLocation(1, null);
						nPlayer.setSelectedLocation(corner == 1 ? 0 : 1, region.getCorner(corner == 1 ? 0 : 1));
					}
				}
			}
			else {
				if(!player.hasPermission("novaguilds.region.create")) {
					return;
				}

				Location sl0 = nPlayer.getSelectedLocation(0);
				Location sl1 = nPlayer.getSelectedLocation(1);
				event.setCancelled(true);

				//Corner 1
				if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if(!nPlayer.isResizing()) {
						if(nPlayer.getSelectedLocation(0) != null) {
							RegionUtils.setCorner(player, nPlayer.getSelectedLocation(0), null);

							if(nPlayer.getSelectedLocation(1) != null) {
								RegionUtils.sendSquare(player, sl0, sl1, null, (byte) 0);
							}
						}

						RegionUtils.setCorner(player, pointedLocation, Material.EMERALD_BLOCK);
						nPlayer.setSelectedLocation(0, pointedLocation);
						sl0 = pointedLocation;
					}
				}

				//Corner 2
				if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(nPlayer.isResizing()) {
						if(nPlayer.getSelectedLocation(nPlayer.getResizingCorner()) != null) {
							RegionUtils.setCorner(player, nPlayer.getSelectedLocation(nPlayer.getResizingCorner()), null);

							if(nPlayer.getSelectedLocation(nPlayer.getResizingCorner()==0 ? 1 : 0) != null) {
								RegionUtils.sendSquare(player, sl0, sl1, null, (byte) 0);
							}
						}

						if(nPlayer.getResizingCorner()==0) {
							sl0 = pointedLocation;
						}
						else {
							sl1 = pointedLocation;
						}

						nPlayer.setSelectedLocation(nPlayer.getResizingCorner(), pointedLocation);
					}
					else {
						if(nPlayer.getSelectedLocation(1) != null) {
							RegionUtils.setCorner(player, nPlayer.getSelectedLocation(1), null);

							if(nPlayer.getSelectedLocation(0) != null) {
								RegionUtils.sendSquare(player, sl0, sl1, null, (byte) 0);
							}
						}

						nPlayer.setSelectedLocation(1, pointedLocation);
						sl1 = pointedLocation;
					}

					RegionUtils.setCorner(player, pointedLocation, Material.EMERALD_BLOCK);
				}

				if(sl0 != null && sl1 != null) {
					RegionValidity validSelect = plugin.getRegionManager().checkRegionSelect(sl0, sl1);
					byte data = (byte)15;

					//When resizing if overlaps player's region
					if(nPlayer.isResizing() && validSelect == RegionValidity.OVERLAPS) {
						List<NovaRegion> regionsOverlaped = plugin.getRegionManager().getRegionsInsideArea(sl0,sl1);
						if(regionsOverlaped.size()==1 && regionsOverlaped.get(0).equals(nPlayer.getGuild().getRegion())) {
							validSelect = RegionValidity.VALID;
						}
					}

					switch(validSelect) {
						case VALID:  //valid
							if(nPlayer.hasGuild()) {
								int regionsize = plugin.getRegionManager().checkRegionSize(sl0, sl1);
								double price;
								double ppb = plugin.getGroupManager().getGroup(player).getRegionPricePerBlock();

								if(nPlayer.isResizing()) {
									data = (byte) 6;
									price = ppb * (regionsize - nPlayer.getGuild().getRegion().getSurface());
								}
								else {
									data = (byte) 14;
									price = ppb * regionsize + plugin.getGroupManager().getGroup(player).getRegionCreateMoney();
								}

								vars.put("SIZE", String.valueOf(regionsize));
								vars.put("PRICE", String.valueOf(price));

								Message.CHAT_REGION_SIZE.vars(vars).send(player);

								if(price > 0) {
									Message.CHAT_REGION_PRICE.vars(vars).send(player);

									double guildBalance = nPlayer.getGuild().getMoney();
									if(guildBalance < price) {
										vars.put("NEEDMORE", String.valueOf(price - guildBalance));
										Message.CHAT_REGION_CNOTAFFORD.vars(vars).send(player);
										return;
									}
								}

								Message.CHAT_REGION_VALIDATION_VALID.send(player);
							}
							else {
								Message.CHAT_REGION_MUSTVEGUILD.send(player);
							}
							break;
						case TOOSMALL:
							vars.put("MINSIZE", Config.REGION_MINSIZE.getString());
							Message.CHAT_REGION_VALIDATION_TOOSMALL.vars(vars).send(player);
							break;
						case TOOBIG:
							vars.put("MAXSIZE", Config.REGION_MAXSIZE.getString());
							Message.CHAT_REGION_VALIDATION_TOOBIG.vars(vars).send(player);
							break;
						case OVERLAPS:
							Message.CHAT_REGION_VALIDATION_OVERLAPS.send(player);
							break;
						case TOOCLOSE:
							Message.CHAT_REGION_VALIDATION_TOOCLOSE.send(player);
							break;
					}

					//corners and rectangles
					RegionUtils.sendSquare(player, sl0, sl1, Material.WOOL, data);
					RegionUtils.setCorner(player, sl0, Material.EMERALD_BLOCK);
					RegionUtils.setCorner(player, sl1, Material.EMERALD_BLOCK);
				}
			}
		}
	}
}
