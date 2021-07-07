package com.mario.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mario.MarioBros;
import com.mario.Screens.PlayScreen;

public class Mario extends Sprite {
    public enum State{FALLING,JUMPING,STANDING,RUNNING,GROWING,DEAD}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private final TextureRegion smallMarioStand;
    private final Animation <TextureRegion> smallMarioRun;
    private final TextureRegion smallMarioJump;
    private final TextureRegion marioDead;
    private final TextureRegion bigMarioStand;
    private final TextureRegion bigMarioJump;
    private final Animation <TextureRegion> bigMarioRun;
    private final Animation <TextureRegion> marioGrowing;
    private float stateTimer;
    private boolean runningRight;
    private boolean isMarioBig;
    private boolean runGrowingAnimation;
    public boolean timeToDefineBigMario;
    public boolean timeToRedefineSmallMario;
    private boolean isMarioDead;


    public Mario(PlayScreen screen){

        this.world = screen.getWorld();
        currentState=State.STANDING;
        previousState=State.STANDING;
        stateTimer=0;
        runningRight=true;

        Array<TextureRegion> frames = new Array<TextureRegion>();//Tablica przechowywująca textury róznych stanów Mario
        for(int i =1;i<4;i++){
            frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("little_mario"),i*16,0,16,16));
        }
        smallMarioRun =new Animation(0.1f,frames);
        frames.clear();

        for(int i =1;i<4;i++){
            frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("big_mario"),i*16,0,16,32));
        }
        bigMarioRun=new Animation(0.1f,frames);


        frames.clear();

        //animacja rosniecia mario
        frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getTextureAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        marioGrowing = new Animation(0.2f, frames);

        smallMarioJump = new TextureRegion(screen.getTextureAtlas().findRegion("little_mario"), 80 , 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getTextureAtlas().findRegion("big_mario"), 80 , 0, 16, 32);


        smallMarioStand = new TextureRegion(screen.getTextureAtlas().findRegion("little_mario"),0,0,16,16);
        bigMarioStand = new TextureRegion(screen.getTextureAtlas().findRegion("big_mario"), 0 , 0, 16, 32);
        marioDead = new TextureRegion(screen.getTextureAtlas().findRegion("little_mario"),96,0,16,16);


        defineMario();
        setBounds(0,0,16/MarioBros.PPM,16/MarioBros.PPM);
        setRegion(smallMarioStand);
    }
    public void update(float deltaTime){

        if(isMarioBig){
            setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2 - 6 / MarioBros.PPM );
        }else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(deltaTime));

        if(timeToDefineBigMario){
            defineBigMario();
        }
        if(timeToRedefineSmallMario){
            redefineMario();
        }

        if ((0 > b2body.getPosition().y && !isMarioDead)) {
            die();
        }

    }
    public State getState(){

        if(isMarioDead){
            return State.DEAD;
        }
        else if (runGrowingAnimation) {
            return State.GROWING;
        }
        else if(b2body.getLinearVelocity().y>0 || b2body.getLinearVelocity().y<0 && previousState==State.JUMPING){
            return State.JUMPING;
        }
        else if(b2body.getLinearVelocity().y<0){
            return State.FALLING;
        }
        else if(b2body.getLinearVelocity().x!=0){
            return State.RUNNING;
        }
        else{
            return State.STANDING;
        }

    }

    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10 / MarioBros.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);

        fdef.filter.categoryBits=MarioBros.MARIO_BIT;
        fdef.filter.maskBits =MarioBros.ENEMY_BIT| MarioBros.OBJECT_BIT|MarioBros.GROUND_BIT |MarioBros.COIN_BIT|MarioBros.BRICK_BIT|MarioBros.ENEMY_HEAD_BIT| MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits= MarioBros.MARIO_HEAD_BIT;
        fdef.shape=head;
        fdef.isSensor=true;
        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(20/MarioBros.PPM, 20/MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);

        fdef.filter.categoryBits=MarioBros.MARIO_BIT;
        fdef.filter.maskBits =MarioBros.ENEMY_BIT| MarioBros.OBJECT_BIT|MarioBros.GROUND_BIT |MarioBros.COIN_BIT|MarioBros.BRICK_BIT|MarioBros.ENEMY_HEAD_BIT| MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);


        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits= MarioBros.MARIO_HEAD_BIT;
        fdef.shape=head;
        fdef.isSensor=true;
        b2body.createFixture(fdef).setUserData(this);
    }

    public TextureRegion getFrame(float dt){
        currentState=getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region =  marioGrowing.getKeyFrame(stateTimer);
                if(marioGrowing.isAnimationFinished(stateTimer)){
                    runGrowingAnimation = false;
                }
                break;
            case JUMPING:
                region= isMarioBig ? bigMarioJump : smallMarioJump;
                break;
            case RUNNING:
                region= isMarioBig ? bigMarioRun.getKeyFrame(stateTimer,true) : smallMarioRun.getKeyFrame(stateTimer,true);
                break;
            case FALLING:
            case STANDING:
            default:
                region= isMarioBig ? bigMarioStand : smallMarioStand;
                break;
        }
        if((b2body.getLinearVelocity().x<0||!runningRight)&& !region.isFlipX() ){
            region.flip(true,false);
            runningRight=false;
        }
        else if((b2body.getLinearVelocity().x>0||runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight=true;
        }
        stateTimer =currentState ==previousState?stateTimer+dt:0;
        previousState=currentState;
        return region;
    }

    public void grow(){
        if(!isMarioBig) {
            MarioBros.manager.get("audio/sounds/growing.mp3", Sound.class).play();
            runGrowingAnimation = true;
            isMarioBig = true;
            timeToDefineBigMario = true;
            PlayScreen.marioState = "big";


        setBounds(getX(),getY(),getWidth(),getHeight() * 2);
        }
    }



    public void hit(Rival rival){
        if(rival instanceof Turtle && ((Turtle) rival).getCurrentState() == Turtle.State.STANDNING_SHELL){
            ((Turtle) rival).kick(this.getX() <= rival.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }else {
            if (isMarioBig) {
                isMarioBig = false;
                timeToRedefineSmallMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.manager.get("audio/sounds/powerDown.mp3", Sound.class).play();
            } else {
                die();
            }
        }
    }

    public void redefineMario(){
        //zapisanie aktualnej pozycji duzego mario
        Vector2 position = b2body.getPosition();
        //usuniecie duzego mario
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        //stworzenie malego mario w miejscu duzego
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);

        fdef.filter.categoryBits=MarioBros.MARIO_BIT;
        fdef.filter.maskBits =MarioBros.ENEMY_BIT| MarioBros.OBJECT_BIT|MarioBros.GROUND_BIT |MarioBros.COIN_BIT|MarioBros.BRICK_BIT|MarioBros.ENEMY_HEAD_BIT| MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);


        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits= MarioBros.MARIO_HEAD_BIT;
        fdef.shape=head;
        fdef.isSensor=true;
        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineSmallMario = false;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void die(){
        MarioBros.manager.get("audio/music/bgmusic.ogg", Music.class).stop();
        MarioBros.manager.get("audio/sounds/marioDie.mp3", Sound.class).play();
        isMarioDead = true;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;

        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }

        //animacja "podskoczenia" mario przed smiercia
        b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }

    public void setMarioPosition(float positionX, float positionY){

        //zapisanie aktualnej pozycji duzego mario
        Vector2 position = new Vector2(positionX,positionY);
        //usuniecie duzego mario
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        //stworzenie malego mario w miejscu duzego
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/MarioBros.PPM);

        fdef.filter.categoryBits=MarioBros.MARIO_BIT;
        fdef.filter.maskBits =MarioBros.ENEMY_BIT| MarioBros.OBJECT_BIT|MarioBros.GROUND_BIT |MarioBros.COIN_BIT|MarioBros.BRICK_BIT|MarioBros.ENEMY_HEAD_BIT| MarioBros.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);


        EdgeShape head=new EdgeShape();
        head.set(new Vector2(-2/MarioBros.PPM,6/MarioBros.PPM),new Vector2(2/MarioBros.PPM,6/MarioBros.PPM));
        fdef.filter.categoryBits= MarioBros.MARIO_HEAD_BIT;
        fdef.shape=head;
        fdef.isSensor=true;
        b2body.createFixture(fdef).setUserData(this);
    }

    public boolean isBig(){
        return isMarioBig;
    }

    public void setBig(){
        isMarioBig = true;
    }


}
