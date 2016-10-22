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
    private static int BRAKE_PATTERN_FRONT_PEAK = 1;
    private static int BREAK_PATTERN_BACK_PEAK = 2;
    private static int BRAKE_PATTERN_SUDDEN = 3;// 急ブレーキ
    private static int BREAK_PATTERN_GOOD = 4;
    private double timeRange = 0.5; //理想的なプレーキ時間±0.5sの範囲はgoodブレーキ


    public Calclater(Context context) {
        this.appContext = context;
    }


    //理想値を算出するメソッド．
    public double[] getIdealPeakTime(double initSpeed, double endSpeed, float distance) {

        double[] results = new double[2];

        //速度と止まるまでの距離から算出した減速終了時間
        finTime = ((2 * (6 - Math.sqrt(6))) * distance) / (3 * (endSpeed - initSpeed));

        //理想的な減速終了時間を用いた減速度最大時刻
        peakTime = ((8 * p - (15 * Math.sqrt(14 * p * p - 75 * p - 75))) / (15 * (p - 2))) * finTime;

        results[0] = peakTime;
        results[1] = finTime;


        return results;
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
    public void caseSeparator(double initSpeed, double endSpeed, float distance, long peakTime, float azMax) {

        double[] idealPeakTimeResult;
        TeachResult teachResult = new TeachResult(appContext);

        idealPeakTimeResult = getIdealPeakTime(initSpeed, endSpeed, distance);


        //教示用のメソッドに投げる
        if (azMax > 2.94) {

            teachResult.teaching(BRAKE_PATTERN_SUDDEN);//加速度のピークが2.94を超えていたら急ブレーキ

        } else if ((idealPeakTimeResult[0] - timeRange) > ((double) peakTime) / 1000) {//理想よりも手前ピークのとき

            teachResult.teaching(BRAKE_PATTERN_FRONT_PEAK);

        } else if ((idealPeakTimeResult[0] + timeRange) < ((double) peakTime) / 1000) {//理想よりも奥ピークのとき

            teachResult.teaching(BREAK_PATTERN_BACK_PEAK);

        } else { //それ以外はおそらく良いブレーキ

            teachResult.teaching(BREAK_PATTERN_GOOD);
        }
    }

}


