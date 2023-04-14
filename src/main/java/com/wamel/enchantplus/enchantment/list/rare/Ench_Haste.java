package com.wamel.enchantplus.enchantment.list.rare;

import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusEquipType;
import com.wamel.enchantplus.enchantment.EnchPlusRank;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Ench_Haste extends EnchPlusBase {

    public Ench_Haste(String name, Integer maxLevel, EnchPlusRank rank, EnchPlusEquipType equipType, String... descriptions) {
        super("성급함",
                2,
                EnchPlusRank.RARE,
                EnchPlusEquipType.PICKAXE,
                "§7손에 들고 있을 시 §e성급함 I §7효과를 부여받습니다.",
                "§7손에 들고 있을 시 §e성급함 II §7효과를 부여받습니다.");

        registerEffectScheduler(this, 1,
                new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 7, 0));
        registerEffectScheduler(this, 2,
                new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 7, 1));
    }

}
