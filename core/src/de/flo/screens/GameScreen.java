package de.flo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.HashSet;

import de.flo.Utils;
import de.flo.actors.Ball;
import de.flo.actors.Brick;


public class GameScreen extends InputAdapter implements Screen {


    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch spriteBatch;

    private Brick[][] map = new Brick[11][15];
    private Ball[] balls = new Ball[80];

    private Texture ballTexture;
    private Texture brickTexture;

    private float speedIncreaseTimer = 0;
    private float speed = 10;
    private final float MAX_SPEED = 40;
    private final float START_SPEED = 10;
    private boolean changedX, changedY = changedX = false;
    private float ballDiameter = Utils.BRICK_SIZE / 2.5f;
    private HashSet<Brick> collisionMap;
    private float slowMow = 0;

    private Label lblMove;
    private boolean allowShoot = true;
    private Vector2 firstBallDown;
    private int ballsDown = 0;
    private boolean gameOver = false;


    public GameScreen(SpriteBatch sb) {
        this.spriteBatch = sb;
        this.collisionMap = new HashSet<Brick>();
        this.camera = new OrthographicCamera();
        viewport = new FitViewport(11 * Utils.BRICK_SIZE, 20 * Utils.BRICK_SIZE, camera);
        camera.translate(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
        //camera.translate(0, - 2 * Utils.BRICK_SIZE);
        //ballPos = new Vector2(Utils.getCoord(5.5f), 0);

        ballTexture = new Texture("Ball.png");
        brickTexture = new Texture("Brick.png");

        // initMap();
        readMap();
        Gdx.input.setInputProcessor(this);
    }

    private void readMap(){
        Texture fontTexture = new Texture(Gdx.files.internal("Fonts/MyFont.png"), true);
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        JsonValue value;
        JsonReader reader = new JsonReader();
        value = reader.parse(Gdx.files.internal("Levels/Normal/Level1.json"));
        balls = new Ball[value.getInt("balls")];
        for (int i = 0; i < balls.length; i++) {
            balls[i] = new Ball();
            balls[i].position = new Vector2(Utils.getCoord(5.5f), 0);
        }
        ballsDown = balls.length;

        JsonValue map = value.get("map");
        this.map = new Brick[map.child.size][map.size];
        JsonValue line = map.child;
        for (int y = map.size - 1; y >= 0; y--) {
            for (int x = 0; x < line.size; x++) {
                if(line.getInt(x) > 0)
                    this.map[x][y] = new Brick(line.getInt(x), fontTexture, Utils.getCoord(x), Utils.getCoord(y));
            }
            if(y > 0)
                line = line.next;
        }
    }


    private void initMap() {
        Texture fontTexture = new Texture(Gdx.files.internal("Fonts/MyFont.png"), true);
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // DEBUG
        lblMove = new Label("", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("Fonts/MyFont.fnt"), new TextureRegion(fontTexture)), Color.WHITE));
        lblMove.setFontScale(.3f);
        lblMove.setPosition(100, 100);

