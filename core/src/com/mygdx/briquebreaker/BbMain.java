package com.mygdx.briquebreaker;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
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
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import java.util.Iterator;

import static com.badlogic.gdx.math.MathUtils.random;
import java.security.PublicKey;


public class BbMain extends ApplicationAdapter {

    // Déclarations de variables globales
    private enum GameState {MENU, PLAYING, GAME_OVER, RULES, PARAM, HIGH_SCORES,MODE}

    private long startTime;
    private BitmapFont timeFont;

    private Texture ballTexture;
    private Texture paddleTexture;
    private Texture backgroundTexture;

    private Texture brickTexture;

    private GameState gameState;
    private BitmapFont menuFont;
    private BitmapFont drawMessage;
    private int selectedMenuItem;
    private String[] menuItems;

    private boolean restartButtonClicked = false;

    private String gameOverMessage;
    private int gameOverScore;
    private String gameOverButton;

    private List<Ball> Balls;

    private String[] menuHighScores;
    private String[] menuMode;
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


    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICK_MARGIN = 5;


    private static final int MAP_WIDTH = 12;
    private static final int MAP_HEIGHT = 12;

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

    private Texture menuBackgroundTexture;

    @Override
    public void create() {
        menuBackgroundTexture = new Texture(Gdx.files.internal("wallpaper.jpg"));

        // Initialisation des textures
        ballTexture = new Texture(Gdx.files.internal("balle.png"));
        paddleTexture = new Texture(Gdx.files.internal("paddle.png"));
        backgroundTexture = new Texture(Gdx.files.internal("wallpaper.jpg"));
        brickTexture = new Texture(Gdx.files.internal("brick.png"));

        // Initialisation des variables de menu
        gameState = GameState.MENU;
        menuFont = new BitmapFont();
        menuFont.setColor(Color.WHITE);
        drawMessage = new BitmapFont();
        drawMessage.setColor(Color.WHITE);
        selectedMenuItem = 0;
        menuItems = new String[]{"Start Game", "Param", "Rules","Mode", "High Scores", "Exit"};
        menuRules = new String[]{"Start Game", "Return Last Menu", "Exit"};
        menuParam = new String[]{"Music Sound", "Sound Effect", "Paddle size", "Ball Speed", "Main menu"};
        menuMode = new String[]{"Level 1", "Level 2", "Level 3", "Main menu"};
        menuHighScores = new String[]{"Return to Menu"};
        Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        Balls = new ArrayList<Ball>();

        paddle = new Paddle((Gdx.graphics.getWidth() - PADDLE_WIDTH) / 2, 20, PADDLE_WIDTH, PADDLE_HEIGHT);
        Balls.add(new Ball(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, BALL_SPEED, BALL_SPEED));

        bricks = new Rectangle[MAP_HEIGHT][MAP_WIDTH];



        timeFont = new BitmapFont();
        timeFont.setColor(Color.WHITE);
    }



    private void initmap(String filename) {
        Map map = new Map();
        map.loadMapFromXML(filename);
        map.printMap();
        int[][] brickDurability = map.getBrickDurability();
        for (int i = 0; i < brickDurability.length; i++) {
            for (int j = 0; j < brickDurability[i].length; j++) {
                if (brickDurability[i][j] == 0) {
                    continue;
                }
                Brick brick = new Brick(j * (BRICK_WIDTH + BRICK_MARGIN) + BRICK_MARGIN, Gdx.graphics.getHeight() - (i + 1) * (BRICK_HEIGHT + BRICK_MARGIN), BRICK_WIDTH, BRICK_HEIGHT, brickDurability[i][j]);
                bricks[i][j] = brick;
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
                if (startTime == 0) {
                    startTime = TimeUtils.nanoTime();
                }
                renderGame();
                break;
            case GAME_OVER:
                renderGameOver();
                break;
            case RULES:
                renderRules();
                break;
            case PARAM:
                renderParam();
                break;
            case HIGH_SCORES:
                // Ajouter l'appel à renderMode() lorsque le jeu est en mode
                renderMode();
                break;
            case MODE:
                renderMode();
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


    public void renderMode (){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < menuMode.length; i++) {
            if (i == selectedMenuItem) {
                menuFont.setColor(Color.RED);
            } else {
                menuFont.setColor(Color.WHITE);
            }
            menuFont.draw(batch, menuMode[i], 100, 400 - i * 50);
        }
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedMenuItem = Math.max(0, selectedMenuItem - 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedMenuItem = Math.min(menuMode.length - 1, selectedMenuItem + 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selectedMenuItem) {
                case 0:
                    initmap("map.xml");
                    gameState = GameState.PLAYING;
                    break;
                case 1:
                    initmap("map2.xml");
                    gameState = GameState.PLAYING;
                    break;
                case 2:
                    initmap("map3.xml");
                    gameState = GameState.PLAYING;
                    break;
                case 3:
                    this.selectedMenuItem = 0;
                    gameState = GameState.MENU;
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Dessiner le fond d'écran du menu
        batch.draw(menuBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Dessiner les éléments du menu
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
                    gameState = GameState.HIGH_SCORES;
                    break;
                case 4:
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

        // Temps écoulé
        long elapsedTime = (TimeUtils.nanoTime() - startTime) / 1000000000;
        int minutes = (int) (elapsedTime / 60);
        int seconds = (int) (elapsedTime % 60);

        // Dessiner le fond d'écran
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        batch.begin();

        // Dessiner le paddle
        batch.draw(paddleTexture, paddle.x, paddle.y, paddle.width, paddle.height);

        // Dessiner la balle
        for (Ball ball : Balls) {
            batch.draw(ballTexture, ball.position.x - BALL_RADIUS, ball.position.y - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        }

        // Dessiner les briques
        for (int row = 0; row < MAP_HEIGHT; row++) {
            for (int col = 0; col < MAP_WIDTH; col++) {
                if (bricks[row][col] != null) {
                    Brick brick = (Brick) bricks[row][col];
                    // Utiliser une couleur différente en fonction de la durabilité
                    batch.setColor(brick.getColor());
                    batch.draw(brickTexture, brick.x, brick.y, brick.width, brick.height);
                }
            }
        }

        batch.setColor(Color.WHITE); // Réinitialiser la couleur après avoir dessiné les briques colorées

        // Afficher le score
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
        timeFont.draw(batch, String.format("Time: %02d:%02d", minutes, seconds), 20, Gdx.graphics.getHeight() - 40);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gameState = GameState.MENU;
            startTime = 0; // Réinitialiser le temps
        }
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

                // faire un render mode lorsque le bouton est cliqué
                if (restartButtonClicked) {
                    gameState = GameState.MODE;
                    restartButtonClicked = false;
                }


            }

            // Vérifier la collision avec les briques
            for (int row = 0; row < MAP_HEIGHT; row++) {
                for (int col = 0; col < MAP_WIDTH; col++) {
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
                            System.out.println("brick.getDurability() = " + brick.getDurability());
                            brick.reduceDurability();
                            System.out.println("brick.getDurability() = " + brick.getDurability());
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
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        backgroundMusic.stop();
        backgroundMusic.dispose();
        collisionSound.dispose();

        // Libérer les textures
        ballTexture.dispose();
        paddleTexture.dispose();
        backgroundTexture.dispose();
        brickTexture.dispose();
    }
}