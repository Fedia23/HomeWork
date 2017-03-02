package com.pineapple.softgroup.interfaces;

import com.pineapple.softgroup.DB.model.Marker;

import java.util.List;

public interface IDBHelperMap {
    void addMarker(Marker marker);
    Marker getMarker(double lat, double lnt);
    List<Marker> getAllMarkers();
    int getMarkersCount();
    int updateMarker(Marker marker);
    void deleteMarker(Marker marker);
    void deleteAll();
}
