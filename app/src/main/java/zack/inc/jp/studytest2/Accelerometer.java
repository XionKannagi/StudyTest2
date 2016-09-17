package zack.inc.jp.studytest2;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

/**
 * Created by togane on 2016/07/25.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Accelerometer implements SensorEventListener {

    final static String TAG = "Accelerometer";
    private SensorManager sensorManager;
    private float _x, _y, _z;
    private float x[], y[], z[];
    //private int position; //現在の場所
    private int p;

    public void onCreate(Context context) {
        Log.v("Accelerometer", "onCreate");
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
    }


    public void onResume() {
        Log.v("AccelSensor", "onResume");
        // Listenerの登録
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() > 0) {
            Sensor s = sensorList.get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }


    public void onPause() {
        if (sensorManager == null) {
            return;
        }
        // Listenerの登録解除
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            /** shimada Create follows code.
             Log.i(TAG, "onSensorChanged");
             String str = "加速度センサー値:"
             + "\nX軸:" + event.values[SensorManager.DATA_X]
             + "\nY軸:" + event.values[SensorManager.DATA_Y]
             + "\nZ軸:" + event.values[SensorManager.DATA_Z];

             Log.d(TAG,str);
             */

        /*
            _x = event.values[SensorManager.DATA_X];
            _y = event.values[SensorManager.DATA_Y];
            _z = event.values[SensorManager.DATA_Z];
            */

            Log.d(TAG, "Call Sensor Changed");
            p = getArrayIndex(p + 1);
            x[p] = event.values[SensorManager.DATA_X]; // X軸
            y[p] = event.values[SensorManager.DATA_Y]; // Y軸
            z[p] = event.values[SensorManager.DATA_Z]; // Z軸


        }

    }

    //簡易的に加速度を取得する
    public float getX() {
        return _x;
    }

    public float getY() {
        return _y;
    }

    public float getZ() {
        return _z;
    }


    float old_old_sum[] = new float[3];
    float old_sum[] = new float[3];
    float old_sum_opt[] = new float[3]; // 加速度データゼロ個時の回避用
    int last_mark; //最後にアクセスした配列位置

    // データ保存配列のインデックスpを上手いこと循環させる
    private int getArrayIndex(int p_tmp) {
        int p_real = p_tmp;
        while (p_real >= Define.SENSOR_STORE_MAX)
            p_real -= Define.SENSOR_STORE_MAX;
        while (p_real < 0)
            p_real += Define.SENSOR_STORE_MAX;
        return p_real;
    }


    public float[] getValueAverageFromLast() { // 前回アクセス時からの平均を取得,毎回配列の保存位置を0に戻して実装

        if (last_mark == p) return old_sum_opt;
        float sum[] = new float[3];
        sum[0] = sum[1] = sum[2] = 0.0f;
        int loop_counter = 0;
        for (int i = getArrayIndex(last_mark + 1); i != getArrayIndex(p + 1); i = getArrayIndex(i + 1)) {  // 最後のアクセス位置から回す
            sum[0] += x[i];
            sum[1] += y[i];
            sum[2] += z[i];
            loop_counter++;
        }
        last_mark = p;

        sum[0] = sum[0] / (loop_counter + 1);
        sum[1] = sum[1] / (loop_counter + 1);
        sum[2] = sum[2] / (loop_counter + 1);

        float alpha = 0.3f;
        float sum_opt[] = new float[3];

        sum_opt[0] = old_old_sum[0] * alpha + old_sum[0] * (1.0f - alpha * 2) + alpha * sum[0];
        sum_opt[1] = old_old_sum[1] * alpha + old_sum[1] * (1.0f - alpha * 2) + alpha * sum[1];
        sum_opt[2] = old_old_sum[2] * alpha + old_sum[2] * (1.0f - alpha * 2) + alpha * sum[2];
        old_old_sum = old_sum;
        old_sum = sum;
        old_sum_opt = sum_opt;
        return sum_opt;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //シングルトン 外にインスタンスを作らなくても、どこからでもつかえるようになる。 Gyroも同じ
    //Inst().xxxx()の形でメソッドを呼び出す
    private static Accelerometer _instance = new Accelerometer();

    private Accelerometer() {
        p = 0;
        //position = 0;
        last_mark = 0;
        x = new float[Define.SENSOR_STORE_MAX];
        y = new float[Define.SENSOR_STORE_MAX];
        z = new float[Define.SENSOR_STORE_MAX];
        //変数の初期化
        _x = _y = _z = 0;

    }

    public static Accelerometer Inst() {
        if (_instance == null) {
            _instance = new Accelerometer();
        }
        return _instance;
    }
}
