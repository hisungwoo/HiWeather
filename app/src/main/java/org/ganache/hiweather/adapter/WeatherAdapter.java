package org.ganache.hiweather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.ganache.hiweather.R;
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
        private TextView itemTmpView;
        private TextView itemDayView;

        private Animation rotateAnim_s;
        private Animation scale_s;

        public WeatherViewHolder(View view) {
            super(view);
            itemTimeView = view.findViewById(R.id.item_time_tv);
            itemTmpView = view.findViewById(R.id.item_tmp_tv);
            itemDayView = view.findViewById(R.id.item_day_tv);
            itemImgView = view.findViewById(R.id.item_img_iv);

            rotateAnim_s = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_s);
            scale_s = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_s);
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

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, final int position) {
        TomorrowWeather item = items.get(position);
        holder.itemTimeView.setText(item.getTime());
        holder.itemTmpView.setText(item.getTomoTmp());
        holder.itemDayView.setText(item.getDay());

        // 맑음, 구름많음, 흐림, 비, 눈, 소나기
        if (item.getWeather().equals("맑음")) {
            holder.itemImgView.setImageResource(R.drawable.sunny);
        } else if (item.getWeather().equals("구름많음")) {
            holder.itemImgView.setImageResource(R.drawable.cloudy);
        } else if (item.getWeather().equals("흐림")) {
            holder.itemImgView.setImageResource(R.drawable.murky);
        } else if (item.getWeather().equals("비")) {
            holder.itemImgView.setImageResource(R.drawable.rain);
        } else if (item.getWeather().equals("눈")) {
            holder.itemImgView.setImageResource(R.drawable.snow);
        } else if (item.getWeather().equals("소나기")) {
            holder.itemImgView.setImageResource(R.drawable.shower);
        }

        holder.itemImgView.startAnimation(holder.rotateAnim_s);
        holder.itemDayView.startAnimation(holder.scale_s);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}










