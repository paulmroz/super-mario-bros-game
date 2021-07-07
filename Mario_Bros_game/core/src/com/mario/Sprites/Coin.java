package com.mario.Sprites;

import Items.ItemDef;
import Items.Mushroom;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mario.Scenes.HeadUpDisplay;
import com.mario.Screens.PlayScreen;
import com.mario.MarioBros;


public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {

        int EMPTY_COIN_BLOCK = 28;
        if (getCell().getTile().getId() == EMPTY_COIN_BLOCK) {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            if(object.getProperties().containsKey("mushroom")){
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 18/MarioBros.PPM), Mushroom.class));
                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                HeadUpDisplay.addToScore(250);
                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
        }
        getCell().setTile(tileSet.getTile(EMPTY_COIN_BLOCK));

    }
}
