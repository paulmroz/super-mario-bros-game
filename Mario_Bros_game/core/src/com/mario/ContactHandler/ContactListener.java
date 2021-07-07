package com.mario.ContactHandler;

import com.badlogic.gdx.physics.box2d.*;
import com.mario.MarioBros;
import com.mario.Sprites.Rival;
import com.mario.Sprites.InteractiveTileObject;
import Items.Item;
import com.mario.Sprites.Mario;

public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {


    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA=contact.getFixtureA();
        Fixture fixtureB=contact.getFixtureB();

        int collisionDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;


        switch (collisionDefinition) {
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if (fixtureA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT) {
                    ((InteractiveTileObject) fixtureB.getUserData()).onHeadHit((Mario) fixtureA.getUserData());
                }else{
                    ((InteractiveTileObject) fixtureA.getUserData()).onHeadHit((Mario) fixtureB.getUserData());
                }
                break;
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT) {
                    ((Rival)fixtureA.getUserData()).hitOnHead((Mario) fixtureB.getUserData());
                } else{
                    ((Rival) fixtureB.getUserData()).hitOnHead((Mario) fixtureA.getUserData());
                }
                break;
            //po kontakcie przeciwnika z obiektem odwracamy jego kierunek poruszania
            case MarioBros.ENEMY_BIT | MarioBros.GROUND_BIT:
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
                    ((Rival) fixtureA.getUserData()).reverseVelocity(true,false);

                } else{
                    ((Rival) fixtureB.getUserData()).reverseVelocity(true,false);
                }
                break;

            //po spotkaniu dwoch wrogow odrwacamy ich kierunek poruszania
            case MarioBros.ENEMY_BIT:
                    ((Rival) fixtureA.getUserData()).onEnemyHit((Rival) fixtureB.getUserData());
                    ((Rival) fixtureB.getUserData()).onEnemyHit((Rival) fixtureA.getUserData());
                    break;

            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
                    ((Item) fixtureA.getUserData()).reverseVelocity(true,false);

                } else{
                    ((Item) fixtureB.getUserData()).reverseVelocity(true,false);
                }
                break;

            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if (fixtureA.getFilterData().categoryBits == MarioBros.MARIO_BIT) {
                    ((Mario) fixtureA.getUserData()).hit((Rival) fixtureB.getUserData());
                }else{
                    ((Mario) fixtureB.getUserData()).hit((Rival) fixtureA.getUserData());
                }
                break;

            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT) {
                    ((Item) fixtureA.getUserData()).use((Mario) fixtureB.getUserData());

                } else{
                    ((Item) fixtureB.getUserData()).use((Mario) fixtureA.getUserData());
                }
                break;


        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
