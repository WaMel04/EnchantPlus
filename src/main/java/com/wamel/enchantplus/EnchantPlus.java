package com.wamel.enchantplus;

import com.wamel.enchantplus.command.Cmd_Ep;
import com.wamel.enchantplus.command.Cmd_Epbook;
import com.wamel.enchantplus.enchantment.EnchPlusEffectScheduler;
import com.wamel.enchantplus.enchantment.EnchPlusRegister;
import com.wamel.enchantplus.gui.EnchAnvilGui;
import com.wamel.enchantplus.gui.EnchBookGui;
import com.wamel.enchantplus.gui.EnchSelectGui;
import com.wamel.enchantplus.gui.EnchTableGui;
import com.wamel.enchantplus.listener.MinecraftEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnchantPlus extends JavaPlugin {

    private static EnchantPlus instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        EnchPlusRegister.start();
        EnchPlusEffectScheduler.start();

        registerGuis();
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
    }

    public static EnchantPlus getInstance() {
        return instance;
    }

    private void registerGuis() {
        getInstance().getServer().getPluginManager().registerEvents(new EnchTableGui(), getInstance());
        getInstance().getServer().getPluginManager().registerEvents(new EnchSelectGui(), getInstance());
        getInstance().getServer().getPluginManager().registerEvents(new EnchAnvilGui(), getInstance());
        getInstance().getServer().getPluginManager().registerEvents(new EnchBookGui(), getInstance());
    }

    private void registerEvents() {
        getInstance().getServer().getPluginManager().registerEvents(new MinecraftEvent(), getInstance());
    }

    private void registerCommands() {
        getCommand("ep").setExecutor(new Cmd_Ep());

        getCommand("epb").setExecutor(new Cmd_Epbook());
        getCommand("epbook").setExecutor(new Cmd_Epbook());
        getCommand("인챈트도감").setExecutor(new Cmd_Epbook());
    }
}
