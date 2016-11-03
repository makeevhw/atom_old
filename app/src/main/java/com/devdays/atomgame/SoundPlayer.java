package com.devdays.atomgame;


import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    private MediaPlayer[] mSoundPlayers;
    private int currenLaserSoud;
    private int laserSoundNumber;

    public SoundPlayer(Context context) {
        laserSoundNumber = 3;
        mSoundPlayers = new MediaPlayer[laserSoundNumber]; // 3 enough
        mSoundPlayers[0] = MediaPlayer.create(context, R.raw.bcfire01);
        mSoundPlayers[1] = MediaPlayer.create(context, R.raw.reptrrico01);
        mSoundPlayers[2] = MediaPlayer.create(context, R.raw.trprsht1);

        currenLaserSoud = 0;
    }

    public void playLaserSound() {
        if (!mSoundPlayers[currenLaserSoud].isPlaying()) {
            mSoundPlayers[currenLaserSoud].start();
            currenLaserSoud++;
            currenLaserSoud %= laserSoundNumber;
        } else {
            // all are bisy
        }
    }

}
