package com.mygdx.briquebreaker;
import com.badlogic.gdx.math.Circle;

public class Bonus extends Circle {
    private int type;
    private int duration;
    private int effect;
    private int value;

    public Bonus(float x, float y, float radius, int type, int duration, int effect, int value) {
        super(x, y, radius);
        this.type = type;
        this.duration = duration;
        this.effect = effect;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getEffect() {
        return effect;
    }

    public int getValue() {
        return value;
    }

}
