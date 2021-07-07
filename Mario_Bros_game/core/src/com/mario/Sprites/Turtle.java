package com.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mario.MarioBros;
import com.mario.Screens.PlayScreen;

public class Turtle extends Rival {

    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;

    public enum State{WALKING, STANDNING_SHELL, MOVING_SHELL, DEAD};
    public State currentState;
    public State previousState;
    private float stateTime;
    private final Animation walkAnimation;
    private TextureRegion shell;
    private float deadRotationDegrees;
    private boolean isDestroyed;


    public Turtle(PlayScreen screen, float positionX, float positionY) {
        super(screen, positionX, positionY);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("turtle"),0,0,16,24));
        frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("turtle"),16,0,16,24));
        shell = new TextureRegion(screen.getTextureAtlas().findRegion("turtle"),64,0,16,24);
        walkAnimation = new Animation(0.2f, frames);
        currentState = previousState = State.WALKING;
        deadRotationDegrees =0;

        setBounds(getX(), getY(), 16/ MarioBros.PPM, 24/ MarioBros.PPM);
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
        fixtureDefinition.restitution = 1.5f;
        fixtureDefinition.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fixtureDefinition).setUserData(this);


    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        // po 3 sekundach zolw zaczyna znow sie poruszac
        if(currentState == State.STANDNING_SHELL && stateTime >3){
            currentState = State.WALKING;
            speed.x = 1;
        }
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 / MarioBros.PPM);

        if(currentState == State.DEAD){
            deadRotationDegrees += 4;
            rotate(deadRotationDegrees);
            if(stateTime > 4 && !isDestroyed){
                world.destroyBody(b2body);
                isDestroyed = true;
            }
        }else {
            b2body.setLinearVelocity(speed);
        }
    }

    @Override
    public void hitOnHead(Mario mario) {
        // jesli zolw nie jest w skorupie, a zostanie uderzony, powinien wejsc do skorupy i zatrzymac sie
        if(currentState != State.STANDNING_SHELL){
            currentState = State.STANDNING_SHELL;
            speed.x = 0;
        } else{
            //kopniecie skorupy w strone przeciwna do ruchu mario po skoczeniu na glowe
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }



    public TextureRegion getFrame(float dt){
        TextureRegion region;

        switch (currentState){
            case STANDNING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
                break;
        }
        //obrocenie zolwia
        if(speed.x > 0 && !region.isFlipX()){
            region.flip(true,false);
        }
        if(speed.x < 0 && region.isFlipX()){
            region.flip(true,false);
        }
        stateTime =currentState ==previousState?stateTime+dt:0;
        previousState=currentState;
        return region;
    }

    //usniecie buga powodujacego pojawianie sie zmarlych zolwi
    public void draw(Batch batch){
        if(!isDestroyed){
            super.draw(batch);
        }
    }

    public void kick(int speed){
        this.speed.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState(){
        return currentState;
    }

    public void onEnemyHit(Rival rival){
        if(rival instanceof Turtle){
            if(((Turtle) rival).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            }else if(currentState == State.MOVING_SHELL && ((Turtle) rival).currentState == State.WALKING){
                return;
            }else{
                reverseVelocity(true, false);
            }

        }else if(currentState != State.MOVING_SHELL){
            reverseVelocity(true,false);
        }
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filer = new Filter();
        filer.maskBits = MarioBros.NOTHING_BIT;

        for(Fixture fixture : b2body.getFixtureList()){
            fixture.setFilterData(filer);
        }
        //podskoczenie po smierci
        b2body.applyLinearImpulse(new Vector2(0,5f), b2body.getWorldCenter(), true);

    }

}
