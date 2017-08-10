package com.example.simran.locationbeacon;

public class DataBeacon {

    private int _major;
    private int _minor;
    private boolean _isDiscovered;
    private String _location;

    public DataBeacon(int major, int minor, boolean isDiscovered, String location ){
        _major = major;
        _minor = minor;
        _isDiscovered = isDiscovered;
        _location = location;

    }

    public boolean getIsDiscovered(){

        return _isDiscovered;
    }

    public void setIsDiscovered(boolean bool){

        _isDiscovered = bool;
    }

    public int getMajor(){

        return _major;
    }

    public int getMinor(){

        return _minor;
    }

    public String getLocation(){

        return _location;
    }
}
