package zack.inc.jp.studytest2;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DriveActivity extends Activity {

    TextView speedText;
    TextView xAccelText;
    TextView yAccelText;
    TextView zAccelText;
    TextView jerkText;
    TextView latitudeText;
    TextView longitudeText;
    private DataLogger DL;
    private SceneAnalyzer mSA;
    private String driverName;

    java.text.DateFormat df;
    private float A[] = new float[3];
    private boolean buttonFlag = true;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Accelerometer.Inst().onCreate(this);
        GPS.Inst().onCreate(this);
        setContentView(R.layout.activity_drive);

        Intent intent = getIntent();
        driverName = intent.getStringExtra("DRIVER_NAME");

        //データ記録用のタイムスタンプフォーマット
        df = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss.SSS", Locale.JAPAN);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));


        mSA = new SceneAnalyzer();
        DL = new DataLogger(getApplicationContext(), df.format(new Date()), driverName);

        //
        speedText = (TextView) findViewById(R.id.speedText);
        xAccelText = (TextView) findViewById(R.id.x_Accel);
        yAccelText = (TextView) findViewById(R.id.y_Accel);
        zAccelText = (TextView) findViewById(R.id.z_Accel);
        jerkText = (TextView) findViewById(R.id.jerkText);
        latitudeText = (TextView) findViewById(R.id.latitude);
        longitudeText = (TextView) findViewById(R.id.longitude);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Accelerometer.Inst().onResume();
        GPS.Inst().onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        Accelerometer.Inst().onPause();
        GPS.Inst().onPause();
    }


    public void start(View v) {
        Log.d("Timer:", "Start");
        if (buttonFlag) {
            buttonFlag = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    infoUpdate(); //<- 情報を更新してみる
                    infoSave(); //<- Logをとってみる
                    mHandler.postDelayed(this, 200); //<- 0.2sごとに情報更新
                }
            }, 200);
        } else {
            buttonFlag = true;
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void infoUpdate() {

        A = Accelerometer.Inst().getValueAverageFromLast();
        xAccelText.setText("X:" + String.valueOf(A[0]));
        yAccelText.setText("y:" + String.valueOf(A[1]));
        zAccelText.setText("z:" + String.valueOf(A[2]));
        jerkText.setText(String.valueOf(0));
        latitudeText.setText(String.valueOf(GPS.Inst().getLatitude()));
        //Log.d("latitude:",String.valueOf(GPS.Inst().getLatitude()));
        longitudeText.setText(String.valueOf(GPS.Inst().getLongitude()));
        speedText.setText(String.valueOf(GPS.Inst().getSpeed()));

    }

    public void infoSave() {
        //計測データの記録
        //DL.saveLog(df.format(new Date()), A[0], A[1], A[2], GPS.Inst().getSpeed(), mSA.judgeStatus(), 0f, 0f);->一時凍結
        DL.saveLog(df.format(new Date()), A[0], A[1], A[2], GPS.Inst().getSpeed(), 0, 0f, 0f);//記録テスト
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }


}
