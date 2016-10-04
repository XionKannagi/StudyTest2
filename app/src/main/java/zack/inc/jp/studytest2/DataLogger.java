package zack.inc.jp.studytest2;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by togane on 2016/09/14.
 */

//TODO CSV ファイル書き込み部分の作成->ファイル名はTime+Driver_Nameで書き込む
//TODO 書き込めてないかも．パスが必要?

public class DataLogger {

    private String driverName;
    private String saveTime;
    private Context appContext;


    public DataLogger(Context context, String timeStamp, String driverName) {

        this.driverName = driverName;
        this.saveTime = timeStamp;
        this.appContext = context;

    }


    public void saveLog(String date, float Ax, float Ay, float Az, double latitude, double longitude, double speed) {
        FileOutputStream fos;
        PrintWriter pw = null;
        try {

            fos = appContext.openFileOutput(saveTime + driverName + ".csv", Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
            pw = new PrintWriter(fos);
            StringBuilder sb = new StringBuilder();
            //Logに必要なデータを詰めていく
            sb.append(date);
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
            sb.append("¥n");
            pw.println(sb.toString());
            pw.flush();
            fos.close();
            pw.close();

        } catch (FileNotFoundException nfe) {
            nfe.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }

        }

    }

}
