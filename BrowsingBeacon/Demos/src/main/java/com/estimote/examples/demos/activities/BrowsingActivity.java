package com.estimote.examples.demos.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import com.estimote.examples.demos.DataBeacon;
import com.estimote.examples.demos.R;
import com.estimote.examples.demos.adapters.BeaconListAdapter;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BrowsingActivity extends Activity{

    private static final String TAG = BrowsingActivity.class.getSimpleName();


    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager;
    private BeaconListAdapter adapter;
    Beacon mBeacon;
    int minor;
    int major;
    public TextToSpeech textToSpeech;
    public ArrayList<DataBeacon> beaconList = new ArrayList<DataBeacon>();


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browsing_beacon);

        adapter = new BeaconListAdapter(this);
        DataBeacon beacon1 = new DataBeacon(36513,11819,false);
        DataBeacon beacon2 = new DataBeacon(29246,7567,false);
        DataBeacon beacon3 = new DataBeacon(17986,64068,false);
        beaconList.add(beacon1);
        beaconList.add(beacon2);
        beaconList.add(beacon3);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("eng", "usa"));
                    textToSpeech.setSpeechRate(1.5f);
                    textToSpeech.setPitch(1.618f);
                }
            }

        });

        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                adapter.replaceWith(beacons);
                if(adapter.getCount() != 0){
                    if(adapter.getItem(0) != null){
                        mBeacon = adapter.getItem(0);
                        for(DataBeacon b: beaconList){

                            if(b.getMajor() == mBeacon.getMajor() && b.getMinor() == mBeacon.getMinor()){
                                if(b.getIsDiscovered() == false){
                                    b.setIsDiscovered(true);
                                    beaconLocation(mBeacon);
                                }
                            }
                            else{
                                if(b.getIsDiscovered()){
                                    b.setIsDiscovered(false);
                                }
                            }
                        }
                    }
                }else{
                    TextView statusTextView = (TextView) findViewById(R.id.locationArea);
                    statusTextView.setText("No Location Found");
                    for(DataBeacon iB: beaconList){
                        if(iB.getIsDiscovered()){
                            iB.setIsDiscovered(false);
                        }
                    }
                }
            }
        });
    }

    @Override protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override protected void onResume() {
        super.onResume();

        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            startScanning();
        }
    }

    @Override protected void onStop() {
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);

        super.onStop();
    }

    private void startScanning() {
        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            }
        });
    }

    public void beaconLocation(Beacon beacon){

        major = beacon.getMajor();
        minor  = beacon.getMinor();

        if(major == 29246 && minor == 7567){
            Context context = getApplicationContext();
            CharSequence text = "Academic Center";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            TextView statusTextView = (TextView) findViewById(R.id.locationArea);
            statusTextView.setText("Academic Center");

            textToSpeech.speak("Academic Center", TextToSpeech.QUEUE_ADD, null, null);

        } else if(major == 36513 && minor == 11819){
            Context context = getApplicationContext();
            CharSequence text = "Campus Center";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            TextView statusTextView = (TextView) findViewById(R.id.locationArea);
            statusTextView.setText("Campus Center");

            textToSpeech.speak("Campus Center", TextToSpeech.QUEUE_ADD, null, null);

        } else if(major == 17986 && minor == 64068){
            Context context = getApplicationContext();
            CharSequence text = "Milas Hall";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            TextView statusTextView = (TextView) findViewById(R.id.locationArea);
            statusTextView.setText("Milas Hall");

            textToSpeech.speak("Milas Hall", TextToSpeech.QUEUE_ADD, null, null);

        } else{

            TextView statusTextView = (TextView) findViewById(R.id.locationArea);
            statusTextView.setText("No Location FOund");

            Log.e(TAG,Integer.toString(major));
            Log.e(TAG,Integer.toString(minor));
        }
    }
}
