package zack.inc.jp.studytest2;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by togane on 2016/09/28.
 * 数値計算専用クラス
 */
public class Calclater {

    private Location location;
    private double p = 2.367;
    private double finTime;
    private double peakTime;
    private Context appContext;
    private TeachResult teachResult;
    private SoundPlayer mSoundPlayer;
    private static int BRAKE_PATTERN_FRONT_PEAK_1 = 1;//理想に対してだいぶ早い
    private static int BRAKE_PATTERN_FRONT_PEAK_2 = 2;//理想に対してすこし早い
    private static int BREAK_PATTERN_BACK_PEAK_1 = 3;//理想に対してだいぶ遅い
    private static int BREAK_PATTERN_BACK_PEAK_2 = 4;//理想に対して少し遅い
    private static int BRAKE_PATTERN_SUDDEN = 5;// 急ブレーキ
    private static int BREAK_PATTERN_GOOD = 6;//いいブレーキ
    private static int BREAK_PATTERN_GOOD_BAD_BEFORE = 7;
    private static int BREAK_PATTERN_GOOD_BAD_AFTER = 8;
    private static int BREAK_PATTERN_GOOD_BAD_NO_FIT = 9;
    private double goodTimeRange = 0.3; //理想的なプレーキ時間±0.3sの範囲はgoodブレーキ
    private double badTimeRange = goodTimeRange * 2;


    public Calclater(Context context) {
        this.appContext = context;
        teachResult = new TeachResult(appContext);
        mSoundPlayer = new SoundPlayer(appContext);
    }


    /**
     * 理想値を算出するメソッド．
     * minimum jerk Theory そのまま
     **/
    public double[] getIdealPeakTimes(double initSpeed, double endSpeed, float distance) {

        double finTime;
        double peakTime;
        double[] results = new double[2];

        //速度と止まるまでの距離から算出した減速終了時間
        finTime = ((2 * (6 - Math.sqrt(6))) * distance) / (3 * (initSpeed - endSpeed));

        //理想的な減速終了時間を用いた減速度最大時刻
        peakTime = (8 * p - 15 - Math.sqrt(19 * Math.pow(p, 2) - 75 * p + 75)) * finTime / (15 * (p - 2));

        results[0] = peakTime;
        results[1] = finTime;

        Log.v("getIdealTime :", "Peak" + results[0] + "Fin" + results[1]);

        return results;
    }

    /**
     * 理想値を算出するメソッド．
     * 実測値を元にminimum jerk theoryを適応
     * そのままだと，誤差が大きかったが，これを使うとある程度，実際の運転に即して評価できる
     * これは，単純にtmの理想値がtfの理想値の定数倍であることに着目して，実際のtfに同じ定数をかけたもの
     **/

    public double getIdealPeakTime(double finTime) {

        //理想的な減速終了時間を用いた減速度最大時刻
        peakTime = (8 * p - 15 - Math.sqrt(19 * p * p - 75 * p + 75)) * finTime / (15 * (p - 2));

        return peakTime;
    }

    //GPS距離を算出するメソッド簡易版
    public float getDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {

        float[] results = new float[3];
        //2点間距離を計測するメソッド -> 配列で帰ってくる
        //results[0] = [２点間の距離] ->基本的にはこいつを使う
        //results[1] = [始点から見た方位角]
        //results[2] = [終点から見た方位角]

        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);

