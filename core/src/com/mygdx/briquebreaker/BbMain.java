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

public class BrickBreakerGame extends ApplicationAdapter {
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
                    if (ballPosition.overlaps(bricks[row][col])) {
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