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

        Double x = 58.0;
        Double y = 125.0;

        String result = String.format("%.0f", x);
        System.out.println("x = " + result);



    }
}