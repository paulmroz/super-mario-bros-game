package Items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mario.MarioBros;
import com.mario.Screens.PlayScreen;
import com.mario.Sprites.Mario;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;


public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean isDestroyed;
    protected Body body;

    public Item(PlayScreen screen, float positionX, float posistionY){
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(positionX,posistionY);
        setBounds(getX(), getY(), 16/ MarioBros.PPM, 16/MarioBros.PPM);
        defineItem();
        toDestroy = false;
        isDestroyed = false;
    }

    public abstract void defineItem();
    public abstract void use(Mario mario);

    public void update(float deltaTime){
        if(toDestroy && !isDestroyed){
            world.destroyBody(body);
            isDestroyed = true;
        }
    }

    public void draw(Batch batch){
        if(!isDestroyed){
            super.draw(batch);
        }
    }

    public void destroy(){
        toDestroy = true;
    }

    public void reverseVelocity(boolean x, boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }
    }
}
