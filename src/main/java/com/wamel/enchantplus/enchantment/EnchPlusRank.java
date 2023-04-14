package com.wamel.enchantplus.enchantment;

public enum EnchPlusRank {

    //COMMON(80), RARE(15), EPIC(5), UNIQUE(0), LEGENDARY(0);
    COMMON(80), RARE(20), EPIC(0), UNIQUE(0), LEGENDARY(0);

    private final Integer chance;

    EnchPlusRank(Integer chance) {
        this.chance = chance;
    }

    public Integer getChance() {
        return chance;
    }

    public String getTranslatedString() {
        switch (this) {
            case COMMON:
                return "§f일반";
            case RARE:
                return "§b레어";
            case EPIC:
                return "§5에픽";
            case UNIQUE:
                return "§e유니크";
            case LEGENDARY:
                return "§a레전드리";
        }
        return null;
    }

    public String getRankColor() {
        switch (this) {
            case COMMON:
                return "§f";
            case RARE:
                return "§b";
            case EPIC:
                return "§5";
            case UNIQUE:
                return "§e";
            case LEGENDARY:
                return "§a";
        }
        return null;
    }

}
