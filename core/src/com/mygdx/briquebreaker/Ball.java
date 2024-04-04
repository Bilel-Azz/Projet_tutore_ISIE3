package com.mygdx.briquebreaker;

import com.badlogic.gdx.math.Vector2;

public class Ball {
    public Vector2 position;
    public Vector2 velocity;

    public Ball(float x, float y, float vx, float vy) {
        position = new Vector2(x, y);
        velocity = new Vector2(vx, vy);
    }
}

