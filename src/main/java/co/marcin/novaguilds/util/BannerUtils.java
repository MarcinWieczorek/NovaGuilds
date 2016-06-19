package co.marcin.novaguilds.util;


import co.marcin.novaguilds.api.util.BannerMetaSerializer;
import co.marcin.novaguilds.impl.util.BannerMetaSerializerImpl;
import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class BannerUtils {
	private static final BannerMetaSerializer serializer = new BannerMetaSerializerImpl();

	private BannerUtils() {

	}

	/**
	 * Gets the serializer
	 *
	 * @return banner meta serializer
	 */
	public static BannerMetaSerializer getSerializer() {
		return serializer;
	}

	/**
	 * Gets random banner item
	 *
	 * @return banner item stack
	 */
	public static ItemStack randomBannerItemStack() {
		ItemStack itemStack = new ItemStack(Material.BANNER);

		BannerMeta meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);

		for(int i = NumberUtils.randInt(0, PatternType.values().length) + 2; i > 0; i--) {
			meta.addPattern(new Pattern(randomDyeColor(), randomPatternType()));
		}

		itemStack.setItemMeta(meta);

		return itemStack;
	}

	/**
	 * Gets random pattern type
	 *
	 * @return pattern type
	 */
	protected static PatternType randomPatternType() {
		return PatternType.values()[NumberUtils.randInt(0, PatternType.values().length - 1)];
	}

	/**
	 * Gets random dye color
	 *
	 * @return dye color
	 */
	protected static DyeColor randomDyeColor() {
		return DyeColor.values()[NumberUtils.randInt(0, DyeColor.values().length - 1)];
	}

	/**
	 * Deserializes a string to banner meta
	 *
	 * @param string serialized meta
	 * @return banner meta
	 */
	public static BannerMeta deserialize(String string) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8)) {
			return null;
		}

		return getSerializer().deserialize(string);
	}

	/**
	 * Serializes banner into a string
	 *
	 * @param banner banner meta
	 * @return serialized meta
	 */
	public static String serialize(Banner banner) {
		return getSerializer().serialize(getBannerMeta(banner));
	}

	/**
	 * Serializes banner meta into a string
	 *
	 * @param bannerMeta banner meta
	 * @return serialized meta
	 */
	public static String serialize(BannerMeta bannerMeta) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8)) {
			return "";
		}

		return getSerializer().serialize(bannerMeta);
	}

	/**
	 * Applies meta to a banner
	 *
	 * @param banner banner block
	 * @param meta   banner meta
	 * @return banner block
	 */
	public static Banner applyMeta(Banner banner, BannerMeta meta) {
		banner.setBaseColor(meta.getBaseColor());
		banner.setPatterns(meta.getPatterns());
		return banner;
	}

	/**
	 * Turns a banner into banner meta
	 *
	 * @param banner banner block
	 * @return banner meta
	 */
	public static BannerMeta getBannerMeta(Banner banner) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_8)) {
			return null;
		}

		BannerMeta meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);

		meta.setBaseColor(banner.getBaseColor());
		for(Pattern pattern : banner.getPatterns()) {
			meta.addPattern(pattern);
		}

		return meta;
	}
}
