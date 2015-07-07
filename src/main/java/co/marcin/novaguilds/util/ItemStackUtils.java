package co.marcin.novaguilds.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackUtils {
	@SuppressWarnings("deprecation")
	public static ItemStack stringToItemStack(String str) {
		if(!str.isEmpty()) {
			ItemStack itemStack = null;
			Material material;
			String name = "";
			int amount = 0;
			List<String> lore = new ArrayList<>();
			String loreString = "";
			String bookAuthor = null;
			String bookBook = null;
			String player = null;
			short durability = 0;
			PotionType potionType = null;
			int potionLevel = 0;
			byte data = (byte)0;
			Map<Enchantment,Integer> enchantments = new HashMap<>();

			String[] explode = str.split(" ");

			String materialString = explode[0];

			DyeColor color;

			if(explode[0].contains(":")) {
				String[] dataSplit = explode[0].split(":");
				materialString = dataSplit[0];
				String dataString = dataSplit[1];

				if(NumberUtils.isNumeric(dataString)) {
					String mName = materialString.toUpperCase();
					if(mName.contains("_CHESTPLATE") ||
							mName.contains("_HELMET") ||
							mName.contains("_LEGGINS") ||
							mName.contains("_BOOTS") ||
							mName.contains("_AXE") ||
							mName.contains("_HOE") ||
							mName.contains("_SWORD") ||
							mName.contains("_SPADE") ||
							mName.contains("_PICKAXE")) {

						durability = Short.parseShort(dataString);
					}
					else {
						data = Byte.parseByte(dataSplit[1]);
					}
				}
				else {
					color = DyeColor.valueOf(dataString.toUpperCase());
					if(color != null) {
						data = color.getData();
					}
				}
			}

			if(NumberUtils.isNumeric(materialString)) {
				material = Material.getMaterial(Integer.parseInt(materialString));
			}
			else {
				material = Material.getMaterial(materialString.toUpperCase());
			}

			if(material == null) {
				return null;
			}

			if(explode.length>1) { //amount
				if(NumberUtils.isNumeric(explode[1])) {
					amount = Integer.parseInt(explode[1]);
					explode[1] = null;
				}
			}
			else {
				amount = material.getMaxStackSize();
			}

			explode[0] = null;

			for(String detail : explode) {
				if(detail != null) {
					if(detail.contains(":")) {
						String[] detailSplit = detail.split(":");
						String value = detailSplit[1];
						Bukkit.getLogger().info(detailSplit[0] + " : " + value);
						switch(detailSplit[0].toLowerCase()) {
							case "name":
								name = value;
								break;
							case "lore":
								loreString = value;
								break;
							case "title":
								name = value;
								break;
							case "author":
								bookAuthor = value;
								break;
							case "book":
								bookBook = value;
								break;
							case "power":
								if(material == Material.BOW) {
									enchantments.put(Enchantment.ARROW_DAMAGE, Integer.valueOf(value));
								}
								else if(material == Material.POTION) {
									if(NumberUtils.isNumeric(value)) {
										potionLevel = Integer.parseInt(value);
									}
								}
								else if(material == Material.FIREWORK) {

								}
								break;
							case "effect":
								if(material == Material.POTION) {
									potionType = PotionType.valueOf(value.toUpperCase());
								}
								break;
							case "duration":
								break;
							case "color":
								color = DyeColor.valueOf(value.toUpperCase());
								break;
							case "player":
								player = value;
								break;
							case "fade":
								break;
							case "shape":
								break;

							case "alldamage":
								enchantments.put(Enchantment.DAMAGE_ALL, Integer.valueOf(value));
								break;
							case "ardmg":
								enchantments.put(Enchantment.ARROW_DAMAGE,Integer.valueOf(value));
								break;
							case "baneofarthropods":
								enchantments.put(Enchantment.DAMAGE_ARTHROPODS,Integer.valueOf(value));
								break;
							case "durability":
								enchantments.put(Enchantment.DURABILITY,Integer.valueOf(value));
								break;
							case "fire":
								enchantments.put(Enchantment.FIRE_ASPECT,Integer.valueOf(value));
								break;
							case "fireaspect":
								enchantments.put(Enchantment.FIRE_ASPECT,Integer.valueOf(value));
								break;
							case "knockback":
								enchantments.put(Enchantment.KNOCKBACK,Integer.valueOf(value));
								break;
							case "looting":
								enchantments.put(Enchantment.LOOT_BONUS_BLOCKS,Integer.valueOf(value));
								break;
							case "mobloot":
								enchantments.put(Enchantment.LOOT_BONUS_MOBS,Integer.valueOf(value));
								break;
							case "sharpness":
								enchantments.put(Enchantment.DAMAGE_ALL,Integer.valueOf(value));
								break;
							case "smite":
								enchantments.put(Enchantment.DAMAGE_UNDEAD,Integer.valueOf(value));
								break;
							case "unbreaking":
								enchantments.put(Enchantment.DURABILITY,Integer.valueOf(value));
								break;
							case "undeaddamage":
								enchantments.put(Enchantment.DAMAGE_UNDEAD,Integer.valueOf(value));
								break;

							case "arrowdamage":
								enchantments.put(Enchantment.ARROW_DAMAGE,Integer.valueOf(value));
								break;
							case "arrowknockback":
								enchantments.put(Enchantment.ARROW_KNOCKBACK,Integer.valueOf(value));
								break;
							case "flame":
								enchantments.put(Enchantment.ARROW_FIRE,Integer.valueOf(value));
								break;
							case "flamearrow":
								enchantments.put(Enchantment.ARROW_FIRE,Integer.valueOf(value));
								break;
							case "infarrows":
								enchantments.put(Enchantment.ARROW_INFINITE,Integer.valueOf(value));
								break;
							case "infinity":
								enchantments.put(Enchantment.ARROW_INFINITE,Integer.valueOf(value));
								break;
							case "punch":
								enchantments.put(Enchantment.ARROW_KNOCKBACK,Integer.valueOf(value));
								break;
						}
					}
				}
			}

			//replace _ with spaces
			name = name.replace("_"," ");
			loreString = loreString.replace("_"," ");

			itemStack = new ItemStack(material,amount,data);
			System.out.println(itemStack==null);
			itemStack.addUnsafeEnchantments(enchantments);
			ItemMeta itemMeta = itemStack.getItemMeta();

			if(!name.isEmpty()) {
				itemMeta.setDisplayName(name);
			}

			if(!loreString.isEmpty()) {
				lore.add(loreString);
				itemMeta.setLore(lore);
			}

			if(material == Material.POTION && potionLevel != 0 && potionType != null) {
				Potion potion = new Potion(potionType,potionLevel);
				potion.apply(itemStack);
			}

			itemStack.setDurability(durability);
			itemStack.setItemMeta(itemMeta);

			if(player != null) {
				SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
				skullMeta.setOwner(player);
				itemStack.setItemMeta(skullMeta);
			}
			return itemStack;
		}
		return null;
	}

	public static List<ItemStack> stringToItemStackList(List<String> list) {
		List<ItemStack> itemList = new ArrayList<>();
		for(String item : list) {
			ItemStack itemStack = stringToItemStack(item);
			if(itemStack != null) {
				itemList.add(itemStack);
			}
		}

		return itemList;
	}

	public static void takeItems(Player player, List<ItemStack> items) {
		if(player.getGameMode() != GameMode.CREATIVE) {
			for(ItemStack item : items) {
				player.getInventory().removeItem(item);
			}

			player.updateInventory();
		}
	}

	public static boolean hasAllRequiredItems(Player player, List<ItemStack> items) {
		return getMissingItems(player, items).isEmpty();
	}

	public static List<ItemStack> getMissingItems(Player player, List<ItemStack> items) {
		List<ItemStack> missing = new ArrayList<>();

		if(items != null && player.getGameMode() != GameMode.CREATIVE) {
			for(ItemStack item : items) {
				if(!player.getInventory().containsAtLeast(item, item.getAmount())) {
					ItemStack missingItemStack = item.clone();
					missingItemStack.setAmount(item.getAmount() - getTotalAmountOfItemStackInInventory(player, item));
					missing.add(missingItemStack);
				}
			}
		}

		return missing;
	}

	public static int getTotalAmountOfItemStackInInventory(Player player, ItemStack itemStack) {
		int amount = 0;

		for(ItemStack item : player.getInventory().getContents()) {
			if(item != null && item.getType() != Material.AIR) {
				if(item.isSimilar(itemStack)) {
					amount += item.getAmount();
				}
			}
		}

		return amount;
	}
}
