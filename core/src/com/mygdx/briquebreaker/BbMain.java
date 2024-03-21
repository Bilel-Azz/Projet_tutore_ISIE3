package com.mygdx.briquebreaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BbMain extends ApplicationAdapter {

    // Déclarations de variables globales
    private enum GameState { MENU, PLAYING }
    private GameState gameState;
    private BitmapFont menuFont;
    private int selectedMenuItem;
    private String[] menuItems;


    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Rectangle paddle;
    private Vector2 ballPosition;
    private Vector2 ballVelocity;
    private BitmapFont font;
    private int score;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 20;
    private static final int BALL_RADIUS = 10;
    private static final int BALL_SPEED = 5;
    private static final int BRICK_ROWS = 5;
    private static final int BRICK_COLS = 10;
    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICK_MARGIN = 5;
    private Rectangle[][] bricks;


    @Override
    public void create () {

        // Initialisation des variables de menu
        gameState = GameState.MENU;
        menuFont = new BitmapFont();
        menuFont.setColor(Color.WHITE);
        selectedMenuItem = 0;
        menuItems = new String[] { "Start Game", "Exit" };
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        paddle = new Rectangle((Gdx.graphics.getWidth() - PADDLE_WIDTH) / 2, 20, PADDLE_WIDTH, PADDLE_HEIGHT);
        ballPosition = new Vector2(Gdx.graphics.getWidth() / 2, PADDLE_HEIGHT + BALL_RADIUS * 2);
        ballVelocity = new Vector2(BALL_SPEED, BALL_SPEED);

        bricks = new Rectangle[BRICK_ROWS][BRICK_COLS];
        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                bricks[row][col] = new Rectangle(col * (BRICK_WIDTH + BRICK_MARGIN) + BRICK_MARGIN,
                        Gdx.graphics.getHeight() - (row + 1) * (BRICK_HEIGHT + BRICK_MARGIN),
                        BRICK_WIDTH, BRICK_HEIGHT);
            }
        }
    }

    @Override
    public void render () {

        // Gestion de l'affichage en fonction de l'état du jeu
        switch (gameState) {
            case MENU:
                renderMenu();
                break;
            case PLAYING:
                renderGame();
                break;
        }
    }
    private void renderMenu() {
        // Affichage du menu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        for (int i = 0; i < menuItems.length; i++) {
            if (i == selectedMenuItem) {
                menuFont.setColor(Color.RED);
            } else {
                menuFont.setColor(Color.WHITE);
            }
            menuFont.draw(batch, menuItems[i], 100, 400 - i * 50);
        }

        batch.end();

        // Gestion des entrées du clavier pour naviguer dans le menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedMenuItem = Math.max(0, selectedMenuItem - 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedMenuItem = Math.min(menuItems.length - 1, selectedMenuItem + 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedMenuItem) {
                case 0:
                    gameState = GameState.PLAYING;
                    break;
                case 1:
                    Gdx.app.exit();
                    break;
            }
        }
    }

    private void renderGame() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        update();
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(paddle.x, paddle.y, paddle.width, paddle.height);
        shapeRenderer.circle(ballPosition.x, ballPosition.y, BALL_RADIUS);

        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                if (bricks[row][col] != null) {
                    shapeRenderer.rect(bricks[row][col].x, bricks[row][col].y, bricks[row][col].width, bricks[row][col].height);
                }
            }
        }

        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            paddle.x -= 10;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            paddle.x += 10;
        }
    }

    private void update() {
        // Update ball position
        ballPosition.x += ballVelocity.x;
        ballPosition.y += ballVelocity.y;

        // Check collision with paddle
        if (ballPosition.y - BALL_RADIUS < paddle.y + paddle.height && ballPosition.x > paddle.x && ballPosition.x < paddle.x + paddle.width) {
            ballVelocity.y *= -1;
        }

        // Check collision with walls
        if (ballPosition.x < 0 || ballPosition.x > Gdx.graphics.getWidth()) {
            ballVelocity.x *= -1;
        }
        if (ballPosition.y < 0 || ballPosition.y > Gdx.graphics.getHeight()) {
            ballVelocity.y *= -1;
        }

        // Check collision with bricks
        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                if (bricks[row][col] != null) {
                    Rectangle brick = bricks[row][col];
                    if (ballPosition.x < brick.x + brick.width &&
                            ballPosition.x + 2 * BALL_RADIUS > brick.x &&
                            ballPosition.y < brick.y + brick.height &&
                            ballPosition.y + 2 * BALL_RADIUS > brick.y) {
                        bricks[row][col] = null;
                        score += 10;
                        ballVelocity.y *= -1;
                    }
                }
            }
        }
    }

    @Override
    public void dispose () {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}