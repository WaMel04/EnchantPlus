package com.wamel.enchantplus.gui;

import com.wamel.enchantplus.EnchantPlus;
import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusRegister;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import com.wamel.enchantplus.util.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class EnchBookGui implements Listener {

    private EnchantPlus plugin = EnchantPlus.getInstance();

    private static final String INVENTORY_NAME = "§f[§b인챈트§c+§f] §e도감";
    private static final String PREFIX = "§f[§b인챈트§c+§f] ";
    private static final Integer PAGE_LIMIT = 500;

    private static ItemStack leftArrow = getSkull("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9", "§c<- 이전 페이지", null);
    private static ItemStack rightArrow = getSkull("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf", "§a다음 페이지 ->", null);

    private Inventory inventory;

    public EnchBookGui() {

    }

    public EnchBookGui(String keyword, Integer page) {
        if (page == null)
            page = 1;

        if (keyword == null) {
            this.inventory = Bukkit.createInventory(null, 54, INVENTORY_NAME + "(" + page + ")");

            ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1);
            glassPane.setDurability((short) 0);
            ItemMeta glassMeta = glassPane.getItemMeta();
            glassMeta.setDisplayName("§f");
            glassPane.setItemMeta(glassMeta);


            for (int i=46; i<53; i++) {
                inventory.setItem(i, glassPane);
            }

            inventory.setItem(45, leftArrow);
            inventory.setItem(53, rightArrow);

            int slot = 0;
            for (int j=45 * (page - 1); j<45 * page; j++) {
                if (EnchPlusRegister.registeredEnchList.size() <= j)
                    break;

                EnchPlusBase ench = EnchPlusRegister.registeredEnchList.get(j);
                LinkedList<String> descriptions = new LinkedList<>();
                descriptions.add("");

                for (int k=0; k<ench.getMaxLevel(); k++) {
                    descriptions.add("§e§l" + EnchantmentPlusUtil.convertArabicToRoman(k + 1) + " | " + ench.getDescription(k));
                    descriptions.add("");
                }

                setSlot(slot, new SimpleItem(Material.ENCHANTED_BOOK, 1, null,
                            ench.getRank().getRankColor() + ench.getName(),
                            descriptions.toArray(new String[descriptions.size()])));
                slot++;
            }
        } else {
            this.inventory = Bukkit.createInventory(null, 54, INVENTORY_NAME + " - " + keyword + " (" + page + ")");

            ItemStack glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1);
            glassPane.setDurability((short) 0);
            ItemMeta glassMeta = glassPane.getItemMeta();
            glassMeta.setDisplayName("§f");
            glassPane.setItemMeta(glassMeta);


            for (int i=46; i<53; i++) {
                inventory.setItem(i, glassPane);
            }

            inventory.setItem(45, leftArrow);
            inventory.setItem(53, rightArrow);

            int slot = 0;
            for (int j=45 * (page - 1); j<45 * page; j++) {
                if (EnchPlusRegister.registeredEnchList.size() <= j)
                    break;

                EnchPlusBase ench = EnchPlusRegister.registeredEnchList.get(j);

                if (ench.getName().contains(keyword)) {
                    LinkedList<String> descriptions = new LinkedList<>();
                    descriptions.add("");

                    for (int k=0; k<ench.getMaxLevel(); k++) {
                        descriptions.add("§e§l" + EnchantmentPlusUtil.convertArabicToRoman(k + 1) + " | " + ench.getDescription(k));
                        descriptions.add("");
                    }

                    setSlot(slot, new SimpleItem(Material.ENCHANTED_BOOK, 1, null,
                            ench.getRank().getRankColor() + ench.getName(),
                            descriptions.toArray(new String[descriptions.size()])));
                    slot++;
                }
            }
        }
    }

    public void setSlot(Integer slot, SimpleItem enchItem) {
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

    private static ItemStack getSkull(String url, String name, String... lores) {
        String s = "http://textures.minecraft.net/texture/" + url;

        ItemStack skull = SkullCreator.itemFromUrl(s);

        if (name != null) {
            ItemMeta meta = skull.getItemMeta();
            meta.setDisplayName(name);
            skull.setItemMeta(meta);
        }
        if (lores != null) {
            ItemMeta meta = skull.getItemMeta();
            meta.setLore(Arrays.stream(lores).collect(Collectors.toList()));
            skull.setItemMeta(meta);
        }
        return skull;
    }

}
