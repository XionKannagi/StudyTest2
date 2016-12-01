package zack.inc.jp.studytest2;

import android.provider.Settings;
import android.support.annotation.IntegerRes;
import android.util.Log;

/**
 * Created by togane on 2016/09/05.
 */
public class SceneAnalyzer {


    static final int STORE_MAX = 5; //1sec
    private float Ax[] = new float[STORE_MAX], Ay[] = new float[STORE_MAX], Az[] = new float[STORE_MAX];
    //TODO getArrayIndexをつかったほうがいい？
    private float accelecAzArray[] = new float[Define.SENSOR_STORE_MAX];//Max 50s
    private double timeArray[] = new double[Define.SENSOR_STORE_MAX];//Max 50s
    private int arraysIndex = 0;
    private long brakeStartTime;

    private int p = 0;
    private double speed;
    private int result = 1;
    private int state = 0;


    //運転の状態に応じて状態値を返すメソッド
    public int judgeStatus() {


        //シーン判定改良版
        //TODO 閾値の部分の調整 -> 速度&加速度での閾値を見るべきかもしてない
        //Log.v("judgeState", Integer.toString(state));
        switch (state) {
            case 0://STOP
                if (speed > 5.0) state = 1;
                else if (Az[p] > 0.5) state = 2;//TODO 2Times Teaching
                return 2;
            case 1://CRUISE
                if (speed == 0.0) state = 0;
                else if (Az[p] > 0.5) state = 2;
                return 3;
            case 2://BRAKE
                //TODO CRUISE状態に戻るときの条件

                /*** 窓を見る ***/
                // 最大最小とる
                // もし差が小さければ止まった
                //
                if (isStop() || speed == 0) state = 0;
                // ポンピングブレーキのとき
                if (Az[getArrayIndex(p - 1)] > 0.4 && Az[p] < 0.4) {
                    if (speed > 3.0) state = 1; //10km/h 以上だとポンピングブレーキ
                }
                if (Az[p] < -0.5) state = 1;
                return 1;
            default:
                return -1; //判定不能エラーコード-1
        }

    }

    private boolean isStop() {
        float min, max;
        min = max = Az[p];
        for (int i = 0; i < STORE_MAX; i++) {
            if (Az[i] < min) {
                min = Az[i];
            }

            if (Az[i] > max) {
                max = Az[i];
            }
        }

        if ((max - min) < 0.04) {
            return true;
        } else {
            return false;
        }
    }

    // データ保存配列のインデックスpを上手いこと循環させる
    private int getArrayIndex(int p_tmp) {
        int p_real = p_tmp;
        while (p_real >= STORE_MAX)
            p_real -= STORE_MAX;
        while (p_real < 0)
            p_real += STORE_MAX;
        return p_real;
    }

    // 2 -> 3 -> 1 ==> 1 -> 2 -> 3

    private boolean instate1, instate2;
    private float azMax;
    private long azPeakTime;

    //200ms間隔で呼び出されるメソッド
    public int mainFunc(float aX, float aY, float aZ, double v) {
        p = getArrayIndex(p + 1);
        Ax[p] = aX;
        Ay[p] = aY;
        Az[p] = aZ;
        speed = v;

        result = judgeStatus();
        //ブレーキ中の挙動->加速度のピークとその時の時間を記録
        if (result == 1) {
            if (instate1 == false) {
                //ブレーキ開始時刻，位置座標，速度を記録 return true
                // 初めて3 -> 1変わった．
                arraysIndex = 0;//Indexを0に戻す．
                brakeStartTime = System.currentTimeMillis();//ブレーキ開始時刻
                instate1 = true;
                instate2 = false;
                return 1;
            }

            if (azMax < Az[p]) {
                //ピーク時の時刻も記録
                azMax = Az[p];
                azPeakTime = System.currentTimeMillis();
            }

            storeSensorValues(brakeStartTime, aZ); //時間とセンサー値を溜め込んでいく


            return 0;

        } else if (result == 2) {
            if (instate1 == true) {
                instate1 = false;
                instate2 = true;
                return 2; // 状態1 直後の 状態2
            }
            return 0;

        } else if (result == 3) {
            instate1 = false;
            instate2 = false;
            return 0;
        } else {
            //エラーコード-1が返ってきたときの処理
            return 0;//一応何もして欲しくないから
        }
    }


    public void storeSensorValues(long startTime, float aZ) {
        accelecAzArray[arraysIndex] = aZ;
        timeArray[arraysIndex] = (System.currentTimeMillis() - startTime) / 1000; //(s)

        arraysIndex++;
    }

    //各getterの記述(速度，位置座標に関してはDriveActivity側での取得がいいかも？)
    public double getSpeed() {
        //開始速度
        //終了速度
        return speed;
    }

    public float[] getAzArray(){
        return accelecAzArray;
    }

    public double[] getTimeArray(){
        return timeArray;
    }

    public int getArraysIndex(){
        return arraysIndex;
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
