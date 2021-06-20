package org.ganache.hiweather;

import android.util.Log;

import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Repos;
import org.ganache.hiweather.retrofit.WeatherService;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void apiTest() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherService.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);

        System.out.println("???");

        String key = "E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D";

        Call<Example> reposCall = service.listRepos(key, "JSON", "20210620", "0500", "58", "125" );

        reposCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.isSuccessful()) {
                    Log.d("debug", "레트로핏 성공");
                    Example test = response.body();

                    Log.d("debug", "toString = " + test.toString());
                    Log.d("debug", "getTotalCount = " + test.getResponse().getBody().getTotalCount());
                    System.out.println("1");

                    //성공
                } else {
                    Log.d("debug", "레트로핏 실패");
                    System.out.println("2");
                    //실패
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                System.out.println("3");
                Log.d("debug", "레트로핏 예외, 인터넷 끊김 등 시스템적인 이유 실패");
                Log.d("debug", t.toString());
            }

        });




    }
}