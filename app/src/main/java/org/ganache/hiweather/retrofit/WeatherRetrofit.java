package org.ganache.hiweather.retrofit;

import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Repos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherRetrofit {
    String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/";

    @GET("getVilageFcst?serviceKey=E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D")
    Call<Example> getTown(@Query("dataType") String dataType,
                             @Query("base_date") String base_date,
                             @Query("base_time") String base_time,
                             @Query("nx") String nx,
                             @Query("ny") String ny,
                             @Query("numOfRows") String numOfRows);
}
