package org.ganache.hiweather.retrofit;

import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Repos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherService {
    String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService/";

    @GET("serviceKey/{serviceKey}/dataType/{dataType}/base_date/{base_date}/base_time/{base_time}/nx/{nx}/ny/{ny}")
    Call<Repos> listRepos(@Path("serviceKey") String serviceKey,
                            @Path("dataType") String dataType,
                            @Path("base_date") String base_date,
                            @Path("base_time") String base_time,
                            @Path("nx") String nx,
                            @Path("ny") String ny);

    @GET("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D&numOfRows=100&pageNo=1&dataType=JSON&base_date=20210614&base_time=0500&nx=58&ny=125")
    Call<Example> listRepos2();

}
