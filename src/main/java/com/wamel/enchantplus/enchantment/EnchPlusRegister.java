package com.wamel.enchantplus.enchantment;

import com.wamel.enchantplus.EnchantPlus;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EnchPlusRegister {

    private static EnchantPlus plugin = EnchantPlus.getInstance();
    public static HashMap<EnchPlusBase, EnchPlusRank> registeredEnchMap = new HashMap<>();
    public static LinkedList<EnchPlusBase> registeredEnchList = new LinkedList<>();

    // com.wamel.enchantplus.enchantment.list에 생성시 자동으로 등록.
    public static void start() {
        Set<Class<?>> classes = getClasses("com.wamel.enchantplus.enchantment.list");

        int i = 0;
        for (Class clazz : classes) {
            registerEnchListener(clazz);
            i++;
        }
        Bukkit.getConsoleSender().sendMessage("§b[EnchantPlus] 성공적으로 " + i + "개의 인챈트를 로드했습니다.");
    }

    private static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        try {
            JavaPlugin pluginObject = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin(plugin.getName());
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(pluginObject);
            JarFile jarFile = new JarFile(file);

            for (Enumeration<JarEntry> entry = jarFile.entries(); entry.hasMoreElements();) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName().replace("/", ".");

                if (name.startsWith(packageName)) {
                    if (name.endsWith(".class") && !name.contains("$")) { // 내부 클래스 포함 X
                        classes.add(Class.forName(name.substring(0, name.length() - 6)));
                        continue;
                    } if (!name.endsWith(".class")) { // 재귀적으로 해당 패키지에 위치한 모든 클래스를 불러 옴
                        classes.addAll(getClasses("name"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static void registerEnchListener(Class<? extends EnchPlusBase> ench) {
        try {
            Constructor constructor = ench.getConstructors()[0];

            Object instance = constructor.newInstance(null, null, null, null, null);

            plugin.getServer().getPluginManager().registerEvents((Listener) instance, plugin);

            registeredEnchMap.put((EnchPlusBase) instance, ((EnchPlusBase) instance).getRank());
            registeredEnchList.add((EnchPlusBase) instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
