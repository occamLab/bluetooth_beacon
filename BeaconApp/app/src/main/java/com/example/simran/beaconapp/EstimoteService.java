package com.example.simran.beaconapp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class EstimoteService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        System.out.println("STARTING THIS UP! bind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("STARTING THIS UP!");
        try {
            System.out.println("starting notification service");
            EstimoteManager.Create((NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE), this,
                    intent);
        } catch (Exception e) {
            System.out.println("Failed to start notification service");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EstimoteManager.stop();
    }
}
