package com.mario.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mario.MarioBros;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;



public class GameOverScreen implements Screen {

    private final Stage stage;
    private final Game game;

    public GameOverScreen(Game game, Label TIME, Label SCORE){

        this.game = game;
        Viewport viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioBros) game).spriteBatch);

        Label.LabelStyle fontRed = new Label.LabelStyle(new BitmapFont(), Color.RED);
        Label.LabelStyle fontWhite = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Table table = new Table();
        table.center();
        //ustawienie tabeli zeby zajmowala caly ekran
        table.setFillParent(true);

        int timeInt = Integer.parseInt(String.valueOf(TIME.getText()));
        String timeString = String.valueOf(300-timeInt);

        Label gameOverLabel = new Label("GAME OVER", fontRed);
        Label resultLabel = new Label("RESULT", fontWhite);
        Label timeLabel = new Label("Time: " + timeString + "s", fontWhite);
        Label scoreLabel = new Label("Score: " + SCORE.getText(), fontWhite);


        Label playAgainLabel = new Label("Click to play again!", fontRed);

        //wypelnienie napisu na calej szerooksci tabeli
        table.add(gameOverLabel).expandX();
        //nowy wiersz tabeli
        table.row();
        table.add(resultLabel).expandX().padTop(5f);
        table.row();
        table.add(timeLabel).expandX().padTop(5f);
        table.row();
        table.add(scoreLabel).expandX().padTop(5f);
        table.row();
        table.add(playAgainLabel).expandX().padTop(5f);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        if(Gdx.input.justTouched()){
            game.setScreen(new PlayScreen((MarioBros) game, "mapa1.tmx","small","200","0",0.2f,0.2f));
            dispose();
        }
        //wyczyszczenie ekranu
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //wyrenderowanie tabeli
        stage.draw();
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
        stage.dispose();
    }
}
