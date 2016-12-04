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
    private SoundPlayer mSoundPlayer;
    private static int BRAKE_PATTERN_FRONT_PEAK_1 = 1;//理想に対してだいぶ早い
    private static int BRAKE_PATTERN_FRONT_PEAK_2 = 2;//理想に対してすこし早い
    private static int BREAK_PATTERN_BACK_PEAK_1 = 3;//理想に対してだいぶ遅い
    private static int BREAK_PATTERN_BACK_PEAK_2 = 4;//理想に対して少し遅い
    private static int BRAKE_PATTERN_SUDDEN = 5;// 急ブレーキ
    private static int BREAK_PATTERN_GOOD = 6;//いいブレーキ
    private double goodTimeRange = 0.3; //理想的なプレーキ時間±0.3sの範囲はgoodブレーキ
    private double badTimeRange = goodTimeRange * 2;


    public Calclater(Context context) {
        this.appContext = context;
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
        finTime = ((2 * (6 - Math.sqrt(6))) * distance) / (3 * (initSpeed-endSpeed));

        //理想的な減速終了時間を用いた減速度最大時刻
        peakTime = (8 * p - 15 - Math.sqrt(19 * p * p - 75 * p + 75)) * finTime / (15 * (p - 2));

        results[0] = peakTime;
        results[1] = finTime;


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
        TeachResult teachResult = new TeachResult(appContext);
        mSoundPlayer = new SoundPlayer(appContext);

        idealPeakTimeResults = getIdealPeakTimes(initSpeed, endSpeed, distance);
        idealPeakTimeResult = idealPeakTimeResults[0];
        idealFinTimeResult = idealPeakTimeResults[1];


        //TODO finTimeの情報を入れるといいかも
        //教示用のメソッドに投げる
        if (azMax > 2.94) {
            teachResult.teaching(BRAKE_PATTERN_SUDDEN);//加速度のピークが2.94を超えていたら急ブレーキ
            mSoundPlayer.play(BRAKE_PATTERN_SUDDEN);
        } else if ((idealPeakTimeResult - goodTimeRange) > ((double) peakTime) / 1000) {//理想よりも手前ピークのとき
            if ((idealPeakTimeResult - (goodTimeRange + badTimeRange)) > ((double) peakTime) / 1000) {
                teachResult.teaching(BRAKE_PATTERN_FRONT_PEAK_1);
                // log idealPeakTimeResult(left), peakTime(right)
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
        Log.i("caseSeparator", idealPeakTimeResult + " " + peakTime);
    }

    public void caseSeparatorV2(double initSpeed, double endSpeed, float distance, long peakTime, long finTime, float azMax, int arraysIndex, double[] timeArray, float[] acceleAzArray) {

        double[] idealPeakTimeResults;
        double idealFinTimeResult;
        double delta_v;


        idealPeakTimeResults = getIdealPeakTimes(initSpeed, endSpeed, distance);
        idealFinTimeResult = idealPeakTimeResults[1];
        delta_v = endSpeed - initSpeed;

        if(arraysIndex != 0) {
            evalBrake(distance, idealFinTimeResult, delta_v, arraysIndex, timeArray, acceleAzArray);//TODO Jerkで評価できるようにするもの．
        }
    }

    //理想値でのグラフを作るメソッド
    public void evalBrake(float dist, double idealFinTime, double deltaV, int Index, double[] timeArray, float[] acceleAzArray) {

        double b0, b1, b2;
        double idealAcceleArray[] = new double[Index];
        int peakTimeIndex = 0;


        /* 係数 */
        b0 = (6 * (double) dist / Math.pow(idealFinTime, 5) - (3 * deltaV / Math.pow(idealFinTime, 4)));
        b1 = (15 * (double) dist / Math.pow(idealFinTime, 4) - (8 * deltaV / Math.pow(idealFinTime, 3)));
        b2 = (10 * (double) dist / Math.pow(idealFinTime, 3) - (6 * deltaV / Math.pow(idealFinTime, 2)));


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

        double beforeSumValue = 0.0d;
        double afterSumValue = 0.0d;
        double beforeAverage;
        double afterAverage;
        int beforeCount = 0;
        int afterCount = 0;
        TeachResult teachResult = new TeachResult(appContext);

        for (int i = 1; i <= peakTimeIndex; i++) {
            beforeSumValue += idealJerk[i] - acceleAzJerk[i];//マイナス踏みすぎ
            beforeCount++;
        }

        beforeAverage = beforeSumValue / beforeCount;


        for (int i = peakTimeIndex + 1; i < Index; i++) {
            afterSumValue += idealJerk[i] - acceleAzJerk[i];//マイナス踏まなすぎ
            afterCount++;
        }

        afterAverage = afterSumValue / afterCount;

        Toast.makeText(appContext, "beforeValue:" + beforeAverage + "afterValue:" + afterAverage, Toast.LENGTH_LONG).show();
        //TODO 教示部分を書く

    }

    //
    public void onDestroy() {
        mSoundPlayer.onDestroy();
    }



}


