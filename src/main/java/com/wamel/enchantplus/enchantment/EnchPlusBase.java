package com.wamel.enchantplus.enchantment;

import com.wamel.enchantplus.EnchantPlus;
import com.wamel.enchantplus.data.DataStorage_Config;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Random;

public class EnchPlusBase implements Listener {

    protected EnchantPlus plugin = EnchantPlus.getInstance();
    protected Random random = new Random();
    protected static final String PREFIX = "§f[§b인챈트§c+§f] §f";

    // 인챈트의 이름입니다.
    protected String name;
    // 인챈트의 최대 레벨입니다. (1~maxLevel)
    protected Integer maxLevel;
    // 인챈트의 랭크입니다.
    protected EnchPlusRank rank;
    // 인챈트를 부여할 수 있는 아이템 타입입니다.
    protected EnchPlusEquipType equipType;
    // 인챈트의 레벨 별 설명입니다.
    protected String[] descriptions;

    public EnchPlusBase(String name, Integer maxLevel, EnchPlusRank rank, EnchPlusEquipType equipType, String... descriptions) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.rank = rank;
        this.equipType = equipType;
        this.descriptions = descriptions;
    }

    public String getName() {
        return name;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public EnchPlusRank getRank() {
        return rank;
    }

    public EnchPlusEquipType getEquipType() {
        return equipType;
    }

    public String getDescription(Integer index) {
        return descriptions[index];
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    // -1: 해당 인챈트가 존재하지 않음
    protected Integer getEnchLevel(String enchName, ItemStack item) {
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

    protected Boolean isSucceed(Integer chance) {
        if (random.nextInt(100) < chance) {
            return true;
        } else {
            return false;
        }
    }

    protected Boolean isEnabledOptimization() {
        return DataStorage_Config.OPTIMIZATION_MODE;
    }

    protected void registerEffectScheduler(EnchPlusBase ench, Integer level, PotionEffect effect) {
        if (!EnchPlusEffectScheduler.effectMap.containsKey(ench)) {
            HashMap<Integer, PotionEffect> map = new HashMap<>();

            map.put(level, effect);

            EnchPlusEffectScheduler.effectMap.put(ench, map);
        } else {
            HashMap<Integer, PotionEffect> map = EnchPlusEffectScheduler.effectMap.get(ench);

            map.put(level, effect);

            EnchPlusEffectScheduler.effectMap.put(ench, map);
        }
    }
}