        for (int i = 0; i < map.length; i++) {
            for (int j = 6; j < map[i].length; j++) {
                int nr = (int)(Math.random() * 100);
                if(nr > 0 && Math.random() > .2f)
                    map[i][j] = new Brick(nr, fontTexture, Utils.getCoord(i), Utils.getCoord(j));
            }
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.04f, .04f, .08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);

        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);

        for (int i = 0; i < balls.length; i++) {
            spriteBatch.draw(ballTexture, balls[i].position.x, balls[i].position.y, ballDiameter, ballDiameter);
        }

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] != null)
                    map[x][y].render(spriteBatch, brickTexture);
            }
        }
        //lblMove.draw(spriteBatch, 1);
        spriteBatch.end();
    }

    private void update(float delta) {
        if(!gameOver && !allowShoot) {
            speedIncreaseTimer += delta;
            if (speedIncreaseTimer >= 5) {
                speedIncreaseTimer = 0;
                if (speed < MAX_SPEED) {
                    speed += 5;
                    System.out.println("Speed increased!");
                    for (int i = balls.length - 1; i >= 0; i--) {
                        if (balls[i].movingDirection != null) {
                            balls[i].movingDirection.nor().scl(speed);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < balls.length; i++) {
            if (balls[i].movingDirection != null) {
                balls[i].position.add(balls[i].movingDirection);
                checkCollisions(balls[i]);
            }
        }
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] != null)
                    map[i][j].act(delta);
            }
        }
    }


    private void checkCollisions(final Ball ball) {
        Vector2 movement = new Vector2(ball.movingDirection.x, ball.movingDirection.y).nor().scl(ballDiameter * .2f);
        Vector2 nextPos = new Vector2(ball.position.x, ball.position.y);
        int iterations = (int)Math.ceil(speed / (ballDiameter * .2f));
        changedX = changedY = false;
        collisionMap.clear();
        // Next pos half calculated
        B: for (int i = 0; i < iterations; i++) {
            nextPos.add(movement);

            // Check down
            if (nextPos.y <= Utils.getCoord(0)) {                         // Collision ground
                ball.movingDirection = null;
                ball.position.y = 0;
                slowMow = 0;
                ballsDown++;
                if(firstBallDown == null)
                    firstBallDown = ball.position;
                else{
                    final double xStep = (firstBallDown.x - ball.position.x) / 10.0;
                    final float destinationX = firstBallDown.x;
                    for (int j = 0; j < 10; j++) {
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                ball.position.x += xStep;
                            }
                        }, 0.03f * j);
                    }
                }
                if(!allowShoot && !gameOver && ballsDown == balls.length) {
                    speed = START_SPEED;
                    speedIncreaseTimer = 0;
                    for (int x = 0; x < map.length; x++) {
                        for (int y = 1; y < map[x].length; y++) {
                            map[x][y - 1] = map[x][y];
                            if (map[x][y] != null) {
                                map[x][y].moveDown();
                            }
                        }
                        map[x][map[x].length - 1] = null;
                    }
                    if(!checkGameOver())
                        allowShoot = true;
                }
                return;
            } else if (nextPos.y - ballDiameter >= Utils.getCoord(16)) {      // Collision top wall
                ball.movingDirection.y *= -1;
                return;
            } else if (nextPos.x + ballDiameter >= Utils.getCoord(map.length)   // Collision right wall or left wall
                    || nextPos.x <= Utils.getCoord(0)) {
                ball.movingDirection.x *= -1;
                return;
            }

            int indexX = (int) (nextPos.x / Utils.BRICK_SIZE);
            int indexY = (int) (nextPos.y / Utils.BRICK_SIZE);
            // Having 4 rects
            Rectangle ballRectTop = new Rectangle(nextPos.x + ballDiameter * .2f, nextPos.y + ballDiameter * .8f, ballDiameter * .6f, ballDiameter * .2f);
            Rectangle ballRectRight = new Rectangle(nextPos.x + ballDiameter * .8f, nextPos.y + ballDiameter * .2f, ballDiameter * .2f, ballDiameter * .6f);
            Rectangle ballRectBottom = new Rectangle(nextPos.x + ballDiameter * .2f, nextPos.y, ballDiameter * .6f, ballDiameter * .2f);
            Rectangle ballRectLeft = new Rectangle(nextPos.x, nextPos.y + ballDiameter * .2f, ballDiameter * .2f, ballDiameter * .6f);

            A:
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (indexX + x >= 0 && indexX + x < map.length && indexY + y >= 0 && indexY + y < map[indexX + x].length
                            && map[indexX + x][indexY + y] != null) {
                        Rectangle brickRect = new Rectangle(Utils.getCoord(indexX + x), Utils.getCoord(indexY + y), Utils.BRICK_SIZE, Utils.BRICK_SIZE);

                        if (ballRectTop.overlaps(brickRect)) {
                            subtractPoints(indexX + x, indexY + y);
                            changeY(ball);
                        }
                        if (ballRectRight.overlaps(brickRect)) {
                            subtractPoints(indexX + x, indexY + y);
                            changeX(ball);
                        }
                        if (ballRectBottom.overlaps(brickRect)) {
                            subtractPoints(indexX + x, indexY + y);
                            changeY(ball);
                        }
                        if (ballRectLeft.overlaps(brickRect)) {
                            subtractPoints(indexX + x, indexY + y);
                            changeX(ball);
                        }

                    }
                }
            }
            if (changedX || changedY) {
                nextPos.sub(movement);
                ball.position.x = nextPos.x;
                ball.position.y = nextPos.y;
                return;
            }
        }

    }

    private boolean checkGameOver(){
        for (int i = 0; i < map.length; i++) {
            if(map[i][0] != null) {   // Game over
                gameOver = true;
                break;
            }
        }
        return gameOver;
    }

    private void changeX(Ball ball) {
        if (!changedX) {
            changedX = true;
            ball.movingDirection.x *= -1;
        }
    }

    private void changeY(Ball ball) {
        if (!changedY) {
            changedY = true;
            ball.movingDirection.y *= -1;
        }
    }

    private void subtractPoints(int x, int y) {
        if(map[x][y] != null && collisionMap.add(map[x][y])) {
            map[x][y].points--;
            if (map[x][y].points <= 0) {
                map[x][y] = null;
            } else {
                map[x][y].flash();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
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

    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(allowShoot) {
            allowShoot = false;
            firstBallDown = null;
            final int x = Utils.calcWorldXFromScreenX(screenX, viewport);
            final int y = Utils.calcWorldYFromScreenY(screenY, viewport);
            for (int i = 0; i < balls.length; i++) {
                final Vector2 moveDir = new Vector2(x - balls[0].position.x, y - balls[0].position.y).nor().scl(speed);
                final Ball ball = balls[i];
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ball.movingDirection = new Vector2(moveDir.x, moveDir.y);
                        ballsDown--;
                    }
                }, .07f * i);
            }
            //lblMove.setText("x " + movingDirection.x + " y " + movingDirection.y);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return super.touchDragged(screenX, screenY, pointer);
    }


}