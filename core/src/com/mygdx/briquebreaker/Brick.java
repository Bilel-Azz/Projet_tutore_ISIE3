package com.mygdx.briquebreaker;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;

import static com.badlogic.gdx.math.MathUtils.random;

public class Brick extends Rectangle {
    private int durability;
    private int bonus;
    private Color[] colors; // Tableau de couleurs pour représenter différentes durabilités

    public Brick(float x, float y, float width, float height, int durability) {
        super(x, y, width, height);
        this.durability = durability;

        // Génération aléatoire d'un bonus avec une chance de 1/10
        if (random.nextInt(10) == 0) {
            this.bonus = 1;
        } else {
            this.bonus = 0;
        }

        // Initialisation des couleurs en fonction de la durabilité
        colors = new Color[]{
                Color.RED,      // Durabilité = 1
                Color.ORANGE,   // Durabilité = 2
                Color.YELLOW,   // Durabilité = 3
                Color.GREEN,    // Durabilité = 4
                Color.BLUE      // Durabilité = 5 (et au-delà)
        };
    }

    public void reduceDurability() {
        durability--;
    }

    public int getDurability() {
        return durability;
    }

    public Color getColor() {
        if (durability > 0 && durability <= colors.length) {
            return colors[durability - 1];
        }
        return Color.WHITE;
    }
}
