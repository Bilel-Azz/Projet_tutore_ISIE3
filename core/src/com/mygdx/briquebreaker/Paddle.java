package com.mygdx.briquebreaker;
import com.badlogic.gdx.math.Rectangle;
public class Paddle extends Rectangle{

    private int durability ;
    

    public Paddle( float x, float y, float width, float height){
        super(x,y,width,height);
        int durability = 1 ;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }
}
