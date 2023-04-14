package com.wamel.enchantplus.util;

import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusEquipType;
import com.wamel.enchantplus.enchantment.EnchPlusRegister;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class EnchantmentPlusUtil {

    private static HashMap<String, Integer> romanMap = new LinkedHashMap<String, Integer>() {{
        put("M", 1000);
        put("D", 500);
        put("C", 100);
        put("L", 50);
        put("X", 10);
        put("V", 5);
        put("I", 1);
    }};

    public static Integer convertRomanToArabic(String romanNumber) {
        Integer arabic = 0;

        for (int i=0; i<romanNumber.length(); i++) {
            String romanString = String.valueOf(romanNumber.charAt(i));

            Integer value = romanMap.get(romanString);

            if (!romanMap.containsKey(romanString)) // 인식 불가 시 -1 반환
                return -1;
            if (i == romanNumber.length()-1) { // 마지막 글자일 시
                arabic += value;
            } else if (romanMap.get(romanString) < romanMap.get(String.valueOf(romanNumber.charAt(i+1)))) { // 앞의 문자가 뒤의 문자보다 더 작을 시
                arabic -= value;
            } else {
                arabic += value;
            }
        }

        return arabic;
    }

    public static String convertArabicToRoman(Integer arabicNumber) {
        Integer base = arabicNumber;
        String result = "";

        for (String roman : romanMap.keySet()) {
            Integer quotient = Integer.valueOf(base / romanMap.get(roman));

            if (quotient >= 1 && !roman.equalsIgnoreCase("I")) {
                result = result + roman;
                base -= romanMap.get(roman) * quotient;
            }
            if (quotient >= 1 && roman.equalsIgnoreCase("I")) {
                switch (quotient) {
                    case 1:
                        result = result + "I";
                        break;
                    case 2:
                        result = result + "II";
                        break;
                    case 3:
                        result = result + "III";
                        break;
                    case 4:
                        result = result + "IV";
                        break;
                }
            }
        }

        return result;
    }

    public static Integer getChanceEnchLevel(Integer level, Integer maxLevel) {
        int totalWeight = 0;
        for (int i=1; i<=maxLevel; i++) {
            totalWeight += maxLevel-i + 1;
        }

        return Math.round((level / totalWeight) * 100);
    }

    public static Integer getEmptySlotsInStorage(Inventory inventory) {
        int emptySlot = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null)
                emptySlot++;
        }

        return emptySlot;
    }

    public static void removeItemCorrectlyfromPlayer(Player player, ItemStack stack) {
        int i = 0;

        Inventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                if (item.clone().isSimilar(stack.clone())) {
                    if (item.getAmount() > stack.getAmount()) {
                        ItemStack newItem = item.clone();
                        newItem.setAmount(item.getAmount() - stack.getAmount());

                        player.getInventory().setItem(i, newItem);
                    } else {
                        player.getInventory().setItem(i, null);
                    }
                    break;
                }
            }
            i++;
        }
    }

    public static Boolean canEnchant(ItemStack item, EnchPlusEquipType type) {
        String itemType = item.getType().toString();
        String[] equipTypes = type.getTypes();

        for (String typeName : equipTypes) {
            String extractedTypeName = itemType.substring(itemType.lastIndexOf("_") + 1);

            if (extractedTypeName.equals(typeName))
                return true;
        }

        return false;
    }

    public static Boolean isEnchantable(ItemStack item) {
        for (EnchPlusEquipType type : EnchPlusEquipType.values()) {
            if (canEnchant(item, type))
                return true;
        }

        return false;
    }

    // 해당 인챈트 타입을 가지고 있는지를 반환
    public static Boolean hasDefaultEnchant(ItemStack item, String enchName) {
        HashMap<String, LinkedList<EnchPlusBase>> map = getEnchants(item);

        if (map.get("default").size() == 0)
            return false;

        for (EnchPlusBase base : map.get("default")) {
            if (base.getName().equals(enchName))
                return true;
        }

        return false;
    }

    // 해당 인챈트 타입을 가지고 있는지를 반환
    public static Boolean hasAdditionalEnchant(ItemStack item, String enchName) {
        HashMap<String, LinkedList<EnchPlusBase>> map = getEnchants(item);

        if (map.get("additional").size() == 0)
            return false;
        if (map.get("additional").size() == 1 && map.get("additional").get(0).getName().equals(enchName))
            return true;

        return false;
    }

    // 해당 인챈트 타입을 가지고 있는지를 반환
    public static Boolean hasEnchant(ItemStack item, String enchName) {
        HashMap<String, LinkedList<EnchPlusBase>> map = getEnchants(item);

        if (map.get("additional").size() == 0 && map.get("default").size() == 0)
            return false;
        if (map.get("additional").size() == 1 && map.get("additional").get(0).getName().equals(enchName))
            return true;

        for (EnchPlusBase base : map.get("default")) {
            if (base.getName().equals(enchName))
                return true;
        }
        for (EnchPlusBase base : map.get("additional")) {
            if (base.getName().equals(enchName))
                return true;
        }

        return false;
    }

    public static Boolean isEnchanted(ItemStack item, String enchName, Integer level) {
        if (item.getItemMeta() == null)
            return false;
        if (item.getItemMeta().getLore() == null)
            return false;

        String romanLevel = convertArabicToRoman(level);
        List<String> lores = item.getItemMeta().getLore();

        for (String lore : lores) {
            String uncoloredLore = ChatColor.stripColor(lore);

            String eName = uncoloredLore.substring(0, uncoloredLore.lastIndexOf(" "));
            String rLevel = uncoloredLore.substring(uncoloredLore.lastIndexOf(" ") + 1);

            EnchPlusBase ench = null;

            for (EnchPlusBase base : EnchPlusRegister.registeredEnchMap.keySet()) {
                if (base.getName().equals(enchName))
                    ench = base;
            }

            if (ench == null)
                continue;
            if (EnchantmentPlusUtil.convertRomanToArabic(romanLevel) == -1)
                continue;
            if (eName.equals(enchName) && rLevel.equals(romanLevel))
                return true;
        }

        return false;
    }

    // 해당 인챈트 타입이 적혀있는 로어의 위치(라인)을 찾음.
    // 없을 시 -1 반환.
    public static Integer findEnchant(ItemStack item, String enchName) {
        if (item.getItemMeta() == null)
            return -1;
        if (item.getItemMeta().getLore() == null)
            return -1;

        List<String> lores = item.getItemMeta().getLore();

        int i = 0;
        for (String lore : lores) {
            String stripedLore = ChatColor.stripColor(lore);

            String eName = stripedLore.substring(0, stripedLore.lastIndexOf(" "));
            String romanLevel = stripedLore.substring(stripedLore.lastIndexOf(" ") + 1);

            EnchPlusBase ench = null;

            for (EnchPlusBase base : EnchPlusRegister.registeredEnchMap.keySet()) {
                if (base.getName().equals(enchName))
                    ench = base;
            }

            i++;

            if (ench == null)
                continue;
            if (EnchantmentPlusUtil.convertRomanToArabic(romanLevel) == -1)
                continue;
            if (eName.equals(enchName))
                return (i-1);
        }

        return -1;
    }

    // 해당 아이템에 부여된 인챈트 목록을 HashMap<String, List<EnchPlusBase>>로 반환함.
    // key...
    // "default": 아이템이 기본적으로 가지고 있는 인챈트의 목록
    // "additional": 아이템에 추가적으로 부여된 인챈트의 목록 (목록이지만 1개의 값만 가짐)
    public static HashMap<String, LinkedList<EnchPlusBase>> getEnchants(ItemStack item) {
        HashMap<String, LinkedList<EnchPlusBase>> map = new HashMap<>();
        map.put("default", new LinkedList<>());
        map.put("additional", new LinkedList<>());

        if (item.getItemMeta() == null)
            return map;
        if (item.getItemMeta().getLore() == null)
            return map;

        List<String> lores = item.getItemMeta().getLore();

        for (String lore : lores) {
            String key = null;

            if (lore.startsWith("§b"))
                key = "default";
            if (lore.startsWith("§f"))
                key = "additional";

            String stripedLore = ChatColor.stripColor(lore);

            if (!stripedLore.contains(" "))
                continue;

            String enchName = stripedLore.substring(0, stripedLore.lastIndexOf(" "));
            String romanLevel = stripedLore.substring(stripedLore.lastIndexOf(" ") + 1);

            EnchPlusBase ench = null;

            for (EnchPlusBase base : EnchPlusRegister.registeredEnchMap.keySet()) {
                if (base.getName().equals(enchName))
                    ench = base;
            }

            if (ench == null)
                continue;
            if (EnchantmentPlusUtil.convertRomanToArabic(romanLevel) == -1)
                continue;

            LinkedList list = map.get(key);
            list.add(ench);

            map.put(key, list);
        }

        return map;
    }

    // 아이템 자체적으로 가진 인챈트 ('§b'로 시작)을 추가함.
    public static ItemStack addDefaultEnchant(ItemStack item, EnchPlusBase ench, Integer level) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return null;

        if (meta.getLore() == null) {
            List<String> lores = new LinkedList<>();
            lores.add("§b" + ench.getName() + " " + convertArabicToRoman(level));

            meta.setLore(lores);
            item.setItemMeta(meta);

            return item;
        } else {
            List<String> lores = meta.getLore();

            if (hasEnchant(item, ench.getName())) {
                Integer line = findEnchant(item, ench.getName());
                List<String> newLores = new LinkedList<>();

                int i = 0;
                for (String lore : lores) {
                    if (i == line) {
                        newLores.add("§b" + ench.getName() + " " + convertArabicToRoman(level));
                    } else {
                        newLores.add(lore);
                    }
                    i++;
                }

                meta.setLore(newLores);
                item.setItemMeta(meta);

                return item;
            } else {
                List<String> newLores = new LinkedList<>();

                int i = 0;
                boolean success = false;
                for (String lore : lores) {
                    if (i != lores.size() - 1) {
                        if (lore.startsWith("§b") && !lores.get(i + 1).startsWith("§b")) { // default 인챈트 중 마지막일 경우
                            newLores.add(lore);
                            newLores.add("§b" + ench.getName() + " " + convertArabicToRoman(level));
                            success = true;
                        }
                    } else {
                        newLores.add(lore);
                    }
                    i++;
                }
                if (success == false) {
                    newLores = new LinkedList<>();

                    int j = 0;
                    for (String lore : lores) {
                        if (j == 0) { // 가장 첫번째 라인에 인챈트 추가
                            newLores.add("§b" + ench.getName() + " " + convertArabicToRoman(level));
                            newLores.add(lore);
                        } else {
                            newLores.add(lore);
                        }
                        j++;
                    }
                }

                meta.setLore(newLores);
                item.setItemMeta(meta);
                return item;
            }
        }
    }

    // 아이템에 추가적인 인챈트 ('§f'로 시작)을 추가함.
    // 추가적 인챈트는 아이템 당 한 개만 보유할 수 있음.
    public static ItemStack addAdditionalEnchant(ItemStack item, EnchPlusBase ench, Integer level) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return null;

        if (meta.getLore() == null) {
            List<String> lores = new LinkedList<>();
            lores.add("§f" + ench.getName() + " " + convertArabicToRoman(level));

            meta.setLore(lores);
            item.setItemMeta(meta);

            return item;
        } else {
            List<String> lores = meta.getLore();

            if (hasEnchant(item, ench.getName())) {
                Integer line = findEnchant(item, ench.getName());;

                List<String> newLores = new LinkedList<>();

                int i = 0;
                for (String lore : lores) {
                    if (i == line) {
                        newLores.add("§f" + ench.getName() + " " + convertArabicToRoman(level));
                    } else {
                        newLores.add(lore);
                    }
                    i++;
                }

                meta.setLore(newLores);
                item.setItemMeta(meta);

                return item;
            } else {
                List<String> newLores = new LinkedList<>();

                int i = 0;
                boolean replace = false;

                for (String lore : lores) {
                    if (lore.startsWith("§f")) {
                        String uncoloredLore = ChatColor.stripColor(lore);

                        if (!uncoloredLore.contains(" "))
                            continue;

                        String eName = uncoloredLore.substring(0, uncoloredLore.lastIndexOf(" "));
                        String rLevel = uncoloredLore.substring(uncoloredLore.lastIndexOf(" ") + 1);

                        if (EnchantmentPlusUtil.convertRomanToArabic(rLevel) == -1)
                            continue;

                        EnchPlusBase base = null;

                        for (EnchPlusBase base2 : EnchPlusRegister.registeredEnchMap.keySet()) {
                            if (base2.getName().equals(eName))
                                base = base2;
                        }

                        if (base == null)
                            continue;

                        newLores.add("§f" + ench.getName() + " " + convertArabicToRoman(level));
                        replace = true;
                    } else {
                        newLores.add(lore);
                    }
                    i++;
                }
                if (replace == false) {
                    newLores = new LinkedList<>();

                    int j = 0;
                    for (String lore : lores) {
                        if (j == 0) { // 가장 첫번째 라인에 인챈트 추가
                            newLores.add("§f" + ench.getName() + " " + convertArabicToRoman(level));
                            newLores.add(lore);
                        } else {
                            newLores.add(lore);
                        }
                        j++;
                    }
                }

                meta.setLore(newLores);
                item.setItemMeta(meta);

                return item;
            }
        }
    }
}
