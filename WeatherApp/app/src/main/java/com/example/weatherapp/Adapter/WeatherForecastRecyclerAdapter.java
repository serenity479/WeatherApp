package com.example.weatherapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Model.ForecastWeather.WeatherForecastResult;
import com.example.weatherapp.R;

public class WeatherForecastRecyclerAdapter extends RecyclerView.Adapter<WeatherForecastRecyclerAdapter.ViewHolder>{

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastRecyclerAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, parent, false); // создадим наш item в код
        return new ViewHolder(itemView); // передадим его в Холдер
    }

    @Override // на привязке установи все параметры для карточки
    public void onBindViewHolder(WeatherForecastRecyclerAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.image_weather);

        holder.text_date_time.setText(Common.convertUnixToDate(weatherForecastResult.list.get(position).dt));
        holder.text_description.setText(weatherForecastResult.list.get(position).weather.get(0).getDescription());
        holder.text_temperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getTemp())).append("°C"));

    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_date_time, text_description, text_temperature;
        ImageView image_weather;
        public ViewHolder( View itemView) {
            super(itemView);

            image_weather = itemView.findViewById(R.id.image_weather);
            text_date_time = itemView.findViewById(R.id.text_date);
            text_description = itemView.findViewById(R.id.text_description);
            text_temperature = itemView.findViewById(R.id.text_temperature);

        }
    }
}
