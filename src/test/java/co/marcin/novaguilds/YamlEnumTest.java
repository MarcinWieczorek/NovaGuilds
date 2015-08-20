package co.marcin.novaguilds;

import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YamlEnumTest {
    private String[] ignoreConfig;

    public YamlEnumTest() {
        ignoreConfig = new String[]{
            "aliases.",
            "gguicmd",
            "groups"
        };
    }

    @Test
    public void testConfig() throws Exception {
        File configFile = new File(YamlParseTest.resourcesDirectory, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        List<String> configEnumNames = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for(Config v : Config.values()) {
            configEnumNames.add(v.name());
        }

        for(String key : config.getKeys(true)) {
            //System.out.println(key);
            boolean ig = config.isConfigurationSection(key);
            for(String ignore : ignoreConfig) {
                if(key.startsWith(ignore)) {
                    ig = true;
                    break;
                }
            }

            if(!ig) {
                String name = StringUtils.replace(key, ".", "_").toUpperCase();
                if(!configEnumNames.contains(name)) {
                    missing.add(name);
                }
            }
        }

        for(String name : missing) {
            System.out.println(name+",");
        }

        if(missing.size() > 0) {
            throw new Exception("Missing config enums!");
        }
    }
}
