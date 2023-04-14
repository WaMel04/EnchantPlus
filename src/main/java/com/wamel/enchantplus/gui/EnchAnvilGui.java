package com.wamel.enchantplus.gui;

import com.wamel.enchantplus.EnchantPlus;
import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusRegister;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchAnvilGui implements Listener {

    private EnchantPlus plugin = EnchantPlus.getInstance();

    private static final String INVENTORY_NAME = "§f[§b인챈트§c+§f] 모루";
    private static final String PREFIX = "§f[§b인챈트§c+§f] ";

    private Inventory inventory;

    public EnchAnvilGui() {
        this.inventory = Bukkit.createInventory(null, 27, INVENTORY_NAME);

        for (int i=0; i<27; i++) {
            setSlot(i, new EnchGui_Item(Material.THIN_GLASS, 1, null, "§6모루에 커서를 올려 설명을 확인해주세요", null));
        }

        setSlot(1, new EnchGui_Item(Material.HOPPER, 1, null, "§c인챈트를 부여할 아이템을 넣어주세요"));
        setSlot(4, new EnchGui_Item(Material.ANVIL, 1, null, PREFIX + "§e도움말",
                "§7왼쪽에 인챈트를 부여할 아이템을 넣어주세요.",
                "§7오른쪽에는 인챈트 북을 넣어주세요.",
                "§7좌클릭시 아이템 슬롯, 우클릭시 인챈트 북 슬롯에 클릭한 아이템이 들어갑니다.",
                "§b인챈트§c+§7는 특정 장비에만 부여할 수 있습니다.",
                "§f아이템을 넣고 모루를 클릭시 §b인챈트§c+§f를 시작합니다."));
        setSlot(7, new EnchGui_Item(Material.HOPPER, 1, null, "§c인챈트 북을 넣어주세요"));
        setSlot(10, new EnchGui_Item(Material.AIR, 0, null, null, null));
        setSlot(16, new EnchGui_Item(Material.AIR, 0, null, null, null));
    }

    public void setSlot(Integer slot, EnchGui_Item enchItem) {
        ItemStack item = enchItem.getItemStack();

        inventory.setItem(slot, item);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getName().equalsIgnoreCase(INVENTORY_NAME))
            return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getRawSlot() > 26 && event.getCurrentItem() != null) {
            if (event.getClick().isLeftClick() && EnchantmentPlusUtil.isEnchantable(event.getCurrentItem())) {
                event.getInventory().setItem(10, event.getCurrentItem());
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1f);
                return;
            }
            if (event.getClick().isRightClick() && event.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)) {
                event.getInventory().setItem(16, event.getCurrentItem());
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1f);
                return;
            }
        }
        if (event.getRawSlot() == 4) {
            if (event.getInventory().getItem(10) == null)
                return;
            if (event.getInventory().getItem(16) == null)
                return;
            if (!event.getInventory().getItem(16).getType().equals(Material.ENCHANTED_BOOK)) {
                player.sendMessage(PREFIX + "인챈트 북을 넣어주세요.");
                return;
            }

            ItemStack enchBook = event.getInventory().getItem(16).clone();
            String enchBookName = ChatColor.stripColor(enchBook.getItemMeta().getDisplayName());

            String enchName = enchBookName.substring(0, enchBookName.lastIndexOf(" "));
            String romanLevel = enchBookName.substring(enchBookName.lastIndexOf(" ") + 1);

            if (EnchantmentPlusUtil.convertRomanToArabic(romanLevel) == -1) {
                player.sendMessage(PREFIX + "정상적인 인챈트 북을 넣어주세요.");
                return;
            }

            EnchPlusBase ench = null;

            for (EnchPlusBase base : EnchPlusRegister.registeredEnchMap.keySet()) {
                if (base.getName().equals(enchName))
                    ench = base;
            }

            if (ench == null) {
                player.sendMessage(PREFIX + "정상적인 인챈트 북을 넣어주세요.");
                return;
            }

            ItemStack tool = event.getInventory().getItem(10).clone();

            if (!EnchantmentPlusUtil.canEnchant(tool, ench.getEquipType())) {
                player.sendMessage(PREFIX + "§e" + enchName + " " + romanLevel + " 인챈트§f는 해당 아이템에 부여할 수 없습니다.");
                return;
            }
            if (EnchantmentPlusUtil.hasDefaultEnchant(tool, enchName)) {
                player.sendMessage(PREFIX + "이미 해당 아이템에 §e" + enchName + " " + "인챈트§f가 부여되어있습니다.");
                return;
            }

            ItemStack resultItem = EnchantmentPlusUtil.addAdditionalEnchant(tool.clone(), ench, EnchantmentPlusUtil.convertRomanToArabic(romanLevel));

            EnchantmentPlusUtil.removeItemCorrectlyfromPlayer(player, enchBook);
            EnchantmentPlusUtil.removeItemCorrectlyfromPlayer(player, tool);

            player.getInventory().addItem(resultItem);

            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 10, 0.8f, 1.3f, 0.8f, 0);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1f);
            player.closeInventory();
        }
    }

}
