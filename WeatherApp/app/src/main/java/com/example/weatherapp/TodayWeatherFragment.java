package com.example.weatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Model.CurrentWeather.WeatherResult;
import com.example.weatherapp.Retrofit.ApiInterface;
import com.example.weatherapp.Retrofit.RetrofitClient;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayWeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ImageView img_weather;
    TextView text_city_name, text_humidity, text_sunrise, text_sunset,
             text_pressure, text_temperature, text_description, text_date_time,
             text_wind, text_geocoords;

    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    ApiInterface service;

    static TodayWeatherFragment instance;


    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        service = retrofit.create(ApiInterface.class);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayWeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayWeatherFragment newInstance(String param1, String param2) {
        TodayWeatherFragment fragment = new TodayWeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public static TodayWeatherFragment getInstance(){
        if(instance == null){
            instance = newInstance("Today", "weather");
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);

        img_weather = (ImageView) itemView.findViewById(R.id.image_weather);
        text_city_name = (TextView) itemView.findViewById(R.id.tv_city_name);
        text_temperature = (TextView) itemView.findViewById(R.id.text_temperature);
        text_description = (TextView) itemView.findViewById(R.id.text_description);
        text_date_time = (TextView) itemView.findViewById(R.id.text_date_time);
        text_pressure = (TextView) itemView.findViewById(R.id.text_pressure);
        text_humidity = (TextView) itemView.findViewById(R.id.text_humidity);
        text_sunrise = (TextView) itemView.findViewById(R.id.text_sunrise);
        text_sunset = (TextView) itemView.findViewById(R.id.text_sunset);
        text_wind = (TextView) itemView.findViewById(R.id.text_wind);
        text_geocoords = (TextView) itemView.findViewById(R.id.text_geo_coords);


        weather_panel = (LinearLayout) itemView.findViewById(R.id.weather_panel);
        loading = (ProgressBar) itemView.findViewById(R.id.loading);
        
        getWeatherInformation();
        return itemView;
    }

    private void getWeatherInformation() {
        // Это и есть весь конвеер RXJava
        compositeDisposable.add(service.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()), // одновременно добавим несколько запросов? чтобы потом разом отменить
                String.valueOf(Common.current_location.getLongitude()),
                Common.API_KEY,"metric")  // получим данные, это как бы наш observable

                .subscribeOn(Schedulers.io()) //  запихиваем прием в другой поток. Все, что он получил будет происходить в другом потоке после этого метода

                .observeOn(AndroidSchedulers.mainThread()) // Возвращаем данные в основной поток. а это в конце будет выдаваться

                // после запуска получателей код запустится
                .subscribe(new Consumer<WeatherResult>() { // получатель на результат
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Glide.with(getActivity()).load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                        .append(".png").toString()).into(img_weather);

                        text_city_name.setText(weatherResult.getName());
                        text_description.setText(new StringBuilder("Weather in ").append(weatherResult.getName()).toString());
                        text_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        text_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        text_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        text_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                        text_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        text_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        text_geocoords.setText(weatherResult.getCoord().toString());

                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);


                    }
                }, new Consumer<Throwable>() {  // получатель на ошибку
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }


    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}