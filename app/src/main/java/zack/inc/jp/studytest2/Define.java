package zack.inc.jp.studytest2;

/**
 * Created by togane on 2016/07/25.
 */
public class Define {

    // for Accelerometer, GyroSensor センサーの値を保存しておく最大数
    static final int SENSOR_STORE_MAX = 300;

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


}
