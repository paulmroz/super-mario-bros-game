package com.mario.Sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mario.MarioBros;
import com.mario.Screens.PlayScreen;

public abstract class InteractiveTileObject {

    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected PlayScreen screen;
    protected Fixture fixture;
    protected MapObject object;

    public InteractiveTileObject(PlayScreen screen, MapObject object) {
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bodyDefinition = new BodyDef();
        FixtureDef fixtureDefinition = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDefinition.type = BodyDef.BodyType.StaticBody;
        bodyDefinition.position.set((bounds.getX() + bounds.getWidth()/2)/ MarioBros.PPM, (bounds.getY() + bounds.getHeight()/2)/ MarioBros.PPM);

        body = world.createBody(bodyDefinition);
        shape.setAsBox(bounds.getWidth()/2/ MarioBros.PPM, bounds.getHeight()/2/ MarioBros.PPM);
        fixtureDefinition.shape = shape;
        fixture = body.createFixture(fixtureDefinition);


    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer=(TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x*MarioBros.PPM/16),(int)(body.getPosition().y*MarioBros.PPM/16));
    }


    public abstract void onHeadHit(Mario mario);
    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits=filterBit;
        fixture.setFilterData(filter);
    }


}
