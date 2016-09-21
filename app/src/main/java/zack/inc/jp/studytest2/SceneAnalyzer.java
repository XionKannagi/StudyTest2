package zack.inc.jp.studytest2;

/**
 * Created by togane on 2016/09/05.
 */
public class SceneAnalyzer {


    private float Ax, Ay, Az;
    private double speed;
    private int result = 1;


    //運転の状態に応じて状態値を返すメソッド
    public int judgeStatus() {
        if (Az < 0.4f && speed <= 1.4) {
            return 1;//停車中が状態orブレーキ終了状態１->return 1
        } else if (speed > 1.4 && Az < 0.4f) {
            return 2;//走行中が状態２->return 2
        } else {
            return 3;//ブレーキ中が状態３->return 3
        }
    }

    // 2 -> 3 -> 1

    private boolean inState1, inState3;
    private float max;

    public boolean mainfunc(float aX, float aY, float aZ, double v) {
        Ax = aX;
        Ay = aY;
        Az = aZ;
        speed = v;

        result = judgeStatus();

        if (result == 1) {

            inState1 = true;
            inState3 = false;

            if (inState3 == true) {
                //end_time = getTime();
                return true; // 状態3 直後の 状態1
            }

        } else if (result == 2) {

            inState1 = false;
            inState3 = false;

        } else {
            //TODO ブレーキ中の挙動->加速度のピークとその時の時間を
            if (inState3 == false) {
                //start_time = getTime(); // 初めての状態3
            }

            inState1 = false;
            inState3 = true;

            if (max < Az) {
                max = Az;
            }
        }
        return false;
    }


    private void get(){

        //開始速度
        //開始位置
        //終了速度
        //終了位置
        //ピーク値
        //ピーク時刻
    }

}
