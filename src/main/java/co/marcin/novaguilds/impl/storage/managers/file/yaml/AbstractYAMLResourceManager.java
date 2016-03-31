package co.marcin.novaguilds.impl.storage.managers.file.yaml;

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.storage.managers.file.AbstractFileResourceManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class AbstractYAMLResourceManager<T extends Resource> extends AbstractFileResourceManager<T> {
	/**
	 * The constructor
	 *
	 * @param storage       the storage
	 * @param clazz         type class
	 * @param directoryPath the path
	 */
	protected AbstractYAMLResourceManager(Storage storage, Class clazz, String directoryPath) {
		super(storage, clazz, directoryPath);
	}

	/**
	 * Gets data from YAML
	 *
	 * @param t instance
	 * @return configuration
	 */
	protected FileConfiguration getData(T t) {
		return loadConfiguration(getFile(t));
	}

	/**
	 * Loads FileConfiguration from file
	 *
	 * @param file the file
	 * @return the configuration
	 */
	protected FileConfiguration loadConfiguration(File file) {
		return file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
	}
}
