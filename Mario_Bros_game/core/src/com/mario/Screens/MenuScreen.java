package com.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.mario.MarioBros;


public class MenuScreen implements Screen {


    //tekstura tla w menu
    private final Texture background = new Texture("tlo.png");

    //tekstura na przycisk "nowa gra"
    private final Texture playButton = new Texture("nowa_gra.png");

    //tekstura na przycisk "wznow gre"
    private final Texture resumeButton = new Texture("wznow_gre.png");

    //tekstura na przycisk "wyjscie"
    private final Texture closeButton = new Texture("wyjscie.png");


    private final MarioBros game;


    public MenuScreen(MarioBros game) {
        this.game = game;

    }

    //obsługa przyciskow w menu
    public void handleInput(){

        int currentWidth = Gdx.graphics.getWidth();
        int currentHeight = Gdx.graphics.getHeight();

        //przypadek wcisniecia przycisku "nowa gra"
       if((Gdx.input.getX() >=  currentWidth /2.67 && Gdx.input.getY() >= currentHeight /3.2) && (Gdx.input.getX() <=  currentWidth /1.6 && Gdx.input.getY() <= currentHeight /2.3)) {
           if (Gdx.input.isTouched()) {

               game.setScreen(new PlayScreen(game,"mapa1.tmx", "small","200","0",20/MarioBros.PPM, 20/MarioBros.PPM));
               dispose();
           }

           //przypadek wcisniecia przycisku "wznow gre"
       }else if((Gdx.input.getX() >=  currentWidth /2.67 && Gdx.input.getY() >= currentHeight /1.92) && (Gdx.input.getX() <=  currentWidth /1.6 && Gdx.input.getY() <= currentHeight /1.47) &&  game.getPreferences().getFloat("xValue") != 0){
            if(Gdx.input.isTouched()){

                game.setScreen(new PlayScreen(game,game.getPreferences().getString("level"),game.getPreferences().getString("state"), game.getPreferences().getString("time"), game.getPreferences().getString("score"),game.getPreferences().getFloat("xValue"), game.getPreferences().getFloat("yValue")));

                dispose();
            }

            //przypadek wcisniecia przycisku "wyjscie"
        }else if((Gdx.input.getX() >=  currentWidth /2.67 && Gdx.input.getY() >= currentHeight /1.37) && (Gdx.input.getX() <=  currentWidth /1.6 && Gdx.input.getY() <= currentHeight /1.13)) {
           if (Gdx.input.isTouched()) {
               Gdx.app.exit();

        }

       }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        //wlaczenie obslugi klikniec
        handleInput();


        //renderowanie menu
       game.getBatch().begin(); // otwieranie pliku "batch" gdzie znajduja sie grafiki i tekstury
       game.getBatch().draw(background, 0, 0 , 800, 480); // rysowanie wybranej tekstury we wskazanych w parametrach wspolrzednych
       game.getBatch().draw(playButton, 300, 250, 200, 75);
       game.getBatch().draw(resumeButton, 300, 150, 200, 75 );
       game.getBatch().draw(closeButton, 300, 50, 200, 75 );
       game.getBatch().end(); // zamykanie pliku "batch"

    }

    @Override
    public void resize(int width, int height) {

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

    @Override
    public void dispose() {
      background.dispose();
      playButton.dispose();
      resumeButton.dispose();
      closeButton.dispose();
    }
}
