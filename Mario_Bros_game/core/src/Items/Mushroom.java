package Items;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mario.MarioBros;
import com.mario.Screens.PlayScreen;
import com.mario.Sprites.Mario;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;


public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float positionX, float positionY) {
        super(screen, positionX, positionY);
        setRegion(screen.getTextureAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity=new Vector2(0.7f,0);
    }

    @Override
    public void defineItem() {
        BodyDef bodyDefinition = new BodyDef();
        bodyDefinition.position.set(getX(),getY());
        bodyDefinition.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDefinition);

        FixtureDef fixtureDefinition = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/ MarioBros.PPM);
        fixtureDefinition.filter.categoryBits = MarioBros.ITEM_BIT;
        fixtureDefinition.filter.maskBits = MarioBros.MARIO_BIT | MarioBros.OBJECT_BIT | MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT;

        fixtureDefinition.shape = shape;
        body.createFixture(fixtureDefinition).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        destroy();
        mario.grow();
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/ 2);
        velocity.y= body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);

    }

}
