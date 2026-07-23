package com.example.mygame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView scoreText;
    private Button btnRestart;
    private LinearLayout gameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreText = findViewById(R.id.scoreText);
        btnRestart = findViewById(R.id.btnRestart);
        gameContainer = findViewById(R.id.gameContainer);

        gameView = new GameView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                600, 600);
        gameContainer.addView(gameView, params);

        btnRestart.setOnClickListener(v -> gameView.restartGame());
        gameView.setGameListener(new GameView.GameListener() {
            @Override
            public void onScoreChanged(int score) {
                scoreText.setText("Score: " + score);
            }

            @Override
            public void onGameOver(int score) {
                btnRestart.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.resumeGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pauseGame();
        }
    }
}
