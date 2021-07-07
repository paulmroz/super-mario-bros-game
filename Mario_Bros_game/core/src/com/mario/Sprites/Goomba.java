package com.mario.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mario.MarioBros;
import com.mario.Screens.PlayScreen;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;



public class Goomba extends Rival {
    private float stateTime;
    private final Animation walkAnimation;
    private boolean prepareToDestroy;
    private boolean isDestroyed;

    public Goomba(PlayScreen screen, float positionX, float positionY) {
        super(screen, positionX, positionY);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i =0;i<2;i++){
            frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("goomba"),i*16,0,16,16));
        }
        walkAnimation = new Animation(0.4f, frames);
        stateTime=0;
        setBounds(getX(),getY(),16/MarioBros.PPM,16/MarioBros.PPM);
        prepareToDestroy = false;
        isDestroyed = false;
    }
    public void update(float deltaTime){
        stateTime +=deltaTime;
        //sprawdzenie czy obiekt powinien zostac zniszczony
        if(prepareToDestroy && !isDestroyed){
            world.destroyBody(b2body);
            isDestroyed = true;
            setRegion(new TextureRegion(display.getTextureAtlas().findRegion("goomba"),32,0,16,16));
            stateTime = 0;
        }else if(!isDestroyed) {
            b2body.setLinearVelocity(speed);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDefinition = new BodyDef();
        bodyDefinition.position.set(getX(),getY());
        bodyDefinition.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDefinition);

        FixtureDef fixtureDefinition = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);

        fixtureDefinition.filter.categoryBits=MarioBros.ENEMY_BIT;
        fixtureDefinition.filter.maskBits = MarioBros.GROUND_BIT |MarioBros.COIN_BIT|MarioBros.BRICK_BIT|MarioBros.ENEMY_BIT|MarioBros.OBJECT_BIT|MarioBros.MARIO_BIT;

        fixtureDefinition.shape = shape;
        b2body.createFixture(fixtureDefinition).setUserData(this);


        //tworzenie glowy
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        //tworzenie wierzcholkow
        vertice[0] = new Vector2(-5,8).scl(1/ MarioBros.PPM);
        vertice[1] = new Vector2(5,8).scl(1/ MarioBros.PPM);
        vertice[2] = new Vector2(-3,3).scl(1/ MarioBros.PPM);
        vertice[3] = new Vector2(3,3).scl(1/ MarioBros.PPM);
        head.set(vertice);

        fixtureDefinition.shape = head;
        //dodanie podskoku po skoczeniu na glowe goomby
        fixtureDefinition.restitution = 0.4f;
        fixtureDefinition.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fixtureDefinition).setUserData(this);


    }

    public void draw(Batch batch){
        //usuniecie obiektu po 0.8 sekundzie od zabicia
        if(!isDestroyed || stateTime < 0.8){
            super.draw(batch);
        }
    }

    @Override
    public void hitOnHead(Mario mario) {
        prepareToDestroy = true;
        MarioBros.manager.get("audio/sounds/stomp.mp3", Sound.class).play();
    }

    public void onEnemyHit(Rival rival){
        if(rival instanceof Turtle && ((Turtle) rival).currentState == Turtle.State.MOVING_SHELL){
            prepareToDestroy = true;
        }else{
            reverseVelocity(true, false);
        }
    }

}
