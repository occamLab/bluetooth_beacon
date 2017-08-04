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
    public TextToSpeech textToSpeech;
    public ArrayList<DataBeacon> beaconList = new ArrayList<DataBeacon>();
    public boolean reset = true;
    TextView textView;
    int strenght;
    TextView signalText;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browsing_beacon);

        adapter = new BeaconListAdapter(this);
        DataBeacon beacon1 = new DataBeacon(36513,11819,false, "Campus Center");
        DataBeacon beacon2 = new DataBeacon(29246,7567,false, "Academic Center");
        DataBeacon beacon3 = new DataBeacon(17986,64068,false, "Milas Hall");
        beaconList.add(beacon1);
        beaconList.add(beacon2);
        beaconList.add(beacon3);
        textView = (TextView) findViewById(R.id.locationArea);
        signalText = (TextView) findViewById(R.id.signalStrenght);

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
                System.out.println("Beacon Discovered");
                adapter.replaceWith(beacons);
                if(adapter.getCount() != 0){
                    if(adapter.getItem(0) != null){
                        mBeacon = adapter.getItem(0);
                        strenght = mBeacon.getRssi();
                        signalText.setText(Integer.toString(strenght));
                        if(mBeacon.getRssi() >= -85){
                            locateBeacon(mBeacon);
                            reset = false;
                        }
                    }
                }else{
                    if(reset == false){
                        resetEverything();
                        reset = true;
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

    public void locateBeacon(Beacon beacon){

        for(DataBeacon b: beaconList){
            if(b.getMajor() == beacon.getMajor() && b.getMinor() == beacon.getMinor()){
                if(b.getIsDiscovered() == false){
                    b.setIsDiscovered(true);
                    textToSpeech.speak(b.getLocation(), TextToSpeech.QUEUE_ADD, null, null);
                    textView.setText(b.getLocation());
                }
            }
            else{
                if(b.getIsDiscovered()){
                    b.setIsDiscovered(false);
                }
            }
        }

    }

    public void resetEverything(){
        TextView statusTextView = (TextView) findViewById(R.id.locationArea);
        statusTextView.setText("No Location Found");
        for(DataBeacon iB: beaconList){
            if(iB.getIsDiscovered()){
                iB.setIsDiscovered(false);
            }
        }
    }
}
