package zack.inc.jp.studytest2;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Switch;

/**
 * Created by togane on 2016/11/10.
 */
public class SoundPlayer {

    private Context context;
    MediaPlayer mediaPlayer;

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


    }

    public void play(int pattern) {


        mediaPlayer = MediaPlayer.create(context, soundResource[0]);
        mediaPlayer.start();

        if (pattern != 0) {
            switch (pattern) {
                case 1:
                    mediaPlayer = MediaPlayer.create(context, soundResource[pattern]);
                    mediaPlayer.start();
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(context, soundResource[pattern]);
                    mediaPlayer.start();
                    break;
                case 3:
                    mediaPlayer = MediaPlayer.create(context, soundResource[pattern]);
                    mediaPlayer.start();
                    break;
                case 4:
                    mediaPlayer = MediaPlayer.create(context, soundResource[pattern]);
                    mediaPlayer.start();
                    break;
                case 5:
                    mediaPlayer = MediaPlayer.create(context, soundResource[pattern]);
                    mediaPlayer.start();
                    break;
                case 6:
                    mediaPlayer = MediaPlayer.create(context, soundResource[pattern]);
                    mediaPlayer.start();
                    break;

            }
        }


    }

    public void onDestroy() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }


}