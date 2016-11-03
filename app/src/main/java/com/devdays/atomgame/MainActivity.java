package com.devdays.atomgame;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
        setContentView(R.layout.main);

        final AtomGameView gameView = (AtomGameView) findViewById(R.id.gameview);
        gameView.findTextView();
        gameView.generateMapWithParam(2);


        final Button checkButton = (Button) findViewById(R.id.button_check);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.checkResults();
            }
        });

        final Button resetButton = (Button) findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.resetGame();
            }
        });

        final Button incMapSizeBtn = (Button) findViewById(R.id.button_increase_map_size);
        incMapSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.incMapSize();
            }
        });

    }
}
