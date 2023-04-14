package com.wamel.enchantplus.command;

import com.wamel.enchantplus.enchantment.EnchPlusBase;
import com.wamel.enchantplus.enchantment.EnchPlusRegister;
import com.wamel.enchantplus.gui.EnchTableGui;
import com.wamel.enchantplus.util.EnchantmentPlusUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cmd_Ep implements CommandExecutor {

    private static final String PREFIX = "§f[§b인챈트§c+§f] §f";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§c권한이 부족합니다.");
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage("");
            sender.sendMessage("   §b/ep table §7- 인챈트+ 테이블을 엽니다.");
            sender.sendMessage("   §b/ep anvil §7- 인챈트+ 모루를 엽니다.");
            sender.sendMessage("   §b/ep d_add [인챈트 이름] [레벨] §7- 들고있는 아이템에 기본 인챈트+를 부여합니다.");
            sender.sendMessage("   §b/ep a_add [인챈트 이름] [레벨] §7- 들고있는 아이템에 추가 인챈트+를 부여합니다.");
            sender.sendMessage("");
            return false;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("table")) {
            EnchTableGui gui = new EnchTableGui();
            gui.open(player);
            return false;
        }
        if (args[0].equalsIgnoreCase("anvil")) {
            EnchTableGui gui = new EnchTableGui();
            gui.open(player);
            return false;
        }
        if (args[0].equalsIgnoreCase("d_add")) {
            if (player.getInventory().getItemInHand().getType().equals(Material.AIR)) {
                player.sendMessage(PREFIX + "아이템을 손에 들어주세요.");
                return false;
            }
            if (args.length == 2) {
                player.sendMessage(PREFIX + "인챈트를 입력해주세요.");
            }
            if (args.length == 3) {
                ItemStack tool = player.getItemInHand().clone();
                String enchName = args[1];

                EnchPlusBase ench = null;

                for (EnchPlusBase base : EnchPlusRegister.registeredEnchMap.keySet()) {
                    if (base.getName().equals(enchName))
                        ench = base;
                }

                if (ench == null) {
                    player.sendMessage(PREFIX + "§e" + enchName + " 인챈트§f는 존재하지 않는 인챈트입니다.");
                    return false;
                }

                Integer level;

                try {
                    level = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(PREFIX + "레벨은 자연수여야 합니다.");
                    return false;
                }

                if (level > ench.getMaxLevel()) {
                    player.sendMessage(PREFIX + "레벨이 최대 레벨(" + ench.getMaxLevel() + ")을 넘어 인챈트가 불가능합니다.");
                    return false;
                }

                ItemStack resultItem = EnchantmentPlusUtil.addDefaultEnchant(tool, ench, level);
                player.getInventory().setItemInHand(resultItem);

                player.sendMessage(PREFIX + "§e" + enchName + " " + EnchantmentPlusUtil.convertArabicToRoman(level) + " §f인챈트를 부여했습니다.");
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("a_add")) {
            if (player.getInventory().getItemInHand().getType().equals(Material.AIR)) {
                player.sendMessage(PREFIX + "아이템을 손에 들어주세요.");
                return false;
            }
            if (args.length == 2) {
                player.sendMessage(PREFIX + "인챈트를 입력해주세요.");
            }
            if (args.length == 3) {
                ItemStack tool = player.getItemInHand().clone();
                String enchName = args[1];

                EnchPlusBase ench = null;

                for (EnchPlusBase base : EnchPlusRegister.registeredEnchMap.keySet()) {
                    if (base.getName().equals(enchName))
                        ench = base;
                }

                if (ench == null) {
                    player.sendMessage(PREFIX + "§e" + enchName + " 인챈트§f는 존재하지 않는 인챈트입니다.");
                    return false;
                }

                Integer level;

                try {
                    level = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(PREFIX + "레벨은 자연수여야 합니다.");
                    return false;
                }

                if (level > ench.getMaxLevel()) {
                    player.sendMessage(PREFIX + "레벨이 최대 레벨(" + ench.getMaxLevel() + ")을 넘어 인챈트가 불가능합니다.");
                    return false;
                }

                ItemStack resultItem = EnchantmentPlusUtil.addAdditionalEnchant(tool, ench, level);
                player.getInventory().setItemInHand(resultItem);

                player.sendMessage(PREFIX + "§e" + enchName + " " + EnchantmentPlusUtil.convertArabicToRoman(level) + " §f인챈트를 부여했습니다.");
                return false;
            }
        }
        return false;
    }

}
