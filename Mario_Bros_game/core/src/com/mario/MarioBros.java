package com.mario;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.mario.Screens.MenuScreen;

public class MarioBros extends Game {

	// inicjalizacja zmiennej odpowiadajacej z wirtualna szerokosc
	public static final int V_WIDTH = 400;

	// inicjalizacja zmiennej odpowiadajacej z wirtualna wysokosc
	public static final int V_HEIGHT = 208;

	public static final float PPM = 100;
	// inicjalizacj zmiennych statycznych odpowiadających za bity kategori w filtrach
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	Preferences preferences;

	public SpriteBatch spriteBatch; //kontener przechowujący wszystkie grafiki i tekstury

	public static AssetManager manager; //menadżer zasobów przechowywujący obsługujący dzwięki i muzyke




	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/bgmusic.ogg", Music.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/growing.mp3", Sound.class);
        manager.load("audio/sounds/stomp.mp3", Sound.class);
        manager.load("audio/sounds/powerDown.mp3", Sound.class);
		manager.load("audio/sounds/marioDie.mp3", Sound.class);
		manager.load("audio/sounds/victory.mp3", Sound.class);
		manager.finishLoading();
		setScreen(new MenuScreen(this));


		preferences = Gdx.app.getPreferences("gameState");
	}

	@Override
	public void render () {
		super.render(); //wywoluje metode renderujaca aktywny ekran
		manager.update();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		manager.dispose();
		spriteBatch.dispose();

	}

	public SpriteBatch getBatch(){
		return spriteBatch;
	}

    public Preferences getPreferences() {
        return preferences;
    }
}
