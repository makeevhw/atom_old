package com.devdays.atomgame;


import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    private MediaPlayer[] mSoundPlayers;
    private int currenLaserSoud;

    public SoundPlayer(Context context) {
        mSoundPlayers = new MediaPlayer[3]; // 3 enough
        mSoundPlayers[0] = MediaPlayer.create(context, R.raw.bcfire01);
        mSoundPlayers[1] = MediaPlayer.create(context, R.raw.trprsht2);
        currenLaserSoud = 0;
    }

    public void playLaserSound() {
        if (!mSoundPlayers[currenLaserSoud].isPlaying()) {
            mSoundPlayers[currenLaserSoud].start();
            currenLaserSoud++;
            currenLaserSoud %= 2;
        }
    }

}
