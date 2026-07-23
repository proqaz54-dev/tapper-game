import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final long GAME_DURATION_MS = 30000;

    private Paint paint;
    private Handler handler;
    private long startTime;
    private boolean gameRunning;
    private boolean gameOver;
    private int score;
    private int timeRemaining;

    private GameListener gameListener;

    public interface GameListener {
        void onScoreChanged(int score);
        void onGameOver(int score);
    }

    public GameView(Context context) {
        super(context);
        initGame();
    }

    private void initGame() {
        paint = new Paint();
        handler = new Handler();
        startTime = System.currentTimeMillis();
        gameRunning = true;
        gameOver = false;
        score = 0;
        timeRemaining = (int)(GAME_DURATION_MS / 1000);
        startTimer();
    }

    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!gameRunning) return;

                long elapsed = System.currentTimeMillis() - startTime;
                timeRemaining = (int)((GAME_DURATION_MS - elapsed) / 1000);

                if (timeRemaining <= 0) {
                    gameRunning = false;
                    gameOver = true;
                    if (gameListener != null) {
                        gameListener.onGameOver(score);
                    }
                }

                invalidate();

                if (gameRunning) {
                    handler.postDelayed(this, 200);
                }
            }
        }, 200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawRect(5, 5, WIDTH - 5, HEIGHT - 5, paint);

        paint.setColor(Color.YELLOW);
        paint.setTextSize(32);
        canvas.drawText("Score: " + score, 20, 30, paint);

        paint.setColor(Color.CYAN);
        paint.setTextSize(32);
        canvas.drawText("Time: " + timeRemaining + "s", WIDTH - 150, 30, paint);

        if (!gameOver) {
            float buttonWidth = 200;
            float buttonHeight = 80;
            float cx = (WIDTH - buttonWidth) / 2;
            float cy = (HEIGHT - buttonHeight) / 2;

            paint.setColor(Color.BLUE);
            canvas.drawRoundRect(cx, cy, cx + buttonWidth, cy + buttonHeight, 15, 15, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(28);
            canvas.drawText("TAP ME", cx + 50, cy + 48, paint);
        } else {
            paint.setColor(Color.RED);
            paint.setTextSize(48);
            canvas.drawText("GAME OVER", (WIDTH - paint.measureText("GAME OVER")) / 2, HEIGHT / 2 - 30, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(32);
            canvas.drawText("Score: " + score, (WIDTH - paint.measureText("Score: " + score)) / 2, HEIGHT / 2 + 10, paint);

            paint.setColor(Color.YELLOW);
            paint.setTextSize(24);
            canvas.drawText("Tap to restart", (WIDTH - paint.measureText("Tap to restart")) / 2, HEIGHT - 50, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameOver) {
                initGame();
                return true;
            }

            if (!gameRunning) return true;

            float buttonWidth = 200;
            float buttonHeight = 80;
            float cx = (WIDTH - buttonWidth) / 2;
            float cy = (HEIGHT - buttonHeight) / 2;

            if (event.getX() > cx && event.getX() < cx + buttonWidth &&
                    event.getY() > cy && event.getY() < cy + buttonHeight) {
                score++;
                if (gameListener != null) {
                    gameListener.onScoreChanged(score);
                }
            }
            invalidate();
        }
        return true;
    }

    public void resumeGame() {
        if (gameRunning) {
            startTimer();
        }
    }

    public void pauseGame() {
        handler.removeCallbacksAndMessages(null);
    }

    public void restartGame() {
        handler.removeCallbacksAndMessages(null);
        initGame();
        if (gameListener != null) {
            gameListener.onScoreChanged(0);
        }
    }

    public void setGameListener(GameListener listener) {
        gameListener = listener;
    }
}