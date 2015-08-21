package co.marcin.novaguilds;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.*;

public class YamlIncompleteTranslationTest {
	@Test
	public void testTranslations() throws Exception {
		File langsDir = new File(YamlParseTest.resourcesDirectory, "/lang");

		//Mother lang setup
		File motherLangFile = new File(langsDir, "en-en.yml");
		YamlConfiguration motherConfiguration = YamlConfiguration.loadConfiguration(motherLangFile);
		List<String> motherKeys = new ArrayList<>();
		for(String key : motherConfiguration.getKeys(true)) {
			if(!motherConfiguration.isConfigurationSection(key)) {
				motherKeys.add(key);
			}
		}

		//List all languages and configuration sections
		Map<String, YamlConfiguration> configurationMap = new HashMap<>();
		if(langsDir.isDirectory()) {
			File[] list = langsDir.listFiles();

			if(list != null) {
				for(File langFile : list) {
					if(!langFile.getName().equals("en-en.yml")) {
						configurationMap.put(langFile.getName().replace(".yml", ""), YamlConfiguration.loadConfiguration(langFile));
					}
				}
			}
		}

		//Get keys from all langs
		System.out.println("Testing lang files for missing keys...");
		for(Map.Entry<String, YamlConfiguration> entry : configurationMap.entrySet()) {
			int missingCount = 0;
			String name = entry.getKey();
			YamlConfiguration configuration = entry.getValue();

			System.out.println("---");
			System.out.println();
			System.out.println("Testing lang: "+name);

			for(String mKey : motherKeys) {
				if(!configuration.contains(mKey)) {
					if(missingCount==0) {
						System.out.println("Missing keys:");
					}

					System.out.println(" - "+mKey);
					missingCount++;
				}
			}

			if(missingCount == 0) {
				System.out.println("Result: No missing keys");
			}
			else {
				throw new Exception("Found "+missingCount+" missing keys in lang "+name);
			}
		}
	}
}
