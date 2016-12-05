package zack.inc.jp.studytest2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by togane on 2016/07/25.
 */
public class GPS implements LocationListener {

    private LocationManager locman;
    private Location location;
    private Location stored_location[];
    private int p;
    private int count;
    //Gpsからの情報
    private double latitude;
    private double longitude;
    private double gpsSpeed;

    public void onCreate(Context context) {


        //stored_location[p] = new Location(LocationManager.GPS_PROVIDER);



        locman = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        location = locman.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        //TODO 初期値に前回のデータが残っている
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            gpsSpeed = location.getSpeed();
        }


        /*
        //GPSのオプション
        Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_HIGH);//取得制度
        criteria.setSpeedRequired(true);//速度情報の取得を要求
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);//速度制度
        */
    }

    public void onResume() {
        //enableGPS();
        locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    public void onPause() {
        if (locman != null) {
            locman.removeUpdates(this);
        }
    }


    public void enableGPS() {
        if (locman != null) {
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        gpsSpeed = location.getSpeed();
        this.location = location;
        if(location.getSpeed()==0){
            //Log.v("speed", String.valueOf(location.getSpeed()));
        }

    }

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude;}

    public double getSpeed() {return gpsSpeed;}

    public Location getLocation() {return location;}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                //Log.v("Status", "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                //Log.v("Status", "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                //Log.v("Status", "TEMPORARILY_UNAVAILABLE");
                break;
        }
    }


    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    //シングルトン 外にインスタンスを作らなくても、どこからでもつかえるようになる。 Gyroも同じ
    //Inst().xxxx()の形でメソッドを呼び出す
    private static GPS _instance = new GPS();

    private GPS() {
        p = 0;
        count = 0;
        stored_location = new Location[Define.GPS_STORE_MAX];

    }

    public static GPS Inst() {
        return _instance;
    }
}
