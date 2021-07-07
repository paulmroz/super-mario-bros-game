package com.mario.Screens;

import Items.Item;
import Items.ItemDef;
import Items.Mushroom;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mario.MarioBros;
import com.mario.Scenes.HeadUpDisplay;
import com.mario.Sprites.*;
import WorldCreator.B2WorldCreator;
import com.mario.ContactHandler.ContactListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;



import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private final MarioBros game;
    private final TextureAtlas textureAtlas;
    private final Viewport gamePort;
    private final Mario mario;
    private final HeadUpDisplay headUpDisplay;
    private final OrthographicCamera camera;



    private final TiledMap map; // referencja do mapy
    private final OrthogonalTiledMapRenderer mapRenderer; // renderowanie mapy na ekranie

    //Zmienne BOX2D
    private final World world;
    //private Box2DDebugRenderer b2dr; //daje graficzna reprezentacje body,fixtures swiata
    private final B2WorldCreator creator;


    private final Array<Item> items;
    public LinkedBlockingQueue<ItemDef> itemsToSpawn;
    private final String levelName;
    public static String marioState;

    public PlayScreen(MarioBros game, String levelName, String marioState, String worldTimer, String score, float marioPositionX, float marioPositionY){



        PlayScreen.marioState = marioState;
        this.levelName = levelName;
        textureAtlas =new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;

        camera = new OrthographicCamera(); // tworzenie obiektu kamery. To czym rozni sie od drugiego rodzaju kamery (Perspective) to renderowanie obiektow w scenie w ich dokladnych rozmiarach
        gamePort = new StretchViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, camera); // tworzenie obiektu odpowiadajacego za dopasowanie rozmiaru okna do roznych wyswietlaczy. StretchViewport rozciaga podany w argumentach rozmiar ekranu do odpowiednich dla wyswietlacza rozmiarow.
        headUpDisplay = new HeadUpDisplay(game.spriteBatch, worldTimer, score);

        // zmienna odpowiedzialna za ladowanie mapy
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load(levelName);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ MarioBros.PPM); //przekazujemy mape do wyrenderowania
        camera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0); // wysrodkowanie mapy

        world = new World(new Vector2(0,-10), true);

        creator = new B2WorldCreator(this);
        mario = new Mario(this);

        world.setContactListener(new ContactListener());

        Music music = MarioBros.manager.get("audio/music/bgmusic.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();

        if(PlayScreen.marioState.equals("big")){
            mario.grow();
            mario.setBig();
        }

        mario.setMarioPosition(marioPositionX,marioPositionY);

        if(levelName.equals("mapa2.tmx")){
            headUpDisplay.updateLevel();
        }
    }

    public void spawnItem(ItemDef iDef){
        itemsToSpawn.add(iDef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef iDef = itemsToSpawn.poll();
            if(iDef.type == Mushroom.class){
                items.add(new Mushroom(this, iDef.position.x, iDef.position.y));
            }
        }
    }




    @Override
    public void show() {

    }

    public void handleInput(){
        if(mario.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && (mario.getState() != Mario.State.FALLING) &&  (mario.getState() != Mario.State.JUMPING) ) {
                mario.b2body.applyLinearImpulse(new Vector2(0, 3.8f), mario.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && mario.b2body.getLinearVelocity().x <= 1.5)
                mario.b2body.applyLinearImpulse(new Vector2(0.1f, 0), mario.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && mario.b2body.getLinearVelocity().x >= -1.5 && (mario.b2body.getPosition().x>1.2))
                mario.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), mario.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if(mario.isBig()) {
                    game.getPreferences().putString("state", "big");
                }else{
                    game.getPreferences().putString("state", "small");
                }
                game.getPreferences().putString("level", levelName);
                game.getPreferences().putString("time", String.valueOf(headUpDisplay.getCounterLabel().getText()));
                game.getPreferences().putString("score", String.valueOf(HeadUpDisplay.getCurrentScoreLabel().getText()));
                game.getPreferences().putFloat("xValue", mario.b2body.getPosition().x);
                game.getPreferences().putFloat("yValue", mario.b2body.getPosition().y);
                game.getPreferences().flush();
                headUpDisplay.save();
            }
        }
    }

    public void update(float deltaTime){

        handleInput();
        handleSpawningItems();
        world.step(1/65f,5,3);
        mario.update(deltaTime);
        for(Rival rival : creator.getEnemies()){
            rival.update(deltaTime);
            //aktywowanie przeciwnikow, gdy mario sie zblizy
            if(rival.getX() < mario.getX() +224/MarioBros.PPM){
                rival.b2body.setActive(true);
            }
        }

        for(Item  item : items){
            item.update(deltaTime);
        }
        headUpDisplay.update(deltaTime);

        camera.update();
        mapRenderer.setView(camera); // ustawienie kamery na widok zdefiniowanej wczesniej kamery


        if(mario.currentState != Mario.State.DEAD) {
            camera.position.x = mario.b2body.getPosition().x; //kamera sledzi mario
        }
    }




    @Override
    public void render(float deltaTime) {

        update(deltaTime); // aktualizowanie gry
        // metody czyszczące ekran
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        mapRenderer.render();//renderwanie mapy
       // b2dr.render(world,gamecam.combined);//renderowanie box2ddebuglines

        game.spriteBatch.setProjectionMatrix(camera.combined);
        game.spriteBatch.begin();
        mario.draw(game.spriteBatch);

        for(Item  item : items){
            item.draw(game.spriteBatch);
        }

        for(Rival rival : creator.getEnemies()){
            rival.draw(game.spriteBatch);
        }

        game.spriteBatch.end();

        game.spriteBatch.setProjectionMatrix(headUpDisplay.stage.getCamera().combined); //przekazanie do obiektu batch naszej kamery
        headUpDisplay.stage.draw(); //rysowanie sceny z hudem

        if(gameOver()){
            game.setScreen(new GameOverScreen(game, headUpDisplay.getCounterLabel(), HeadUpDisplay.getCurrentScoreLabel()));
            dispose();
        }

        if(win()){

            if(levelName.equals("mapa2.tmx")) {

                game.setScreen(new GameWinScreen(game, headUpDisplay.getCounterLabel(), HeadUpDisplay.getCurrentScoreLabel()));
            }
            else{
                game.setScreen(new PlayScreen(game,"mapa2.tmx",marioState ,String.valueOf(headUpDisplay.getCounterLabel().getText()),String.valueOf(HeadUpDisplay.getCurrentScoreLabel().getText()),20/MarioBros.PPM, 20/MarioBros.PPM));
            }
            dispose();
        }



    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height); //przekazywanie do obiektu StretchViewport nowych rozmiarow okna po ich zmianie.

    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }
    public TextureAtlas getTextureAtlas(){
        return textureAtlas;
    }
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public boolean gameOver(){
        return mario.currentState == Mario.State.DEAD && mario.getStateTimer() > 3;
    }

    public boolean win(){
        if(mario.currentState != Mario.State.DEAD && mario.getX() > 37){
            MarioBros.manager.get("audio/music/bgmusic.ogg", Music.class).stop();
            MarioBros.manager.get("audio/sounds/victory.mp3", Sound.class).play();
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        headUpDisplay.dispose();
    }
}
