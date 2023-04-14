package com.wamel.enchantplus.enchantment;

import com.wamel.enchantplus.EnchantPlus;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class EnchPlusEffectScheduler {

    private static EnchantPlus plugin = EnchantPlus.getInstance();

    public static HashMap<EnchPlusBase, HashMap<Integer, PotionEffect>> effectMap = new HashMap<>();

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (EnchPlusBase ench : effectMap.keySet()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getInventory().getItemInMainHand() != null) {
                            Integer level = getEnchLevel(ench.getName(), player.getInventory().getItemInMainHand());
                            HashMap<Integer, PotionEffect> map = effectMap.get(ench);

                            if (map.containsKey(level)) {
                                PotionEffect effect = map.get(level);

                                player.removePotionEffect(effect.getType());
                                player.addPotionEffect(effect);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20*5);
    }

    private static Integer getEnchLevel(String enchName, ItemStack item) {
        if(item.getItemMeta() == null)
            return -1;

        ItemMeta meta = item.getItemMeta();

        if (meta.getLore() == null)
            return -1;

        for (String lore : meta.getLore()) {
            String uncoloredLore = ChatColor.stripColor(lore);

            if (!uncoloredLore.contains(" "))
                continue;

            String ench = uncoloredLore.substring(0, uncoloredLore.lastIndexOf(" "));
            String romanLevel = uncoloredLore.substring(uncoloredLore.lastIndexOf(" ") + 1);
            Integer level = EnchantmentPlusUtil.convertRomanToArabic(romanLevel);

            if (ench.equalsIgnoreCase(enchName))
                return level;
        }

        return -1;
    }
}
