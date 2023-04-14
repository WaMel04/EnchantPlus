package com.wamel.enchantplus.listener;

import com.wamel.enchantplus.gui.EnchAnvilGui;
import com.wamel.enchantplus.gui.EnchTableGui;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MinecraftEvent implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (player.isOp() && player.isSneaking())
            return;
        if (block.getType().equals(Material.ENCHANTMENT_TABLE)) {
            event.setCancelled(true);

            EnchTableGui table = new EnchTableGui();
            table.open(player);
            return;
        }
        if (block.getType().equals(Material.ANVIL)) {
            event.setCancelled(true);

            EnchAnvilGui anvil = new EnchAnvilGui();
            anvil.open(player);
        }
    }
}
