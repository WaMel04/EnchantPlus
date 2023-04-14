package com.wamel.enchantplus.enchantment.list.common;

import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusEquipType;
import com.wamel.enchantplus.enchantment.EnchPlusRank;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.potion.CraftPotionUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Color;

public class Ench_Wither extends EnchPlusBase {

    public Ench_Wither(String name, Integer maxLevel, EnchPlusRank rank, EnchPlusEquipType equipType, String... descriptions) {
        super("부패",
                3,
                EnchPlusRank.COMMON,
                EnchPlusEquipType.WEAPON,
                "§7적을 공격시 §b3%§7의 확률로 적에게 §8위더 I §7효과를 §e5초 §7동안 부여합니다.",
                "§7적을 공격시 §b5%§7의 확률로 적에게 §8위더 I §7효과를 §e5초 §7동안 부여합니다.",
                "§7적을 공격시 §b5%§7의 확률로 적에게 §8위더 II §7효과를 §e5초 §7동안 부여합니다.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (!(event.getDamager() instanceof Player))
            return;
        if (!(event.getEntity() instanceof LivingEntity))
            return;

        Player attacker = (Player) event.getDamager();
        LivingEntity victim = (LivingEntity) event.getEntity();

        if (attacker.getInventory().getItemInMainHand() == null)
            return;

        int level = getEnchLevel(name, attacker.getInventory().getItemInMainHand());
        int chance = 0;
        int amplifier = 0;
        int tick = 0;

        switch (level) {
            case -1:
                return;
            case 1:
                chance = 3;
                amplifier = 0;
                tick = 20*5;
            case 2:
                chance = 5;
                amplifier = 0;
                tick = 20*5;
            case 3:
                chance = 500;
                amplifier = 1;
                tick = 20*5;
        }

        PotionEffectType effect = PotionEffectType.WITHER;

        if (isSucceed(chance)) {
            victim.removePotionEffect(effect);
            victim.addPotionEffect(new PotionEffect(effect, tick, amplifier));
            attacker.getWorld().playSound(victim.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 0.5f);

            if (!isEnabledOptimization()) {
                Location loc = victim.getLocation();

                new BukkitRunnable() {

                    int count = 0;
                    @Override
                    public void run() {
                        if (count >= 5)
                            super.cancel();

                        loc.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, loc, 30, 0.8f, 1.3f, 0.8f, 0);
                        loc.getWorld().spawnParticle(Particle.SUSPENDED_DEPTH, loc, 100, 0.8f, 1.3f, 0.8f, 0);

                        count++;
                    }
                }.runTaskTimer(plugin, 0, 10);
            }
        }
    }

}
