package com.pineapple.softgroup.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pineapple.softgroup.DB.DBHelperLastLocation;
import com.pineapple.softgroup.DB.DBHelperMap;
import com.pineapple.softgroup.DB.model.LastLocation;
import com.pineapple.softgroup.R;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.view.FrameMetrics.ANIMATION_DURATION;

public class FragmentMap extends Fragment implements OnMapReadyCallback {

    final String TAG = "Map";

    private String name;
    private String description;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Marker marker;

    private List<com.pineapple.softgroup.DB.model.Marker> markersList;
    private List<LastLocation> lastLocationsList;
    private LatLng latLng;

    private DBHelperLastLocation dbHelperLastLocation;
    private DBHelperMap dbHelperMap;


    public static FragmentMap newInstance() {
        FragmentMap fragment = new FragmentMap();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, null);

        dbHelperMap = new DBHelperMap(getActivity());
        dbHelperLastLocation = new DBHelperLastLocation(getActivity());

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.lmapFragment);

        mapFragment.getMapAsync(this);

        markersList = dbHelperMap.getAllMarkers();
        lastLocationsList = dbHelperLastLocation.getAllLockation();

        return v;
    }

    public LatLng getLatLng() {
        return latLng;
    }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setLatLnd(LatLng latLnd) {
        this.latLng = latLnd;
    }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setUpMap();

        init();

        onClickTest();
    }

    public void setUpMap() {

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; }
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.getCameraPosition();
        map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng()), ANIMATION_DURATION, null);
        map.setMinZoomPreference(12);
        setLatLnd(map.getCameraPosition().target);
    }

    public void onClickTest() {
        dbHelperMap = new DBHelperMap(getActivity());
        //Ввести title маркера
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng latLng) {

             AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
             LinearLayout layout = new LinearLayout(mapFragment.getActivity());
             layout.setOrientation(LinearLayout.VERTICAL);

             final EditText inputName = new EditText(mapFragment.getActivity());
             final EditText inputDescription = new EditText(mapFragment.getActivity());
             inputName.setHint("Enter name");
             inputDescription.setHint("Enter description");
             layout.addView(inputName);
             layout.addView(inputDescription);

             alertDialog.setView(layout);

             alertDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     if (inputName.getText().toString().isEmpty()) {
                         setName(getState(latLng.latitude, latLng.longitude));
                         if (inputDescription.getText().toString().isEmpty()) {
                             setDescription(getState(latLng.latitude, latLng.longitude));
                         }
                     } else {
                         setName(inputName.getText().toString());
                         setDescription(inputDescription.getText().toString());
                     }
                     //Додати дані маркеру в БД
                     dbHelperMap.addMarker(new com.pineapple.softgroup.DB.model.Marker
                             (name, description, latLng.latitude, latLng.longitude));
                     init();
                 }
             });

             alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                 }
             });

             alertDialog.show();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                AlertDialog.Builder markerAlertDialog = new AlertDialog.Builder(getActivity());
                LinearLayout markerLayout = new LinearLayout(mapFragment.getActivity());
                markerLayout.setOrientation(LinearLayout.VERTICAL);

                final TextView titleMarker = new TextView(mapFragment.getActivity());
                final TextView descriptMarker = new TextView(mapFragment.getActivity());

                titleMarker.setText(marker.getTitle());
                titleMarker.setTextSize(23);
                titleMarker.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                descriptMarker.setText(marker.getSnippet());
                descriptMarker.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                descriptMarker.setTextSize(15);
                markerLayout.addView(titleMarker);
                markerLayout.addView(descriptMarker);

                markerAlertDialog.setView(markerLayout);

                markerAlertDialog.setPositiveButton("CHENGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeMarker(marker);
                    }
                });

                markerAlertDialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMarker(marker.getPosition());
                    }
                });

                markerAlertDialog.setNeutralButton("BACK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                markerAlertDialog.show();
                setLatLnd(marker.getPosition());
                return true;
            }
        });
    }

    public LatLng lastLatLng() {
        LatLng lastLatLng;
        if (lastLocationsList.size() > 0) {
            lastLatLng = new LatLng(lastLocationsList.get(lastLocationsList.size() - 1).getLatitude(),
                    lastLocationsList.get(lastLocationsList.size() - 1).getLongitude());
        } else { lastLatLng = new LatLng(48.428568, 26.1721407); }
        return lastLatLng;
    }

    public void deleteMarker(LatLng ltng) {
        markersList = dbHelperMap.getAllMarkers();
        if (ltng == null) {
            Toast.makeText(getActivity(), "Виберіть маркер", Toast.LENGTH_LONG).show();
        } else {
            Iterator<com.pineapple.softgroup.DB.model.Marker> iter = markersList.iterator();
            while (iter.hasNext()) {
                com.pineapple.softgroup.DB.model.Marker s = iter.next();

                if ((s.getLatitude() == (ltng.latitude))
                        && (s.getLongitude() == (ltng.longitude))) {

                    iter.remove();
                    dbHelperMap.deleteMarker(s);
                }
            }
        }
        init();
    }

    public void changeMarker(final Marker marker) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        final LinearLayout layout = new LinearLayout(mapFragment.getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(mapFragment.getActivity());
        final EditText inputDescription = new EditText(mapFragment.getActivity());
        inputName.setHint("Enter name");
        inputName.setText(marker.getTitle());
        inputDescription.setHint("Enter description");
        inputDescription.setText(marker.getSnippet());
        layout.addView(inputName);
        layout.addView(inputDescription);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMarker(getLatLng());

                if (inputName.getText().toString().isEmpty()) {
                    setName(getState(marker.getPosition().latitude, marker.getPosition().longitude));
                    if (inputDescription.getText().toString().isEmpty()) {
                         setDescription(getState(marker.getPosition().latitude, marker.getPosition().longitude));
                    }
                } else {
                     setName(inputName.getText().toString());
                     setDescription(inputDescription.getText().toString());
                }
                 //Оновити дані маркеру в БД
                dbHelperMap.addMarker(new com.pineapple.softgroup.DB.model.Marker(getName(), getDescription(), marker.getPosition().latitude, marker.getPosition().longitude));

                init();
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    //Моє місцезнаходження місцезнаходження
    public void getLocation() {
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title("Ви знаходетеся в " + getState(location.getLatitude(), location.getLongitude())));
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }
        });
    }

    //Отримати назву місцезнаходження
    public String getState(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            return obj.getAddressLine(1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateList() {
        dbHelperMap = new DBHelperMap(getActivity());
        markersList.clear();
        markersList = dbHelperMap.getAllMarkers();
        map.clear();
    }

    //Добавити маркер з БД
    private void init() {
        updateList();
        for (com.pineapple.softgroup.DB.model.Marker mark : markersList) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(mark.getLatitude(), mark.getLongitude()))
                    .title(mark.getName())
                    .draggable(true)
                    .snippet(mark.getDescription()));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dbHelperLastLocation = new DBHelperLastLocation(getActivity());
        dbHelperLastLocation.addLockation(new LastLocation
                (map.getCameraPosition().target.latitude,
                map.getCameraPosition().target.longitude));
    }
}
