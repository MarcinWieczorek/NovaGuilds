/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.util.guiinventory.guild.settings;

import co.marcin.novaguilds.api.util.SignGUI;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.impl.util.signgui.SignGUIPatternImpl;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryGuildSettings extends AbstractGUIInventory {
	private ItemStack setNameItem;
	private ItemStack setTagItem;
	private ItemStack setHomeItem;
	private ItemStack togglePvpItem;
	private ItemStack openInvitationItem;
	private ItemStack buyLifeItem;
	private ItemStack buySlotItem;
	private ItemStack inviteItem;

	public GUIInventoryGuildSettings() {
		super(9, Message.INVENTORY_GUI_SETTINGS_TITLE);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		ItemStack clicked = event.getCurrentItem();
		if(clicked.equals(setNameItem)) {
			final SignGUIPatternImpl pattern = new SignGUIPatternImpl(Message.SIGNGUI_GUILD_SETTINGS_SET_NAME.setVar(VarKey.INPUT, getViewer().getGuild().getName()));
			plugin.getSignGUI().open(getViewer().getPlayer(), pattern, new SignGUI.SignGUIListener() {
				@Override
				public void onSignDone(Player player, String[] lines) {
					player.performCommand("g setname " + lines[pattern.getInputLine()]);
					reopen();
				}
			});
		}
		else if(clicked.equals(setTagItem)) {
			final SignGUIPatternImpl pattern = new SignGUIPatternImpl(Message.SIGNGUI_GUILD_SETTINGS_SET_TAG.setVar(VarKey.INPUT, getViewer().getGuild().getTag()));
			plugin.getSignGUI().open(getViewer().getPlayer(), pattern, new SignGUI.SignGUIListener() {
				@Override
				public void onSignDone(Player player, String[] lines) {
					player.performCommand("g settag " + lines[pattern.getInputLine()]);
					reopen();
				}
			});
		}
		else if(clicked.equals(setHomeItem)) {
			getViewer().getPlayer().performCommand("g home set");
		}
		else if(clicked.equals(togglePvpItem)) {
			getViewer().getPlayer().performCommand("g pvp");
			generateContent();
		}
		else if(clicked.equals(openInvitationItem)) {
			getViewer().getPlayer().performCommand("g openinv");
			generateContent();
		}
		else if(clicked.equals(buyLifeItem)) {
			getViewer().getPlayer().performCommand("g buylife");
			generateContent();
		}
		else if(clicked.equals(buySlotItem)) {
			getViewer().getPlayer().performCommand("g buyslot");
			generateContent();
		}
		else if(clicked.equals(inviteItem)) {
			new GUIInventoryGuildInvite().open(getViewer());
		}
	}

	@Override
	public void generateContent() {
		inventory.clear();
		setNameItem = Message.INVENTORY_GUI_SETTINGS_ITEM_SET_NAME.getItemStack();
		setTagItem = Message.INVENTORY_GUI_SETTINGS_ITEM_SET_TAG.getItemStack();
		setHomeItem = Message.INVENTORY_GUI_SETTINGS_ITEM_SET_HOME.getItemStack();
		togglePvpItem = (getViewer().getGuild().getFriendlyPvp() ? Message.INVENTORY_GUI_SETTINGS_ITEM_TOGGLEPVP_ON : Message.INVENTORY_GUI_SETTINGS_ITEM_TOGGLEPVP_OFF).getItemStack();
		openInvitationItem = Message.INVENTORY_GUI_SETTINGS_ITEM_OPENINVITATION.setVar(VarKey.FLAG, Message.getOnOff(getViewer().getGuild().isOpenInvitation())).getItemStack();
		buyLifeItem = Message.INVENTORY_GUI_SETTINGS_ITEM_BUYLIFE.getItemStack();
		buySlotItem = Message.INVENTORY_GUI_SETTINGS_ITEM_BUYSLOT.getItemStack();
		inviteItem = Message.INVENTORY_GUI_SETTINGS_ITEM_INVITE.getItemStack();

		if(getViewer().hasPermission(GuildPermission.SET_NAME)) {
			add(setNameItem);
		}

		if(getViewer().hasPermission(GuildPermission.SET_TAG)) {
			add(setTagItem);
		}

		if(getViewer().hasPermission(GuildPermission.HOME_SET)) {
			add(setHomeItem);
		}

		if(getViewer().hasPermission(GuildPermission.PVPTOGGLE)) {
			add(togglePvpItem);
		}

		if(getViewer().hasPermission(GuildPermission.OPENINVITATION)) {
			add(openInvitationItem);
		}

		if(getViewer().hasPermission(GuildPermission.BUYLIFE)) {
			add(buyLifeItem);
		}

		if(getViewer().hasPermission(GuildPermission.BUYSLOT)) {
			add(buySlotItem);
		}

		if(getViewer().hasPermission(GuildPermission.INVITE)) {
			add(inviteItem);
		}

		ChestGUIUtils.addBackItem(this);
	}
}
