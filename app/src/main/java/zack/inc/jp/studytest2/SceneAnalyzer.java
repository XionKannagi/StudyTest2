package zack.inc.jp.studytest2;

/**
 * Created by togane on 2016/09/05.
 */
public class SceneAnalyzer {


    private float Ax, Ay, Az;
    private double speed;
    private int result = 1;
    private int state = 0;


    //運転の状態に応じて状態値を返すメソッド
    public int judgeStatus() {

        /*
        //条件に不備がある？(speed > 1.4 && Az > 0.5fのときブレーキ中に走行状態と判定される)
        if (Az > 0.6f && speed > 1.4) {
            return 1; //ブレーキ中が状態1->開始時の加速度閾値0.6f
        } else if (speed <= 1.4 && Az < 0.4f) {
            return 2; //停車中が状態orブレーキ終了状態2->加速度閾値終了0.4f
        } else {
            return 3;//走行中が状態3->ある程度の速度がある時でブレーキがない時
        }
        */

        //シーン判定改良版
        switch (state) {
            case 0://STOP
                if (speed > 5.0) state = 1;
                else if (Az > 0.5) state = 2;
                return 2;
            case 1://CRUISE
                if (speed < 4.0) state = 0;
                else if (Az > 0.5) {
                    state = 2;
                }
                return 3;
            case 2://BRAKE
                if (speed < 4.0) state = 0;
                else if (Az < 0.4) state = 1;
                return 1;
            default:
                return -1; //判定不能エラーコード-1
        }

    }

    // 2 -> 3 -> 1 ==> 1 -> 2 -> 3

    private boolean instate1, instate2;
    private float azMax;
    private long azPeakTime;

    //200ms間隔で呼び出されるメソッド
    public boolean mainFunc(float aX, float aY, float aZ, double v) {
        Ax = aX;
        Ay = aY;
        Az = aZ;
        speed = v;

        result = judgeStatus();
        //ブレーキ中の挙動->加速度のピークとその時の時間を記録
        if (result == 1) {
            if (instate1 == false) {
                //ブレーキ開始時刻，位置座標，速度を記録 return true
                // 初めて3-> 1変わった．
                instate1 = true;
                instate2 = false;
                return true;
            }

            if (azMax < Az) {
                //ピーク時の時刻も記録
                azMax = Az;
                azPeakTime = System.currentTimeMillis();
            }

            return false;

        } else if (result == 2) {
            if (instate1 == true) {
                instate1 = false;
                instate2 = true;
                return true; // 状態1 直後の 状態2
            }

            return false;

        } else {
            instate1 = false;
            instate2 = false;
            return false;
        }
    }

    //各getterの記述(速度，位置座標に関してはDriveActivity側での取得がいいかも？)

    public double getSpeed() {
        //開始速度
        //終了速度
        return speed;
    }


    //加速度のピーク値
    public float getAzMax() {
        //ピーク値
        return azMax;
    }

    //加速度ピーク時の時刻
    public long getPeakTime() {
        //ピーク時刻
        return azPeakTime;
    }


}
