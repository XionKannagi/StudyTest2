package zack.inc.jp.studytest2;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by togane on 2016/09/14.
 */



public class DataLogger {

    private String driverName;
    private String saveTime;
    private long initTime;
    private Context appContext;
    private String sysMode;


    public DataLogger(Context context, long initTime, String timeStamp, String driverName, boolean modeFlag) {

        this.driverName = driverName;
        this.initTime = initTime;
        this.saveTime = timeStamp;
        this.appContext = context;

        if(modeFlag){
            this.sysMode = "_on";//教示モードのとき
        } else {
            this.sysMode = "_off";//計測モードのとき
        }

    }


    public void saveLog(long time, float Ax, float Ay, float Az, double latitude, double longitude, double speed) {
        FileOutputStream fos;
        BufferedWriter bw = null;
        String exPathStr = appContext.getExternalFilesDir(null).getPath();// /root/sdcard/Android/data/package_name/filesを取得
        try {
            fos = new FileOutputStream(exPathStr + "/" + saveTime + "_" + driverName + sysMode + ".csv", true);// /root/sdcard/Android/data/package_name/files/直下に記録
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            StringBuilder sb = new StringBuilder();
            //Logに必要なデータを詰めていく
            sb.append(time - initTime); //記録確定時間からの経過時間
            sb.append(",");
            sb.append(Ax);
            sb.append(",");
            sb.append(Ay);
            sb.append(",");
            sb.append(Az);
            sb.append(",");
            sb.append(longitude);
            sb.append(",");
            sb.append(latitude);
            sb.append(",");
            sb.append(speed);

            bw.write(sb.toString());
            bw.newLine();
            bw.flush();

            fos.close();

        } catch (FileNotFoundException nfe) {
            nfe.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }

        }

    }

}
