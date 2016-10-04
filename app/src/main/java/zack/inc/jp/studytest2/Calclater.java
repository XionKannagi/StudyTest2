package zack.inc.jp.studytest2;

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
}


