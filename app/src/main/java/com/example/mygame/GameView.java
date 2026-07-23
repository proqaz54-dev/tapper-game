import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends View {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int GRID_SIZE = 20;
    private static final long GAME_DURATION_MS = 30000; // 30 seconds
    
    private Paint paint;
    private Handler handler;
    private Timer timer;
    private long startTime;
    private boolean gameRunning = true;
    private int score = 0;
    private int timeRemaining = (int)(GAME_DURATION_MS / 1000);
    private ScoreTask scoreTask;
    
    public GameView(Context context) {
        super(context);
        initGame();
    }
    
    private void initGame() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(48);
        
        // Initialize timer
        startTime = System.currentTimeMillis();
        timer = new Timer();
        scoreTask = new ScoreTask();
        scoreTask.run();
        
        gameRunning = true;
        score = 0;
        timeRemaining = (int)(GAME_DURATION_MS / 1000);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Clear background
        canvas.drawColor(Color.BLACK);
        
        // Draw border
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawRect(5, 5, WIDTH - 5, HEIGHT - 5, paint);
        
        // Draw score
        paint.setColor(Color.YELLOW);
        paint.setTextSize(32);
        canvas.drawText("Score: " + score, 20, 30, paint);
        
        // Draw timer
        paint.setColor(Color.CYAN);
        paint.setTextSize(32);
        canvas.drawText("Time: " + timeRemaining + "s", WIDTH - 150, 30, paint);
        
        // Draw Tap Me button
        if (gameRunning && timeRemaining > 0) {
            float buttonWidth = 200;
            float buttonHeight = 80;
            float cx = (WIDTH - buttonWidth) / 2;
            float cy = (HEIGHT - buttonHeight) / 2;
            canvas.drawRoundRect(cx, cy, cx + buttonWidth, cy + buttonHeight, 15, 15, paint);
            
            paint.setColor(Color.BLUE);
            paint.setTextSize(24);
            canvas.drawText("TAP ME", cx + 50, cy + 45, paint);
        } 
        else if (!gameRunning) {
            // Game over screen
            paint.setColor(Color.RED);
            paint.setTextSize(48);
            canvas.drawText("GAME OVER", (WIDTH - paint.measureText("GAME OVER")) / 2, HEIGHT/2 - 30, paint);
        
            paint.setColor(Color.WHITE);
            paint.setTextSize(32);
            canvas.drawText("Final Score: " + score, (WIDTH - paint.measureText("Final Score: " + score)) / 2, HEIGHT/2 + 10, paint);
            
            paint.setColor(Color.YELLOW);
            paint.setTextSize(24);
            canvas.drawText("Tap to restart", (WIDTH - paint.measureText("Tap to restart")) / 2, HEIGHT - 50, paint);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameRunning) return true;
        
        float centerX = (WIDTH - 200) / 2;
        float centerY = (HEIGHT - 80) / 2;
        float right = centerX + 200;
        float bottom = centerY + 80;
        
        if (event.getX() > centerX && event.getX() < right && 
            event.getY() > centerY && event.getY() < bottom) {
            score++;
            if (gameListener != null) {
                gameListener.onScoreChanged(score);
            }
        }
        invalidate();
        return true;
    }
    
    public void stopGame() {
        gameRunning = false;
        if (timer != null) {
            timer.cancel();
        }
    }
    
    private class ScoreTask extends TimerTask {
        public void run() {
            while (gameRunning) {
                long elapsed = System.currentTimeMillis() - startTime;
                timeRemaining = (int)((GAME_DURATION_MS - elapsed) / 1000);
                
                if (timeRemaining <= 0) {
                    stopGame();
                    break;
                }
                
                // Update UI
                if (scoreListener != null) {
                    scoreListener.onScoreChanged(score);
                }
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
    
    public interface GameListener {
        void onScoreChanged(int score);
        void onGameOver(int score);
    }
}