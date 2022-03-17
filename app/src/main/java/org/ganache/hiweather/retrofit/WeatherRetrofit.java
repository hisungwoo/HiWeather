package org.ganache.hiweather.retrofit;

import org.ganache.hiweather.BuildConfig;
import org.ganache.hiweather.model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRetrofit {
    String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";

    @GET("getVilageFcst?serviceKey=" + BuildConfig.OPEN_API_KEY)
    Call<Example> getTown(@Query("dataType") String dataType,
                             @Query("base_date") String base_date,
                             @Query("base_time") String base_time,
                             @Query("nx") String nx,
                             @Query("ny") String ny,
                             @Query("numOfRows") String numOfRows
    );
}

