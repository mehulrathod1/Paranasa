package com.ni.parnasa.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;


import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.ni.parnasa.AsyncTaskUtils.ConnectionCheck;
import com.ni.parnasa.AsyncTaskUtils.ParseJSON;
import com.ni.parnasa.R;
import com.ni.parnasa.pojos.CommonPojo;
import com.ni.parnasa.utils.BaseUrl;
import com.ni.parnasa.utils.MyLogs;
import com.ni.parnasa.utils.PrefsUtil;

public class BackendUpdateLatLngService extends Service {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private String CHANNEL_ID = "my_pick_me_app";
    private String CHANNEL_NAME = "my_pickmeapp";
    private String CHANNEL_DESCRIPTION = "This is my channel";
    private String ACTION_STOP_SERVICE = "STOP";

    private NotificationManager notificationManager;
    private ConnectionCheck connectionCheck;
    private LocationManager locationManager;
    private String PROVIDER;
    private Location currentLocation = null;
    private int UPDATE_INTERVAL;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (currentLocation == null) {

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager.requestLocationUpdates(PROVIDER, 5000, 0, locationListener);

                MyLogs.w("TAG", "location not found");
                handler.postDelayed(runnable, (1000 * UPDATE_INTERVAL));
            } else {

//                MyLogs.w("TAG", "backend api call with location : " + currentLocation.getLatitude() + " | " + currentLocation.getLongitude());
                apiCallForUpdateLatLng();

            }
        }
    };

    private void apiCallForUpdateLatLng() {
        ArrayList<String> param = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        param.add("authorised_key");
        value.add(BaseUrl.authorised_key);

        param.add("device_auth_token");
        value.add(prefsUtil.getDeviceAuthToken());

        param.add("user_id");
        value.add(prefsUtil.GetUserID());

        param.add("current_latitude");
        value.add(String.valueOf(currentLocation.getLatitude()));

        param.add("current_longitude");
        value.add(String.valueOf(currentLocation.getLongitude()));

        new ParseJSON(mContext, BaseUrl.updateProfessionalLatLng, param, value, CommonPojo.class, new ParseJSON.OnResultListenerNew() {
            @Override
            public void onResult(boolean status, Object obj) {
                handler.postDelayed(runnable, (1000 * UPDATE_INTERVAL));
            }

            @Override
            public void onFailed() {
                handler.postDelayed(runnable, (1000 * UPDATE_INTERVAL));
            }
        }, false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        prefsUtil = new PrefsUtil(mContext);

        UPDATE_INTERVAL = prefsUtil.getUpdateInterval();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        connectionCheck = new ConnectionCheck();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        PROVIDER = LocationManager.NETWORK_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(PROVIDER);
        locationManager.requestLocationUpdates(PROVIDER, 5000, 0, locationListener);

        handler.post(runnable);

        MyLogs.e("TAG", "Service has been started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            notificationManager.cancel(107);
            stopSelf();
        }

        startNotificationDisplay();

        return START_STICKY;
    }

    private void startNotificationDisplay() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(mChannel);
        }

        /*Intent stopSelf = new Intent(this, BackgroundTaskService.class);
        stopSelf.setAction(this.ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);*/

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running")
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .addAction(R.drawable.ic_stop, "Stop", pStopSelf)
                .build();

        startForeground(107, notification);
    }

    @Override
    public void onDestroy() {
        MyLogs.e("TAG", "Service has been stopped");
        locationManager.removeUpdates(locationListener);
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
