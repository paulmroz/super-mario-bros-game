package com.mario.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mario.MarioBros;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class HeadUpDisplay implements Disposable {

    public Stage stage;
    private Integer hudTimer;
    private float timeCounter;
    private static Integer score;

    // deklaracja labeli odpowiedzialnych za hud
    Label counterLabel;
    static Label currentScoreLabel;
    Label timeLabel;
    Label currentLevelLabel;
    Label levelLabel;
    Label scoreLabel;
    Label saveLabel;

    public HeadUpDisplay(SpriteBatch spriteBatch, String hudTimer, String score){
        timeCounter = 0;
        this.hudTimer = Integer.parseInt(String.valueOf(hudTimer));
        HeadUpDisplay.score = Integer.parseInt(String.valueOf(score));


        Viewport viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch); // stworzenie sceny - pojemnika na elementy


        Table table = new Table(); //stworzenie tabeli ulatwiajacej ulozenie elementow na scenie
        table.top(); //wskazanie na gorna czesc tabeli
        table.setFillParent(true); // dopasowanie tabeli do rozmiaru sceny

        // inicjalizacja labeli odpowiedzialnych za hud
        counterLabel = new Label(String.format("%03d", this.hudTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        currentScoreLabel = new Label(String.format("%06d", this.score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        currentLevelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //dodawanie labeli do tabeli i odpowiednie ich ulozenie
        table.add(scoreLabel).expandX().padTop(10);
        table.add(levelLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row(); // stworzenie wiersza tabeli oddzielajacego labele
        table.add(currentScoreLabel).expandX();
        table.add(currentLevelLabel).expandX();
        table.add(counterLabel).expandX();

        stage.addActor(table); //dodanie tabeli do sceny




    }

    //metoda odpowiedzialna za zliczanie punktów z zebranych monet
    public static void addToScore(int value){
        score+=value;
        currentScoreLabel.setText(String.format("%06d", score));
    }

    //metoda odpowiedzialna za odmierzanie czasu rozgrywki
    public void update(float dt){
        timeCounter +=dt;
        if(timeCounter >=1){
            hudTimer--;
            counterLabel.setText(String.format("%03d", hudTimer));
            timeCounter =0;
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Label getCounterLabel() {
        return counterLabel;
    }

    public static Label getCurrentScoreLabel() {
        return currentScoreLabel;
    }

    public void save() {
        saveLabel = new Label("SAVED", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        stage.addActor(saveLabel);
    }

    public void updateLevel(){
        currentLevelLabel.setText("2");
    }
}
