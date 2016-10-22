package zack.inc.jp.studytest2;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView driverNameText;
    private DataLogger DL;
    private SceneAnalyzer mSA;
    private Calclater mCalc;
    private String driverName;
    private double startSpeed;
    private double endSpeed;
    private Location mlcation;
    private double mLatitude;
    private double mLongitude;
    private double mSpeed;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private float azMax;
    private long startTime;
    private long azPeakTime;
    private int stateCount = 0;//ブレーキ区間の判別に使用0 -> 開始/1 -> 終了

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
        mCalc = new Calclater(this);
        DL = new DataLogger(this, System.currentTimeMillis(), df.format(new Date()), driverName);

        //
        speedText = (TextView) findViewById(R.id.speedText);
        xAccelText = (TextView) findViewById(R.id.x_Accel);
        yAccelText = (TextView) findViewById(R.id.y_Accel);
        zAccelText = (TextView) findViewById(R.id.z_Accel);
        jerkText = (TextView) findViewById(R.id.jerkText);
        latitudeText = (TextView) findViewById(R.id.latitude);
        longitudeText = (TextView) findViewById(R.id.longitude);
        driverNameText = (TextView) findViewById(R.id.textView2);

        driverNameText.setText(driverName);
        infoUpdate();

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

    float dist;

    public void start(View v) {
        if (buttonFlag) {
            buttonFlag = false;
            if (GPS.Inst().getLocation() != null) {
                Log.d("Timer:", "Start");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        infoUpdate(); //<- 情報を更新してみる
                        infoSave(); //<- Log記録
                        //judge(); //<-こいつを動かすと判定＋教示がされる
                        mHandler.postDelayed(this, 200); //<- 0.2sごとに情報更新
                    }
                }, 200);
            } else {
                Log.d("GPS", "Location not found");
                Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            }
        } else {
            buttonFlag = true;
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    Location oldLocation;

    public void infoUpdate() {

        //加速度センサー
        A = Accelerometer.Inst().getValueAverageFromLast();
        xAccelText.setText("X:" + String.valueOf(A[0]));
        yAccelText.setText("y:" + String.valueOf(A[1]));
        zAccelText.setText("z:" + String.valueOf(A[2]));
        jerkText.setText(String.valueOf(0));

        //GPS
        oldLocation = mlcation;
        mlcation = GPS.Inst().getLocation();
        mLatitude = mlcation.getLatitude();
        mLongitude = mlcation.getLongitude();
        mSpeed = mlcation.getSpeed();
        latitudeText.setText(String.valueOf(mLatitude));
        longitudeText.setText(String.valueOf(mLongitude));
        speedText.setText(String.valueOf(mSpeed));


    }

    //TODO 計測モードと計測＆システムモードを作る．


    public void setStartInfo() {
        startSpeed = mSpeed;
        startTime = System.currentTimeMillis();
    }

    public void setEndInfo() {
        endSpeed = mSpeed;
        azMax = mSA.getAzMax();
        azPeakTime = mSA.getPeakTime() - startTime; // msで取得されている


    }

    public void infoSave() {
        //計測データの記録
        Log.d("information", "saved");
        DL.saveLog(System.currentTimeMillis(), A[0], A[1], A[2], mLatitude, mLongitude, mSpeed);//記録テスト
    }


    //TODO 見て欲しいのは判定メソッドの動き
    public void judge() {
        if (mSA.mainFunc(A[0], A[1], A[3], GPS.Inst().getSpeed())) {
            if (stateCount == 0) {
                setStartInfo();
                stateCount = 1;
            } else {
                setEndInfo();
                //理想のピーク時刻を取得するメソッドに，上の情報を投げる
                mCalc.caseSeparator(startSpeed, endSpeed, dist, azPeakTime, azMax);

                stateCount = 0;
            }
        }

        if (stateCount == 1) {
            dist += mCalc.getDistance(oldLocation.getLatitude(), oldLocation.getLongitude(), mLatitude, mLongitude);
        } else {
            dist = 0;
        }


    }

    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }


}
