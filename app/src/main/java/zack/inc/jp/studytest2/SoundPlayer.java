package zack.inc.jp.studytest2;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Switch;

/**
 * Created by togane on 2016/11/10.
 */
public class SoundPlayer {

    private Context context;

    private static final int VOICE_TYPES = 7;
    MediaPlayer voicePlayer[] = new MediaPlayer[VOICE_TYPES];

    private int[] soundResource = {
            R.raw.beep_pon,
            R.raw.case_01,
            R.raw.case_02,
            R.raw.case_03,
            R.raw.case_04,
            R.raw.case_05,
            R.raw.case_06
    };

    public SoundPlayer(Context context) {
        this.context = context;
        for (int i = 0; i < VOICE_TYPES; i++) {
            voicePlayer[i] = MediaPlayer.create(context, soundResource[i]);
        }


    }

    public void play(int pattern) {

        voicePlayer[0].start();
        switch (pattern) {
            case 1:
                voicePlayer[pattern].start();
                break;
            case 2:
                voicePlayer[pattern].start();
                break;
            case 3:
                voicePlayer[pattern].start();
                break;
            case 4:
                voicePlayer[pattern].start();
                break;
            case 5:
                voicePlayer[pattern].start();
                break;
            case 6:
                voicePlayer[pattern].start();
                break;
        }
    }

    public void onDestroy() {

        if (voicePlayer != null) {
            if (voicePlayer[0].isPlaying()) {
                playerStop();
            }
            playerRelease();
        }
    }

    public void playerStop() {
        for (int i = 0; i < VOICE_TYPES; i++) {
            voicePlayer[i].stop();
        }
    }

    public void playerRelease() {
        for (int i = 0; i < VOICE_TYPES; i++) {
            voicePlayer[i].release();
        }
    }
}