package com.wamel.enchantplus.util.particle;

public class ParticleColorData {

    private int r;
    private int g;
    private int b;

    public ParticleColorData(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float getR() {
        if (r == 0)
            return 0.001F;
        else
            return (float) r / 255;
    }

    public float getG() {
        if (g == 0)
            return 0.001F;
        else
            return (float) g / 255;
    }

    public float getB() {
        if (b == 0)
            return 0.001F;
        else
            return (float) b / 255;
    }
}
