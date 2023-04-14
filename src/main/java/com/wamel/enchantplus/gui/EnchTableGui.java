package com.wamel.enchantplus.gui;

import com.wamel.enchantplus.EnchantPlus;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchTableGui implements Listener {

    private EnchantPlus plugin = EnchantPlus.getInstance();

    private static final String INVENTORY_NAME = "§f[§b인챈트§c+§f] 테이블";
    private static final String PREFIX = "§f[§b인챈트§c+§f] ";
    private static final Integer LEVEL = 30;

    private Inventory inventory;

    public EnchTableGui() {
        this.inventory = Bukkit.createInventory(null, 27, INVENTORY_NAME);

        for (int i=0; i<27; i++) {
            setSlot(i, new EnchGui_Item(Material.THIN_GLASS, 1, null, "§6인챈트를 부여하고 싶은 책을 클릭하세요", null));
        }

        setSlot(4, new EnchGui_Item(Material.ENCHANTMENT_TABLE, 1, null, PREFIX + "§e도움말",
                "§7기존 마인크래프트에 없던 새로운 인챈트를 부여할 수 있습니다.",
                "§b인챈트§c+§7의 소모 레벨은 §a" + LEVEL + " 레벨§7입니다.",
                "§b인챈트§c+§7의 목록은 '/인챈트도감'으로 확인할 수 있습니다.",
                "§f인벤토리에서 책을 클릭해 책을 넣고 인챈트 테이블을 클릭시 §b인챈트§c+§f를 시작합니다."));
        setSlot(13, new EnchGui_Item(Material.AIR, 0, null, null, null));
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
            if (event.getCurrentItem().getType().equals(Material.BOOK)) {
                event.getInventory().setItem(13, event.getCurrentItem());
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1f);
                return;
            }
        }
        if (event.getRawSlot() == 4) {
            if (event.getInventory().getItem(13) == null)
                return;
            if (event.getInventory().getItem(13).getType().equals(Material.BOOK)) {
                if (player.getLevel() < LEVEL) {
                    player.sendMessage(PREFIX + "레벨이 부족합니다.");
                    return;
                }
                if (EnchantmentPlusUtil.getEmptySlotsInStorage(player.getInventory()) <= 0) {
                    player.sendMessage(PREFIX + "인벤토리에 자리가 부족합니다.");
                    return;
                }

                EnchSelectGui gui = new EnchSelectGui();
                gui.open(player);
            }
        }
    }

}
