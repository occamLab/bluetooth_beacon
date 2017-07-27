package com.example.simran.beaconapp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.UUID;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager.BeaconMonitoringListener;

public class EstimoteManager {
    private static final int NOTIFICATION_ID = 123;
    private static BeaconManager beaconManager;
    private static NotificationManager notificationManager;
    public static final String EXTRAS_BEACON = "extrasBeacon";
    private static final String ESTIMOTE_PROXIMITY_UUID = "b9407f30-f5f8-466e-aff9-25556b57feed";
    private static final BeaconRegion ALL_ESTIMOTE_BEACONS = new BeaconRegion("regionId",
            UUID.fromString(ESTIMOTE_PROXIMITY_UUID), 29246, 7567);

    private static Context currentContext;

    // Create everything we need to monitor the beacons
    public static void Create(NotificationManager notificationMngr,
                              Context context, final Intent i) {
        try {
            EstimoteSDK.initialize(context, "paullundyruvolo-gmail-com--c7u", "45f1aaec5f81b9a53f17f159f808124a");

            notificationManager = notificationMngr;
            System.err.println("context is "  + context);
            currentContext = context;

            // Create a beacon manager
            beaconManager = new BeaconManager(currentContext);

            // We want the beacons heartbeat to be set at one second.
            beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1),
                    0);

            // Method called when a beacon gets...
            beaconManager.setMonitoringListener(new BeaconMonitoringListener() {
                // ... close to us.
                @Override
                public void onEnteredRegion(BeaconRegion beaconRegion, java.util.List<Beacon> beacons) {
                    System.out.println("\"Estimote testing\",\n" +
                                    "                            \"I have found an estimote !!!\"");
                    postNotificationIntent("Estimote testing",
                            "I have found an estimote !!!", i);
                }

                // ... far away from us.
                @Override
                public void onExitedRegion(BeaconRegion beaconRegion)
                {
                   postNotificationIntent("Estimote testing",
                            "I have lost my estimote !!!", i);
                }
            });

            // Connect to the beacon manager...
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        // ... and start the monitoring
                        beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS);
                        System.out.println("Started monitoring successfully");
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    // Pops a notification in the task bar
    public static void postNotificationIntent(String title, String msg, Intent i) {
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                currentContext, 0, new Intent[] { i },
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(currentContext)
                .setContentText(msg).setAutoCancel(true)
                .setContentIntent(pendingIntent).build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    // Stop beacons monitoring, and closes the service
    public static void stop() {
        try {
            beaconManager.stopMonitoring("regionId");
            beaconManager.disconnect();
            System.out.println("Disconnect is successful");
        } catch (Exception e) {
        }
    }
}
