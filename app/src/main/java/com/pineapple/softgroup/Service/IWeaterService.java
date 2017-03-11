package com.pineapple.softgroup.Service;

import com.pineapple.softgroup.json.forecastJson.Forecast;
import com.pineapple.softgroup.json.forecastJson.ForecastExample;
import com.pineapple.softgroup.json.Example;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IWeaterService {

    @GET("/v1/current.json")
    Call<Example> listRepos(@Query("key") String key, @Query("q") String q);

    @GET("/v1/forecast.json")
    Call<ForecastExample> getInfo(@Query("key") String key, @Query("q") String q, @Query("days") int days);

    @GET("/v1/forecast.json")
    Call<List<Forecast>> getForecastDay(@Query("key") String key, @Query("q") String q);


}
