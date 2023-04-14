package com.wamel.enchantplus.gui;

import com.wamel.enchantplus.EnchantPlus;
import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusRank;
import com.wamel.enchantplus.enchantment.EnchPlusRegister;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import com.wamel.enchantplus.util.Pair;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EnchSelectGui implements Listener {

    private EnchantPlus plugin = EnchantPlus.getInstance();

    private static final String INVENTORY_NAME = "§f[§b인챈트§c+§f] 선택";
    private static final String PREFIX = "§f[§b인챈트§c+§f] ";
    private static final Integer LEVEL = 30;
    // 이름 | 가중치
    // 가중치가 높을 수록 좋은 인챈트가 뜰 확률이 올라감.
    private static HashMap<String, Integer> randomSelectedNameMap = new HashMap<String, Integer>() {{
        put("§b신비한", 1);
        put("§e반짝이는", 1);
        put("§5마법의", 1);
        put("§8저주받은", -1);
        put("§c이글거리는", 1);
        put("§a대자연의", 1);
        put("§7날카로운", -1);
        put("§5몽환의", 2);
        put("§4강한", 1);
        put("§6기쁨의", 1);
        put("§5절망의", -2);
        put("§a행운의", 2);
        put("§9청순한", 1);
        put("§f?????", 4);
        put("§d좋은 인챈트가 나올 것만 같은", 5);
        put("§2역겨운", -1);
        put("§4파멸의", -3);
        put("§d사랑스러운", 1);
        put("§e놀라운", 2);
        put("§3이국적인", 1);
        put("§1바다의", 1);
        put("§2심각한", -2);
        put("§4실패한", -4);
    }};

    private Inventory inventory;

    public EnchSelectGui() {
        this.inventory = Bukkit.createInventory(null, 27, INVENTORY_NAME);

        setSlot(11, new SimpleItem(Material.ENCHANTED_BOOK, 1, null, getRandomSelectedName(), null));
        setSlot(13, new SimpleItem(Material.ENCHANTED_BOOK, 1, null, getRandomSelectedName(), null));
        setSlot(15, new SimpleItem(Material.ENCHANTED_BOOK, 1, null, getRandomSelectedName(), null));
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
        if (!event.getInventory().getName().equalsIgnoreCase(INVENTORY_NAME))
            return;

        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 11:
            case 13:
            case 15:
                break;
            default:
                return;
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getLevel() < LEVEL) {
            player.sendMessage(PREFIX + "레벨이 부족합니다.");
            return;
        }
        if (EnchantmentPlusUtil.getEmptySlotsInStorage(player.getInventory()) <= 0) {
            player.sendMessage(PREFIX + "인벤토리에 자리가 부족합니다.");
            return;
        }

        ItemStack item = event.getCurrentItem();
        String[] coloredEnchNames = item.getItemMeta().getDisplayName().split("§l §r");

        int bonus = 0;
        for (String enchName : coloredEnchNames) {
            String uncoloredEnchName = ChatColor.stripColor(enchName);
            if (randomSelectedNameMap.containsKey(uncoloredEnchName))
                bonus += randomSelectedNameMap.get(uncoloredEnchName);
        }

        Random random = new Random();
        Integer pivot = random.nextInt(100) + bonus * 2;
        Integer accumulation = EnchPlusRank.values()[0].getChance();
        EnchPlusRank rank = null;

        for (int i=0; i<EnchPlusRank.values().length; i++) {
            if (pivot < accumulation || i == EnchPlusRank.values().length - 1) {
                rank = EnchPlusRank.values()[i];
                break;
            } else {
                accumulation += EnchPlusRank.values()[i+1].getChance();
            }
        }

        EnchPlusBase ench = getRandomEnch(rank);

        String romanLevel = getWeightedRandomEnchRomanLevel(ench.getMaxLevel());
        SimpleItem book = new SimpleItem(Material.ENCHANTED_BOOK, 1, null, ench.getRank().getRankColor() + ench.getName() + " " + romanLevel,
                "§b" + ench.getEquipType().getTranslatedString() + " 전용 §7| " + rank.getTranslatedString(),
                "§7서버에 배치된 §b인챈트§c+ §7모루로 아이템에 마법을 부여할 수 있습니다.",
                "§7자세한 설명은 '/인챈트도감 " + ench.getName() + "'을 확인하세요.");

        player.setLevel(player.getLevel() - LEVEL);
        player.getInventory().removeItem(new ItemStack(Material.BOOK, 1));
        player.getInventory().addItem(book.getItemStack());
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(), 100, 0.8f, 1.3f, 0.8f, 0);
        player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 100, 0.8f, 1.3f, 0.8f, 0);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1f);
        player.closeInventory();

        if (rank.getChance() <= EnchPlusRank.EPIC.getChance()) { // 뽑은 랭크가 에픽 이상일 경우
            Bukkit.broadcastMessage(PREFIX + "§a" + player.getName() + "§f님이 " + rank.getRankColor() + rank.getTranslatedString() + " 인챈트§f를 획득했습니다! §7("
            + ench.getName() + " " + romanLevel + ")");
        }
    }

    private Integer getEmptySlotsInStorage(Inventory inventory) {
        int emptySlot = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null)
                emptySlot++;
        }

        return emptySlot;
    }


    private String getRandomSelectedName() {
        Random random = new Random();
        ArrayList<String> list = new ArrayList<>(3);
        String[] keys = randomSelectedNameMap.keySet().toArray(new String[0]);

        for (int i=0; i<3; i++) {
            String selectedName = keys[random.nextInt(randomSelectedNameMap.size())];
            while (list.contains(selectedName)) {
                selectedName = keys[random.nextInt(randomSelectedNameMap.size())];
            }
            list.add(selectedName);
        }

        String result = list.get(0) + "§l §r" + list.get(1) + "§l §r" + list.get(2) + " §f인챈트";
        return result;
    }

    private EnchPlusBase getRandomEnch(EnchPlusRank rank) {
        ArrayList<EnchPlusBase> list = new ArrayList<>();

        for (EnchPlusBase base : EnchPlusRegister.registeredEnchMap.keySet()) {
            if (EnchPlusRegister.registeredEnchMap.get(base).equals(rank))
                list.add(base);
        }

        Random random = new Random();

        return list.get(random.nextInt(list.size()));
    }
    private String getWeightedRandomEnchRomanLevel(Integer maxLevel) {
        List<Pair<String, Integer>> candidates = new ArrayList<>();

        int totalWeight = 0;
        for (int i=1; i<=maxLevel; i++) {
            Pair<String, Integer> pair = new Pair<>(EnchantmentPlusUtil.convertArabicToRoman(i), maxLevel-i + 1);
            totalWeight += maxLevel-i + 1;
            candidates.add(pair);
        }

        double pivot = Math.random();

        double accumulation = 0;
        for (Pair<String, Integer> pair : candidates) {
            accumulation += (double) pair.right / (double) totalWeight;

            if (pivot <= accumulation)
                return pair.left;
        }

        return null;
    }


}
