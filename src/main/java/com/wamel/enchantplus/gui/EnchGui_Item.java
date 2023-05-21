package com.wamel.enchantplus.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class EnchGui_Item {

    private Material material;
    private Integer amount;
    private String name;
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();

    private ArrayList<String> loreList = new ArrayList<>();

    private ItemStack item;

    public EnchGui_Item(Material material, Integer amount, @Nullable HashMap<Enchantment, Integer> enchantments, @Nullable String name, @Nullable String... lores) {
        this.material = material;
        this.amount = amount;
        this.name = name;

        if (enchantments != null) {
            this.enchantments = enchantments;
        }
        if (lores != null) {
            for (String lore : lores) {
                this.loreList.add(lore);
            }
        }
    }

    public EnchGui_Item(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItemStack() {
        if (item != null)
            return item;

        ItemStack item = new ItemStack(material, amount);

        if (enchantments != null) {
            for (Enchantment enchantment : enchantments.keySet()) {
                item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment));
            }
        }

        ItemMeta meta = item.getItemMeta();

        if (name != null)
            meta.setDisplayName(name);

        if (loreList.size() != 0)
            meta.setLore(loreList);

        item.setItemMeta(meta);

        return item;
    }

}
