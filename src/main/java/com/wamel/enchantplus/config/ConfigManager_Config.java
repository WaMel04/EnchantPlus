package com.wamel.enchantplus.config;

import com.wamel.enchantplus.EnchantPlus;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager_Config {

    private static EnchantPlus plugin = EnchantPlus.getInstance();

    public static Object get(String key) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        return yaml.get(key);
    }
}
