package org.ganache.hiweather;

import android.util.Log;

import org.ganache.hiweather.model.Repos;
import org.ganache.hiweather.retrofit.WeatherService;
import org.junit.Test;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
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

        Call<Repos> reposCall = service.listRepos(key, "JSON", "20210614", "0500", "58", "125" );

        reposCall.enqueue(new Callback<Repos>() {
            @Override
            public void onResponse(Call<Repos> call, retrofit2.Response<Repos> response) {
                Log.i("api" , "성공");
                System.out.println("1");
            }

            @Override
            public void onFailure(Call<Repos> call, Throwable t) {
                assertEquals("asdf",t.getMessage());
                System.out.println("2");
            }

        });




    }
}