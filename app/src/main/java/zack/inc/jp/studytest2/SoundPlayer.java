package zack.inc.jp.studytest2;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Switch;

/**
 * Created by togane on 2016/11/10.
 */

/**音声内容
 * Case01:踏み始めを緩やかにしましょう！
 *
 * Case02:おしい！踏み始めをもう少し緩やかにしましょう！
 *
 * Case03:踏み込みを早めにしましょう！
 *
 * Case04:あとちょっと！踏み込みをもう少し早めにしましょう！
 *
 * Case05:急ブレーキです！気をつけてください！
 *
 * Case06:いいですね！理想的です！
 *
 * Case07:踏み始めをもう少しスムーズにしましょう
 *
 * Case08:踏み終わりをもう少し丁寧にしましょう
 *
 * Case09:全体的に，スムーズなブレーキを心がけましょう．**/

public class SoundPlayer {

    private Context context;

    private static final int VOICE_TYPES = 10;
    MediaPlayer voicePlayer[] = new MediaPlayer[VOICE_TYPES];

    private int[] soundResource = {
            R.raw.beep_pon,
            R.raw.case_01,
            R.raw.case_02,
            R.raw.case_03,
            R.raw.case_04,
            R.raw.case_05,
            R.raw.case_06,
            R.raw.case_07,
            R.raw.case_08,
            R.raw.case_09
    };

    public SoundPlayer(Context context) {
        this.context = context;
        for (int i = 0; i < VOICE_TYPES; i++) {
            voicePlayer[i] = MediaPlayer.create(context, soundResource[i]);
        }


    }

    public void play(int pattern) {

        voicePlayer[0].start();//Beep
        while (voicePlayer[0].isPlaying()){
        }
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
            case 7:
                voicePlayer[pattern].start();
                break;
            case 8:
                voicePlayer[pattern].start();
                break;
            case 9:
                voicePlayer[pattern].start();
                break;
        }
    }

    public void onDestroy() {

        if (voicePlayer != null) {
            if (voicePlayer[0].isPlaying()) {
                //Stop all
                playerStop();
            }
            //Release all
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