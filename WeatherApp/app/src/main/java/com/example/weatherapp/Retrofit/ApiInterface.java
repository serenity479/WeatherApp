package com.example.weatherapp.Retrofit;




import com.example.weatherapp.Model.ForecastWeather.WeatherForecastResult;
import com.example.weatherapp.Model.CurrentWeather.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat,
                                                 @Query("lon") String lng,
                                                 @Query("appid") String appid,
                                                 @Query("units") String units);


    @GET("forecast")
    Observable<WeatherForecastResult> getWeatherForecastByLatLng(@Query("lat") String lat,
                                                                 @Query("lon") String lng,
                                                                 @Query("appid") String appid,
                                                                 @Query("units") String units);



}
