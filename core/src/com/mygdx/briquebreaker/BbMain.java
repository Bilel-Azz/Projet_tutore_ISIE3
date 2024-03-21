import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class BbMain extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Paddle paddle;
    private Ball ball;
    private Array<Brick> bricks;

    @Override
    public void create () {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        paddle = new Paddle();
        ball = new Ball();
        
        // Create bricks
        bricks = new Array<>();
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                Brick brick = new Brick(col * 80 + 10, 450 - row * 20);
                bricks.add(brick);
            }
        }
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        paddle.render(batch);
        ball.render(batch);
        for (Brick brick : bricks) {
            brick.render(shapeRenderer);
        }
        batch.end();
        
        paddle.update();
        ball.update();

        // Check collision with paddle
        if (ball.getBounds().overlaps(paddle.getBounds())) {
            ball.bounceOffPaddle(paddle);
        }

        // Check collision with bricks
        for (Brick brick : bricks) {
            if (ball.getBounds().overlaps(brick.getBounds())) {
                ball.bounceOffBrick(brick);
                bricks.removeValue(brick, true);
                break;
            }
        }
    }

    @Override
    public void dispose () {
        batch.dispose();
        shapeRenderer.dispose();
    }
}

