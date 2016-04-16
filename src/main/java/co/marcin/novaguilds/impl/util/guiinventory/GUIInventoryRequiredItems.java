package co.marcin.novaguilds.impl.util.guiinventory;

import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.util.ChestGUIUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIInventoryRequiredItems extends AbstractGUIInventory {
	private final List<ItemStack> requiredItems = new ArrayList<>();

	public GUIInventoryRequiredItems(List<ItemStack> itemStackList) {
		super(ChestGUIUtils.getChestSize(itemStackList.size()), Message.INVENTORY_REQUIREDITEMS_NAME);
		requiredItems.addAll(itemStackList);
	}

	@Override
	public void onClick(InventoryClickEvent event) {

	}

	@Override
	public void generateContent() {
		for(ItemStack item : requiredItems) {
			add(item);
		}
	}
}
