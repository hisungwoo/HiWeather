package org.ganache.hiweather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.ganache.hiweather.R;
import org.ganache.hiweather.model.Item;
import org.ganache.hiweather.model.TomorrowWeather;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private List<TomorrowWeather> items = new ArrayList<>();


    // 아이템 뷰 정보를 가지고 있는 클래스
    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTimeView;
        private ImageView itemImgView;
        private TextView itemT3hView;
        private TextView itemDayView;


        public WeatherViewHolder(View view) {
            super(view);
            itemTimeView = (TextView) view.findViewById(R.id.item_time_tv);
            itemT3hView = (TextView) view.findViewById(R.id.item_t3h_tv);
            itemDayView = (TextView) view.findViewById(R.id.item_day_tv);
        }

        public TextView getTextView() {
            return itemTimeView;
        }
    }

    public void updateItems(List<TomorrowWeather> dataSet) {
        items = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_weather, viewGroup, false);

        return new WeatherViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WeatherViewHolder holder, final int position) {
        TomorrowWeather item = items.get(position);
        holder.itemTimeView.setText(item.getTime());
        holder.itemT3hView.setText(item.getTomoT3h());
        holder.itemDayView.setText(item.getDay());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}


