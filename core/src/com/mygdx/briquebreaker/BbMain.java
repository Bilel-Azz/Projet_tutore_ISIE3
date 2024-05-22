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
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.security.PublicKey;


public class BbMain extends ApplicationAdapter {

    // Déclarations de variables globales
    private enum GameState {MENU, PLAYING, RULES, PARAM}

    private GameState gameState;
    private BitmapFont menuFont;
    private BitmapFont drawMessage;
    private int selectedMenuItem;
    private String[] menuItems;

    private String[] menuParam;
    private String[] menuRules;
    private String DrawRules = "Le joueur déplace la raquette de droite à gauche pour empêcher la balle de tomber dans la zone en dessous";
    private String DrawRules1 = "A chaque fois que la balle touche une brique, elle disparaît et le score augmente de 1 point.";
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Rectangle paddle;
    private Vector2 ballPosition;
    private Vector2 ballVelocity;
    private BitmapFont font;
    private int score;
    public int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 20;
    private static final int BALL_RADIUS = 7;
    public int BALL_SPEED = 5;

    private int largepaddle = 200;

    private int mediumPaddle = 100;
    private int smallPaddle = 50;
    private int fastSpeedBall = 7;

    private static int mediumSpeedBall = 5;

    private int slowSpeedBall = 3;
    public int DeplacementSpeedBall = 1;
    private static final int BRICK_ROWS = 5;
    private static final int BRICK_COLS = 10;
    public int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICK_MARGIN = 5;
    private Rectangle[][] bricks;
    private Stage stage;
    private Skin skinButton;
    private TextureAtlas buttonAtlas;
    private TextButton.TextButtonStyle textButtonStyle;
    private TextButton button;
    public boolean soundEnabled = true;
    public boolean soundEffect = true;
    private String paddleSize = "Medium";
    private String ballSpeed = "Medium";
    Music backgroundMusic;
    Sound collisionSound;

