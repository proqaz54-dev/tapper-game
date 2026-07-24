package com.example.mygame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {

    static class FruitType {
        final String name;
        final int color;
        final int pointsPerTap;
        final int unlockScore;
        final int size;
        boolean unlocked;

        FruitType(String name, int color, int pointsPerTap, int unlockScore, int size) {
            this.name = name;
            this.color = color;
            this.pointsPerTap = pointsPerTap;
            this.unlockScore = unlockScore;
            this.size = size;
            this.unlocked = false;
        }
    }

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private Paint paint;
    private Random random;
    private int score;
    private List<FruitType> fruitTypes;
    private FruitType currentFruit;
    private float fruitX, fruitY;
    private String unlockMessage;
    private long unlockMessageTime;

    private GameListener gameListener;

    public interface GameListener {
        void onScoreChanged(int score);
    }

    public GameView(Context context) {
        super(context);
        initGame();
    }

    private void initGame() {
        paint = new Paint();
        paint.setAntiAlias(true);
        random = new Random();
        score = 0;
        unlockMessage = null;

        fruitTypes = new ArrayList<>();
        fruitTypes.add(new FruitType("Apple", Color.RED, 1, 0, 50));
        fruitTypes.add(new FruitType("Orange", Color.rgb(255, 165, 0), 2, 10, 55));
        fruitTypes.add(new FruitType("Lemon", Color.YELLOW, 3, 25, 48));
        fruitTypes.add(new FruitType("Grape", Color.rgb(128, 0, 128), 5, 50, 45));
        fruitTypes.add(new FruitType("Watermelon", Color.rgb(0, 180, 0), 8, 100, 65));
        fruitTypes.add(new FruitType("Cherry", Color.rgb(200, 0, 50), 12, 200, 42));
        fruitTypes.add(new FruitType("Kiwi", Color.rgb(140, 200, 80), 18, 350, 44));
        fruitTypes.add(new FruitType("Dragon Fruit", Color.rgb(255, 20, 147), 25, 500, 52));

        for (FruitType ft : fruitTypes) {
            if (ft.unlockScore == 0) {
                ft.unlocked = true;
            }
        }

        spawnFruit();
    }

    private void spawnFruit() {
        List<FruitType> unlocked = new ArrayList<>();
        for (FruitType ft : fruitTypes) {
            if (ft.unlocked) unlocked.add(ft);
        }
        if (unlocked.isEmpty()) {
            currentFruit = fruitTypes.get(0);
        } else {
            currentFruit = unlocked.get(random.nextInt(unlocked.size()));
        }
        fruitX = 20 + random.nextInt(WIDTH - 20 - currentFruit.size * 2);
        fruitY = 80 + random.nextInt(HEIGHT - 80 - currentFruit.size * 2);
    }

    private void checkUnlocks() {
        for (FruitType ft : fruitTypes) {
            if (!ft.unlocked && score >= ft.unlockScore) {
                ft.unlocked = true;
                unlockMessage = "Unlocked: " + ft.name + " (+" + ft.pointsPerTap + " pts)!";
                unlockMessageTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawRect(5, 5, WIDTH - 5, HEIGHT - 5, paint);

        paint.setColor(Color.YELLOW);
        paint.setTextSize(28);
        canvas.drawText("Score: " + score, 15, 30, paint);

        float y = HEIGHT - 12;
        for (int i = fruitTypes.size() - 1; i >= 0; i--) {
            FruitType ft = fruitTypes.get(i);
            paint.setColor(ft.unlocked ? ft.color : Color.GRAY);
            paint.setTextSize(14);
            float txtW = paint.measureText(ft.name);
            canvas.drawText(ft.name, WIDTH - txtW - 10, y, paint);
            if (!ft.unlocked) {
                paint.setTextSize(10);
                canvas.drawText("(" + ft.unlockScore + ")", WIDTH - txtW - 10, y - 12, paint);
            }
            y -= 16;
        }

        if (unlockMessage != null && System.currentTimeMillis() - unlockMessageTime < 2000) {
            paint.setColor(Color.GREEN);
            paint.setTextSize(22);
            float msgW = paint.measureText(unlockMessage);
            canvas.drawText(unlockMessage, (WIDTH - msgW) / 2, HEIGHT / 2 - 30, paint);
        }

        if (currentFruit != null) {
            paint.setColor(currentFruit.color);
            int r = currentFruit.size / 2;
            canvas.drawCircle(fruitX + r, fruitY + r, r, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(currentFruit.size / 3);
            float txtW = paint.measureText("+" + currentFruit.pointsPerTap);
            canvas.drawText("+" + currentFruit.pointsPerTap, fruitX + r - txtW / 2, fruitY + r + currentFruit.size / 6, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (currentFruit == null) return true;

            int r = currentFruit.size / 2;
            float cx = fruitX + r;
            float cy = fruitY + r;
            float dx = event.getX() - cx;
            float dy = event.getY() - cy;

            if (dx * dx + dy * dy <= r * r) {
                score += currentFruit.pointsPerTap;
                checkUnlocks();
                spawnFruit();
                if (gameListener != null) {
                    gameListener.onScoreChanged(score);
                }
                invalidate();
            }
        }
        return true;
    }

    public void setGameListener(GameListener listener) {
        gameListener = listener;
    }

    public void restartGame() {
        initGame();
        if (gameListener != null) {
            gameListener.onScoreChanged(0);
        }
        invalidate();
    }
}