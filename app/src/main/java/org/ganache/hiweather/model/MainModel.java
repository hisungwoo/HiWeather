//MainModel class
//데이터 관리를 해줄 클래스
package org.ganache.hiweather.model;

import android.util.Log;

import androidx.annotation.NonNull;

import org.ganache.hiweather.R;
import org.ganache.hiweather.contract.MainContract;
import org.ganache.hiweather.retrofit.WeatherRetrofit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainModel implements MainContract.Model {
    MainContract.Presenter presenter;

    public MainModel(MainContract.Presenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void getWeatherList(String nowDay, String nowTime, LatXLngY gridXy) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherRetrofit.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        WeatherRetrofit service = retrofit.create(WeatherRetrofit.class);
        Call<Example> reposCall = service.getTown("JSON", nowDay, nowTime, gridXy.x, gridXy.y, "150");
        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(@NonNull Call<Example> call, @NonNull Response<Example> response) {
                if(response.isSuccessful()) {
                    try {
                        if (response.body() != null && response.body().getResponse() != null) {
                            List<Item> items = response.body().getResponse().getBody().getItems().getItem();
                            presenter.setWeatherInfo(items);

                        }
                    } catch (Exception e) {
                        presenter.setWeatherInfo(null);
                        Log.d("debug_test", "오류 : " + e.toString());
                    }
                } else {
                    presenter.setWeatherInfo(null);
                    Log.d("debug_test", "레트로핏 실패");
                    //실패
                }
            }

            @Override
            public void onFailure(@NonNull Call<Example> call, @NonNull Throwable t) {
                presenter.setWeatherInfo(null);
                Log.d("debug_test", "레트로핏 예외, 인터넷 끊김 등 시스템적인 이유 실패");
                Log.d("debug_test", t.toString());
            }

        });
    }
}
