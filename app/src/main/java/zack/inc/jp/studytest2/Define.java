package zack.inc.jp.studytest2;

/**
 * Created by togane on 2016/07/25.
 */
public class Define {

    // for Accelerometer, GyroSensor センサーの値を保存しておく最大数
    static final int SENSOR_STORE_MAX = 300;

    // for LevelingController センサーの値をいくつ平均するかの値
    static final int SENSOR_AVERAGE_NUM = 20;

    // for LevelingController 横Gの大きさを何個保存しておくか
    static final int LATERAL_G_FORCE_STORE_MAX = 5;
    static final int LATERAL_G_FORCE_AVERAGE_NUM = 1;

    // for LevelingController 右左折判定の定数
    static final int TURN_LEFT  = 1;
    static final int TURN_RIGHT = 2;
    static final int TURN_STRAIGHT = 0;

    // for GraphSurfaceView グラフ描画用のログ数
    static final int GRAPH_LOG_NUM = 400;
    static final int GRAPH_Y_RANGE = 8; //グラフ表示の最大G(m/s^2)
    static final int GRAPH_X_RANGE = 80; // 1000msに使う横幅ピクセル数

    //for GraphSurfaceView, LevelingController 横Gのサンプリング周期(ms)
    static int LATERAL_G_FORCE_SAMPLING_RATE_Hz = 12;
    static int LATERAL_G_FORCE_SAMPLING_RATE = 1000/12;

    static void setSamplingRate(int freq){
        LATERAL_G_FORCE_SAMPLING_RATE_Hz = freq;
        LATERAL_G_FORCE_SAMPLING_RATE = 1000/freq;
    }
    static int LPF_CUT_OFF_FREQUENCY = 1;

    static void setCutoffFreq(int freq){
        LPF_CUT_OFF_FREQUENCY = freq;
    }
    static final float LPF_Q_VALUE = 0.5f;

    // for GPS GPSデータの収集数
    static final int GPS_STORE_MAX = 10;
    static final int GPS_ELEMENTS_NUM = 2;

    // for GPS GPSから測定した速度の情報
    static final int GPS_SPEED_STATUS_STOP = 10;
    static final int GPS_SPEED_STATUS_CRUISE = 11;
    static final int GPS_SPEED_STATUS_ACCELERATE= 12;
    static final int GPS_SPEED_STATUS_BRAKING = 13;
    static final int GPS_SPEED_STATUS_UNDEFINED = 20;

    // for GraphSurfaceView 加速度データを横Gか縦Gか決めて送ってもらう
    static final int DATA_TYPE_FRONT_G = 0;
    static final int DATA_TYPE_LATERAL_G = 1;

    // for DriveAnalizer, Scorer
    static final float SCORE_FALSE = -10000.0f; //非採点対象からのスコア

}
