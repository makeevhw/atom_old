package com.devdays.atomgame;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class SoundPlayer {

    private MediaPlayer[] mLazerSoundPlayers;
    private ArrayList<MediaPlayer> mChoseAtomPlayerArr;
    private ArrayList<MediaPlayer> mDechoseAtomPlayerArr;
    private ArrayList<MediaPlayer> mButtonClickPlayerArr;
    private MediaPlayer mHiglightedPlayer;
    private MediaPlayer mLosePlayer;
    private MediaPlayer mWinPlayer;
    private MediaPlayer mErrorPlayer;

    private int mChoseSoundID = R.raw.button_11; // was _3 , but changed
    private int mDechoseSoundID = R.raw.button_11;
    private int mHighlightSoudID = R.raw.lightsaberpulse; // may be should change
    private int mBtnClickSoundID = R.raw.button_33a;
    private int mLoseSoundID = R.raw.lose1;
    private int mWinSoundID = R.raw.win1;
    private int mErrorSoundID = R.raw.error1;


    private Context context;
    private int currenLaserSoud;
    private int laserSoundNumber;
    private boolean isSoundEnabled;

    public SoundPlayer(Context context, boolean isEnabled) {
        this.context = context;
        this.isSoundEnabled = isEnabled;
        init();
    }

    /**
     * initialize all players with resourses
     */
    public void init() {
        laserSoundNumber = 5;
        mLazerSoundPlayers = new MediaPlayer[laserSoundNumber]; // 3 enough?
        mLazerSoundPlayers[0] = MediaPlayer.create(context, R.raw.bcfire01);
        mLazerSoundPlayers[1] = MediaPlayer.create(context, R.raw.reptrrico01);
        mLazerSoundPlayers[2] = MediaPlayer.create(context, R.raw.trprsht1);
        mLazerSoundPlayers[3] = MediaPlayer.create(context, R.raw.sprobegun01);
        mLazerSoundPlayers[4] = MediaPlayer.create(context, R.raw.repeat_1);

        //show lazer path sounds
        mHiglightedPlayer = MediaPlayer.create(context, mHighlightSoudID);
        mHiglightedPlayer.setLooping(true);
        mHiglightedPlayer.setVolume(0.8f, 0.8f);

        //atom chose//dechose sound
        mChoseAtomPlayerArr = new ArrayList<>();
        mChoseAtomPlayerArr.add(MediaPlayer.create(context, mChoseSoundID));

        mDechoseAtomPlayerArr = new ArrayList<>();
        mDechoseAtomPlayerArr.add(MediaPlayer.create(context, mDechoseSoundID));

        mButtonClickPlayerArr = new ArrayList<>();
        mButtonClickPlayerArr.add(MediaPlayer.create(context, mBtnClickSoundID));

        mLosePlayer = MediaPlayer.create(context, mLoseSoundID);
        mWinPlayer = MediaPlayer.create(context, mWinSoundID);
        mErrorPlayer = MediaPlayer.create(context, mErrorSoundID);

        currenLaserSoud = 0;
    }

    /**
     * call for releasing resourses
     */
    public void destroy() {
        for (int i = 0; i < mLazerSoundPlayers.length; i++) {
            mLazerSoundPlayers[i].stop();
            mLazerSoundPlayers[i].release();
        }

        for (int i = 0; i < mChoseAtomPlayerArr.size(); i++) {
            mChoseAtomPlayerArr.get(i).stop();
            mChoseAtomPlayerArr.get(i).release();
        }

        for (int i = 0; i < mDechoseAtomPlayerArr.size(); i++) {
            mDechoseAtomPlayerArr.get(i).stop();
            mDechoseAtomPlayerArr.get(i).release();
        }

        for (int i = 0; i < mButtonClickPlayerArr.size(); i++) {
            mButtonClickPlayerArr.get(i).stop();
            mButtonClickPlayerArr.get(i).release();
        }

        mHiglightedPlayer.stop();
        mHiglightedPlayer.release();

        mLosePlayer.stop();
        mLosePlayer.release();

        mWinPlayer.stop();
        mWinPlayer.release();
    }

    public void playLaserSound() {
        if (isSoundEnabled) {
            if (!mLazerSoundPlayers[currenLaserSoud].isPlaying()) {
                mLazerSoundPlayers[currenLaserSoud].start();
                currenLaserSoud++;
                currenLaserSoud %= laserSoundNumber;
            } else { /* not availible  */
            }
        }
    }

    public void playHighlightedSound() {
        if (isSoundEnabled) mHiglightedPlayer.start();
    }

    public void stopHighlightedSound() {
        if (isSoundEnabled)
            mHiglightedPlayer.pause();
    }

    public void playChoseSound() {
        if (isSoundEnabled) {
            for (int i = 0; i < mChoseAtomPlayerArr.size(); i++) {
                if (!mChoseAtomPlayerArr.get(i).isPlaying()) {
                    mChoseAtomPlayerArr.get(i).start();
                    return;
                }
            }
            mChoseAtomPlayerArr.add(MediaPlayer.create(context, mChoseSoundID));
            mChoseAtomPlayerArr.get(mChoseAtomPlayerArr.size() - 1).start();
        }
    }

    public void playDechoseSound() {
        if (isSoundEnabled) {
            for (int i = 0; i < mDechoseAtomPlayerArr.size(); i++) {
                if (!mDechoseAtomPlayerArr.get(i).isPlaying()) {
                    mDechoseAtomPlayerArr.get(i).start();
                    return;
                }
            }
            mDechoseAtomPlayerArr.add(MediaPlayer.create(context, mDechoseSoundID));
            mDechoseAtomPlayerArr.get(mDechoseAtomPlayerArr.size() - 1).start();
        }
    }

    public void playButtonClickSound() {
        if (isSoundEnabled) {
            for (int i = 0; i < mButtonClickPlayerArr.size(); i++) {
                if (!mButtonClickPlayerArr.get(i).isPlaying()) {
                    mButtonClickPlayerArr.get(i).start();
                    return;
                }
            }
            mButtonClickPlayerArr.add(MediaPlayer.create(context, mBtnClickSoundID));
            mButtonClickPlayerArr.get(mButtonClickPlayerArr.size() - 1).start();
        }
    }

    public void playSoundLose() {
        if (isSoundEnabled) mLosePlayer.start();
    }

    public void playSoundWin() {
        if (isSoundEnabled) mWinPlayer.start();
    }

    public void playSoundError() {
        if (isSoundEnabled)
            mErrorPlayer.start();
    }

    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        isSoundEnabled = soundEnabled;
    }
}
