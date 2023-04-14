package com.wamel.enchantplus.gui;

import com.wamel.enchantplus.EnchantPlus;
import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusRegister;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchBookGui implements Listener {

    private EnchantPlus plugin = EnchantPlus.getInstance();

    private static final String INVENTORY_NAME = "§f[§b인챈트§c+§f] §e도감";
    private static final String PREFIX = "§f[§b인챈트§c+§f] ";
    private static final Integer PAGE_LIMIT = 500;

    private Inventory inventory;

    public EnchBookGui() {

    }

    public EnchBookGui(String keyword, Integer page) {
        if (page == null)
            page = 1;

        if (keyword == null) {
            this.inventory = Bukkit.createInventory(null, 54, INVENTORY_NAME + "(" + page + ")");

            for (int i=46; i<53; i++) {
                setSlot(i, new EnchGui_Item(Material.IRON_FENCE, 1, null, "§f", null));
            }

            setSlot(45, new EnchGui_Item(Material.ARROW, 1, null, "§c<- 이전 페이지", null));
            setSlot(53, new EnchGui_Item(Material.ARROW, 1, null, "§a다음 페이지 ->", null));

            int slot = 0;
            for (int j=45 * (page - 1); j<45 * page; j++) {
                if (EnchPlusRegister.registeredEnchList.size() <= j)
                    break;

                EnchPlusBase ench = EnchPlusRegister.registeredEnchList.get(j);

                for (int k=0; k<ench.getMaxLevel(); k++) {
                    setSlot(slot, new EnchGui_Item(Material.ENCHANTED_BOOK, 1, null,
                            ench.getRank().getRankColor() + ench.getName() + " " + EnchantmentPlusUtil.convertArabicToRoman(k + 1),
                            ench.getDescription(k)));
                    slot++;
                }
            }
        } else {
            this.inventory = Bukkit.createInventory(null, 54, INVENTORY_NAME + " - " + keyword + " (" + page + ")");

            for (int i=46; i<53; i++) {
                setSlot(i, new EnchGui_Item(Material.IRON_FENCE, 1, null, "§f", null));
            }

            setSlot(45, new EnchGui_Item(Material.ARROW, 1, null, "§c<- 이전 페이지", null));
            setSlot(53, new EnchGui_Item(Material.ARROW, 1, null, "§a다음 페이지 ->", null));

            int slot = 0;
            for (int j=45 * (page - 1); j<45 * page; j++) {
                if (EnchPlusRegister.registeredEnchList.size() <= j)
                    break;

                EnchPlusBase ench = EnchPlusRegister.registeredEnchList.get(j);

                if (ench.getName().contains(keyword)) {
                    for (int k = 0; k < ench.getMaxLevel(); k++) {
                        setSlot(slot, new EnchGui_Item(Material.ENCHANTED_BOOK, 1, null,
                                ench.getRank().getRankColor() + ench.getName() + " " + EnchantmentPlusUtil.convertArabicToRoman(k + 1),
                                ench.getDescription(k)));
                        slot++;
                    }
                }
            }
        }
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
        if (!event.getInventory().getName().contains(INVENTORY_NAME))
            return;

        Player player = (Player) event.getWhoClicked();
        String uncoloredInvName = ChatColor.stripColor(event.getInventory().getName().replace(INVENTORY_NAME, ""));

        event.setCancelled(true);

        if (event.getRawSlot() != 45 && event.getRawSlot() != 53)
            return;

        if (uncoloredInvName.contains("-")) { // keyword // " - 키워드 (2)"
            uncoloredInvName = uncoloredInvName.replace(" - ", "").replace("(", "").replace(")", "");

            String keyword = uncoloredInvName.substring(0, uncoloredInvName.lastIndexOf(" "));
            Integer page = Integer.valueOf(uncoloredInvName.substring(uncoloredInvName.lastIndexOf(" ") + 1));

            if (event.getRawSlot() == 45) {
                if (page == 1) {
                    player.sendMessage(PREFIX + "이전 페이지가 존재하지 않습니다.");
                } else {
                    EnchBookGui gui = new EnchBookGui(keyword, page - 1);
                    gui.open(player);
                }
                return;
            }
            if (event.getRawSlot() == 53) {
                if (page == PAGE_LIMIT) {
                    player.sendMessage(PREFIX + "페이지의 끝입니다.");
                    return;
                }
                EnchBookGui gui = new EnchBookGui(keyword, page + 1);
                gui.open(player);
                return;
            }
        } else { // non-keyword
            uncoloredInvName = uncoloredInvName.replace("(", "").replace(")", "");

            Integer page = Integer.valueOf(uncoloredInvName);

            if (event.getRawSlot() == 45) {
                if (page == 1) {
                    player.sendMessage(PREFIX + "이전 페이지가 존재하지 않습니다.");
                } else {
                    EnchBookGui gui = new EnchBookGui(null, page - 1);
                    gui.open(player);
                }
                return;
            }
            if (event.getRawSlot() == 53) {
                if (page == PAGE_LIMIT) {
                    player.sendMessage(PREFIX + "페이지의 끝입니다.");
                    return;
                }
                EnchBookGui gui = new EnchBookGui(null, page + 1);
                gui.open(player);
                return;
            }
        }
    }

}
