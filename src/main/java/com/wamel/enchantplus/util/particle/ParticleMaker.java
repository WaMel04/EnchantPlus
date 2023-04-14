package com.wamel.enchantplus.util.particle;

import com.wamel.enchantplus.EnchantPlus;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class ParticleMaker {

    private static EnchantPlus plugin = EnchantPlus.getInstance();
    private static Random random = new Random();

    public static void drawColoredCircleParticleHorizontally(Entity entity, float offX, float offY, float offZ, Integer radius, ParticleColorData data, int period, int time) {
        new BukkitRunnable() {
            int tick = 0;

            int circleTick = 0;
            int speed = (period <= 3) ? 10 * period : 360 / period;
            double x;
            double y = entity.getLocation().getY() + offY;
            double z;
            @Override
            public void run() {
                if (entity.isDead())
                    super.cancel();
                if (tick*period >= time)
                    super.cancel();

                double angle = circleTick * Math.PI / 180;

                if (angle >= 360) {
                    angle = 0;
                    circleTick = 0;
                }

                x = entity.getLocation().getX() + offX + radius * Math.cos(angle);
                z = entity.getLocation().getZ() + offZ + radius * Math.sin(angle);

                entity.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 0, data.getR(), data.getG(), data.getB(), 1);

                tick++;
                circleTick = circleTick + speed;
            }
        }.runTaskTimer(plugin, 0, period);
    }

    public static void drawColoredInjuredParticle(Entity entity, float offX, float offY, float offZ, ParticleColorData data, int count, double rX, double rY, double rZ, int period, int time) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (entity.isDead())
                    super.cancel();
                if (tick*period >= time)
                    super.cancel();

                for (int i=0; i<count; i++) {
                    Location loc = entity.getLocation().clone();

                    double addX = (Math.random() >= 0.5) ? Math.random() * rX + offX : -1 * Math.random() * rX + offX;
                    double addY = (Math.random() >= 0.5) ? Math.random() * rY + offY : -1 * Math.random() * rY + offY;
                    double addZ = (Math.random() >= 0.5) ? Math.random() * rZ + offZ : -1 * Math.random() * rZ + offZ;
                    entity.getWorld().spawnParticle(Particle.REDSTONE,
                            loc.getX() + addX, entity.getLocation().getY() + addY, entity.getLocation().getZ() + addZ,
                            0, data.getR(), data.getG(), data.getB(), 1);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0, period);
    }

    public static void drawColoredSpreadParticle(Entity entity, float offX, float offY, float offZ, ParticleColorData data, int count, double rX, double rY, double rZ) {
        for (int i=0; i<count; i++) {
            Location loc = entity.getLocation().clone();
            double addX = (Math.random() >= 0.5) ? Math.random() * rX + offX : -1 * Math.random() * rX + offX;
            double addY = (Math.random() >= 0.5) ? Math.random() * rY + offY : -1 * Math.random() * rY + offY;
            double addZ = (Math.random() >= 0.5) ? Math.random() * rZ + offZ : -1 * Math.random() * rZ + offZ;
            entity.getWorld().spawnParticle(Particle.REDSTONE,
                    loc.getX() + addX, entity.getLocation().getY() + addY, entity.getLocation().getZ() + addZ,
                    0, data.getR(), data.getG(), data.getB(), 1);
        }
    }
}
