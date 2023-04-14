package com.wamel.enchantplus.command;

import com.wamel.enchantplus.gui.EnchBookGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmd_Epbook implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            String keyword = args[0];
            EnchBookGui book = new EnchBookGui(keyword, 1);
            book.open((Player) sender);
            return false;
        } else {
            EnchBookGui book = new EnchBookGui(null, 1);
            book.open((Player) sender);
            return false;
        }
    }

}
