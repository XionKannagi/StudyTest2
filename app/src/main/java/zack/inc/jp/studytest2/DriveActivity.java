package zack.inc.jp.studytest2;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
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
    TextView logModeText;
    Button strStpButton;
    Switch modeSwitch;
    Switch logModeSwitch;
    ActionBar mActionBar;
    private DataLogger DL;
    private SceneAnalyzer mSA;
    private Calclater mCalc;
    private String driverName;
    private double startSpeed;
    private double endSpeed;
    private Location mlcation;
    Location oldLocation;
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
    private long finTime;
    private int stateCount = 0;//ブレーキ区間の判別に使用0 -> 開始/1 -> 終了

    java.text.DateFormat df;
    private float A[] = new float[3];
    private boolean buttonFlag = true;
    private boolean modeFlag; //教示モード <-> 計測モード 切り替えよう
    private boolean logModeFlag;
    private Handler mHandler = new Handler();
    private static int SUMPLING_RATE = 100;//ms


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


        //
        speedText = (TextView) findViewById(R.id.speedText);
        xAccelText = (TextView) findViewById(R.id.x_Accel);
        yAccelText = (TextView) findViewById(R.id.y_Accel);
        zAccelText = (TextView) findViewById(R.id.z_Accel);
        jerkText = (TextView) findViewById(R.id.jerkText);
        latitudeText = (TextView) findViewById(R.id.latitude);
        longitudeText = (TextView) findViewById(R.id.longitude);
        driverNameText = (TextView) findViewById(R.id.textView2);

        strStpButton = (Button) findViewById(R.id.str_stp_Button);

        mActionBar = getActionBar();
        mActionBar.setTitle("Measuring");

        logModeText = (TextView) findViewById(R.id.logModeText);
        logModeText.setText("Logging OFF");
        logModeText.setTextColor(Color.RED);

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
            strStpButton.setText("Stop!");
            if (GPS.Inst().getLocation() != null) {
                DL = new DataLogger(this, System.currentTimeMillis(), df.format(new Date()), driverName, modeFlag);
                Log.d("Timer:", "Start");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //handlerの関係で，この中での処理は避けるべき．
                        infoUpdate(); //<- 情報を更新
                        infoSave(); //<- 情報をLogに記録
                        judge(); //<-こいつを動かすと判定＋教示がされる
                        //TODO 更新，sampling を 25,50,100msに変更してみる
                        mHandler.postDelayed(this, SUMPLING_RATE);
                    }
                }, SUMPLING_RATE); //<- 0.1sごとに情報更新
            } else {
                Log.d("GPS", "Location not found");
                Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            }
        } else {
            buttonFlag = true;
            strStpButton.setText("Start");
            Log.d("Timer:", "Stop");
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    //情報の更新
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


    public void setStartInfo() {
        startSpeed = mSpeed;
        startTime = System.currentTimeMillis();//ブレーキ開始時刻
    }

    public void setEndInfo() {
        endSpeed = mSpeed;
        finTime = System.currentTimeMillis() - startTime;//ブレーキ終了時刻
        azMax = mSA.getAzMax();
        //TODO もしかすると，Calclaterクラスで計算させたほうがいいかもしれない．
        azPeakTime = mSA.getPeakTime(); // msで取得されている


    }


    //計測データの記録
    public void infoSave() {
        if (logModeFlag) {
            DL.saveLog(System.currentTimeMillis(), A[0], A[1], A[2], mLatitude, mLongitude, mSpeed);//記録
        }
    }


    //TODO 見て欲しいのは判定メソッドの動き
    public void judge() {

        int flag; //何から何に状態が変わったか入れとく
        if (modeFlag) {
            flag = mSA.mainFunc(A[0], A[1], A[2], GPS.Inst().getSpeed());
            if (flag == 1) {
                Log.v("judge", "ブレーキ始め");
                setStartInfo();
                stateCount = 1;
            } else if (flag == 2) {
                Log.v("judge", "ブレーキ終わり");
                setEndInfo();
                if (mSA.getArraysIndex() > 10) { //3s以下は誤差大きそうなので弾く
                    //理想のピーク時刻を取得するメソッドに，上の情報を投げる
                    mCalc.caseSeparator(startSpeed, endSpeed, dist, azPeakTime, finTime, azMax);
                    //mCalc.caseSeparatorV2(startSpeed,endSpeed,dist,azPeakTime,finTime,azMax,mSA.getArraysIndex(),mSA.getTimeArray(),mSA.getAzArray());//TODO ここを動かすとv２が動きます
                    Log.v("Index :"," "+mSA.getArraysIndex());
                    Log.v("start speed :", " " + startSpeed);
                }
                stateCount = 0;
            }
            if (flag == 1) {
                dist = 0;
            } else {
                dist += mCalc.getDistance(oldLocation.getLatitude(), oldLocation.getLongitude(), mLatitude, mLongitude);
                Log.v("Distance :", "" + dist);
            }

        } else {
            //なにもしない

        }


    }


    public void goBack(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Switchによるモード変更
    public void onSwitchCheck(View view) {
        modeSwitch = (Switch) view;

        if (modeSwitch.isChecked()) {

            mActionBar.setTitle("Teaching");
            mActionBar.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.color.teachModeColorPrimary));
            modeFlag = true; //教示モードのとき

        } else {

            mActionBar.setTitle("Measuring");
            mActionBar.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.color.background_material_dark));
            modeFlag = false;//計測モードのみのとき
        }

    }

    public void onLogSwitchChecked(View v) {
        logModeSwitch = (Switch) v;

        if (logModeSwitch.isChecked()) {
            logModeFlag = true;
            logModeText.setText("Logging ON");
            logModeText.setTextColor(getResources().getColor(R.color.LogModeTextColor));

        } else {
            logModeFlag = false;
            logModeText.setText("Logging OFF");
            logModeText.setTextColor(Color.RED);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCalc.onDestroy();
    }


}
