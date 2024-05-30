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
    private enum GameState { MENU, PLAYING, GAME_OVER }
    private GameState gameState;
    private BitmapFont menuFont;
    private int selectedMenuItem;
    private String[] menuItems;

    private boolean restartButtonClicked = false;

    private String gameOverMessage;
    private int gameOverScore;
    private String gameOverButton;

    private List<Ball> Balls;


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
    private static final int BALL_RADIUS = 7;
    private static final int BALL_SPEED = 5;

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

        bricks = new Rectangle[10][10];

    }


    @Override
    public void render() {
        switch (gameState) {
            case MENU:
                renderMenu();
                break;
            case PLAYING:
                renderGame();
                break;
            case GAME_OVER:
                renderGameOver();
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


        Map map = new Map();
        map.loadMapFromXML("map.xml");
        int[][] brickDurability = map.getBrickDurability();
        for (int i = 0; i < brickDurability.length; i++) {
            for (int j = 0; j < brickDurability[0].length; j++) {
                Brick brick = new Brick(j * (BRICK_WIDTH + BRICK_MARGIN) + BRICK_MARGIN, Gdx.graphics.getHeight() - (i + 1) * (BRICK_HEIGHT + BRICK_MARGIN), BRICK_WIDTH, BRICK_HEIGHT, brickDurability[i][j]);
                bricks[i][j] = brick;
                System.out.println("brickDurability[i][j] = " + brickDurability[i][j]);
                shapeRenderer.setColor(brick.getColor()); // Utilisation de la couleur de la brique
                shapeRenderer.rect(brick.x, brick.y, brick.width, brick.height);


            }
        }


        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
        batch.end();
    }

    private void renderGameOver() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Afficher le message de fin de partie
        font.draw(batch, "Vous avez perdu", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 50);
        font.draw(batch, "Score: " + gameOverScore, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);

        batch.end();
    }


    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (paddle.x > 0) { // Vérifie que le pad ne dépasse pas le bord gauche
                paddle.x -= 10;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (paddle.x + paddle.width < Gdx.graphics.getWidth()) { // Vérifie que le pad ne dépasse pas le bord droit
                paddle.x += 10;
            }
        }
    }

    private void update() {

        for (Ball ball : Balls) {
            ball.position.x += ball.velocity.x;
            ball.position.y += ball.velocity.y;


            // Update ball position
            ball.position.x += ball.velocity.x;
            ball.position.y += ball.velocity.y;

            // Vérifier la collision avec le paddle
            if (ball.position.y - BALL_RADIUS < paddle.y + paddle.height && ball.position.y + BALL_RADIUS > paddle.y &&
                    ball.position.x + BALL_RADIUS > paddle.x && ball.position.x - BALL_RADIUS < paddle.x + paddle.width) {

                // Calculer le point de collision relatif à la largeur du paddle
                float hitPosition = (ball.position.x - paddle.x) / paddle.width;

                // Modifier la vélocité en fonction du point de collision
                ball.velocity.x = BALL_SPEED * (hitPosition - 0.5f) * 2;  // Ball_SPEED * [-1, 1] for left to right
                ball.velocity.y *= -1; // Inverser la composante y de la vélocité

                // Augmenter légèrement la vitesse de la balle
                ball.velocity.scl(1.05f);
            }

            // Check collision with walls
            if (ball.position.x - BALL_RADIUS < 0 || ball.position.x + BALL_RADIUS > Gdx.graphics.getWidth()) {
                ball.velocity.x *= -1;
            }
            if (ball.position.y + BALL_RADIUS > Gdx.graphics.getHeight()) {
                ball.velocity.y *= -1;
            }

            // Check collision with bottom of the screen
            if (ball.position.y - BALL_RADIUS < 0 && Balls.size() == 1) {
                // Définir les informations pour la fin de partie
                gameOverMessage = "Vous avez perdu";
                gameOverScore = score;
                gameOverButton = "Relancer la partie";

                // Changer l'état du jeu en GAME_OVER
                gameState = GameState.GAME_OVER;

                // Réinitialiser les paramètres du jeu
                score = 0; // Réinitialiser le score
                ball.position.set(Gdx.graphics.getWidth() / 2, PADDLE_HEIGHT + BALL_RADIUS * 2); // Réinitialiser la position de la balle
                ball.velocity.set(BALL_SPEED, BALL_SPEED); // Réinitialiser la vitesse de la balle
                bricks = new Rectangle[BRICK_ROWS][BRICK_COLS]; // Réinitialiser les briques
            }

            // Vérifier la collision avec les briques
            for (int row = 0; row < BRICK_ROWS; row++) {
                for (int col = 0; col < BRICK_COLS; col++) {
                    if (bricks[row][col] != null) {
                        Brick brick = (Brick) bricks[row][col];
                        if (ball.position.x + BALL_RADIUS > brick.x && ball.position.x - BALL_RADIUS < brick.x + brick.width &&
                                ball.position.y + BALL_RADIUS > brick.y && ball.position.y - BALL_RADIUS < brick.y + brick.height) {

                            // Détection de la collision
                            if (ball.velocity.x > 0 && ball.position.x - BALL_RADIUS < brick.x) {
                                // Collision avec le côté gauche de la brique
                                ball.velocity.x *= -1;
                            } else if (ball.velocity.x < 0 && ball.position.x + BALL_RADIUS > brick.x + brick.width) {
                                // Collision avec le côté droit de la brique
                                ball.velocity.x *= -1;
                            } else if (ball.velocity.x < 0 && ball.position.x + BALL_RADIUS > brick.x + brick.width) {
                                // Collision avec le côté droit de la brique
                                ball.velocity.x *= -1;
                            }
                            if (ball.velocity.y > 0 && ball.position.y - BALL_RADIUS < brick.y) {
                                // Collision avec le côté supérieur de la brique
                                ball.velocity.y *= -1;
                            } else if (ball.velocity.y < 0 && ball.position.y + BALL_RADIUS > brick.y + brick.height) {
                                // Collision avec le côté inférieur de la brique
                                ball.velocity.y *= -1;
                            }
                            if (ball.velocity.y > 0 && ball.position.y - BALL_RADIUS < brick.y) {
                                // Collision avec le côté supérieur de la brique
                                ball.velocity.y *= -1;
                            } else if (ball.velocity.y < 0 && ball.position.y + BALL_RADIUS > brick.y + brick.height) {
                                // Collision avec le côté inférieur de la brique
                                ball.velocity.y *= -1;
                            }

                            // Réduction de la durabilité de la brique et mise à jour du score
                            brick.reduceDurability();
                            if (brick.getDurability() <= 0) {
                                bricks[row][col] = null;
                                score += 10;
                            }

                            // Sortir de la boucle pour éviter les collisions multiples avec la même brique
                            break;
                        }
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