package org.ganache.hiweather;

import android.util.Log;
import android.view.View;

import org.ganache.hiweather.adapter.WeatherAdapter;
import org.ganache.hiweather.model.Example;
import org.ganache.hiweather.model.Item;
import org.ganache.hiweather.model.TomorrowWeather;
import org.ganache.hiweather.retrofit.WeatherRetrofit;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
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
        List<String> tmpDataList = new ArrayList<>();
        tmpDataList.add("13");
        tmpDataList.add("14");
        tmpDataList.add("12");
        tmpDataList.add("14");
        tmpDataList.add("13");

        System.out.println("tmpData = " + tmpDataList);

        String test = Collections.max(tmpDataList);
        System.out.println("test = " + test);


        String test2 = Collections.min(tmpDataList);
        System.out.println("test2 = " + test2);

    }


    @Test
    public void apiTest2() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherRetrofit.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        WeatherRetrofit service = retrofit.create(WeatherRetrofit.class);
        Call<Example> reposCall = service.getTown("JSON", "20211103", "1700", "58", "125", "200");
        List<Item> items = reposCall.execute().body().getResponse().getBody().getItems().getItem();

        LinkedHashSet days = new LinkedHashSet();
        LinkedHashSet times = new LinkedHashSet();

        List<String> dayList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();

        for (int i = 0 ; i < items.size() ; i++) {
            days.add(items.get(i).getFcstDate());
            times.add(items.get(i).getFcstTime());
        }

        dayList.addAll(days);
        timeList.addAll(times);

        System.out.println("dayList = " + dayList.toString());
        System.out.println("timeList = " + timeList.toString());

    }

    @Test
    public void apiTest3() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WeatherRetrofit.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        WeatherRetrofit service = retrofit.create(WeatherRetrofit.class);
        Call<Example> reposCall = service.getTown("JSON", "20211103", "1700", "58", "125", "200");
        List<Item> items = reposCall.execute().body().getResponse().getBody().getItems().getItem();

        List<String> dayList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();

        for (int i = 0 ; i < items.size() ; i++) {
            dayList.add(items.get(i).getFcstDate());
            timeList.add(items.get(i).getFcstTime());
        }

        List<String> dayList2 = dayList.stream().distinct().collect(Collectors.toList());
        List<String> timeList2 = timeList.stream().distinct().collect(Collectors.toList());

        System.out.println("dayList2 = " + dayList2.toString());
        System.out.println("timeList2 = " + timeList2.toString());

    }

    @Test
    public void apiTest4() throws IOException {
        String t1 = "2";
        String t2 = "-5";

        int i1 = Integer.parseInt(t1);
        int i2 = Integer.parseInt(t2);

        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);

        if (i1 > i2) {
            System.out.println("i1이 큼");
        } else {
            System.out.println("i2가 큼");
        }

    }
}