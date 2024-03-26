package com.mygdx.briquebreaker;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;



public class Brick extends Rectangle {
    private int durability;
    private Color[] colors; // Tableau de couleurs pour représenter différentes durabilités

    public Brick(float x, float y, float width, float height, int durability) {
        super(x, y, width, height);
        this.durability = durability;
        // Initialisation des couleurs en fonction de la durabilité
        colors = new Color[] {
                Color.RED,      // Durabilité = 1
                Color.ORANGE,   // Durabilité = 2
                Color.YELLOW,   // Durabilité = 3
                Color.GREEN,    // Durabilité = 4
                Color.BLUE      // Durabilité = 5 (et au-delà)
        };
    }

    public int getDurability() {
        return durability;
    }

    public void reduceDurability() {
        durability--;
    }

    public Color getColor() {
        // Utilisation de la couleur correspondante à la durabilité actuelle
        int index = Math.min(durability - 1, colors.length - 1);
        return colors[index];
    }
}
