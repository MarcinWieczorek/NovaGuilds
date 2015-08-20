package co.marcin.novaguilds;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class YamlParseTest {
	public static final File resourcesDirectory = new File("./src/main/resources/");

	@Test
	public void testConfig() throws FileNotFoundException, InvalidConfigurationException {
		File configFile = new File(resourcesDirectory, "config.yml");

		if(!configFile.exists()) {
			throw new FileNotFoundException("Config file does not exist.");
		}

		try {
			YamlConfiguration.loadConfiguration(configFile);
		}
		catch(NullPointerException e) {
			throw new InvalidConfigurationException("Invalid YAML file ("+configFile.getPath()+")");
		}
	}

	@Test
	public void testLangs() throws NullPointerException, InvalidConfigurationException, FileNotFoundException {
		File langsDir = new File(resourcesDirectory, "/lang");

		if(langsDir.isDirectory()) {
			File[] list = langsDir.listFiles();

			if(list != null) {
				for(File lang : list) {
					try {
						YamlConfiguration.loadConfiguration(lang);
					}
					catch(NullPointerException e) {
						throw new InvalidConfigurationException("Invalid YAML file ("+lang.getPath()+")");
					}
				}
			}
		}
		else {
			throw new FileNotFoundException("Lang dir does not exist.");
		}
	}
}