        return results[0];
    }


    //Teachメソッドに渡すためのケース分け
    public void caseSeparator(double initSpeed, double endSpeed, float distance, long peakTime, long finTime, float azMax) {

        double[] idealPeakTimeResults;
        double idealPeakTimeResult;
        double idealFinTimeResult;

        idealPeakTimeResults = getIdealPeakTimes(initSpeed, endSpeed, distance);
        idealPeakTimeResult = idealPeakTimeResults[0];
        idealFinTimeResult = idealPeakTimeResults[1];

        goodTimeRange = idealFinTimeResult / 30; //全体の時間のうち±1/30のピーク時間誤差を許容
        badTimeRange = goodTimeRange * 2;

        //TODO finTimeの情報を入れるといいかも
        //教示用のメソッドに投げる
        if (azMax > 2.94) {
            teachResult.teaching(BRAKE_PATTERN_SUDDEN);//加速度のピークが2.94を超えていたら急ブレーキ
            mSoundPlayer.play(BRAKE_PATTERN_SUDDEN);
        } else if ((idealPeakTimeResult - goodTimeRange) > ((double) peakTime) / 1000) {//理想よりも手前ピークのとき
            if ((idealPeakTimeResult - (goodTimeRange + badTimeRange)) > ((double) peakTime) / 1000) {
                teachResult.teaching(BRAKE_PATTERN_FRONT_PEAK_1);
                mSoundPlayer.play(BRAKE_PATTERN_FRONT_PEAK_1);
            } else {
                teachResult.teaching(BRAKE_PATTERN_FRONT_PEAK_2);
                mSoundPlayer.play(BRAKE_PATTERN_FRONT_PEAK_2);
            }
        } else if ((idealPeakTimeResult + goodTimeRange) < ((double) peakTime) / 1000) {//理想よりも奥ピークのとき
            if ((idealPeakTimeResult + (goodTimeRange + badTimeRange)) < ((double) peakTime) / 1000) {
                teachResult.teaching(BREAK_PATTERN_BACK_PEAK_1);
                mSoundPlayer.play(BREAK_PATTERN_BACK_PEAK_1);
            } else {
                teachResult.teaching(BREAK_PATTERN_BACK_PEAK_2);
                mSoundPlayer.play(BREAK_PATTERN_BACK_PEAK_2);
            }
        } else { //それ以外はおそらく良いブレーキ
            teachResult.teaching(BREAK_PATTERN_GOOD);
            mSoundPlayer.play(BREAK_PATTERN_GOOD);

        }

        Log.i("caseSeparator", idealPeakTimeResult + " " + (double) peakTime / 1000);
    }


    public void caseSeparatorV2(double initSpeed, double endSpeed, float distance, long peakTime, long finTime, float azMax, int arraysIndex, double[] timeArray, float[] acceleAzArray) {

        double[] idealPeakTimeResults;
        double idealFinTimeResult;
        double delta_v;


        idealPeakTimeResults = getIdealPeakTimes(initSpeed, endSpeed, distance);
        idealFinTimeResult = idealPeakTimeResults[1];
        delta_v = initSpeed - endSpeed;


        if (arraysIndex != 0) {
            evalBrake(distance, idealFinTimeResult, delta_v, arraysIndex, timeArray, acceleAzArray);//TODO Jerkで評価できるようにするもの．
        }

    }

    //理想値でのグラフを作るメソッド
    public void evalBrake(float dist, double idealFinTime, double deltaV, int Index, double[] timeArray, float[] acceleAzArray) {

        double b0, b1, b2;
        double idealAcceleArray[] = new double[Index];
        int peakTimeIndex = 0;

        /**係数たち**/
        b0 = (6 * (double) dist / Math.pow(idealFinTime, 5) - (3 * deltaV / Math.pow(idealFinTime, 4)));

        b1 = (15 * (double) dist / Math.pow(idealFinTime, 4) - (8 * deltaV / Math.pow(idealFinTime, 3)));

        b2 = (10 * (double) dist / Math.pow(idealFinTime, 3) - (6 * deltaV / Math.pow(idealFinTime, 2)));

        Log.i("evalBrake", "Index:" + Index);
        for (int i = 0; i < Index; i++) {
            idealAcceleArray[i] = -((20 * b0 * Math.pow(timeArray[i], 3)) - (12 * b1 * Math.pow(timeArray[i], 2)) + 6 * b2 * timeArray[i]);
            if (i > 0) {
                if (idealAcceleArray[i - 1] < idealAcceleArray[i]) {
                    peakTimeIndex = i;
                }
            }
        }

        calcJerks(idealAcceleArray, acceleAzArray, Index, peakTimeIndex);

    }


    //それぞれのJerkを計算しグラフ化
    public void calcJerks(double[] idealAcceleArray, float[] acceleAzArray, int Index, int peakTimeIndex) {

        double idealJerk[] = new double[Index];
        double acceleAzJerk[] = new double[Index];

        idealJerk[0] = 0;
        acceleAzJerk[0] = 0;

        for (int i = 1; i < Index; i++) {
            idealJerk[i] = idealAcceleArray[i] - idealAcceleArray[i - 1];
            acceleAzJerk[i] = (double) acceleAzArray[i] - (double) acceleAzArray[i - 1];
        }

        evalJerks(idealJerk, acceleAzJerk, peakTimeIndex, Index);
    }


    //理想値でのピークの前後半で実測とのJerkの差を計算
    public void evalJerks(double[] idealJerk, double[] acceleAzJerk, int peakTimeIndex, int Index) {

        double beforePowerPlus = 0.0d;
        double beforePowerMinus = 0.0d;
        double afterPowerPlus = 0.0d;
        double afterPowerMinus = 0.0d;
        double beforePlusAverage;
        double beforeMinusAverage;
        double afterPlusAverage;
        double afterMinusAverage;
        double beforeValue;
        double afterValue;
        int beforePlusCount = 0;
        int beforeMinusCount = 0;
        int afterPlusCount = 0;
        int afterMinusCount = 0;

        for (int i = 1; i <= peakTimeIndex; i++) {

            if ((idealJerk[i] - acceleAzJerk[i]) < 0) {//マイナス踏みすぎ
                beforePowerMinus += Math.pow((idealJerk[i] - acceleAzJerk[i]), 2);
                beforeMinusCount++;
            } else {
                beforePowerPlus += Math.pow((idealJerk[i] - acceleAzJerk[i]), 2);
                beforePlusCount++;
            }


        }

        //こいつらの合計が0.4dを上回る時はスムーズじゃない
        beforePlusAverage = beforePowerPlus / beforePlusCount;
        beforeMinusAverage = beforePowerMinus / beforeMinusCount;
        beforeValue = beforePlusAverage + beforeMinusAverage;


        for (int i = peakTimeIndex + 1; i < Index; i++) {
            if ((idealJerk[i] - acceleAzJerk[i]) < 0) {//マイナス踏みすぎ
                afterPowerMinus += Math.pow((idealJerk[i] - acceleAzJerk[i]), 2);
                afterMinusCount++;
            } else {
                afterPowerPlus += Math.pow((idealJerk[i] - acceleAzJerk[i]), 2);
                afterPlusCount++;
            }
        }

        //こいつらの合計が0.4dを上回る時はスムーズじゃない
        afterPlusAverage = afterPowerPlus / afterPlusCount;
        afterMinusAverage = afterPowerMinus / afterMinusCount;
        afterValue = afterPlusAverage + afterMinusCount;


        Toast.makeText(appContext, "beforeValue:" + beforePlusAverage + "afterValue:" + beforeMinusAverage, Toast.LENGTH_LONG).show();
        Log.i("calcなんたら", "beforeValue:" + beforePlusAverage + "afterValue:" + afterPlusAverage + "peakTimeIndex:" + peakTimeIndex);

        //TODO ここがv2の教示部分

        /*
        if(beforeValue < 0.4 && afterValue<0.4){
            teachResult.teaching(BREAK_PATTERN_GOOD);
            mSoundPlayer.play(BREAK_PATTERN_GOOD);
        }else if(beforeValue <= 0.4 && afterValue < 0.4){
            teachResult.teaching(BREAK_PATTERN_GOOD_BAD_BEFORE);
            mSoundPlayer.play(BREAK_PATTERN_GOOD_BAD_BEFORE);
        } else if (beforeValue < 0.4 && afterValue >= 0.4){
            teachResult.teaching(BREAK_PATTERN_GOOD_BAD_AFTER);
            mSoundPlayer.play(BREAK_PATTERN_GOOD_BAD_AFTER);
        } else {
            teachResult.teaching(BREAK_PATTERN_GOOD_BAD_NO_FIT);
            mSoundPlayer.play(BREAK_PATTERN_GOOD_BAD_NO_FIT);
        }
        */
    }

    //
    public void onDestroy() {
        if (mSoundPlayer != null) {
            mSoundPlayer.onDestroy();
        }
    }


}


