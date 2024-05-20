package com.example.weatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.Adapter.WeatherForecastRecyclerAdapter;
import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Model.ForecastWeather.WeatherForecastResult;
import com.example.weatherapp.Retrofit.ApiInterface;
import com.example.weatherapp.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastWeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    static ForecastWeatherFragment instance;
    CompositeDisposable compositeDisposable;
    ApiInterface service;

    TextView text_city_name, text_geocoord;
    RecyclerView recycler_forecast;




    public ForecastWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        service = retrofit.create(ApiInterface.class);
    }


    public static ForecastWeatherFragment getInstance(){
        if(instance == null){
            instance = newInstance("Today", "weather");
        }
        return instance;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastWeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastWeatherFragment newInstance(String param1, String param2) {
        ForecastWeatherFragment fragment = new ForecastWeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        View itemView = inflater.inflate(R.layout.fragment_forecast_weather, container, false);

        text_city_name = itemView.findViewById(R.id.text_city_name);
        text_geocoord = itemView.findViewById(R.id.text_geocoord);

        recycler_forecast = itemView.findViewById(R.id.recycler_forecast);
        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        getForecastWeatherInformation();

        return itemView;
    }


    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void getForecastWeatherInformation() {
        // Это и есть весь конвеер RXJava
        compositeDisposable.add(service.getWeatherForecastByLatLng(String.valueOf(Common.current_location.getLatitude()), // одновременно добавим несколько запросов? чтобы потом разом отменить
                String.valueOf(Common.current_location.getLongitude()), Common.API_KEY,"metric")  // получим данные, это как бы наш observable

                .subscribeOn(Schedulers.io()) //  запихиваем прием в другой поток. Все, что он получил будет происходить в другом потоке после этого метода

                .observeOn(AndroidSchedulers.mainThread()) // Возвращаем данные в основной поток. а это в конце будет выдаваться

                // после запуска получателей код запустится
                .subscribe(new Consumer<WeatherForecastResult>() { // получатель на результат
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        text_city_name.setText(weatherForecastResult.city.name);
                        text_geocoord.setText(weatherForecastResult.city.coord.toString());

                        WeatherForecastRecyclerAdapter weatherForecastRecyclerAdapter = new WeatherForecastRecyclerAdapter(getContext(), weatherForecastResult); // передадим в адаптер то, где он будет исполняться и полученные данные из интернета для заполнения item
                        recycler_forecast.setAdapter(weatherForecastRecyclerAdapter); // установим адаптер в recycler, чтоб он начал выполнять свои функции


                    }
                }, new Consumer<Throwable>() {  // получатель на ошибку
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }
}