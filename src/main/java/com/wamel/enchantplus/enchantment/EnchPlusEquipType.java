package com.wamel.enchantplus.enchantment;

public enum EnchPlusEquipType {

    HELMET("HELMET"), CHESTPLATE("CHESTPLATE"), LEGGINGS("LEGGINGS"), BOOTS("BOOTS"), WEAPON("SWORD", "AXE"), PICKAXE("PICKAXE");

    private final String[] types;

    EnchPlusEquipType(String... types) {
        this.types = types;
    }

    public String[] getTypes() {
        return types;
    }

    public String getTranslatedString() {
        switch (this) {
            case HELMET:
                return "투구";
            case CHESTPLATE:
                return "갑옷";
            case LEGGINGS:
                return "바지";
            case BOOTS:
                return "신발";
            case WEAPON:
                return "무기";
            case PICKAXE:
                return "곡괭이";
        }
        return null;
    }
}
