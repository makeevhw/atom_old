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

    private AtomGameView gameView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
        setContentView(R.layout.main);

        gameView = (AtomGameView) findViewById(R.id.gameview);
        gameView.findTextView();
        gameView.generateMapWithParam(1);


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
                gameView.mSoundPlayer.playButtonClickSound();
                gameView.resetGame();
            }
        });

        final Button incMapSizeBtn = (Button) findViewById(R.id.button_increase_map_size);
        incMapSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.mSoundPlayer.playButtonClickSound();
                gameView.incMapSize();
            }
        });


        final Button decMapSizeBtn = (Button) findViewById(R.id.button_decrease_map_size);
        decMapSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.mSoundPlayer.playButtonClickSound();
                gameView.decMapSize();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameView.mSoundPlayer.destroy();
    }

}
