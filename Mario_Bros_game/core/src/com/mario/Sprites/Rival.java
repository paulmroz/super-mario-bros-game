package com.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mario.Screens.PlayScreen;

public abstract class Rival extends Sprite {
    protected World world;
    protected PlayScreen display;
    public Body b2body;
    public Vector2 speed;

    public Rival(PlayScreen display, float positionX, float positionY){
        this.world= display.getWorld();
        this.display = display;
        setPosition(positionX,positionY);
        defineEnemy();
        speed = new Vector2(1, 0);
        //domyslnie wrogowie nie sa "wlaczeni", dopiero gdy mario sie do nich zbliza- aktywuja sie
        b2body.setActive(false);
    }
    protected abstract void defineEnemy();
    public abstract void update(float dt);
    public abstract void hitOnHead(Mario mario);
    public abstract void onEnemyHit(Rival rival);
    public void reverseVelocity(boolean x, boolean y){

        if(y){
            speed.y = -speed.y;
        }

        if(x){
            speed.x = -speed.x;
        }

    }
}