    @Override
    public void create() {
        // Initialisation des variables de menu
        gameState = GameState.MENU;
        menuFont = new BitmapFont();
        menuFont.setColor(Color.WHITE);
        drawMessage = new BitmapFont();
        drawMessage.setColor(Color.WHITE);
        selectedMenuItem = 0;
        menuItems = new String[]{"Start Game", "Param", "Rules", "Exit"};
        menuRules = new String[]{"Start Game", "Return Last Menu", "Exit"};
        menuParam = new String[]{"Music Sound","Sound Effect", "Paddle size", "Ball Speed", "Main menu"};
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        paddle = new Paddle((Gdx.graphics.getWidth() - PADDLE_WIDTH) / 2, 20, PADDLE_WIDTH, PADDLE_HEIGHT);
        ballPosition = new Vector2(Gdx.graphics.getWidth() / 2, PADDLE_HEIGHT + BALL_RADIUS * 2);
        ballVelocity = new Vector2(BALL_SPEED, BALL_SPEED); // ballVelocity = new Vector2(BALL_SPEED, BALL_SPEED);
        bricks = new Rectangle[BRICK_ROWS][BRICK_COLS];
        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                bricks[row][col] = new Brick(col * (BRICK_WIDTH + BRICK_MARGIN) + BRICK_MARGIN,
                        Gdx.graphics.getHeight() - (row + 1) * (BRICK_HEIGHT + BRICK_MARGIN),
                        BRICK_WIDTH, BRICK_HEIGHT, 3);
            }
        }
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        // Configurer la musique pour qu'elle boucle en continu
        backgroundMusic.setLooping(true);

        // Démarrer la lecture de la musique
        backgroundMusic.play();
        // Charger le bruitage de collision
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("bruitage.mp3"));
    }
    @Override
    public void render() {
        // Gestion de l'affichage en fonction de l'état du jeu
        switch (gameState) {
            case MENU:
                renderMenu();
                break;
            case PLAYING:
                //CreateComponent();
                renderGame();
                break;
            case RULES:
                renderRules();
                break;
            case PARAM:
                renderParam();
                break;
        }
    }

    public void renderParam(){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < menuParam.length; i++) {

            if (i == selectedMenuItem) {
                menuFont.setColor(Color.RED);
            } else {
                menuFont.setColor(Color.WHITE);
            }
            menuFont.draw(batch, menuParam[i], 100, 400 - i * 50);
        }
        //System.out.println(soundEnabled);
        String soundStatus = soundEnabled ? "Enabled" : "Disabled";
        String soundEffectStatus = soundEffect ? "Enabled" : "Disabled";
        drawMessage.draw(batch, "" + soundStatus, 250, 400);
        drawMessage.draw(batch, "" + soundEffectStatus, 250, 350);
        drawMessage.draw(batch, "" + paddleSize, 250, 300);
        drawMessage.draw(batch, "" + ballSpeed, 250, 250);
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedMenuItem = Math.max(0, selectedMenuItem - 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedMenuItem = Math.min(menuParam.length - 1, selectedMenuItem + 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedMenuItem==4) {
                this.selectedMenuItem = 0;
                gameState = GameState.MENU;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
            switch (selectedMenuItem) {
                case 0:
                    soundEnabled = !soundEnabled;
                    Setmusic(soundEnabled);
                    break;
                case 1:
                    soundEffect = !soundEffect;
                    SetSoundEffect();
                    break;
                case 2:
                    // Change paddle size
                    switch (paddleSize) {
                        case "Small": // créer méthode set paddleSize
                            paddleSize = "Medium";
                            SetPaddlesize(mediumPaddle);
                            break;
                        case "Medium":
                            paddleSize = "Large";
                            SetPaddlesize(largepaddle);
                            break;
                        case "Large":
                            paddleSize = "Small";
                            SetPaddlesize(smallPaddle);
                            break;
                    }
                    break;
                case 3:
                    // Change ball speed
                    switch (ballSpeed) {
                        case "Slow": // créer méthode set ball speed
                            ballSpeed = "Medium";
                            SetBallSpeed(mediumSpeedBall);
                            //this.BALL_SPEED = 5;
                            //DeplacementSpeedBall =1;
                            break;
                        case "Medium":
                            ballSpeed = "Fast";
                            SetBallSpeed(fastSpeedBall);
                            //this.BALL_SPEED = 5;
                            //DeplacementSpeedBall = 2;
                            break;
                        case "Fast":
                            ballSpeed = "Slow";
                            SetBallSpeed(slowSpeedBall);
                            //this.BALL_SPEED = 2;
                            //this.DeplacementSpeedBall = 1;
                            break;
                    }
                    break;
                case 4:
                    break;
            }
        }
    }
    public void SetSoundEffect(){
        if (soundEffect == false){
            collisionSound.stop();
        }else {
            collisionSound.play();
        }
    }

    public void Setmusic(boolean soundActivation){
        if (soundEnabled == false){// Arreter la lecture de la musique
            backgroundMusic.stop();
        }
        else{// Démarrer la lecture de la musique
            backgroundMusic.play();
        }
    }
    public void SetBallSpeed(int ballspeed){
        this.BALL_SPEED = ballspeed;
        ballVelocity = new Vector2(BALL_SPEED, BALL_SPEED);
    }
    public void SetPaddlesize(int paddleSize){
        this.PADDLE_WIDTH = paddleSize;
        paddle = new Paddle((Gdx.graphics.getWidth() - PADDLE_WIDTH) / 2, 20, PADDLE_WIDTH, PADDLE_HEIGHT);
    }

    public void playCollisionSound() {
        collisionSound.play();
    }

    private void renderRules() {
        // Affichage des Regles
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < menuRules.length; i++) {
            if (i == selectedMenuItem) {
                menuFont.setColor(Color.RED);
            } else {
                menuFont.setColor(Color.WHITE);
            }
            menuFont.draw(batch, menuRules[i], 100, 200 - i * 50);
        }
        drawMessage.draw(batch, DrawRules, 10, 470);
        drawMessage.draw(batch, DrawRules1, 10, 450);
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedMenuItem = Math.max(0, selectedMenuItem - 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedMenuItem = Math.min(menuRules.length - 1, selectedMenuItem + 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedMenuItem) {
                case 0:
                    gameState = GameState.PLAYING;
                    break;
                case 1:
                    this.selectedMenuItem = 0;
                    gameState = GameState.MENU;
                    break;
                case 2:
                    Gdx.app.exit();
                    break;
            }
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
                    this.selectedMenuItem = 0;
                    gameState = GameState.PARAM;
                    break;

                case 2:
                    gameState = GameState.RULES;
                    break;
                case 3:
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

    private void update() {
        // Update ball position
        ballPosition.x += ballVelocity.x;
        ballPosition.y += ballVelocity.y;
        // Check collision with paddle
        if (ballPosition.y - BALL_RADIUS < paddle.y + paddle.height && ballPosition.x > paddle.x && ballPosition.x < paddle.x + paddle.width) {
            ballVelocity.y *= -DeplacementSpeedBall;
        }
        // Check collision with walls
        if (ballPosition.x < 0 || ballPosition.x > Gdx.graphics.getWidth()) {
            ballVelocity.x *= -DeplacementSpeedBall;
        }
        if (ballPosition.y < 0 || ballPosition.y > Gdx.graphics.getHeight()) {
            ballVelocity.y *= -DeplacementSpeedBall;
        }
        // Check collision with bricks
        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                if (bricks[row][col] != null) {
                    Brick brick = (Brick) bricks[row][col];
                    if (ballPosition.x < brick.x + brick.width &&
                            ballPosition.x + 2 * BALL_RADIUS > brick.x &&
                            ballPosition.y < brick.y + brick.height &&
                            ballPosition.y + 2 * BALL_RADIUS > brick.y) {
                        playCollisionSound();
                        // Determine side of collision
                        float ballCenterX = ballPosition.x + BALL_RADIUS;
                        float ballCenterY = ballPosition.y + BALL_RADIUS;
                        boolean isHorizontalCollision = Math.abs(ballCenterY - (brick.y + BRICK_HEIGHT / 2)) < BALL_RADIUS + BRICK_HEIGHT / 2;
                        //boolean isVerticalCollision = Math.abs(ballCenterX - (brick.x + BRICK_WIDTH / 2)) < BALL_RADIUS + BRICK_WIDTH / 2;
                        if (isHorizontalCollision) {
                            // Reverse vertical velocity
                            ballVelocity.y *= -1;
                        }
                        //if (isVerticalCollision) {
                        // Reverse horizontal velocity
                        //ballVelocity.x *= -1;
                        //}

                        // Reduce durability
                        brick.reduceDurability();
                        if (brick.getDurability() <= 0) {
                            bricks[row][col] = null; // Remove brick if durability is zero
                            score += 10;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        backgroundMusic.stop();
        backgroundMusic.dispose();
        collisionSound.dispose();
    }
}