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

import java.util.ArrayList;
import java.util.List;

import java.util.Iterator;

import static com.badlogic.gdx.math.MathUtils.random;



public class BbMain extends ApplicationAdapter {

    // Déclarations de variables globales
    private enum GameState { MENU, PLAYING }
    private GameState gameState;
    private BitmapFont menuFont;
    private int selectedMenuItem;
    private String[] menuItems;


    private List<Ball> Balls;



    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Rectangle paddle;
  
    private BitmapFont font;
    private int score;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 20;
    private static final int BALL_RADIUS = 7;
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

        Balls = new ArrayList<Ball>();

        paddle = new Paddle((Gdx.graphics.getWidth() - PADDLE_WIDTH) / 2, 20, PADDLE_WIDTH, PADDLE_HEIGHT);

        Balls.add(new Ball(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, BALL_SPEED, BALL_SPEED));
        Balls.add(new Ball(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, BALL_SPEED, BALL_SPEED));



        bricks = new Rectangle[BRICK_ROWS][BRICK_COLS];
        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                bricks[row][col] = new Brick(col * (BRICK_WIDTH + BRICK_MARGIN) + BRICK_MARGIN,
                        Gdx.graphics.getHeight() - (row + 1) * (BRICK_HEIGHT + BRICK_MARGIN),
                        BRICK_WIDTH, BRICK_HEIGHT,1);
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
        for (Ball ball : Balls) {
            shapeRenderer.circle(ball.position.x, ball.position.y, BALL_RADIUS);
        }

        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                if (bricks[row][col] != null) {
                    Brick brick = (Brick) bricks[row][col];
                    shapeRenderer.setColor(brick.getColor()); // Utilisation de la couleur de la brique
                    shapeRenderer.rect(brick.x, brick.y, brick.width, brick.height);
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

    public void newBall() {
        Balls.add(new Ball(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, BALL_SPEED, BALL_SPEED));
    }

    public void newBall(float x, float y) {
        Balls.add(new Ball(x, y, BALL_SPEED, BALL_SPEED));
    }

    private void update() {
        Iterator<Ball> iterator = Balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();

            // Update ball position
            ball.position.x += ball.velocity.x;
            ball.position.y += ball.velocity.y;

            // Check collision with paddle
            if (ball.position.y - BALL_RADIUS < paddle.y + paddle.height && ball.position.x > paddle.x && ball.position.x < paddle.x + paddle.width) {
                ball.velocity.y *= -1;
            }

            // Check collision with walls
            if (ball.position.x < 0 || ball.position.x > Gdx.graphics.getWidth()) {
                ball.velocity.x *= -1;
            }
            if (ball.position.y < 0 || ball.position.y > Gdx.graphics.getHeight()) {
                ball.velocity.y *= -1;
            }

            // Check collision with bricks
            for (int row = 0; row < BRICK_ROWS; row++) {
                for (int col = 0; col < BRICK_COLS; col++) {
                    if (bricks[row][col] != null) {
                        Brick brick = (Brick) bricks[row][col];
                        if (ball.position.x < brick.x + brick.width &&
                                ball.position.x + 2 * BALL_RADIUS > brick.x &&
                                ball.position.y < brick.y + brick.height &&
                                ball.position.y + 2 * BALL_RADIUS > brick.y) {

                            // Determine side of collision
                            float ballCenterX = ball.position.x + BALL_RADIUS;
                            float ballCenterY = ball.position.y + BALL_RADIUS;
                            boolean isHorizontalCollision = Math.abs(ballCenterY - (brick.y + BRICK_HEIGHT / 2)) < BALL_RADIUS + BRICK_HEIGHT / 2;
                            boolean isVerticalCollision = Math.abs(ballCenterX - (brick.x + BRICK_WIDTH / 2)) < BALL_RADIUS + BRICK_WIDTH / 2;

                            if (isHorizontalCollision) {
                                // Reverse vertical velocity
                                ball.velocity.y *= -1;
                            }
                            if (isVerticalCollision) {
                                // Reverse horizontal velocity
                                ball.velocity.x *= -1;
                            }

                            // Reduce durability
                            brick.reduceDurability();
                            if (brick.getDurability() <= 0) {
                                bricks[row][col] = null; // Remove brick if durability is zero
                                score += 10;
                            }

                            // Gestion des bonus
                            if (brick.getBonus() == 1 && brick.getDurability() <= 0) {
                                // Ajout d'un bonus
                                // Exemple : augmentation de la taille de la raquette
                                switch (random.nextInt(5)) {
                                    case 1:
                                        paddle.width += 20;
                                        break;
                                    case 2:
                                        paddle.width -= 20;
                                        break;
                                    case 3:
                                        ball.velocity.x *= 1.5;
                                        ball.velocity.y *= 1.5;
                                        break;
                                    case 4:
                                        ball.velocity.x *= 0.5;
                                        ball.velocity.y *= 0.5;
                                        break;
                                    //case 5:
                                        // Bonus pour créer une balle supplémentaire
                                        //newBall(ball.position.x+1, ball.position.y-1);
                                       // break;
                                }
                            }
                        }
                    }
                }
            }

            // Remove ball if it's out of bounds
            if (ball.position.y < -BALL_RADIUS || ball.position.y > Gdx.graphics.getHeight() + BALL_RADIUS) {
                iterator.remove();
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