package zack.inc.jp.studytest2;

import android.content.Context;
import android.location.Location;

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
      *
     * **/
    public double[] getIdealPeakTimes(double initSpeed, double endSpeed, float distance) {

        double finTime;
        double peakTime;
        double[] results = new double[2];

        //速度と止まるまでの距離から算出した減速終了時間
        finTime = ((2 * (6 - Math.sqrt(6))) * distance) / (3 * (endSpeed - initSpeed));

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
        TeachResult teachResult = new TeachResult(appContext);

        idealPeakTimeResults = getIdealPeakTimes(initSpeed, endSpeed, distance);
        idealPeakTimeResult = idealPeakTimeResults[0];


        //idealPeakTimeResult = getIdealPeakTime(finTime);


        //教示用のメソッドに投げる
        if (azMax > 2.94) {
            teachResult.teaching(BRAKE_PATTERN_SUDDEN);//加速度のピークが2.94を超えていたら急ブレーキ
        } else if ((idealPeakTimeResult - goodTimeRange) > ((double) peakTime) / 1000) {//理想よりも手前ピークのとき
            if ((idealPeakTimeResult - (goodTimeRange + badTimeRange)) > ((double) peakTime) / 1000) {
                teachResult.teaching(BRAKE_PATTERN_FRONT_PEAK_1);
            } else {
                teachResult.teaching(BRAKE_PATTERN_FRONT_PEAK_2);
            }
        } else if ((idealPeakTimeResult + goodTimeRange) < ((double) peakTime) / 1000) {//理想よりも奥ピークのとき
            if ((idealPeakTimeResult + (goodTimeRange + badTimeRange)) < ((double) peakTime) / 1000) {
                teachResult.teaching(BREAK_PATTERN_BACK_PEAK_1);
            } else {
                teachResult.teaching(BREAK_PATTERN_BACK_PEAK_2);
            }
        } else { //それ以外はおそらく良いブレーキ
            teachResult.teaching(BREAK_PATTERN_GOOD);

        }
    }

}


