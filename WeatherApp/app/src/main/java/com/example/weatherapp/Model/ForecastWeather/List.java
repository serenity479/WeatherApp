package com.example.weatherapp.Model.ForecastWeather;

import com.example.weatherapp.Model.CurrentWeather.Clouds;
import com.example.weatherapp.Model.CurrentWeather.Main;
import com.example.weatherapp.Model.CurrentWeather.Sys;
import com.example.weatherapp.Model.CurrentWeather.Weather;
import com.example.weatherapp.Model.CurrentWeather.Wind;

import java.util.ArrayList;

public class List {
    public int dt;
    public Main main;
    public ArrayList<Weather> weather;
    public Clouds clouds;
    public Wind wind;
    public int visibility;
    public double pop;
    public Sys sys;
    public String dt_txt;
    public Rain rain;

}
