package com.pineapple.softgroup.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pineapple.softgroup.DB.DBHelperLastLocation;
import com.pineapple.softgroup.DB.model.LastLocation;
import com.pineapple.softgroup.MainActivity;
import com.pineapple.softgroup.R;
import com.pineapple.softgroup.Service.IWeaterService;
import com.pineapple.softgroup.json.forecastJson.Forecast;
import com.pineapple.softgroup.json.forecastJson.ForecastExample;
import com.pineapple.softgroup.json.forecastJson.Forecastday;
import com.pineapple.softgroup.json.Current;
import com.pineapple.softgroup.json.Example;
import com.pineapple.softgroup.json.Location;

import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentWeater extends Fragment {

    private static final String TAG = "Weater";
    public RecyclerView adapterWeater;
    private RecyclerView.LayoutManager mLayoutManager;
    public RecyclerView.Adapter mAdapter;

    private Retrofit retrofit;
    private static IWeaterService service;
    private List<Example> exampleList;
    List<Forecastday> forecastdays;

    private com.pineapple.softgroup.json.Condition condition;
    private int conditionCode;

    private TextView textCurent, textLocation, textCondition, textTemp, textWind;
    private TextView oneDay, twoDay, threeDay, fourDay, fiveDay, sixDay;
    private ImageView imageWeather, oneImage, twoImage, threeImage, fourImage, fiveImage, sixImage;
    private ProgressBar progressBar;
    private LinearLayout forecastLayout;

    private DBHelperLastLocation dbHelperLastLocation;
    private List<LastLocation> lastLocationList;
    private LocationManager locationManager;
    private String myLocation = null;
    private String key;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weater, null);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        componentFindId(v);

        dontShowComponent();

        checkTheInternet();

        new AsynkWeater().execute();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        locationStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelTask();
    }

    private void locationStart() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                int i = 0;
                if (i == 0) {
                    setMyLocation(location);
                    i++;
                }
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

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);

    }

    public void setMyLocation(android.location.Location location) {
            dbHelperLastLocation = new DBHelperLastLocation(getActivity());
            lastLocationList = dbHelperLastLocation.getAllLockation();

        if (location == null) {

            if (lastLocationList.isEmpty()) {
                sleepTime(2);
                Toast.makeText(getActivity(), "Location is empty !!!", Toast.LENGTH_LONG).show();
                myLocation = "Kiev";
            } else {
                sleepTime(2);
                Toast.makeText(getActivity(), " Last Connection", Toast.LENGTH_LONG).show();
                myLocation = lastLocationList.get(lastLocationList.size() - 1).getLatitude() +
                        "," + lastLocationList.get(lastLocationList.size() - 1).getLongitude();
            }

        } else {
            sleepTime(2);
            Toast.makeText(getActivity(), "Location connection . . .", Toast.LENGTH_LONG).show();
            myLocation = (location.getLatitude() + "," + location.getLongitude());
        }
        new AsynkWeater().execute();
    }

    public String getMyLocation() {
        if (myLocation.isEmpty()) {
            Toast.makeText(getActivity(), "Fail", Toast.LENGTH_LONG).show();
        }
        return myLocation;
    }

    private void componentFindId(View v) {
        textCurent = (TextView) v.findViewById(R.id.textCurent);
        textLocation = (TextView) v.findViewById(R.id.textLocation);
        textCondition = (TextView) v.findViewById(R.id.textCondition);
        textTemp = (TextView) v.findViewById(R.id.textTemp);
        textWind = (TextView) v.findViewById(R.id.textWind);
        imageWeather = (ImageView) v.findViewById(R.id.imageWeather);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar2);

        forecastLayout = (LinearLayout) v.findViewById(R.id.forecastLayout);
        oneDay = (TextView) v.findViewById(R.id.oneDay);
        oneImage = (ImageView) v.findViewById(R.id.oneImage);
        twoDay = (TextView) v.findViewById(R.id.twoDay);
        twoImage = (ImageView) v.findViewById(R.id.twoImage);
        threeDay = (TextView) v.findViewById(R.id.threeDay);
        threeImage = (ImageView) v.findViewById(R.id.threeImage);
        fourDay = (TextView) v.findViewById(R.id.fourDay);
        fourImage = (ImageView) v.findViewById(R.id.fourImage);
        fiveDay = (TextView) v.findViewById(R.id.fiveDay);
        fiveImage = (ImageView) v.findViewById(R.id.fiveImage);
        sixDay = (TextView) v.findViewById(R.id.sixDay);
        sixImage = (ImageView) v.findViewById(R.id.sixImage);
    }

    private void sleepTime(Integer second) {
        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void checkTheInternet() {
        if (isNetworkAvailable()) {

        } else {
            sleepTime(5);
            Toast.makeText(getActivity(), "No internet ! ! ! ", Toast.LENGTH_LONG).show();
        }
    }

    public void loadDate() {
        key = "4eea53de339c44399f8181049171302";
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.apixu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(IWeaterService.class);
        int days = 6;
        service.getInfo(key, getMyLocation(), days).enqueue(new Callback<ForecastExample>() {
            @Override
            public void onResponse(Call<ForecastExample> call, Response<ForecastExample> response) {
                Forecast forecast = response.body().getForecast();
                Log.d("List size=", String.valueOf(forecast.getForecastday().size()));
                try {
                    oneDay.setText(forecast.getForecastday().get(0).getDay().getMintempC().toString() + "°C");
                    twoDay.setText(forecast.getForecastday().get(1).getDay().getMintempC().toString() + "°C");
                    threeDay.setText(forecast.getForecastday().get(2).getDay().getMintempC().toString() + "°C");
                    fourDay.setText(forecast.getForecastday().get(3).getDay().getMintempC().toString() + "°C");
                    fiveDay.setText(forecast.getForecastday().get(4).getDay().getMintempC().toString() + "°C");
                    sixDay.setText(forecast.getForecastday().get(5).getDay().getMintempC().toString() + "°C");

                    String dayOneIcon = forecast.getForecastday().get(0).getDay().getCondition().getIcon();
                    String dayTwoIcon = forecast.getForecastday().get(1).getDay().getCondition().getIcon();
                    String dayThreeIcon = forecast.getForecastday().get(2).getDay().getCondition().getIcon();
                    String dayFourIcon = forecast.getForecastday().get(3).getDay().getCondition().getIcon();
                    String dayFiveIcon = forecast.getForecastday().get(4).getDay().getCondition().getIcon();
                    String daySixIcon = forecast.getForecastday().get(5).getDay().getCondition().getIcon();

                    Glide.with(FragmentWeater.this).load("http:" + dayOneIcon).centerCrop().into(oneImage);
                    Glide.with(FragmentWeater.this).load("http:" + dayTwoIcon).centerCrop().into(twoImage);
                    Glide.with(FragmentWeater.this).load("http:" + dayThreeIcon).centerCrop().into(threeImage);
                    Glide.with(FragmentWeater.this).load("http:" + dayFourIcon).centerCrop().into(fourImage);
                    Glide.with(FragmentWeater.this).load("http:" + dayFiveIcon).centerCrop().into(fiveImage);
                    Glide.with(FragmentWeater.this).load("http:" + daySixIcon).centerCrop().into(sixImage);
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ForecastExample> call, Throwable t) {
                Log.e("Error:", t.getMessage());
            }
        });

        service.listRepos(key, getMyLocation()).enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                Location location = response.body().getLocation();
                Current current = response.body().getCurrent();
                try {
                    condition = response.body().getCurrent().getCondition();
                    conditionCode = response.body().getCurrent().getCondition().getCode();

                    textLocation.setText(location.getName() + " ! " + location.getCountry() + ", " + location.getRegion());

                    textCurent.setText(current.getLastUpdated());
                    textTemp.setText("Temperature: " + current.getTempC() + "°C");
                    textWind.setText("Wind: " + current.getWindMph() + "m/ph");
                    textCondition.setText("Condition: " + condition.getText());
                    String ikon = condition.getIcon();

                } catch(Exception e) {

                }
                //Glide.with(FragmentWeater.this).load("http:"+ikon).centerCrop().into(imageWeather);
                // Picasso.with(getActivity()).load("http:"+ikon).into(imageWeather);
                setupUI();
                showComponent();
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.e("Error:", t.getMessage());
            }
        });
    }

    private void dontShowComponent() {
        textCurent.setVisibility(View.INVISIBLE);
        textLocation.setVisibility(View.INVISIBLE);
        textCondition.setVisibility(View.INVISIBLE);
        textTemp.setVisibility(View.INVISIBLE);
        textWind.setVisibility(View.INVISIBLE);
        imageWeather.setVisibility(View.INVISIBLE);
        forecastLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showComponent() {
        textCurent.setVisibility(View.VISIBLE);
        textLocation.setVisibility(View.VISIBLE);
        textCondition.setVisibility(View.VISIBLE);
        textTemp.setVisibility(View.VISIBLE);
        textWind.setVisibility(View.VISIBLE);
        imageWeather.setVisibility(View.VISIBLE);
        forecastLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setupUI() {
        try {
            if (condition != null) {
                if (conditionCode == 1000) {
                    imageWeather.setImageResource(R.drawable.icon_1000);
                } else if (conditionCode == 1003) {
                    imageWeather.setImageResource(R.drawable.icon_1003);
                } else if (conditionCode == 1006) {
                    imageWeather.setImageResource(R.drawable.icon_1006);
                } else if (conditionCode == 1009) {
                    imageWeather.setImageResource(R.drawable.icon_1009);
                } else if (conditionCode == 1030) {
                    imageWeather.setImageResource(R.drawable.icon_1030);
                } else if (conditionCode == 1063) {
                    imageWeather.setImageResource(R.drawable.icon_1063);
                } else if (conditionCode == 1066) {
                    imageWeather.setImageResource(R.drawable.icon_1066);
                } else if (conditionCode == 1069) {
                    imageWeather.setImageResource(R.drawable.icon_1069);
                } else if (conditionCode == 1072) {
                    imageWeather.setImageResource(R.drawable.icon_1072);
                } else if (conditionCode == 1201) {
                    imageWeather.setImageResource(R.drawable.icon_1201);
                } else if (conditionCode == 1087) {
                    imageWeather.setImageResource(R.drawable.icon_1087);
                } else if (conditionCode == 1114) {
                    imageWeather.setImageResource(R.drawable.icon_1114);
                } else if (conditionCode == 1117) {
                    imageWeather.setImageResource(R.drawable.icon_1117);
                } else if (conditionCode == 1135) {
                    imageWeather.setImageResource(R.drawable.icon_1135);
                } else if (conditionCode == 1147) {
                    imageWeather.setImageResource(R.drawable.icon_1147);
                } else if (conditionCode == 1150) {
                    imageWeather.setImageResource(R.drawable.icon_1150);
                } else if (conditionCode == 1153) {
                    imageWeather.setImageResource(R.drawable.rain);
                } else if (conditionCode == 1168) {
                    imageWeather.setImageResource(R.drawable.rain);
                } else if (conditionCode == 1171) {
                    imageWeather.setImageResource(R.drawable.rain);
                } else if (conditionCode == 1180) {
                    imageWeather.setImageResource(R.drawable.icon_1180);
                } else if (conditionCode == 1183) {
                    imageWeather.setImageResource(R.drawable.rain);
                } else if (conditionCode == 1186) {
                    imageWeather.setImageResource(R.drawable.icon_1186);
                } else if (conditionCode == 1189) {
                    imageWeather.setImageResource(R.drawable.rain);
                } else if (conditionCode == 1192) {
                    imageWeather.setImageResource(R.drawable.icon_1192);
                } else if (conditionCode == 1195) {
                    imageWeather.setImageResource(R.drawable.rain);
                } else if (conditionCode == 1198) {
                    imageWeather.setImageResource(R.drawable.icon_1198);
                } else if (conditionCode == 1201) {
                    imageWeather.setImageResource(R.drawable.rain);
                } else if (conditionCode == 1204) {
                    imageWeather.setImageResource(R.drawable.icon_1204);
                } else if (conditionCode == 1207) {
                    imageWeather.setImageResource(R.drawable.icon_1207);
                } else if (conditionCode == 1210) {
                    imageWeather.setImageResource(R.drawable.icon_1210);
                } else if (conditionCode == 1213) {
                    imageWeather.setImageResource(R.drawable.icon_1213);
                } else if (conditionCode == 1216) {
                    imageWeather.setImageResource(R.drawable.icon_1216);
                } else if (conditionCode == 1219) {
                    imageWeather.setImageResource(R.drawable.icon_1219);
                } else if (conditionCode == 1222) {
                    imageWeather.setImageResource(R.drawable.icon_1222);
                } else if (conditionCode == 1225) {
                    imageWeather.setImageResource(R.drawable.icon_1225);
                } else if (conditionCode == 1237) {
                    imageWeather.setImageResource(R.drawable.icon_1237);
                } else if (conditionCode == 1240) {
                    imageWeather.setImageResource(R.drawable.icon_1240);
                } else if (conditionCode == 1243) {
                    imageWeather.setImageResource(R.drawable.icon_1243);
                } else if (conditionCode == 1246) {
                    imageWeather.setImageResource(R.drawable.icon_1246);
                } else if (conditionCode == 1249) {
                    imageWeather.setImageResource(R.drawable.icon_1249);
                } else if (conditionCode == 1252) {
                    imageWeather.setImageResource(R.drawable.icon_1252);
                } else if (conditionCode == 1255) {
                    imageWeather.setImageResource(R.drawable.icon_1255);
                } else if (conditionCode == 1258) {
                    imageWeather.setImageResource(R.drawable.icon_1258);
                } else if (conditionCode == 1261) {
                    imageWeather.setImageResource(R.drawable.icon_1261);
                } else if (conditionCode == 1264) {
                    imageWeather.setImageResource(R.drawable.icon_1264);
                } else if (conditionCode == 1273) {
                    imageWeather.setImageResource(R.drawable.icon_1273);
                } else if (conditionCode == 1276) {
                    imageWeather.setImageResource(R.drawable.icon_1276);
                } else if (conditionCode == 1279) {
                    imageWeather.setImageResource(R.drawable.icon_1279);
                } else if (conditionCode == 1282) {
                    imageWeather.setImageResource(R.drawable.icon_1282);
                } else {

                }
            }
        } catch(Exception e) {

        }
    }

    private void cancelTask(){
        if(new AsynkWeater().execute() == null)
            return;
        new AsynkWeater().execute().cancel(false);
    }

    private class AsynkWeater extends AsyncTask<Void, Void, Void> {

        private static final String LOG_TAG = "AsngWeater";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textCurent.setVisibility(View.INVISIBLE);
            textLocation.setVisibility(View.INVISIBLE);
            textCondition.setVisibility(View.INVISIBLE);
            textTemp.setVisibility(View.INVISIBLE);
            textWind.setVisibility(View.INVISIBLE);
            imageWeather.setVisibility(View.INVISIBLE);
            forecastLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (isCancelled()) return null;
            try {
                loadDate();
            } catch(Exception e) {}

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(LOG_TAG, "Cancel");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
