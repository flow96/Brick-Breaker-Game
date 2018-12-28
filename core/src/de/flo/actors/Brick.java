package de.flo.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;

import de.flo.Utils;

public class Brick extends Actor {

    public int points;
    private Label lblPoints;
    private float x, y;
    private Color color;
    private boolean flashing = false;
    private boolean movingDown = false;
    private int movingCounter = 0;


    public Brick(int points, Texture font, float x, float y) {
        this.points = points;
        this.x = x;
        this.y = y;
        color = new Color(.1f, .25f, .35f, .9f);
        lblPoints = new Label("" + points, new Label.LabelStyle(new BitmapFont(Gdx.files.internal("Fonts/MyFont.fnt"), new TextureRegion(font)), Color.WHITE));
        lblPoints.setFontScale(.2f);
    }

    public void render(SpriteBatch batch, Texture t){
        batch.setColor(color);
        batch.draw(t, x + 1, y + 1, Utils.BRICK_SIZE - 2, Utils.BRICK_SIZE - 2);
        lblPoints.draw(batch, .8f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        lblPoints.setPosition(x + (Utils.BRICK_SIZE / 2) - (lblPoints.getPrefWidth() / 2), y - Utils.BRICK_SIZE + (lblPoints.getPrefHeight() * 1.5f));
        lblPoints.setText("" + points);
    }

    public void moveDown(){
        for (int i = 0; i < 10; i++) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    y -= 3.2f;
                }
            }, 0.05f * i);
        }
    }

    public void flash(){
        if(!flashing) {
            flashing = true;
            addAction(Actions.repeat(3, Actions.run(new Runnable() {
                @Override
                public void run() {
                    color.b += .1f;
                    color.g += .1f;
                    color.r += .1f;
                }
            })));
            addAction(Actions.after(Actions.repeat(3, Actions.run(new Runnable() {
                @Override
                public void run() {
                    color.b -= .1f;
                    color.g -= .1f;
                    color.r -= .1f;
                }
            }))));
            addAction(Actions.after(Actions.run(new Runnable() {
                @Override
                public void run() {
                    flashing = false;
                }
            })));
        }
    }
}
