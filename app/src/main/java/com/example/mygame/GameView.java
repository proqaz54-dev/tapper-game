package com.example.mygame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final long STREAK_TIMEOUT = 1500;

    static class FruitType {
        String name;
        int color;
        int secondaryColor;
        int pointsPerTap;
        int unlockScore;
        float size;
        boolean unlocked;

        FruitType(String name, int color, int secondaryColor, int points, int unlock, float size) {
            this.name = name;
            this.color = color;
            this.secondaryColor = secondaryColor;
            this.pointsPerTap = points;
            this.unlockScore = unlock;
            this.size = size;
        }
    }

    static class FloatingText {
        float x, y;
        String text;
        int color;
        long startTime;

        FloatingText(float x, float y, String text, int color) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
            startTime = System.currentTimeMillis();
        }
    }

    static class ActiveFruit {
        FruitType type;
        float x, y;
        float dx, dy;
        float targetX, targetY;
        int bobPhase;

        ActiveFruit(FruitType type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
            Random r = new Random();
            dx = (r.nextFloat() - 0.5f) * 0.6f;
            dy = (r.nextFloat() - 0.5f) * 0.6f;
            bobPhase = r.nextInt(360);
            targetX = x;
            targetY = y;
        }
    }

    private Paint paint, paintFill;
    private Random random;
    private Handler handler;
    private int score;
    private int combo;
    private long lastTapTime;
    private int bestCombo;
    private List<FruitType> fruitTypes;
    private List<ActiveFruit> activeFruits;
    private List<FloatingText> floatingTexts;
    private String unlockMessage;
    private long unlockMessageTime;
    private boolean running;

    private GameListener gameListener;

    public interface GameListener {
        void onScoreChanged(int score);
    }

    public GameView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        random = new Random();
        handler = new Handler();
        initGame();
    }

    private void initGame() {
        score = 0;
        combo = 0;
        bestCombo = 0;
        lastTapTime = 0;
        unlockMessage = null;
        floatingTexts = new ArrayList<>();
        activeFruits = new ArrayList<>();
        running = true;

        fruitTypes = new ArrayList<>();
        fruitTypes.add(new FruitType("Apple", Color.RED, Color.rgb(180, 0, 0), 1, 0, 44));
        fruitTypes.add(new FruitType("Orange", Color.rgb(255, 165, 0), Color.rgb(200, 130, 0), 2, 10, 42));
        fruitTypes.add(new FruitType("Lemon", Color.YELLOW, Color.rgb(200, 200, 0), 3, 25, 38));
        fruitTypes.add(new FruitType("Grape", Color.rgb(128, 0, 128), Color.rgb(80, 0, 80), 5, 50, 40));
        fruitTypes.add(new FruitType("Watermelon", Color.rgb(0, 180, 0), Color.rgb(0, 120, 0), 8, 100, 50));
        fruitTypes.add(new FruitType("Cherry", Color.rgb(200, 0, 50), Color.rgb(150, 0, 30), 12, 200, 36));
        fruitTypes.add(new FruitType("Kiwi", Color.rgb(140, 200, 80), Color.rgb(100, 160, 40), 18, 350, 40));
        fruitTypes.add(new FruitType("Dragon Fruit", Color.rgb(255, 20, 147), Color.rgb(200, 0, 100), 25, 500, 44));

        for (FruitType ft : fruitTypes) {
            if (ft.unlockScore == 0) ft.unlocked = true;
        }

        spawnFruit();
        startGameLoop();
    }

    private void startGameLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!running) return;
                updateFruits();
                invalidate();
                handler.postDelayed(this, 30);
            }
        }, 30);
    }

    private void updateFruits() {
        for (ActiveFruit af : activeFruits) {
            af.x += af.dx;
            af.y += af.dy;
            af.bobPhase = (af.bobPhase + 3) % 360;

            float r = af.type.size / 2;
            if (af.x < 10) { af.x = 10; af.dx = Math.abs(af.dx); }
            if (af.x > WIDTH - r * 2 - 10) { af.x = WIDTH - r * 2 - 10; af.dx = -Math.abs(af.dx); }
            if (af.y < 75) { af.y = 75; af.dy = Math.abs(af.dy); }
            if (af.y > HEIGHT - r * 2 - 25) { af.y = HEIGHT - r * 2 - 25; af.dy = -Math.abs(af.dy); }
        }
    }

    private FruitType getRandomUnlocked() {
        List<FruitType> unlocked = new ArrayList<>();
        for (FruitType ft : fruitTypes) {
            if (ft.unlocked) unlocked.add(ft);
        }
        return unlocked.get(random.nextInt(unlocked.size()));
    }

    private void spawnFruit() {
        FruitType ft = getRandomUnlocked();
        float r = ft.size / 2;
        float x = 20 + random.nextInt(WIDTH - (int)ft.size - 40);
        float y = 80 + random.nextInt(HEIGHT - (int)ft.size - 60);
        activeFruits.add(new ActiveFruit(ft, x, y));
    }

    private void checkUnlocks() {
        for (FruitType ft : fruitTypes) {
            if (!ft.unlocked && score >= ft.unlockScore) {
                ft.unlocked = true;
                unlockMessage = "NEW: " + ft.name + " (+" + ft.pointsPerTap + ")!";
                unlockMessageTime = System.currentTimeMillis();
            }
        }
    }

    private void drawFruit(Canvas c, ActiveFruit af) {
        FruitType ft = af.type;
        float x = af.x, y = af.y;
        float r = ft.size / 2;
        float bobY = (float) Math.sin(af.bobPhase * Math.PI / 180) * 3;

        paintFill.setColor(ft.color);
        paint.setColor(ft.secondaryColor);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        String name = ft.name;
        if (name.equals("Apple")) {
            c.drawCircle(x + r, y + r + bobY, r, paintFill);
            c.drawCircle(x + r, y + r + bobY, r, paint);
            paint.setColor(Color.rgb(80, 180, 80));
            paint.setStrokeWidth(3);
            c.drawLine(x + r, y + bobY, x + r + 8, y - 10 + bobY, paint);
            paintFill.setColor(Color.rgb(80, 180, 80));
            c.drawCircle(x + r + 8, y - 12 + bobY, 5, paintFill);
        } else if (name.equals("Orange")) {
            c.drawCircle(x + r, y + r + bobY, r, paintFill);
            c.drawCircle(x + r, y + r + bobY, r, paint);
            paint.setStrokeWidth(1);
            paint.setColor(Color.rgb(220, 150, 50));
            for (int i = 0; i < 8; i++) {
                float a = (float) (i * Math.PI / 4);
                c.drawLine(x + r + (float) Math.cos(a) * r * 0.3f, y + r + bobY + (float) Math.sin(a) * r * 0.3f,
                        x + r + (float) Math.cos(a) * r * 0.7f, y + r + bobY + (float) Math.sin(a) * r * 0.7f, paint);
            }
        } else if (name.equals("Lemon")) {
            RectF oval = new RectF(x + r - r * 0.7f, y + bobY, x + r + r * 0.7f, y + r * 2 + bobY);
            c.drawOval(oval, paintFill);
            c.drawOval(oval, paint);
            paint.setColor(Color.rgb(180, 180, 0));
            paint.setStrokeWidth(3);
            c.drawLine(x + r, y + bobY, x + r, y - 8 + bobY, paint);
        } else if (name.equals("Grape")) {
            float gr = r * 0.4f;
            float[][] pos = {{0, 0}, {-gr * 1.7f, gr * 0.5f}, {gr * 1.7f, gr * 0.5f},
                    {-gr * 0.9f, gr * 1.8f}, {gr * 0.9f, gr * 1.8f}, {0, gr * 2.6f}};
            for (float[] p : pos) {
                paintFill.setColor(Color.rgb(128, 0, 128));
                paint.setColor(Color.rgb(80, 0, 80));
                c.drawCircle(x + r + p[0], y + r + bobY + p[1], gr, paintFill);
                c.drawCircle(x + r + p[0], y + r + bobY + p[1], gr, paint);
            }
        } else if (name.equals("Watermelon")) {
            c.drawOval(new RectF(x, y + bobY, x + ft.size, y + ft.size + bobY), paintFill);
            c.drawOval(new RectF(x, y + bobY, x + ft.size, y + ft.size + bobY), paint);
            paint.setColor(Color.rgb(0, 100, 0));
            paint.setStrokeWidth(2);
            c.drawLine(x + r, y + bobY + 5, x + r, y + bobY + ft.size - 5, paint);
            paint.setColor(Color.rgb(0, 80, 0));
            for (int i = 0; i < 3; i++) {
                float sy = y + bobY + 10 + i * 12;
                c.drawLine(x + r * 0.4f, sy, x + r * 1.6f, sy, paint);
            }
        } else if (name.equals("Cherry")) {
            paintFill.setColor(Color.rgb(200, 0, 50));
            c.drawCircle(x + r - 7, y + r + bobY, r * 0.5f, paintFill);
            c.drawCircle(x + r + 7, y + r + 5 + bobY, r * 0.5f, paintFill);
            paint.setColor(Color.rgb(80, 180, 80));
            paint.setStrokeWidth(3);
            c.drawLine(x + r - 7, y + bobY + r * 0.3f, x + r - 12, y - 8 + bobY, paint);
            c.drawLine(x + r + 7, y + bobY + r * 0.3f, x + r + 12, y - 8 + bobY, paint);
        } else if (name.equals("Kiwi")) {
            c.drawCircle(x + r, y + r + bobY, r, paintFill);
            c.drawCircle(x + r, y + r + bobY, r, paint);
            paintFill.setColor(Color.rgb(180, 220, 120));
            float ir = r * 0.5f;
            c.drawCircle(x + r, y + r + bobY, ir, paintFill);
            paint.setColor(Color.rgb(200, 230, 140));
            paint.setStrokeWidth(1);
            for (int i = 0; i < 10; i++) {
                float a = (float) (i * Math.PI / 5);
                c.drawLine(x + r + (float) Math.cos(a) * ir * 0.3f, y + r + bobY + (float) Math.sin(a) * ir * 0.3f,
                        x + r + (float) Math.cos(a) * ir * 0.8f, y + r + bobY + (float) Math.sin(a) * ir * 0.8f, paint);
            }
        } else if (name.equals("Dragon Fruit")) {
            c.drawOval(new RectF(x + r * 0.2f, y + bobY, x + r * 1.8f, y + ft.size + bobY), paintFill);
            c.drawOval(new RectF(x + r * 0.2f, y + bobY, x + r * 1.8f, y + ft.size + bobY), paint);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(1);
            for (int i = 0; i < 5; i++) {
                float sy = y + bobY + 8 + i * 8;
                c.drawLine(x + r + 2, sy, x + r + 2 + r * 0.3f, sy - 3, paint);
                c.drawLine(x + r + 2, sy, x + r + 2 + r * 0.3f, sy + 3, paint);
            }
        }
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(20, 20, 40));

        paint.setColor(Color.rgb(60, 60, 100));
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(5, 5, WIDTH - 5, HEIGHT - 5, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.YELLOW);
        paint.setTextSize(24);
        canvas.drawText("Score: " + score, 12, 28, paint);

        if (combo > 1) {
            paint.setColor(Color.rgb(255, 200, 0));
            paint.setTextSize(20);
            canvas.drawText(combo + "x Combo!", 160, 28, paint);
        }

        FruitType next = null;
        for (FruitType ft : fruitTypes) {
            if (!ft.unlocked) { next = ft; break; }
        }
        if (next != null) {
            float progress = Math.min(1f, (float) score / next.unlockScore);
            paint.setColor(Color.rgb(40, 40, 60));
            canvas.drawRect(12, 38, WIDTH - 12, 52, paint);
            paint.setColor(Color.rgb(0, 200, 100));
            canvas.drawRect(12, 38, 12 + (WIDTH - 24) * progress, 52, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(12);
            canvas.drawText("Next: " + next.name + " (" + score + "/" + next.unlockScore + ")", 16, 49, paint);
        }

        float y = HEIGHT - 14;
        paint.setTextSize(13);
        for (int i = fruitTypes.size() - 1; i >= 0; i--) {
            FruitType ft = fruitTypes.get(i);
            paint.setColor(ft.unlocked ? ft.color : Color.rgb(80, 80, 80));
            String label = ft.name + (ft.unlocked ? "" : " [" + ft.unlockScore + "]");
            canvas.drawText(label, WIDTH - paint.measureText(label) - 8, y, paint);
            y -= 15;
        }

        if (unlockMessage != null && System.currentTimeMillis() - unlockMessageTime < 2000) {
            paint.setColor(Color.rgb(0, 255, 100));
            paint.setTextSize(24);
            float mw = paint.measureText(unlockMessage);
            canvas.drawText(unlockMessage, (WIDTH - mw) / 2, HEIGHT / 2 - 20, paint);
        }

        for (ActiveFruit af : activeFruits) {
            drawFruit(canvas, af);
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(11);
        canvas.drawText("Best combo: " + bestCombo, 12, 65, paint);

        long now = System.currentTimeMillis();
        for (int i = floatingTexts.size() - 1; i >= 0; i--) {
            FloatingText ft = floatingTexts.get(i);
            float age = (now - ft.startTime) / 1000f;
            if (age > 1.2f) { floatingTexts.remove(i); continue; }
            paint.setColor(ft.color);
            paint.setTextSize(22);
            paint.setAlpha((int) Math.max(0, 255 - age * 200));
            canvas.drawText(ft.text, ft.x, ft.y - age * 40, paint);
            paint.setAlpha(255);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            boolean hit = false;
            for (int i = activeFruits.size() - 1; i >= 0; i--) {
                ActiveFruit af = activeFruits.get(i);
                float cx = af.x + af.type.size / 2;
                float cy = af.y + af.type.size / 2;
                float r = af.type.size / 2;
                float dx = event.getX() - cx;
                float dy = event.getY() - cy;
                if (dx * dx + dy * dy <= r * r) {
                    long now = System.currentTimeMillis();
                    if (now - lastTapTime < STREAK_TIMEOUT) {
                        combo++;
                    } else {
                        combo = 1;
                    }
                    lastTapTime = now;
                    if (combo > bestCombo) bestCombo = combo;
                    int mult = Math.min(combo, 10);
                    int earned = af.type.pointsPerTap * mult;
                    score += earned;
                    checkUnlocks();
                    String text = "+" + earned + (mult > 1 ? " x" + mult : "");
                    floatingTexts.add(new FloatingText(event.getX(), event.getY() - 20, text, af.type.color));
                    activeFruits.remove(i);
                    spawnFruit();
                    if (activeFruits.size() < 3 + score / 50) {
                        spawnFruit();
                    }
                    hit = true;
                    if (gameListener != null) gameListener.onScoreChanged(score);
                    invalidate();
                    break;
                }
            }
        }
        return true;
    }

    public void setGameListener(GameListener listener) {
        gameListener = listener;
    }

    public void restartGame() {
        handler.removeCallbacksAndMessages(null);
        initGame();
        if (gameListener != null) gameListener.onScoreChanged(0);
        invalidate();
    }
}