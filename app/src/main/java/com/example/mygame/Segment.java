import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Segment {

    private int x;
    private int y;
    private Paint paint;

    private static final int WIDTH = 600;
    private static final int GRID_SIZE = 20;

    Segment(int x, int y) {
        this.x = x;
        this.y = y;
        paint = new Paint();
        paint.setColor(Color.GREEN);
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    void draw(Canvas canvas) {
        int drawX = x * GRID_SIZE;
        int drawY = y * GRID_SIZE;
        canvas.drawRect(drawX, drawY, drawX + GRID_SIZE, drawY + GRID_SIZE, paint);
    }

    void clear() {
    }
}
