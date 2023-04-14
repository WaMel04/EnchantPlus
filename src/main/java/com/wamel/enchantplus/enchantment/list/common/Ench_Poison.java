package com.wamel.enchantplus.enchantment.list.common;

import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusEquipType;
import com.wamel.enchantplus.enchantment.EnchPlusRank;
import com.wamel.enchantplus.util.particle.ParticleColorData;
import com.wamel.enchantplus.util.particle.ParticleMaker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Ench_Poison extends EnchPlusBase {

    public Ench_Poison(String name, Integer maxLevel, EnchPlusRank rank, EnchPlusEquipType equipType, String... descriptions) {
        super("맹독",
                3,
                EnchPlusRank.COMMON,
                EnchPlusEquipType.WEAPON,
                "§7적을 공격시 §b3%§7의 확률로 적에게 §2독 I §7효과를 §e5초 §7동안 부여합니다.",
                "§7적을 공격시 §b5%§7의 확률로 적에게 §2독 I §7효과를 §e5초 §7동안 부여합니다.",
                "§7적을 공격시 §b5%§7의 확률로 적에게 §2독 II §7효과를 §e5초 §7동안 부여합니다.");
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

        PotionEffectType effect = PotionEffectType.POISON;

        if (isSucceed(chance)) {
            victim.removePotionEffect(effect);
            victim.addPotionEffect(new PotionEffect(effect, tick, amplifier));
            attacker.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ILLUSION_ILLAGER_CAST_SPELL, 1, 0.5f);
            attacker.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 0.5f);

            if (!isEnabledOptimization()) {
                ParticleMaker.drawColoredInjuredParticle(victim, 0, 2.1f, 0, new ParticleColorData(051, 102, 051), 15, 0.5, 0.8, 0.5, 5, 20*5);
                ParticleMaker.drawColoredSpreadParticle(victim, 0, 1f, 0, new ParticleColorData(051, 102, 051), 100, 2, 2, 2);
            }
        }
    }

}
