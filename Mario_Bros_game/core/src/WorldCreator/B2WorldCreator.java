package WorldCreator;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mario.MarioBros;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mario.Screens.PlayScreen;
import com.mario.Sprites.*;




public class B2WorldCreator {
    private final Array<Goomba> goombas;
    private final Array<Turtle> turtles;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bodyDefinition = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDefinition = new FixtureDef();
        Body body;

        //tworzenie body,fixtures kazdego elementu

        //tworzenie body/fixtures dla ziemi
        for(MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bodyDefinition.type = BodyDef.BodyType.StaticBody;
            bodyDefinition.position.set((rectangle.getX() + rectangle.getWidth()/2)/ MarioBros.PPM, (rectangle.getY() + rectangle.getHeight()/2)/ MarioBros.PPM);

            body = world.createBody(bodyDefinition);
            shape.setAsBox(rectangle.getWidth()/2/ MarioBros.PPM, rectangle.getHeight()/2/ MarioBros.PPM);
            fixtureDefinition.shape = shape;
            body.createFixture(fixtureDefinition);
        }

        //tworzenie body/fixtures dla rur
        for(MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bodyDefinition.type = BodyDef.BodyType.StaticBody;
            bodyDefinition.position.set((rectangle.getX() + rectangle.getWidth()/2)/ MarioBros.PPM, (rectangle.getY() + rectangle.getHeight()/2)/ MarioBros.PPM);

            body = world.createBody(bodyDefinition);
            shape.setAsBox(rectangle.getWidth()/2/ MarioBros.PPM, rectangle.getHeight()/2/ MarioBros.PPM);
            fixtureDefinition.shape = shape;
            fixtureDefinition.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fixtureDefinition);
        }

        //tworzenie body/fixtures dla cegiel
        for(MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, object);
        }

        //tworzenie body/fixtures dla monet
        for(MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }


        //tworzenie obietkow Goomba
        goombas = new Array<Goomba>();
        for(MapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rectangle.getX() / MarioBros.PPM, rectangle.getY() / MarioBros.PPM));
        }

        //tworzenie obiektow Turtle
        turtles = new Array<Turtle>();
        for(MapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, rectangle.getX() / MarioBros.PPM, rectangle.getY() / MarioBros.PPM));
        }

    }

    public Array<Rival> getEnemies() {
        Array<Rival> enemies = new Array<Rival>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
