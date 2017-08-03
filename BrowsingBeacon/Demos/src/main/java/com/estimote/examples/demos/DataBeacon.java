package com.estimote.examples.demos;

public class DataBeacon {

    private int _major;
    private int _minor;
    private boolean _isDiscovered;

    public DataBeacon(int major, int minor, boolean isDiscovered ){
        _major = major;
        _minor = minor;
        _isDiscovered = isDiscovered;

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
}
