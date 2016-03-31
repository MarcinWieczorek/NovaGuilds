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

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractGUIInventory implements GUIInventory {
	protected final Inventory inventory;
	private NovaPlayer viewer;
	protected final NovaGuilds plugin = NovaGuilds.getInstance();

	/**
	 * The constructor
	 *
	 * @param size  inventory size (multiply of 9)
	 * @param title title message
	 */
	public AbstractGUIInventory(int size, Message title) {
		inventory = ChestGUIUtils.createInventory(size, title);
	}

	@Override
	public final NovaPlayer getViewer() {
		return viewer;
	}

	@Override
	public final void setViewer(NovaPlayer nPlayer) {
		this.viewer = nPlayer;
	}

	@Override
	public final Inventory getInventory() {
		return inventory;
	}

	@Override
	public final void open(NovaPlayer nPlayer) {
		setViewer(nPlayer);
		ChestGUIUtils.openGUIInventory(nPlayer, this);
	}

	@Override
	public final void close() {
		getViewer().getPlayer().closeInventory();
	}

	/**
	 * Adds an item if not null
	 *
	 * @param itemStack the itemstack
	 */
	protected void add(ItemStack itemStack) {
		if(itemStack != null) {
			getInventory().addItem(itemStack);
		}
	}

	/**
	 * Reopens the GUI
	 */
	protected void reopen() {
		close();
		open(getViewer());
	}
}
