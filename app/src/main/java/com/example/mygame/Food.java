import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;

class Food {

    private int x;
    private int y;
    private Paint paint;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int GRID_SIZE = 20;
    private static final int GRID_WIDTH = WIDTH / GRID_SIZE;
    private static final int GRID_HEIGHT = HEIGHT / GRID_SIZE;

    Food() {
        paint = new Paint();
        paint.setColor(Color.RED);
        respawn();
    }

    void respawn() {
        Random random = new Random();
        x = random.nextInt(GRID_WIDTH);
        y = random.nextInt(GRID_HEIGHT);
    }

    boolean checkEaten(Segment snakeHead) {
        if (snakeHead.getX() == x && snakeHead.getY() == y) {
            respawn();
            return true;
        }
        return false;
    }

    void draw(Canvas canvas) {
        int drawX = x * GRID_SIZE;
        int drawY = y * GRID_SIZE;
        canvas.drawRect(drawX, drawY, drawX + GRID_SIZE, drawY + GRID_SIZE, paint);
    }
}
