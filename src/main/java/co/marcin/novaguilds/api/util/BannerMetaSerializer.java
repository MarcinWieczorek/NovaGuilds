package co.marcin.novaguilds.api.util;

import org.bukkit.inventory.meta.BannerMeta;

public interface BannerMetaSerializer {
	/**
	 * Serializes banner meta into a string
	 *
	 * @param bannerMeta banner meta
	 * @return serialized meta
	 */
	String serialize(BannerMeta bannerMeta);

	/**
	 * Deserializes meta from a string
	 *
	 * @param string serialized meta
	 * @return banner meta
	 */
	BannerMeta deserialize(String string);
}
