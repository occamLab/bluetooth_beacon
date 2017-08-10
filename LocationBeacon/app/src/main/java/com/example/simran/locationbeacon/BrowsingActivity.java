package com.example.simran.locationbeacon;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;


import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BrowsingActivity extends Activity{

    private static final String TAG = BrowsingActivity.class.getSimpleName();
    private static final BeaconRegion ALL_ESTIMOTE_BEACONS_REGION = new BeaconRegion("rid", null, null, null);

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

        //Setting everything up
        adapter = new BeaconListAdapter(this);
        DataBeacon beacon1 = new DataBeacon(2315,33334,false, "Campus Center");
        DataBeacon beacon2 = new DataBeacon(55214,6103,false, "Academic Center");
        DataBeacon beacon3 = new DataBeacon(53530,4724,false, "Milas Hall");
        DataBeacon beacon4 = new DataBeacon(16857,56186,false, "Simran Hall");
        beaconList.add(beacon1);
        beaconList.add(beacon2);
        beaconList.add(beacon3);
        beaconList.add(beacon4);
        textView = (TextView) findViewById(R.id.locationArea);
        signalText = (TextView) findViewById(R.id.signalStrenght);

        //setting up the text to speech
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
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, final List<Beacon> beacons) {
                //set the adapter and check to see if their an item
                adapter.replaceWith(beacons);
                if(adapter.getCount() != 0){
                    if(adapter.getItem(0) != null){
                        mBeacon = adapter.getItem(0); //get the beacon
                        strenght = mBeacon.getRssi(); //get the RSSI number
                        signalText.setText(Integer.toString(strenght)); //print out the RSSI
                        //if RSSI is high enough send beacon to the method
                        if(mBeacon.getRssi() >= -85){
                            locateBeacon(mBeacon);
                            reset = false;
                        }
                    }
                }else{
                    //send to the method
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

    //find the beacon and tells and prints out the location
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

    //sets all discovered to false
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